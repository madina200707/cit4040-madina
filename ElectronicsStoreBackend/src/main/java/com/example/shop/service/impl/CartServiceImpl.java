package com.example.shop.service.impl;

import com.example.shop.dto.request.AddToCartRequest;
import com.example.shop.dto.request.ApplyPromoCodeRequest;
import com.example.shop.dto.request.UpdateCartItemRequest;
import com.example.shop.dto.response.CartItemResponse;
import com.example.shop.dto.response.CartResponse;
import com.example.shop.exception.BadRequestException;
import com.example.shop.exception.InsufficientStockException;
import com.example.shop.exception.PromoCodeInvalidException;
import com.example.shop.exception.ResourceNotFoundException;
import com.example.shop.exception.UnauthorizedException;
import com.example.shop.model.entity.*;
import com.example.shop.repository.CartRepository;
import com.example.shop.repository.ProductRepository;
import com.example.shop.repository.ProductVariantRepository;
import com.example.shop.service.CartService;
import com.example.shop.service.PromoCalculation;
import com.example.shop.service.PromoCodeService;
import com.example.shop.service.PromoLine;
import com.example.shop.util.PriceCalculator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class CartServiceImpl implements CartService {
    private static final int MAX_QUANTITY_PER_LINE = 99;

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;
    private final PromoCodeService promoCodeService;
    private final UserLookup userLookup;

    public CartServiceImpl(CartRepository cartRepository,
                           ProductRepository productRepository,
                           ProductVariantRepository variantRepository,
                           PromoCodeService promoCodeService,
                           UserLookup userLookup) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.variantRepository = variantRepository;
        this.promoCodeService = promoCodeService;
        this.userLookup = userLookup;
    }

    @Override
    public CartResponse getCart(String email, String sessionId) {
        return toResponse(resolveCart(email, sessionId));
    }

    @Override
    public CartResponse addItem(String email, String sessionId, AddToCartRequest request) {
        Cart cart = resolveCart(email, sessionId);
        Product product = productRepository.findByIdAndIsActiveTrue(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("No product with id " + request.productId()));
        ProductVariant variant = findVariant(product, request.variantId());
        addToCart(cart, product, variant, request.quantity());
        return toResponse(cart);
    }

    @Override
    public CartResponse updateItem(String email, String sessionId, Long itemId,
                                   UpdateCartItemRequest request) {
        Cart cart = resolveCart(email, sessionId);
        CartItem item = findItem(cart, itemId);
        checkStock(item.getProduct(), item.getVariant(), request.quantity());
        item.setQuantity(request.quantity());
        return toResponse(cart);
    }

    @Override
    public CartResponse removeItem(String email, String sessionId, Long itemId) {
        Cart cart = resolveCart(email, sessionId);
        cart.getCartItems().remove(findItem(cart, itemId));
        return toResponse(cart);
    }

    @Override
    public void clear(String email, String sessionId) {
        Cart cart = resolveCart(email, sessionId);
        cart.getCartItems().clear();
        cart.setAppliedPromoCode(null);
    }

    @Override
    public CartResponse applyPromoCode(String email, String sessionId, ApplyPromoCodeRequest request) {
        Cart cart = resolveCart(email, sessionId);
        if (cart.getCartItems().isEmpty()) {
            throw new BadRequestException("Add items to the cart before applying a promo code");
        }
        PromoCalculation calculation = promoCodeService.calculate(request.code(), cart.getUser(), lines(cart));
        cart.setAppliedPromoCode(calculation.promoCode().getCode().toUpperCase(Locale.ROOT));
        return toResponse(cart);
    }

    @Override
    public CartResponse removePromoCode(String email, String sessionId) {
        Cart cart = resolveCart(email, sessionId);
        cart.setAppliedPromoCode(null);
        return toResponse(cart);
    }

    @Override
    public CartResponse merge(String email, String guestSessionId) {
        Cart userCart = resolveCart(email, null);
        Cart guestCart = cartRepository.findBySessionIdAndUserIsNull(guestSessionId)
                .orElseThrow(() -> new ResourceNotFoundException("No guest cart for this session id"));
        for (CartItem guestItem : guestCart.getCartItems()) {
            Optional<CartItem> existing = findSameLine(userCart, guestItem.getProduct(), guestItem.getVariant());
            int available = PriceCalculator.availableQuantity(guestItem.getProduct(), guestItem.getVariant());
            if (existing.isPresent()) {
                CartItem item = existing.get();
                int quantity = Math.min(item.getQuantity() + guestItem.getQuantity(),
                        Math.min(available, MAX_QUANTITY_PER_LINE));
                item.setQuantity(Math.max(quantity, item.getQuantity()));
            } else if (available > 0) {
                CartItem item = newItem(userCart, guestItem.getProduct(), guestItem.getVariant(),
                        Math.min(guestItem.getQuantity(), available));
                item.setPriceAtAdd(guestItem.getPriceAtAdd());
                userCart.getCartItems().add(item);
            }
        }
        if (userCart.getAppliedPromoCode() == null) {
            userCart.setAppliedPromoCode(guestCart.getAppliedPromoCode());
        }
        cartRepository.delete(guestCart);
        cartRepository.saveAndFlush(userCart);
        return toResponse(userCart);
    }

    public void addToCart(Cart cart, Product product, ProductVariant variant, int quantity) {
        Optional<CartItem> existing = findSameLine(cart, product, variant);
        int newQuantity = quantity + existing.map(CartItem::getQuantity).orElse(0);
        if (newQuantity > MAX_QUANTITY_PER_LINE) {
            throw new BadRequestException("At most " + MAX_QUANTITY_PER_LINE + " of one product per order");
        }
        checkStock(product, variant, newQuantity);
        if (existing.isPresent()) {
            existing.get().setQuantity(newQuantity);
        } else {
            cart.getCartItems().add(newItem(cart, product, variant, newQuantity));
        }
        cartRepository.saveAndFlush(cart);
    }

    public Cart userCart(User user) {
        return cartRepository.findByUserId(user.getId()).orElseGet(() -> {
            Cart cart = new Cart();
            cart.setUser(user);
            return cartRepository.save(cart);
        });
    }

    public CartResponse toResponse(Cart cart) {
        List<CartItemResponse> items = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        int totalQuantity = 0;
        boolean anyPriceChanged = false;
        for (CartItem item : cart.getCartItems()) {
            Product product = item.getProduct();
            BigDecimal currentPrice = PriceCalculator.currentPrice(product, item.getVariant());
            BigDecimal lineTotal = currentPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
            boolean changed = currentPrice.compareTo(item.getPriceAtAdd()) != 0;
            anyPriceChanged |= changed;
            items.add(new CartItemResponse(
                    item.getId(),
                    product.getId(),
                    product.getName(),
                    product.getSlug(),
                    item.getVariant() == null ? null : item.getVariant().getId(),
                    item.getVariant() == null ? null : item.getVariant().getName(),
                    item.getQuantity(),
                    item.getPriceAtAdd(),
                    currentPrice,
                    changed,
                    currentPrice.subtract(item.getPriceAtAdd()),
                    lineTotal,
                    Math.max(PriceCalculator.availableQuantity(product, item.getVariant()), 0)));
            subtotal = subtotal.add(lineTotal);
            totalQuantity += item.getQuantity();
        }

        BigDecimal discount = BigDecimal.ZERO;
        boolean freeShipping = false;
        String promoMessage = null;
        if (cart.getAppliedPromoCode() != null && !cart.getCartItems().isEmpty()) {
            try {
                PromoCalculation calculation = promoCodeService.calculate(
                        cart.getAppliedPromoCode(), cart.getUser(), lines(cart));
                discount = calculation.discount();
                freeShipping = calculation.freeShipping();
            } catch (PromoCodeInvalidException exception) {
                promoMessage = exception.getMessage();
            }
        }
        return new CartResponse(
                cart.getId(),
                items,
                totalQuantity,
                subtotal,
                cart.getAppliedPromoCode(),
                discount,
                freeShipping,
                promoMessage,
                subtotal.subtract(discount),
                anyPriceChanged);
    }

    private Cart resolveCart(String email, String sessionId) {
        if (email != null) {
            return userCart(userLookup.byEmail(email));
        }
        if (sessionId == null || sessionId.isBlank()) {
            throw new UnauthorizedException(
                    "Log in, or send an X-Session-Id header to use a guest cart");
        }
        return cartRepository.findBySessionIdAndUserIsNull(sessionId).orElseGet(() -> {
            Cart cart = new Cart();
            cart.setSessionId(sessionId);
            return cartRepository.save(cart);
        });
    }

    private ProductVariant findVariant(Product product, Long variantId) {
        if (variantId == null) {
            return null;
        }
        ProductVariant variant = variantRepository.findByIdAndProductId(variantId, product.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No variant with id " + variantId + " for product " + product.getId()));
        if (!Boolean.TRUE.equals(variant.getIsActive())) {
            throw new ResourceNotFoundException("Variant " + variantId + " is not available");
        }
        return variant;
    }

    private CartItem findItem(Cart cart, Long itemId) {
        return cart.getCartItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No item with id " + itemId + " in your cart"));
    }

    private Optional<CartItem> findSameLine(Cart cart, Product product, ProductVariant variant) {
        Long variantId = variant == null ? null : variant.getId();
        return cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .filter(item -> Objects.equals(
                        item.getVariant() == null ? null : item.getVariant().getId(), variantId))
                .findFirst();
    }

    private CartItem newItem(Cart cart, Product product, ProductVariant variant, int quantity) {
        CartItem item = new CartItem();
        item.setCart(cart);
        item.setProduct(product);
        item.setVariant(variant);
        item.setQuantity(quantity);
        item.setPriceAtAdd(PriceCalculator.currentPrice(product, variant));
        return item;
    }

    private void checkStock(Product product, ProductVariant variant, int quantity) {
        int available = PriceCalculator.availableQuantity(product, variant);
        if (quantity > available) {
            throw new InsufficientStockException("Only " + Math.max(available, 0) + " of "
                    + product.getName() + " available");
        }
    }

    private List<PromoLine> lines(Cart cart) {
        List<PromoLine> lines = new ArrayList<>();
        for (CartItem item : cart.getCartItems()) {
            BigDecimal price = PriceCalculator.currentPrice(item.getProduct(), item.getVariant());
            lines.add(PromoLine.of(item.getProduct(), price.multiply(BigDecimal.valueOf(item.getQuantity()))));
        }
        return lines;
    }
}

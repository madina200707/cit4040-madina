package com.example.shop.service.impl;

import com.example.shop.dto.response.CartResponse;
import com.example.shop.dto.response.PageResponse;
import com.example.shop.dto.response.WishlistItemResponse;
import com.example.shop.exception.ConflictException;
import com.example.shop.exception.ResourceNotFoundException;
import com.example.shop.mapper.CatalogMapper;
import com.example.shop.model.entity.Cart;
import com.example.shop.model.entity.Product;
import com.example.shop.model.entity.User;
import com.example.shop.model.entity.Wishlist;
import com.example.shop.repository.ProductRepository;
import com.example.shop.repository.WishlistRepository;
import com.example.shop.service.WishlistService;
import com.example.shop.util.PriceCalculator;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class WishlistServiceImpl implements WishlistService {
    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final CartServiceImpl cartService;
    private final UserLookup userLookup;
    private final CatalogMapper catalogMapper;

    public WishlistServiceImpl(WishlistRepository wishlistRepository,
                               ProductRepository productRepository,
                               CartServiceImpl cartService,
                               UserLookup userLookup,
                               CatalogMapper catalogMapper) {
        this.wishlistRepository = wishlistRepository;
        this.productRepository = productRepository;
        this.cartService = cartService;
        this.userLookup = userLookup;
        this.catalogMapper = catalogMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<WishlistItemResponse> getWishlist(String email, Pageable pageable) {
        User user = userLookup.byEmail(email);
        return PageResponse.from(wishlistRepository.findByUserId(user.getId(), pageable)
                .map(this::toResponse));
    }

    @Override
    public WishlistItemResponse add(String email, Long productId) {
        User user = userLookup.byEmail(email);
        Product product = productRepository.findByIdAndIsActiveTrue(productId)
                .orElseThrow(() -> new ResourceNotFoundException("No product with id " + productId));
        if (wishlistRepository.existsByUserIdAndProductId(user.getId(), productId)) {
            throw new ConflictException("This product is already in your wishlist");
        }
        Wishlist entry = new Wishlist();
        entry.setUser(user);
        entry.setProduct(product);
        entry.setPriceAtAdd(PriceCalculator.currentPrice(product));
        entry.setNotifyOnPriceDrop(false);
        entry.setNotifyOnBackInStock(false);
        return toResponse(wishlistRepository.save(entry));
    }

    @Override
    public void remove(String email, Long productId) {
        User user = userLookup.byEmail(email);
        wishlistRepository.delete(find(user, productId));
    }

    @Override
    public CartResponse moveToCart(String email, Long productId) {
        User user = userLookup.byEmail(email);
        Wishlist entry = find(user, productId);
        Product product = entry.getProduct();
        if (!Boolean.TRUE.equals(product.getIsActive())) {
            throw new ResourceNotFoundException("Product " + productId + " is no longer available");
        }
        Cart cart = cartService.userCart(user);
        cartService.addToCart(cart, product, null, 1);
        wishlistRepository.delete(entry);
        return cartService.toResponse(cart);
    }

    private Wishlist find(User user, Long productId) {
        return wishlistRepository.findByUserIdAndProductId(user.getId(), productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product " + productId + " is not in your wishlist"));
    }

    private WishlistItemResponse toResponse(Wishlist entry) {
        Product product = entry.getProduct();
        BigDecimal currentPrice = PriceCalculator.currentPrice(product);
        return new WishlistItemResponse(
                entry.getId(),
                product.getId(),
                product.getName(),
                product.getSlug(),
                catalogMapper.primaryImageUrl(product),
                entry.getPriceAtAdd(),
                currentPrice,
                entry.getPriceAtAdd() != null && currentPrice.compareTo(entry.getPriceAtAdd()) < 0,
                PriceCalculator.availableQuantity(product, null) > 0,
                entry.getNotifyOnPriceDrop(),
                entry.getNotifyOnBackInStock(),
                entry.getAddedAt());
    }
}

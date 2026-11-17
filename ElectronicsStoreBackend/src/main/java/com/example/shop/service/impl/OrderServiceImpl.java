package com.example.shop.service.impl;

import com.example.shop.dto.request.CreateOrderRequest;
import com.example.shop.dto.request.UpdateOrderNoteRequest;
import com.example.shop.dto.request.UpdateOrderStatusRequest;
import com.example.shop.dto.request.UpdateTrackingRequest;
import com.example.shop.dto.response.AdminOrderResponse;
import com.example.shop.dto.response.CartResponse;
import com.example.shop.dto.response.OrderResponse;
import com.example.shop.dto.response.PageResponse;
import com.example.shop.exception.*;
import com.example.shop.mapper.OrderMapper;
import com.example.shop.model.entity.*;
import com.example.shop.model.enums.OrderStatus;
import com.example.shop.repository.*;
import com.example.shop.service.*;
import com.example.shop.util.JsonConverter;
import com.example.shop.util.PriceCalculator;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    private static final String CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final OrderRepository orderRepository;
    private final AddressRepository addressRepository;
    private final CartRepository cartRepository;
    private final PromoCodeRepository promoCodeRepository;
    private final PromoCodeUsageRepository promoCodeUsageRepository;
    private final PromoCodeService promoCodeService;
    private final DeliveryService deliveryService;
    private final OrderStatusManager statusManager;
    private final CartServiceImpl cartService;
    private final UserLookup userLookup;
    private final OrderMapper mapper;
    private final JsonConverter json;

    public OrderServiceImpl(OrderRepository orderRepository,
                            AddressRepository addressRepository,
                            CartRepository cartRepository,
                            PromoCodeRepository promoCodeRepository,
                            PromoCodeUsageRepository promoCodeUsageRepository,
                            PromoCodeService promoCodeService,
                            DeliveryService deliveryService,
                            OrderStatusManager statusManager,
                            CartServiceImpl cartService,
                            UserLookup userLookup,
                            OrderMapper mapper,
                            JsonConverter json) {
        this.orderRepository = orderRepository;
        this.addressRepository = addressRepository;
        this.cartRepository = cartRepository;
        this.promoCodeRepository = promoCodeRepository;
        this.promoCodeUsageRepository = promoCodeUsageRepository;
        this.promoCodeService = promoCodeService;
        this.deliveryService = deliveryService;
        this.statusManager = statusManager;
        this.cartService = cartService;
        this.userLookup = userLookup;
        this.mapper = mapper;
        this.json = json;
    }

    @Override
    public OrderResponse create(String email, CreateOrderRequest request) {
        User user = userLookup.byEmail(email);
        Cart cart = cartRepository.findByUserId(user.getId())
                .filter(c -> !c.getCartItems().isEmpty())
                .orElseThrow(() -> new BadRequestException("Your cart is empty"));
        Address address = addressRepository.findByIdAndUserId(request.addressId(), user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No address with id " + request.addressId()));
        DeliveryOption deliveryOption = deliveryService.getActiveOption(request.deliveryMethod());

        Order order = new Order();
        order.setUser(user);
        order.setOrderNumber(newOrderNumber());
        order.setStatus(OrderStatus.PENDING);
        order.setShippingAddress(copyAddress(address));
        order.setDeliveryMethod(request.deliveryMethod());
        order.setCustomerNote(request.customerNote());

        BigDecimal subtotal = BigDecimal.ZERO;
        List<PromoLine> promoLines = new ArrayList<>();
        for (CartItem cartItem : cart.getCartItems()) {
            Product product = cartItem.getProduct();
            ProductVariant variant = cartItem.getVariant();
            if (!Boolean.TRUE.equals(product.getIsActive())
                    || (variant != null && !Boolean.TRUE.equals(variant.getIsActive()))) {
                throw new BadRequestException(product.getName()
                        + " is no longer sold; remove it from the cart");
            }
            int quantity = cartItem.getQuantity();
            if (quantity > PriceCalculator.availableQuantity(product, variant)) {
                throw new InsufficientStockException("Not enough stock for " + product.getName());
            }
            statusManager.reserve(product, variant, quantity);

            BigDecimal price = PriceCalculator.currentPrice(product, variant);
            BigDecimal lineTotal = price.multiply(BigDecimal.valueOf(quantity));
            subtotal = subtotal.add(lineTotal);
            promoLines.add(PromoLine.of(product, lineTotal));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setVariant(variant);
            orderItem.setQuantity(quantity);
            orderItem.setPriceAtPurchase(price);
            orderItem.setProductSnapshot(snapshot(product, variant, price));
            order.getOrderItems().add(orderItem);
        }

        PromoCalculation promo = null;
        if (cart.getAppliedPromoCode() != null) {
            promo = promoCodeService.calculate(cart.getAppliedPromoCode(), user, promoLines);
        }

        OrderTotalsCalculator.Totals totals = OrderTotalsCalculator.calculate(
                subtotal,
                promo == null ? BigDecimal.ZERO : promo.discount(),
                promo != null && promo.freeShipping(),
                deliveryOption.getBasePrice(),
                deliveryOption.getFreeThreshold(),
                0,
                0);
        order.setSubtotal(totals.subtotal());
        order.setDiscount(totals.discount());
        order.setShippingCost(totals.shippingCost());
        order.setBonusPointsUsed(totals.bonusPointsUsed());
        order.setBonusPointsEarned(0);
        order.setTotalAmount(totals.totalAmount());
        order.setPromoCode(promo == null ? null : promo.promoCode().getCode());
        Integer maxDays = deliveryOption.getEstimatedDaysMax();
        order.setEstimatedDeliveryDate(LocalDate.now().plusDays(maxDays == null ? 0 : maxDays));

        Order saved = orderRepository.save(order);

        if (promo != null) {
            PromoCode promoCode = promo.promoCode();
            PromoCodeUsage usage = new PromoCodeUsage();
            usage.setPromoCode(promoCode);
            usage.setUser(user);
            usage.setOrder(saved);
            usage.setDiscountApplied(totals.discount());
            usage.setUsedAt(saved.getCreatedAt());
            promoCodeUsageRepository.save(usage);
            promoCode.setCurrentUsageCount(promoCode.getCurrentUsageCount() + 1);
            promoCodeRepository.save(promoCode);
        }

        cart.getCartItems().clear();
        cart.setAppliedPromoCode(null);
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> getMyOrders(String email, Pageable pageable) {
        User user = userLookup.byEmail(email);
        return PageResponse.from(orderRepository.findByUserId(user.getId(), pageable)
                .map(mapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getMyOrder(String email, Long orderId) {
        return mapper.toResponse(findOwnOrder(email, orderId));
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getMyOrderByNumber(String email, String orderNumber) {
        User user = userLookup.byEmail(email);
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("No order with number " + orderNumber));
        checkOwner(order, user);
        return mapper.toResponse(order);
    }

    @Override
    public OrderResponse cancel(String email, Long orderId) {
        Order order = findOwnOrder(email, orderId);
        if (!OrderStatusTransitions.isAllowed(order.getStatus(), OrderStatus.CANCELLED)) {
            throw new OrderCancellationException(
                    "An order with status " + order.getStatus() + " can no longer be cancelled");
        }
        statusManager.changeStatus(order, OrderStatus.CANCELLED);
        return mapper.toResponse(order);
    }

    @Override
    public CartResponse reorder(String email, Long orderId) {
        Order order = findOwnOrder(email, orderId);
        Cart cart = cartService.userCart(order.getUser());
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            ProductVariant variant = item.getVariant();
            boolean stillSold = Boolean.TRUE.equals(product.getIsActive())
                    && (variant == null || Boolean.TRUE.equals(variant.getIsActive()));
            if (stillSold) {
                cartService.addToCart(cart, product, variant, item.getQuantity());
            }
        }
        return cartService.toResponse(cart);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AdminOrderResponse> search(OrderStatus status, String customerEmail,
                                                   LocalDate from, LocalDate to, Pageable pageable) {
        List<Specification<Order>> specs = new ArrayList<>();
        if (status != null) {
            specs.add((root, query, cb) -> cb.equal(root.get("status"), status));
        }
        if (customerEmail != null && !customerEmail.isBlank()) {
            String email = customerEmail.trim().toLowerCase(Locale.ROOT);
            specs.add((root, query, cb) -> cb.equal(cb.lower(root.get("user").<String>get("email")), email));
        }
        if (from != null) {
            specs.add((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.<LocalDateTime>get("createdAt"), from.atStartOfDay()));
        }
        if (to != null) {
            specs.add((root, query, cb) ->
                    cb.lessThan(root.<LocalDateTime>get("createdAt"), to.plusDays(1).atStartOfDay()));
        }
        return PageResponse.from(orderRepository.findAll(Specification.allOf(specs), pageable)
                .map(mapper::toAdminResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public AdminOrderResponse getForStaff(Long orderId) {
        return mapper.toAdminResponse(findOrder(orderId));
    }

    @Override
    public AdminOrderResponse updateStatus(Long orderId, UpdateOrderStatusRequest request) {
        Order order = findOrder(orderId);
        if (order.getStatus() == OrderStatus.DELIVERED && request.status() == OrderStatus.REFUNDED) {
            throw new InvalidStatusTransitionException(
                    "A delivered order is refunded only through a return");
        }
        statusManager.changeStatus(order, request.status());
        return mapper.toAdminResponse(order);
    }

    @Override
    public AdminOrderResponse updateTracking(Long orderId, UpdateTrackingRequest request) {
        Order order = findOrder(orderId);
        if (order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.REFUNDED) {
            throw new ConflictException("A " + order.getStatus() + " order has no delivery to track");
        }
        order.setDeliveryTrackingNumber(request.trackingNumber().trim());
        if (request.estimatedDeliveryDate() != null) {
            order.setEstimatedDeliveryDate(request.estimatedDeliveryDate());
        }
        return mapper.toAdminResponse(order);
    }

    @Override
    public AdminOrderResponse updateNote(Long orderId, UpdateOrderNoteRequest request) {
        Order order = findOrder(orderId);
        order.setAdminNote(request.adminNote());
        return mapper.toAdminResponse(order);
    }

    private Order findOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("No order with id " + orderId));
    }

    private Order findOwnOrder(String email, Long orderId) {
        User user = userLookup.byEmail(email);
        Order order = findOrder(orderId);
        checkOwner(order, user);
        return order;
    }

    private void checkOwner(Order order, User user) {
        if (!order.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("This order belongs to another customer");
        }
    }

    private ShippingAddress copyAddress(Address address) {
        ShippingAddress copy = new ShippingAddress();
        copy.setCountry(address.getCountry());
        copy.setCity(address.getCity());
        copy.setStreet(address.getStreet());
        copy.setBuilding(address.getBuilding());
        copy.setApartment(address.getApartment());
        copy.setPostalCode(address.getPostalCode());
        copy.setRecipientName(address.getRecipientName());
        copy.setRecipientPhone(address.getRecipientPhone());
        copy.setLatitude(address.getLatitude());
        copy.setLongitude(address.getLongitude());
        return copy;
    }

    private String snapshot(Product product, ProductVariant variant, BigDecimal price) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("productId", product.getId());
        data.put("name", product.getName());
        data.put("sku", variant != null ? variant.getSku() : product.getSku());
        data.put("variant", variant != null ? variant.getName() : null);
        data.put("brand", product.getBrand() != null ? product.getBrand().getName() : null);
        data.put("category", product.getCategory().getName());
        data.put("price", price);
        return json.toJson(data);
    }

    private String newOrderNumber() {
        String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String number;
        do {
            StringBuilder suffix = new StringBuilder();
            for (int i = 0; i < 6; i++) {
                suffix.append(CODE_CHARS.charAt(RANDOM.nextInt(CODE_CHARS.length())));
            }
            number = "ORD-" + date + "-" + suffix;
        } while (orderRepository.existsByOrderNumber(number));
        return number;
    }
}

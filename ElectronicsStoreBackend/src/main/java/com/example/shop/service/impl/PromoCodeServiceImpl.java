package com.example.shop.service.impl;

import com.example.shop.dto.request.CreatePromoCodeRequest;
import com.example.shop.dto.request.UpdatePromoCodeRequest;
import com.example.shop.dto.request.ValidatePromoCodeRequest;
import com.example.shop.dto.response.PageResponse;
import com.example.shop.dto.response.PromoCodeResponse;
import com.example.shop.dto.response.PromoValidationResponse;
import com.example.shop.exception.BadRequestException;
import com.example.shop.exception.ConflictException;
import com.example.shop.exception.PromoCodeInvalidException;
import com.example.shop.exception.ResourceNotFoundException;
import com.example.shop.mapper.PromoCodeMapper;
import com.example.shop.model.entity.*;
import com.example.shop.model.enums.DiscountType;
import com.example.shop.repository.*;
import com.example.shop.service.PromoCalculation;
import com.example.shop.service.PromoCodeService;
import com.example.shop.service.PromoLine;
import com.example.shop.util.PriceCalculator;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PromoCodeServiceImpl implements PromoCodeService {
    private static final BigDecimal HUNDRED = new BigDecimal("100");

    private final PromoCodeRepository promoCodeRepository;
    private final PromoCodeUsageRepository usageRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final UserLookup userLookup;
    private final PromoCodeMapper mapper;

    public PromoCodeServiceImpl(PromoCodeRepository promoCodeRepository,
                                PromoCodeUsageRepository usageRepository,
                                CategoryRepository categoryRepository,
                                ProductRepository productRepository,
                                CartRepository cartRepository,
                                UserLookup userLookup,
                                PromoCodeMapper mapper) {
        this.promoCodeRepository = promoCodeRepository;
        this.usageRepository = usageRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.userLookup = userLookup;
        this.mapper = mapper;
    }

    @Override
    public PromoCalculation calculate(String code, User user, List<PromoLine> lines) {
        PromoCode promo = promoCodeRepository.findByCodeIgnoreCase(code.trim())
                .orElseThrow(() -> new PromoCodeInvalidException("Promo code " + code + " does not exist"));
        BigDecimal subtotal = lines.stream().map(PromoLine::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        checkUsable(promo, user, subtotal);

        BigDecimal eligible = eligibleAmount(promo, lines);
        if (eligible.signum() <= 0) {
            throw new PromoCodeInvalidException(
                    "Promo code " + promo.getCode() + " does not apply to these products");
        }

        BigDecimal discount;
        boolean freeShipping = false;
        if (promo.getDiscountType() == DiscountType.PERCENTAGE) {
            discount = eligible.multiply(promo.getDiscountValue())
                    .divide(HUNDRED, 2, RoundingMode.HALF_UP);
        } else if (promo.getDiscountType() == DiscountType.FIXED_AMOUNT) {
            discount = promo.getDiscountValue().min(eligible);
        } else {
            discount = BigDecimal.ZERO;
            freeShipping = true;
        }
        if (promo.getMaxDiscountAmount() != null) {
            discount = discount.min(promo.getMaxDiscountAmount());
        }
        return new PromoCalculation(promo, discount.setScale(2, RoundingMode.HALF_UP), freeShipping);
    }

    @Override
    @Transactional(readOnly = true)
    public PromoValidationResponse validate(String email, ValidatePromoCodeRequest request) {
        User user = userLookup.byEmail(email);
        List<PromoLine> lines;
        if (request.orderAmount() != null) {
            lines = List.of(PromoLine.amountOnly(request.orderAmount()));
        } else {
            lines = cartLines(user);
            if (lines.isEmpty()) {
                throw new BadRequestException("Your cart is empty: send orderAmount or add items first");
            }
        }
        BigDecimal amount = lines.stream().map(PromoLine::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        PromoCalculation result = calculate(request.code(), user, lines);
        return new PromoValidationResponse(
                result.promoCode().getCode(),
                true,
                result.promoCode().getDiscountType(),
                amount,
                result.discount(),
                result.freeShipping(),
                amount.subtract(result.discount()),
                result.freeShipping() ? "Shipping will be free" : "Promo code can be applied");
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PromoCodeResponse> getAll(Pageable pageable) {
        return PageResponse.from(promoCodeRepository.findAll(pageable).map(mapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public PromoCodeResponse getById(Long id) {
        return mapper.toResponse(find(id));
    }

    @Override
    @Transactional
    public PromoCodeResponse create(String adminEmail, CreatePromoCodeRequest request) {
        String code = request.code().trim().toUpperCase(Locale.ROOT);
        if (promoCodeRepository.existsByCodeIgnoreCase(code)) {
            throw new ConflictException("Promo code " + code + " already exists");
        }
        checkValue(request.discountType(), request.discountValue());
        checkDates(request.validFrom(), request.validTo());

        PromoCode promo = new PromoCode();
        promo.setCode(code);
        promo.setDescription(request.description());
        promo.setDiscountType(request.discountType());
        promo.setDiscountValue(request.discountValue());
        promo.setMinOrderAmount(request.minOrderAmount());
        promo.setMaxDiscountAmount(request.maxDiscountAmount());
        promo.setMaxUsageCount(request.maxUsageCount());
        promo.setCurrentUsageCount(0);
        promo.setMaxUsagePerUser(request.maxUsagePerUser());
        promo.setValidFrom(request.validFrom());
        promo.setValidTo(request.validTo());
        promo.setIsActive(true);
        promo.setCreatedBy(adminEmail);
        promo.setApplicableCategories(loadCategories(request.categoryIds()));
        promo.setApplicableProducts(loadProducts(request.productIds()));
        return mapper.toResponse(promoCodeRepository.save(promo));
    }

    @Override
    @Transactional
    public PromoCodeResponse update(Long id, UpdatePromoCodeRequest request) {
        PromoCode promo = find(id);
        DiscountType type = request.discountType() != null ? request.discountType() : promo.getDiscountType();
        BigDecimal value = request.discountValue() != null ? request.discountValue() : promo.getDiscountValue();
        checkValue(type, value);
        promo.setDiscountType(type);
        promo.setDiscountValue(value);
        if (request.description() != null) promo.setDescription(request.description());
        if (request.minOrderAmount() != null) promo.setMinOrderAmount(request.minOrderAmount());
        if (request.maxDiscountAmount() != null) promo.setMaxDiscountAmount(request.maxDiscountAmount());
        if (request.maxUsageCount() != null) {
            if (request.maxUsageCount() < promo.getCurrentUsageCount()) {
                throw new BadRequestException("maxUsageCount cannot be below the current usage count");
            }
            promo.setMaxUsageCount(request.maxUsageCount());
        }
        if (request.maxUsagePerUser() != null) promo.setMaxUsagePerUser(request.maxUsagePerUser());
        LocalDateTime from = request.validFrom() != null ? request.validFrom() : promo.getValidFrom();
        LocalDateTime to = request.validTo() != null ? request.validTo() : promo.getValidTo();
        checkDates(from, to);
        promo.setValidFrom(from);
        promo.setValidTo(to);
        if (request.isActive() != null) promo.setIsActive(request.isActive());
        if (request.categoryIds() != null) promo.setApplicableCategories(loadCategories(request.categoryIds()));
        if (request.productIds() != null) promo.setApplicableProducts(loadProducts(request.productIds()));
        return mapper.toResponse(promo);
    }

    @Override
    @Transactional
    public void deactivate(Long id) {
        find(id).setIsActive(false);
    }

    private void checkUsable(PromoCode promo, User user, BigDecimal subtotal) {
        LocalDateTime now = LocalDateTime.now();
        if (!Boolean.TRUE.equals(promo.getIsActive())) {
            throw new PromoCodeInvalidException("Promo code " + promo.getCode() + " is not active");
        }
        if (promo.getValidFrom() != null && now.isBefore(promo.getValidFrom())) {
            throw new PromoCodeInvalidException("Promo code " + promo.getCode() + " is not valid yet");
        }
        if (promo.getValidTo() != null && now.isAfter(promo.getValidTo())) {
            throw new PromoCodeInvalidException("Promo code " + promo.getCode() + " has expired");
        }
        if (promo.getMaxUsageCount() != null
                && promo.getCurrentUsageCount() >= promo.getMaxUsageCount()) {
            throw new PromoCodeInvalidException("Promo code " + promo.getCode() + " has been used up");
        }
        if (user != null && promo.getMaxUsagePerUser() != null
                && usageRepository.countByPromoCodeIdAndUserId(promo.getId(), user.getId())
                >= promo.getMaxUsagePerUser()) {
            throw new PromoCodeInvalidException(
                    "You have already used promo code " + promo.getCode() + " the maximum number of times");
        }
        if (promo.getMinOrderAmount() != null && subtotal.compareTo(promo.getMinOrderAmount()) < 0) {
            throw new PromoCodeInvalidException("Promo code " + promo.getCode()
                    + " needs an order of at least " + promo.getMinOrderAmount());
        }
    }

    private BigDecimal eligibleAmount(PromoCode promo, List<PromoLine> lines) {
        Set<Long> productIds = new HashSet<>();
        promo.getApplicableProducts().forEach(product -> productIds.add(product.getId()));
        Set<Long> categoryIds = new HashSet<>();
        promo.getApplicableCategories().forEach(category -> categoryIds.add(category.getId()));
        boolean appliesToAll = productIds.isEmpty() && categoryIds.isEmpty();

        BigDecimal eligible = BigDecimal.ZERO;
        for (PromoLine line : lines) {
            boolean matches = appliesToAll
                    || (line.productId() != null && productIds.contains(line.productId()))
                    || line.categoryIds().stream().anyMatch(categoryIds::contains);
            if (matches) {
                eligible = eligible.add(line.lineTotal());
            }
        }
        return eligible;
    }

    private List<PromoLine> cartLines(User user) {
        List<PromoLine> lines = new ArrayList<>();
        cartRepository.findByUserId(user.getId()).ifPresent(cart -> {
            for (CartItem item : cart.getCartItems()) {
                BigDecimal price = PriceCalculator.currentPrice(item.getProduct(), item.getVariant());
                lines.add(PromoLine.of(item.getProduct(),
                        price.multiply(BigDecimal.valueOf(item.getQuantity()))));
            }
        });
        return lines;
    }

    private void checkValue(DiscountType type, BigDecimal value) {
        if (type == DiscountType.PERCENTAGE
                && (value.signum() <= 0 || value.compareTo(HUNDRED) > 0)) {
            throw new BadRequestException("A percentage discount must be between 0 and 100");
        }
        if (type == DiscountType.FIXED_AMOUNT && value.signum() <= 0) {
            throw new BadRequestException("A fixed discount must be greater than 0");
        }
    }

    private void checkDates(LocalDateTime from, LocalDateTime to) {
        if (from != null && to != null && to.isBefore(from)) {
            throw new BadRequestException("validTo must be after validFrom");
        }
    }

    private PromoCode find(Long id) {
        return promoCodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No promo code with id " + id));
    }

    private Set<Category> loadCategories(Set<Long> ids) {
        Set<Category> result = new HashSet<>();
        if (ids != null) {
            for (Long id : ids) {
                result.add(categoryRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("No category with id " + id)));
            }
        }
        return result;
    }

    private Set<Product> loadProducts(Set<Long> ids) {
        Set<Product> result = new HashSet<>();
        if (ids != null) {
            for (Long id : ids) {
                result.add(productRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("No product with id " + id)));
            }
        }
        return result;
    }
}

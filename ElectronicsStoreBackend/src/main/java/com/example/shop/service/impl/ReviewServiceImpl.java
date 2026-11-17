package com.example.shop.service.impl;

import com.example.shop.dto.request.CreateReviewRequest;
import com.example.shop.dto.request.ReviewModerationRequest;
import com.example.shop.dto.request.UpdateReviewRequest;
import com.example.shop.dto.response.PageResponse;
import com.example.shop.dto.response.ReviewResponse;
import com.example.shop.dto.response.ReviewSummaryResponse;
import com.example.shop.exception.ConflictException;
import com.example.shop.exception.ForbiddenException;
import com.example.shop.exception.ResourceNotFoundException;
import com.example.shop.mapper.ReviewMapper;
import com.example.shop.model.entity.Product;
import com.example.shop.model.entity.Review;
import com.example.shop.model.entity.User;
import com.example.shop.model.enums.OrderStatus;
import com.example.shop.model.enums.ReviewStatus;
import com.example.shop.repository.OrderItemRepository;
import com.example.shop.repository.ProductRepository;
import com.example.shop.repository.ReviewRepository;
import com.example.shop.service.ReviewService;
import com.example.shop.util.JsonConverter;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserLookup userLookup;
    private final ReviewMapper mapper;
    private final JsonConverter json;

    public ReviewServiceImpl(ReviewRepository reviewRepository,
                             ProductRepository productRepository,
                             OrderItemRepository orderItemRepository,
                             UserLookup userLookup,
                             ReviewMapper mapper,
                             JsonConverter json) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.orderItemRepository = orderItemRepository;
        this.userLookup = userLookup;
        this.mapper = mapper;
        this.json = json;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ReviewResponse> getApprovedReviews(Long productId, Pageable pageable) {
        findActiveProduct(productId);
        return PageResponse.from(reviewRepository
                .findByProductIdAndStatus(productId, ReviewStatus.APPROVED, pageable)
                .map(mapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewSummaryResponse getSummary(Long productId) {
        Product product = findActiveProduct(productId);
        Map<Integer, Long> distribution = new LinkedHashMap<>();
        for (int stars = 5; stars >= 1; stars--) {
            distribution.put(stars, 0L);
        }
        for (Object[] row : reviewRepository.countByRating(productId, ReviewStatus.APPROVED)) {
            distribution.put(((Number) row[0]).intValue(), ((Number) row[1]).longValue());
        }
        long count = distribution.values().stream().mapToLong(Long::longValue).sum();
        return new ReviewSummaryResponse(productId, product.getAverageRating(), count, distribution);
    }

    @Override
    public ReviewResponse create(String email, Long productId, CreateReviewRequest request) {
        User user = userLookup.byEmail(email);
        Product product = findActiveProduct(productId);
        if (reviewRepository.existsByUserIdAndProductId(user.getId(), productId)) {
            throw new ConflictException("You have already reviewed this product");
        }
        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setRating(request.rating());
        review.setTitle(request.title());
        review.setComment(request.comment());
        review.setPros(request.pros());
        review.setCons(request.cons());
        review.setImages(request.images() == null ? null : json.toJson(request.images()));
        review.setIsVerifiedPurchase(orderItemRepository
                .existsByOrder_User_IdAndProduct_IdAndOrder_Status(
                        user.getId(), productId, OrderStatus.DELIVERED));
        review.setHelpfulCount(0);
        review.setNotHelpfulCount(0);
        review.setStatus(ReviewStatus.APPROVED);
        Review saved = reviewRepository.saveAndFlush(review);
        recalculateRating(product);
        return mapper.toResponse(saved);
    }

    @Override
    public ReviewResponse update(String email, Long productId, Long reviewId,
                                 UpdateReviewRequest request) {
        User user = userLookup.byEmail(email);
        Review review = findReview(productId, reviewId);
        if (!review.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("You can only edit your own review");
        }
        if (request.rating() != null) review.setRating(request.rating());
        if (request.title() != null) review.setTitle(request.title());
        if (request.comment() != null) review.setComment(request.comment());
        if (request.pros() != null) review.setPros(request.pros());
        if (request.cons() != null) review.setCons(request.cons());
        if (request.images() != null) review.setImages(json.toJson(request.images()));
        reviewRepository.flush();
        recalculateRating(review.getProduct());
        return mapper.toResponse(review);
    }

    @Override
    public void delete(String email, Long productId, Long reviewId) {
        User user = userLookup.byEmail(email);
        Review review = findReview(productId, reviewId);
        if (!review.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("You can only delete your own review");
        }
        Product product = review.getProduct();
        reviewRepository.delete(review);
        reviewRepository.flush();
        recalculateRating(product);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ReviewResponse> getByStatus(ReviewStatus status, Pageable pageable) {
        if (status == null) {
            return PageResponse.from(reviewRepository.findAll(pageable).map(mapper::toResponse));
        }
        return PageResponse.from(reviewRepository.findByStatus(status, pageable).map(mapper::toResponse));
    }

    @Override
    public ReviewResponse moderate(Long reviewId, ReviewModerationRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("No review with id " + reviewId));
        review.setStatus(request.status());
        if (request.adminReply() != null && !request.adminReply().isBlank()) {
            review.setAdminReply(request.adminReply());
            review.setAdminReplyAt(LocalDateTime.now());
        }
        reviewRepository.flush();
        recalculateRating(review.getProduct());
        return mapper.toResponse(review);
    }

    private void recalculateRating(Product product) {
        Double average = reviewRepository.averageRating(product.getId(), ReviewStatus.APPROVED);
        long count = reviewRepository.countByProductIdAndStatus(product.getId(), ReviewStatus.APPROVED);
        product.setAverageRating(average == null
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(average).setScale(2, RoundingMode.HALF_UP));
        product.setReviewCount((int) count);
    }

    private Product findActiveProduct(Long productId) {
        return productRepository.findByIdAndIsActiveTrue(productId)
                .orElseThrow(() -> new ResourceNotFoundException("No product with id " + productId));
    }

    private Review findReview(Long productId, Long reviewId) {
        return reviewRepository.findByIdAndProductId(reviewId, productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No review with id " + reviewId + " for product " + productId));
    }
}

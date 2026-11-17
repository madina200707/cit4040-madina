package com.example.shop.controller.customer;

import com.example.shop.dto.request.CreateReviewRequest;
import com.example.shop.dto.request.UpdateReviewRequest;
import com.example.shop.dto.response.PageResponse;
import com.example.shop.dto.response.ReviewResponse;
import com.example.shop.dto.response.ReviewSummaryResponse;
import com.example.shop.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/products/{productId}/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public PageResponse<ReviewResponse> list(
            @PathVariable Long productId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return reviewService.getApprovedReviews(productId, pageable);
    }

    @GetMapping("/summary")
    public ReviewSummaryResponse summary(@PathVariable Long productId) {
        return reviewService.getSummary(productId);
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> create(Authentication authentication,
                                                 @PathVariable Long productId,
                                                 @Valid @RequestBody CreateReviewRequest request) {
        ReviewResponse created = reviewService.create(authentication.getName(), productId, request);
        return ResponseEntity
                .created(URI.create("/api/products/" + productId + "/reviews/" + created.id()))
                .body(created);
    }

    @PutMapping("/{id}")
    public ReviewResponse update(Authentication authentication,
                                 @PathVariable Long productId,
                                 @PathVariable Long id,
                                 @Valid @RequestBody UpdateReviewRequest request) {
        return reviewService.update(authentication.getName(), productId, id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(Authentication authentication,
                                       @PathVariable Long productId,
                                       @PathVariable Long id) {
        reviewService.delete(authentication.getName(), productId, id);
        return ResponseEntity.noContent().build();
    }
}

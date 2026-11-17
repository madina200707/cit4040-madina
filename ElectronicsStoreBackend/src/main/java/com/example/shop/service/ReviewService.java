package com.example.shop.service;

import com.example.shop.dto.request.CreateReviewRequest;
import com.example.shop.dto.request.ReviewModerationRequest;
import com.example.shop.dto.request.UpdateReviewRequest;
import com.example.shop.dto.response.PageResponse;
import com.example.shop.dto.response.ReviewResponse;
import com.example.shop.dto.response.ReviewSummaryResponse;
import com.example.shop.model.enums.ReviewStatus;
import org.springframework.data.domain.Pageable;

public interface ReviewService {
    PageResponse<ReviewResponse> getApprovedReviews(Long productId, Pageable pageable);

    ReviewSummaryResponse getSummary(Long productId);

    ReviewResponse create(String email, Long productId, CreateReviewRequest request);

    ReviewResponse update(String email, Long productId, Long reviewId, UpdateReviewRequest request);

    void delete(String email, Long productId, Long reviewId);

    PageResponse<ReviewResponse> getByStatus(ReviewStatus status, Pageable pageable);

    ReviewResponse moderate(Long reviewId, ReviewModerationRequest request);
}

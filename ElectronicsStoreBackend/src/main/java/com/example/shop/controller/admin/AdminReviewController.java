package com.example.shop.controller.admin;

import com.example.shop.dto.request.ReviewModerationRequest;
import com.example.shop.dto.response.PageResponse;
import com.example.shop.dto.response.ReviewResponse;
import com.example.shop.model.enums.ReviewStatus;
import com.example.shop.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/reviews")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public class AdminReviewController {
    private final ReviewService reviewService;

    public AdminReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public PageResponse<ReviewResponse> list(
            @RequestParam(required = false) ReviewStatus status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return reviewService.getByStatus(status, pageable);
    }

    @PutMapping("/{id}")
    public ReviewResponse moderate(@PathVariable Long id, @Valid @RequestBody ReviewModerationRequest request) {
        return reviewService.moderate(id, request);
    }
}

package com.example.shop.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.example.shop.model.enums.ReviewStatus;

public record ReviewResponse(
Long id,
        Long productId,
        String authorName,
        Integer rating,
        String title,
        String comment,
        String pros,
        String cons,
        List<String> images,
        Boolean isVerifiedPurchase,
        Integer helpfulCount,
        Integer notHelpfulCount,
        String adminReply,
        LocalDateTime adminReplyAt,
        ReviewStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}

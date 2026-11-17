package com.example.shop.dto.response;

public record ProductImageResponse(
Long id,
        String imageUrl,
        String thumbnailUrl,
        String altText,
        Boolean isPrimary,
        Integer sortOrder) {
}

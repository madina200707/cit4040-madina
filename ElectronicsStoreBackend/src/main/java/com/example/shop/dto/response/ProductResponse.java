package com.example.shop.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ProductResponse(
Long id,
        String name,
        String slug,
        String shortDescription,
        String description,
        String sku,
        BigDecimal basePrice,
        BigDecimal discountPrice,
        LocalDateTime discountStartDate,
        LocalDateTime discountEndDate,
        BigDecimal currentPrice,
        int stockQuantity,
        int availableQuantity,
        boolean inStock,
        boolean lowStock,
        BigDecimal weight,
        String dimensions,
        Boolean isActive,
        Boolean isFeatured,
        Boolean isNewArrival,
        BigDecimal averageRating,
        Integer reviewCount,
        Long salesCount,
        String metaTitle,
        String metaDescription,
        BrandResponse brand,
        Long categoryId,
        String categoryName,
        List<ProductImageResponse> images,
        List<ProductSpecificationResponse> specifications,
        List<ProductVariantResponse> variants,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}

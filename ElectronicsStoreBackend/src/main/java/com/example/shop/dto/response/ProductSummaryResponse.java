package com.example.shop.dto.response;

import java.math.BigDecimal;

public record ProductSummaryResponse(
Long id,
        String name,
        String slug,
        String shortDescription,
        String sku,
        BigDecimal basePrice,
        BigDecimal currentPrice,
        boolean onSale,
        int availableQuantity,
        boolean inStock,
        BigDecimal averageRating,
        Integer reviewCount,
        Long brandId,
        String brandName,
        Long categoryId,
        String categoryName,
        String primaryImageUrl,
        Boolean isFeatured,
        Boolean isNewArrival) {
}

package com.example.shop.dto.response;

import java.math.BigDecimal;
import java.util.Map;

public record ProductVariantResponse(
Long id,
        String sku,
        String name,
        Map<String, Object> attributes,
        BigDecimal price,
        int availableQuantity,
        boolean inStock,
        String imageUrl,
        Boolean isActive) {
}

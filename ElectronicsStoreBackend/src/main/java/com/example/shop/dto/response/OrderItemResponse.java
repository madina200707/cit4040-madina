package com.example.shop.dto.response;

import java.math.BigDecimal;
import java.util.Map;

public record OrderItemResponse(
Long id,
        Long productId,
        String productName,
        Long variantId,
        String variantName,
        int quantity,
        BigDecimal priceAtPurchase,
        BigDecimal lineTotal,
        Map<String, Object> productSnapshot) {
}

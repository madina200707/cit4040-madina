package com.example.shop.dto.response;

import java.math.BigDecimal;

public record CartItemResponse(
Long id,
        Long productId,
        String productName,
        String productSlug,
        Long variantId,
        String variantName,
        int quantity,
        BigDecimal priceAtAdd,
        BigDecimal currentPrice,
        boolean priceChanged,
        BigDecimal priceDifference,
        BigDecimal lineTotal,
        int availableQuantity) {
}

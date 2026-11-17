package com.example.shop.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record WishlistItemResponse(
Long id,
        Long productId,
        String productName,
        String productSlug,
        String primaryImageUrl,
        BigDecimal priceAtAdd,
        BigDecimal currentPrice,
        boolean priceDropped,
        boolean inStock,
        Boolean notifyOnPriceDrop,
        Boolean notifyOnBackInStock,
        LocalDateTime addedAt) {
}

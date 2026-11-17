package com.example.shop.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(
Long id,
        List<CartItemResponse> items,
        int totalQuantity,
        BigDecimal subtotal,
        String appliedPromoCode,
        BigDecimal discount,
        boolean freeShipping,
        String promoMessage,
        BigDecimal total,
        boolean hasPriceChanges) {
}

package com.example.shop.dto.response;

import java.math.BigDecimal;

import com.example.shop.model.enums.DiscountType;

public record PromoValidationResponse(
String code,
        boolean valid,
        DiscountType discountType,
        BigDecimal orderAmount,
        BigDecimal discount,
        boolean freeShipping,
        BigDecimal amountAfterDiscount,
        String message) {
}

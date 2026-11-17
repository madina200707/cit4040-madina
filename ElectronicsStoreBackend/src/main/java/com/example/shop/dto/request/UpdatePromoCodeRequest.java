package com.example.shop.dto.request;

import jakarta.validation.constraints.*;
import com.example.shop.model.enums.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

public record UpdatePromoCodeRequest(

        DiscountType discountType,

        @PositiveOrZero
        BigDecimal discountValue,

        Boolean isActive,

        @Size(max = 1000)
        String description,

        @PositiveOrZero
        BigDecimal minOrderAmount,

        @Positive
        BigDecimal maxDiscountAmount,

        @Positive
        Integer maxUsageCount,

        @Positive
        Integer maxUsagePerUser,

        LocalDateTime validFrom,

        LocalDateTime validTo,

        Set<Long> categoryIds,

        Set<Long> productIds) {
}

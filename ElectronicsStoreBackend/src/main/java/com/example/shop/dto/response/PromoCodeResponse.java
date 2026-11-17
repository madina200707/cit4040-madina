package com.example.shop.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import com.example.shop.model.enums.DiscountType;

public record PromoCodeResponse(
Long id,
        String code,
        String description,
        DiscountType discountType,
        BigDecimal discountValue,
        BigDecimal minOrderAmount,
        BigDecimal maxDiscountAmount,
        Integer maxUsageCount,
        Integer currentUsageCount,
        Integer maxUsagePerUser,
        LocalDateTime validFrom,
        LocalDateTime validTo,
        Boolean isActive,
        String createdBy,
        Set<Long> categoryIds,
        Set<Long> productIds) {
}

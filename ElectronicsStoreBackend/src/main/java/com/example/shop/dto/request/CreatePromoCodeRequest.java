package com.example.shop.dto.request;

import jakarta.validation.constraints.*;
import com.example.shop.model.enums.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

public record CreatePromoCodeRequest(

        @NotBlank(message = "Code is required")
        @Pattern(regexp = "^[A-Za-z0-9_-]{3,50}$", message = "Code must be 3 to 50 letters, digits, - or _")
        String code,

        @NotNull(message = "Discount type is required")
        DiscountType discountType,

        @NotNull(message = "Discount value is required")
        @PositiveOrZero
        BigDecimal discountValue,

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

package com.example.shop.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ValidatePromoCodeRequest(

        @NotBlank(message = "Promo code is required")
        @Size(max = 50)
        String code,

        @Positive(message = "Order amount must be positive")
        BigDecimal orderAmount) {
}

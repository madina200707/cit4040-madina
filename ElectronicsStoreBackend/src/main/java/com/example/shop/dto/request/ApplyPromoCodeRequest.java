package com.example.shop.dto.request;

import jakarta.validation.constraints.*;

public record ApplyPromoCodeRequest(

        @NotBlank(message = "Promo code is required")
        @Size(max = 50)
        String code) {
}

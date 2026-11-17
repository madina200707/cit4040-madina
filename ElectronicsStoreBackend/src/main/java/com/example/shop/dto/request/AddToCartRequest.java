package com.example.shop.dto.request;

import jakarta.validation.constraints.*;

public record AddToCartRequest(

        @NotNull(message = "Product id is required")
        Long productId,

        Long variantId,

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        @Max(value = 99, message = "Quantity cannot be more than 99")
        Integer quantity) {
}

package com.example.shop.dto.request;

import jakarta.validation.constraints.*;

public record UpdateStockRequest(

        @NotNull(message = "Stock quantity is required")
        @PositiveOrZero(message = "Stock cannot be negative")
        Integer stockQuantity) {
}

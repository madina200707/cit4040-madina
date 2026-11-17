package com.example.shop.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.Map;

public record CreateVariantRequest(

        @NotBlank(message = "Variant SKU is required")
        @Size(max = 64)
        String sku,

        @NotBlank(message = "Variant name is required")
        @Size(max = 255)
        String name,

        Map<String, String> attributes,

        @Positive
        BigDecimal price,

        @NotNull(message = "Stock quantity is required")
        @PositiveOrZero
        Integer stockQuantity,

        @Size(max = 255)
        String imageUrl) {
}

package com.example.shop.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.Map;

public record UpdateVariantRequest(

        @Size(min = 1, max = 255)
        String name,

        Map<String, String> attributes,

        @Positive
        BigDecimal price,

        @PositiveOrZero
        Integer stockQuantity,

        @Size(max = 255)
        String imageUrl,

        Boolean isActive) {
}

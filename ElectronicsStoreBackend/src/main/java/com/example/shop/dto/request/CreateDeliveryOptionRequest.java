package com.example.shop.dto.request;

import jakarta.validation.constraints.*;
import com.example.shop.model.enums.DeliveryMethod;

import java.math.BigDecimal;

public record CreateDeliveryOptionRequest(

        @NotNull(message = "Delivery method is required")
        DeliveryMethod code,

        @NotBlank(message = "Name is required")
        @Size(max = 255)
        String name,

        @NotNull(message = "Base price is required")
        @PositiveOrZero
        BigDecimal basePrice,

        @PositiveOrZero
        BigDecimal freeThreshold,

        @Size(max = 1000)
        String description,

        @Positive
        Integer estimatedDaysMin,

        @Positive
        Integer estimatedDaysMax,

        Boolean isActive) {
}

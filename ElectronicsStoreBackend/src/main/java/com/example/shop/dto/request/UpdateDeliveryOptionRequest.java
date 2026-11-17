package com.example.shop.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record UpdateDeliveryOptionRequest(

        @Size(max = 255)
        String name,

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

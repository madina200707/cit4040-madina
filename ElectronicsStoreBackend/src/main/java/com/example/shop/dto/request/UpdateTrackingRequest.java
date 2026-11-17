package com.example.shop.dto.request;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record UpdateTrackingRequest(

        @NotBlank(message = "Tracking number is required")
        @Size(max = 100)
        String trackingNumber,

        @FutureOrPresent
        LocalDate estimatedDeliveryDate) {
}

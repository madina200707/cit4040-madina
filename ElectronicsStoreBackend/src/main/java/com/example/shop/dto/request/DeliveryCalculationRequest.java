package com.example.shop.dto.request;

import jakarta.validation.constraints.*;
import com.example.shop.model.enums.DeliveryMethod;

import java.math.BigDecimal;

public record DeliveryCalculationRequest(

        @NotNull(message = "Delivery method is required")
        DeliveryMethod deliveryMethod,

        @NotNull(message = "Subtotal is required")
        @PositiveOrZero
        BigDecimal subtotal) {
}

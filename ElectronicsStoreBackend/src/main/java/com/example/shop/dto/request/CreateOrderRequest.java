package com.example.shop.dto.request;

import jakarta.validation.constraints.*;
import com.example.shop.model.enums.DeliveryMethod;

public record CreateOrderRequest(

        @NotNull(message = "Address is required")
        Long addressId,

        @NotNull(message = "Delivery method is required")
        DeliveryMethod deliveryMethod,

        @Size(max = 1000)
        String customerNote) {
}

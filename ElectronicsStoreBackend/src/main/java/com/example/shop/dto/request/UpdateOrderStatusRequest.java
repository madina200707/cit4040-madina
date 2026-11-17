package com.example.shop.dto.request;

import jakarta.validation.constraints.*;
import com.example.shop.model.enums.OrderStatus;

public record UpdateOrderStatusRequest(

        @NotNull(message = "Status is required")
        OrderStatus status) {
}

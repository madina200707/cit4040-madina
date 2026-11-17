package com.example.shop.dto.response;

public record AdminOrderResponse(
OrderResponse order,
        Long customerId,
        String customerEmail,
        String customerName,
        String adminNote) {
}

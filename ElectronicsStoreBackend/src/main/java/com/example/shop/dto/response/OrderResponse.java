package com.example.shop.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.example.shop.model.enums.DeliveryMethod;
import com.example.shop.model.enums.OrderStatus;
import com.example.shop.model.enums.PaymentStatus;

public record OrderResponse(
Long id,
        String orderNumber,
        OrderStatus status,
        List<OrderItemResponse> items,
        ShippingAddressResponse shippingAddress,
        DeliveryMethod deliveryMethod,
        String trackingNumber,
        LocalDate estimatedDeliveryDate,
        LocalDate actualDeliveryDate,
        BigDecimal subtotal,
        BigDecimal discount,
        BigDecimal shippingCost,
        Integer bonusPointsUsed,
        BigDecimal totalAmount,
        String promoCode,
        String customerNote,
        PaymentStatus paymentStatus,
        LocalDateTime createdAt,
        LocalDateTime paidAt,
        LocalDateTime shippedAt,
        LocalDateTime deliveredAt) {
}

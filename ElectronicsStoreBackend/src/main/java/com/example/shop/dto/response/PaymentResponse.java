package com.example.shop.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.shop.model.enums.PaymentMethod;
import com.example.shop.model.enums.PaymentStatus;

public record PaymentResponse(
Long id,
        Long orderId,
        String orderNumber,
        String transactionId,
        PaymentMethod paymentMethod,
        PaymentStatus status,
        BigDecimal amount,
        String currency,
        String cardLastFourDigits,
        String errorCode,
        String errorMessage,
        LocalDateTime paidAt) {
}

package com.example.shop.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DeliveryCalculationResponse(
String deliveryMethod,
        String name,
        BigDecimal subtotal,
        BigDecimal shippingCost,
        boolean freeShipping,
        BigDecimal amountLeftForFreeShipping,
        LocalDate estimatedDeliveryFrom,
        LocalDate estimatedDeliveryTo) {
}

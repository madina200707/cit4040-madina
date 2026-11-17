package com.example.shop.mapper;

import com.example.shop.dto.response.*;
import com.example.shop.model.entity.*;
import com.example.shop.util.JsonConverter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class OrderMapper {
    private final JsonConverter json;

    public OrderMapper(JsonConverter json) {
        this.json = json;
    }

    public OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getStatus(),
                order.getOrderItems().stream().map(this::toResponse).toList(),
                toResponse(order.getShippingAddress()),
                order.getDeliveryMethod(),
                order.getDeliveryTrackingNumber(),
                order.getEstimatedDeliveryDate(),
                order.getActualDeliveryDate(),
                order.getSubtotal(),
                order.getDiscount(),
                order.getShippingCost(),
                order.getBonusPointsUsed(),
                order.getTotalAmount(),
                order.getPromoCode(),
                order.getCustomerNote(),
                order.getPayment() == null ? null : order.getPayment().getStatus(),
                order.getCreatedAt(),
                order.getPaidAt(),
                order.getShippedAt(),
                order.getDeliveredAt());
    }

    public AdminOrderResponse toAdminResponse(Order order) {
        User user = order.getUser();
        return new AdminOrderResponse(
                toResponse(order),
                user.getId(),
                user.getEmail(),
                user.getFirstName() + " " + user.getLastName(),
                order.getAdminNote());
    }

    public OrderItemResponse toResponse(OrderItem item) {
        return new OrderItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getVariant() == null ? null : item.getVariant().getId(),
                item.getVariant() == null ? null : item.getVariant().getName(),
                item.getQuantity(),
                item.getPriceAtPurchase(),
                item.getPriceAtPurchase().multiply(BigDecimal.valueOf(item.getQuantity())),
                json.toMap(item.getProductSnapshot()));
    }

    public ShippingAddressResponse toResponse(ShippingAddress address) {
        if (address == null) {
            return null;
        }
        return new ShippingAddressResponse(
                address.getCountry(),
                address.getCity(),
                address.getStreet(),
                address.getBuilding(),
                address.getApartment(),
                address.getPostalCode(),
                address.getRecipientName(),
                address.getRecipientPhone());
    }

    public PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getOrder().getId(),
                payment.getOrder().getOrderNumber(),
                payment.getTransactionId(),
                payment.getPaymentMethod(),
                payment.getStatus(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getCardLastFourDigits(),
                payment.getErrorCode(),
                payment.getErrorMessage(),
                payment.getPaidAt());
    }
}

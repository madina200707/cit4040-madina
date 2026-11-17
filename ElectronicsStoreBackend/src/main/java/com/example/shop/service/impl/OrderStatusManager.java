package com.example.shop.service.impl;

import com.example.shop.exception.InvalidStatusTransitionException;
import com.example.shop.model.entity.Order;
import com.example.shop.model.entity.OrderItem;
import com.example.shop.model.entity.Payment;
import com.example.shop.model.entity.Product;
import com.example.shop.model.entity.ProductVariant;
import com.example.shop.model.enums.OrderStatus;
import com.example.shop.model.enums.PaymentStatus;
import com.example.shop.service.OrderStatusTransitions;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

@Component
public class OrderStatusManager {
    public void changeStatus(Order order, OrderStatus target) {
        OrderStatus current = order.getStatus();
        if (!OrderStatusTransitions.isAllowed(current, target)) {
            throw new InvalidStatusTransitionException(
                    "Cannot change order status from " + current + " to " + target);
        }
        LocalDateTime now = LocalDateTime.now();
        switch (target) {
            case PAID -> {
                order.setPaidAt(now);
                Payment payment = order.getPayment();
                if (payment != null && payment.getStatus() != PaymentStatus.COMPLETED) {
                    payment.setStatus(PaymentStatus.COMPLETED);
                    payment.setPaidAt(now);
                }
            }
            case SHIPPED -> {
                order.setShippedAt(now);
                if (order.getDeliveryTrackingNumber() == null) {
                    order.setDeliveryTrackingNumber("TRK-" + UUID.randomUUID().toString()
                            .replace("-", "").substring(0, 12).toUpperCase(Locale.ROOT));
                }
                turnReservationIntoSale(order);
            }
            case DELIVERED -> {
                order.setDeliveredAt(now);
                order.setActualDeliveryDate(LocalDate.now());
            }
            case CANCELLED -> {
                releaseReservation(order);
                refundIfPaid(order);
            }
            case REFUNDED -> {
                if (current == OrderStatus.PAID) {
                    releaseReservation(order);
                }
                refundIfPaid(order);
            }
            default -> {
            }
        }
        order.setStatus(target);
    }

    public void reserve(Product product, ProductVariant variant, int quantity) {
        if (variant != null) {
            variant.setReservedQuantity(variant.getReservedQuantity() + quantity);
        } else {
            product.setReservedQuantity(product.getReservedQuantity() + quantity);
        }
    }

    private void releaseReservation(Order order) {
        for (OrderItem item : order.getOrderItems()) {
            ProductVariant variant = item.getVariant();
            if (variant != null) {
                variant.setReservedQuantity(Math.max(0, variant.getReservedQuantity() - item.getQuantity()));
            } else {
                Product product = item.getProduct();
                product.setReservedQuantity(Math.max(0, product.getReservedQuantity() - item.getQuantity()));
            }
        }
    }

    private void turnReservationIntoSale(Order order) {
        for (OrderItem item : order.getOrderItems()) {
            int quantity = item.getQuantity();
            ProductVariant variant = item.getVariant();
            if (variant != null) {
                variant.setReservedQuantity(Math.max(0, variant.getReservedQuantity() - quantity));
                variant.setStockQuantity(Math.max(0, variant.getStockQuantity() - quantity));
            } else {
                Product product = item.getProduct();
                product.setReservedQuantity(Math.max(0, product.getReservedQuantity() - quantity));
                product.setStockQuantity(Math.max(0, product.getStockQuantity() - quantity));
            }
            Product product = item.getProduct();
            product.setSalesCount((product.getSalesCount() == null ? 0 : product.getSalesCount()) + quantity);
        }
    }

    private void refundIfPaid(Order order) {
        Payment payment = order.getPayment();
        if (payment != null && payment.getStatus() == PaymentStatus.COMPLETED) {
            payment.setStatus(PaymentStatus.REFUNDED);
            payment.setRefundedAmount(payment.getAmount());
        }
    }
}

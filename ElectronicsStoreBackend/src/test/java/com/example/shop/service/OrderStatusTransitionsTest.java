package com.example.shop.service;

import com.example.shop.model.enums.OrderStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderStatusTransitionsTest {
    @ParameterizedTest
    @CsvSource({
            "PENDING, CONFIRMED", "PENDING, CANCELLED",
            "CONFIRMED, PAID", "CONFIRMED, CANCELLED",
            "PAID, PROCESSING", "PAID, REFUNDED",
            "PROCESSING, SHIPPED", "PROCESSING, CANCELLED",
            "SHIPPED, IN_TRANSIT",
            "IN_TRANSIT, DELIVERED",
            "DELIVERED, REFUNDED"
    })
    void allowsEveryTransitionInTheTable(OrderStatus from, OrderStatus to) {
        assertTrue(OrderStatusTransitions.isAllowed(from, to));
    }

    @ParameterizedTest
    @CsvSource({
            "DELIVERED, PENDING", "PENDING, PAID", "PENDING, SHIPPED",
            "CONFIRMED, PROCESSING", "PAID, CANCELLED", "SHIPPED, CANCELLED",
            "SHIPPED, DELIVERED", "IN_TRANSIT, CANCELLED", "PAID, PAID"
    })
    void refusesTransitionsThatAreNotInTheTable(OrderStatus from, OrderStatus to) {
        assertFalse(OrderStatusTransitions.isAllowed(from, to));
    }

    @Test
    void cancelledAndRefundedAreFinal() {
        for (OrderStatus target : OrderStatus.values()) {
            assertFalse(OrderStatusTransitions.isAllowed(OrderStatus.CANCELLED, target));
            assertFalse(OrderStatusTransitions.isAllowed(OrderStatus.REFUNDED, target));
        }
    }

    @Test
    void nullIsNeverAllowed() {
        assertFalse(OrderStatusTransitions.isAllowed(null, OrderStatus.PAID));
        assertFalse(OrderStatusTransitions.isAllowed(OrderStatus.PENDING, null));
    }
}

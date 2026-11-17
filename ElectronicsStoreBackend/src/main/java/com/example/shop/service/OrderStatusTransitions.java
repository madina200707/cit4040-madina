package com.example.shop.service;

import com.example.shop.model.enums.OrderStatus;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public final class OrderStatusTransitions {
    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED =
            new EnumMap<>(OrderStatus.class);

    static {
        ALLOWED.put(OrderStatus.PENDING, EnumSet.of(OrderStatus.CONFIRMED, OrderStatus.CANCELLED));
        ALLOWED.put(OrderStatus.CONFIRMED, EnumSet.of(OrderStatus.PAID, OrderStatus.CANCELLED));
        ALLOWED.put(OrderStatus.PAID, EnumSet.of(OrderStatus.PROCESSING, OrderStatus.REFUNDED));
        ALLOWED.put(OrderStatus.PROCESSING, EnumSet.of(OrderStatus.SHIPPED, OrderStatus.CANCELLED));
        ALLOWED.put(OrderStatus.SHIPPED, EnumSet.of(OrderStatus.IN_TRANSIT));
        ALLOWED.put(OrderStatus.IN_TRANSIT, EnumSet.of(OrderStatus.DELIVERED));
        ALLOWED.put(OrderStatus.DELIVERED, EnumSet.of(OrderStatus.REFUNDED));
        ALLOWED.put(OrderStatus.CANCELLED, EnumSet.noneOf(OrderStatus.class));
        ALLOWED.put(OrderStatus.REFUNDED, EnumSet.noneOf(OrderStatus.class));
    }

    private OrderStatusTransitions() {
    }

    public static boolean isAllowed(OrderStatus from, OrderStatus to) {
        if (from == null || to == null) {
            return false;
        }
        return ALLOWED.getOrDefault(from, Collections.emptySet()).contains(to);
    }

    public static Set<OrderStatus> nextStatuses(OrderStatus from) {
        return Collections.unmodifiableSet(
                ALLOWED.getOrDefault(from, EnumSet.noneOf(OrderStatus.class)));
    }
}

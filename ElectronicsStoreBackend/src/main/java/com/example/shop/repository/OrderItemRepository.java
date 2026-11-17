package com.example.shop.repository;

import com.example.shop.model.entity.OrderItem;
import com.example.shop.model.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    boolean existsByOrder_User_IdAndProduct_IdAndOrder_Status(
            Long userId, Long productId, OrderStatus status);
}

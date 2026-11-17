package com.example.shop.service;

import com.example.shop.dto.request.CreateOrderRequest;
import com.example.shop.dto.request.UpdateOrderNoteRequest;
import com.example.shop.dto.request.UpdateOrderStatusRequest;
import com.example.shop.dto.request.UpdateTrackingRequest;
import com.example.shop.dto.response.AdminOrderResponse;
import com.example.shop.dto.response.CartResponse;
import com.example.shop.dto.response.OrderResponse;
import com.example.shop.dto.response.PageResponse;
import com.example.shop.model.enums.OrderStatus;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface OrderService {
    OrderResponse create(String email, CreateOrderRequest request);

    PageResponse<OrderResponse> getMyOrders(String email, Pageable pageable);

    OrderResponse getMyOrder(String email, Long orderId);

    OrderResponse getMyOrderByNumber(String email, String orderNumber);

    OrderResponse cancel(String email, Long orderId);

    CartResponse reorder(String email, Long orderId);

    PageResponse<AdminOrderResponse> search(OrderStatus status, String customerEmail,
                                            LocalDate from, LocalDate to, Pageable pageable);

    AdminOrderResponse getForStaff(Long orderId);

    AdminOrderResponse updateStatus(Long orderId, UpdateOrderStatusRequest request);

    AdminOrderResponse updateTracking(Long orderId, UpdateTrackingRequest request);

    AdminOrderResponse updateNote(Long orderId, UpdateOrderNoteRequest request);
}

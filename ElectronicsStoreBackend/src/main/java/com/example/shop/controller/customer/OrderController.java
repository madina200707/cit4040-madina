package com.example.shop.controller.customer;

import com.example.shop.dto.request.CreateOrderRequest;
import com.example.shop.dto.response.CartResponse;
import com.example.shop.dto.response.OrderResponse;
import com.example.shop.dto.response.PageResponse;
import com.example.shop.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(Authentication authentication,
                                                @Valid @RequestBody CreateOrderRequest request) {
        OrderResponse created = orderService.create(authentication.getName(), request);
        return ResponseEntity.created(URI.create("/api/orders/" + created.id())).body(created);
    }

    @GetMapping
    public PageResponse<OrderResponse> list(
            Authentication authentication,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return orderService.getMyOrders(authentication.getName(), pageable);
    }

    @GetMapping("/{id:\\d+}")
    public OrderResponse one(Authentication authentication, @PathVariable Long id) {
        return orderService.getMyOrder(authentication.getName(), id);
    }

    @GetMapping("/{orderNumber:ORD-.+}")
    public OrderResponse byNumber(Authentication authentication, @PathVariable String orderNumber) {
        return orderService.getMyOrderByNumber(authentication.getName(), orderNumber);
    }

    @PutMapping("/{id}/cancel")
    public OrderResponse cancel(Authentication authentication, @PathVariable Long id) {
        return orderService.cancel(authentication.getName(), id);
    }

    @PostMapping("/{id}/reorder")
    public CartResponse reorder(Authentication authentication, @PathVariable Long id) {
        return orderService.reorder(authentication.getName(), id);
    }
}

package com.example.shop.controller.admin;

import com.example.shop.dto.request.UpdateOrderNoteRequest;
import com.example.shop.dto.request.UpdateOrderStatusRequest;
import com.example.shop.dto.request.UpdateTrackingRequest;
import com.example.shop.dto.response.AdminOrderResponse;
import com.example.shop.dto.response.PageResponse;
import com.example.shop.model.enums.OrderStatus;
import com.example.shop.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin/orders")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public class AdminOrderController {
    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public PageResponse<AdminOrderResponse> list(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String customerEmail,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return orderService.search(status, customerEmail, from, to, pageable);
    }

    @GetMapping("/{id}")
    public AdminOrderResponse one(@PathVariable Long id) {
        return orderService.getForStaff(id);
    }

    @PutMapping("/{id}/status")
    public AdminOrderResponse updateStatus(@PathVariable Long id,
                                           @Valid @RequestBody UpdateOrderStatusRequest request) {
        return orderService.updateStatus(id, request);
    }

    @PutMapping("/{id}/tracking")
    public AdminOrderResponse updateTracking(@PathVariable Long id,
                                             @Valid @RequestBody UpdateTrackingRequest request) {
        return orderService.updateTracking(id, request);
    }

    @PutMapping("/{id}/note")
    public AdminOrderResponse updateNote(@PathVariable Long id,
                                         @Valid @RequestBody UpdateOrderNoteRequest request) {
        return orderService.updateNote(id, request);
    }
}

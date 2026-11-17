package com.example.shop.controller.admin;

import com.example.shop.dto.request.CreateDeliveryOptionRequest;
import com.example.shop.dto.request.UpdateDeliveryOptionRequest;
import com.example.shop.dto.response.DeliveryOptionResponse;
import com.example.shop.service.DeliveryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/admin/delivery-options")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDeliveryOptionController {
    private final DeliveryService deliveryService;

    public AdminDeliveryOptionController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @GetMapping
    public List<DeliveryOptionResponse> list() {
        return deliveryService.getAll();
    }

    @PostMapping
    public ResponseEntity<DeliveryOptionResponse> create(@Valid @RequestBody CreateDeliveryOptionRequest request) {
        DeliveryOptionResponse created = deliveryService.create(request);
        return ResponseEntity.created(URI.create("/api/admin/delivery-options/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    public DeliveryOptionResponse update(@PathVariable Long id,
                                         @Valid @RequestBody UpdateDeliveryOptionRequest request) {
        return deliveryService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deliveryService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}

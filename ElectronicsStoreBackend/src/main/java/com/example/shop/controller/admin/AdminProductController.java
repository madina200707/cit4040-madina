package com.example.shop.controller.admin;

import com.example.shop.dto.request.*;
import com.example.shop.dto.response.PageResponse;
import com.example.shop.dto.response.ProductResponse;
import com.example.shop.dto.response.ProductSummaryResponse;
import com.example.shop.dto.response.ProductVariantResponse;
import com.example.shop.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/admin/products")
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductController {
    private final ProductService productService;

    public AdminProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public PageResponse<ProductSummaryResponse> list(@PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return productService.adminList(pageable);
    }

    @GetMapping("/{id}")
    public ProductResponse one(@PathVariable Long id) {
        return productService.adminGet(id);
    }

    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody CreateProductRequest request) {
        ProductResponse created = productService.create(request);
        return ResponseEntity.created(URI.create("/api/products/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable Long id, @Valid @RequestBody UpdateProductRequest request) {
        return productService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.softDelete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/stock")
    public ProductResponse updateStock(@PathVariable Long id, @Valid @RequestBody UpdateStockRequest request) {
        return productService.updateStock(id, request);
    }

    @PatchMapping("/{id}/activate")
    public ProductResponse activate(@PathVariable Long id) {
        return productService.setActive(id, true);
    }

    @PatchMapping("/{id}/deactivate")
    public ProductResponse deactivate(@PathVariable Long id) {
        return productService.setActive(id, false);
    }

    @PostMapping("/{id}/variants")
    public ResponseEntity<ProductVariantResponse> addVariant(@PathVariable Long id,
                                                             @Valid @RequestBody CreateVariantRequest request) {
        ProductVariantResponse created = productService.addVariant(id, request);
        return ResponseEntity.created(URI.create("/api/products/" + id + "/variants")).body(created);
    }

    @PutMapping("/{id}/variants/{variantId}")
    public ProductVariantResponse updateVariant(@PathVariable Long id, @PathVariable Long variantId,
                                                @Valid @RequestBody UpdateVariantRequest request) {
        return productService.updateVariant(id, variantId, request);
    }

    @DeleteMapping("/{id}/variants/{variantId}")
    public ResponseEntity<Void> deleteVariant(@PathVariable Long id, @PathVariable Long variantId) {
        productService.deleteVariant(id, variantId);
        return ResponseEntity.noContent().build();
    }
}

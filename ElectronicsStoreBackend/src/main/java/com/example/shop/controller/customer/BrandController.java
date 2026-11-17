package com.example.shop.controller.customer;

import com.example.shop.dto.response.BrandResponse;
import com.example.shop.dto.response.PageResponse;
import com.example.shop.dto.response.ProductSummaryResponse;
import com.example.shop.service.BrandService;
import com.example.shop.service.ProductFilter;
import com.example.shop.service.ProductService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/brands")
public class BrandController {
    private final BrandService brandService;
    private final ProductService productService;

    public BrandController(BrandService brandService, ProductService productService) {
        this.brandService = brandService;
        this.productService = productService;
    }

    @GetMapping
    public PageResponse<BrandResponse> list(
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return brandService.getActiveBrands(pageable);
    }

    @GetMapping("/{id}")
    public BrandResponse one(@PathVariable Long id) {
        return brandService.getActiveBrand(id);
    }

    @GetMapping("/{id}/products")
    public PageResponse<ProductSummaryResponse> products(
            @PathVariable Long id,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        brandService.getActiveBrand(id);
        return productService.search(ProductFilter.empty().withBrand(id), pageable);
    }
}

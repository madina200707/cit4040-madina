package com.example.shop.controller.customer;

import com.example.shop.dto.response.PageResponse;
import com.example.shop.dto.response.ProductResponse;
import com.example.shop.dto.response.ProductSummaryResponse;
import com.example.shop.dto.response.ProductVariantResponse;
import com.example.shop.service.ProductFilter;
import com.example.shop.service.ProductService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public PageResponse<ProductSummaryResponse> list(
            @RequestParam(required = false) Long category,
            @RequestParam(required = false) Long brand,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(required = false) Double rating,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        ProductFilter filter = new ProductFilter(null, brand, minPrice, maxPrice, inStock, rating,
                search, null, null, null);
        if (category != null) {
            return productService.searchInCategory(category, filter, pageable);
        }
        return productService.search(filter, pageable);
    }

    @GetMapping("/search")
    public PageResponse<ProductSummaryResponse> search(
            @RequestParam("q") String query,
            @PageableDefault(size = 20, sort = "salesCount", direction = Sort.Direction.DESC) Pageable pageable) {
        return productService.search(new ProductFilter(null, null, null, null, null, null,
                query, null, null, null), pageable);
    }

    @GetMapping("/featured")
    public PageResponse<ProductSummaryResponse> featured(
            @PageableDefault(size = 12, sort = "salesCount", direction = Sort.Direction.DESC) Pageable pageable) {
        return productService.search(ProductFilter.empty().withFeatured(), pageable);
    }

    @GetMapping("/new-arrivals")
    public PageResponse<ProductSummaryResponse> newArrivals(
            @PageableDefault(size = 12, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return productService.search(ProductFilter.empty().withNewArrival(), pageable);
    }

    @GetMapping("/best-sellers")
    public PageResponse<ProductSummaryResponse> bestSellers(
            @PageableDefault(size = 12, sort = "salesCount", direction = Sort.Direction.DESC) Pageable pageable) {
        return productService.search(ProductFilter.empty(), pageable);
    }

    @GetMapping("/{id:\\d+}")
    public ProductResponse one(@PathVariable Long id) {
        return productService.getActiveProduct(id);
    }

    @GetMapping("/{id:\\d+}/variants")
    public List<ProductVariantResponse> variants(@PathVariable Long id) {
        return productService.getVariants(id);
    }

    @GetMapping("/{id:\\d+}/related")
    public PageResponse<ProductSummaryResponse> related(
            @PathVariable Long id,
            @PageableDefault(size = 8, sort = "salesCount", direction = Sort.Direction.DESC) Pageable pageable) {
        return productService.getRelated(id, pageable);
    }
}

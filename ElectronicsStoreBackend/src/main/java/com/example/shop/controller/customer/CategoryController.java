package com.example.shop.controller.customer;

import com.example.shop.dto.response.CategoryResponse;
import com.example.shop.dto.response.PageResponse;
import com.example.shop.dto.response.ProductSummaryResponse;
import com.example.shop.service.CategoryService;
import com.example.shop.service.ProductFilter;
import com.example.shop.service.ProductService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;
    private final ProductService productService;

    public CategoryController(CategoryService categoryService, ProductService productService) {
        this.categoryService = categoryService;
        this.productService = productService;
    }

    @GetMapping
    public List<CategoryResponse> tree() {
        return categoryService.getTree();
    }

    @GetMapping("/{id:\\d+}")
    public CategoryResponse byId(@PathVariable Long id) {
        return categoryService.getById(id);
    }

    @GetMapping("/{id:\\d+}/products")
    public PageResponse<ProductSummaryResponse> products(
            @PathVariable Long id,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return productService.searchInCategory(id, ProductFilter.empty(), pageable);
    }

    @GetMapping("/{slug:.*[^0-9].*}")
    public CategoryResponse bySlug(@PathVariable String slug) {
        return categoryService.getBySlug(slug);
    }
}

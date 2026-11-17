package com.example.shop.service;

import com.example.shop.dto.request.*;
import com.example.shop.dto.response.PageResponse;
import com.example.shop.dto.response.ProductResponse;
import com.example.shop.dto.response.ProductSummaryResponse;
import com.example.shop.dto.response.ProductVariantResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    PageResponse<ProductSummaryResponse> search(ProductFilter filter, Pageable pageable);

    PageResponse<ProductSummaryResponse> searchInCategory(Long categoryId, ProductFilter filter,
                                                          Pageable pageable);

    ProductResponse getActiveProduct(Long id);

    List<ProductVariantResponse> getVariants(Long productId);

    PageResponse<ProductSummaryResponse> getRelated(Long productId, Pageable pageable);

    PageResponse<ProductSummaryResponse> adminList(Pageable pageable);

    ProductResponse adminGet(Long id);

    ProductResponse create(CreateProductRequest request);

    ProductResponse update(Long id, UpdateProductRequest request);

    void softDelete(Long id);

    ProductResponse updateStock(Long id, UpdateStockRequest request);

    ProductResponse setActive(Long id, boolean active);

    ProductVariantResponse addVariant(Long productId, CreateVariantRequest request);

    ProductVariantResponse updateVariant(Long productId, Long variantId, UpdateVariantRequest request);

    void deleteVariant(Long productId, Long variantId);
}

package com.example.shop.service;

import java.math.BigDecimal;
import java.util.Set;

public record ProductFilter(
        Set<Long> categoryIds,
        Long brandId,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        Boolean inStock,
        Double minRating,
        String search,
        Boolean featured,
        Boolean newArrival,
        Long excludeProductId) {
    public static ProductFilter empty() {
        return new ProductFilter(null, null, null, null, null, null, null, null, null, null);
    }

    public ProductFilter withCategories(Set<Long> ids) {
        return new ProductFilter(ids, brandId, minPrice, maxPrice, inStock, minRating, search,
                featured, newArrival, excludeProductId);
    }

    public ProductFilter withBrand(Long id) {
        return new ProductFilter(categoryIds, id, minPrice, maxPrice, inStock, minRating, search,
                featured, newArrival, excludeProductId);
    }

    public ProductFilter withFeatured() {
        return new ProductFilter(categoryIds, brandId, minPrice, maxPrice, inStock, minRating, search,
                true, newArrival, excludeProductId);
    }

    public ProductFilter withNewArrival() {
        return new ProductFilter(categoryIds, brandId, minPrice, maxPrice, inStock, minRating, search,
                featured, true, excludeProductId);
    }

    public ProductFilter excluding(Long productId) {
        return new ProductFilter(categoryIds, brandId, minPrice, maxPrice, inStock, minRating, search,
                featured, newArrival, productId);
    }
}

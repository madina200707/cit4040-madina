package com.example.shop.service.impl;

import com.example.shop.model.entity.Product;
import com.example.shop.service.ProductFilter;
import jakarta.persistence.criteria.Expression;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

final class ProductSpecifications {
    private ProductSpecifications() {
    }

    static Specification<Product> from(ProductFilter filter, boolean onlyActive) {
        List<Specification<Product>> specs = new ArrayList<>();

        if (onlyActive) {
            specs.add((root, query, cb) -> cb.isTrue(root.<Boolean>get("isActive")));
        }
        if (filter.categoryIds() != null && !filter.categoryIds().isEmpty()) {
            specs.add((root, query, cb) -> root.get("category").get("id").in(filter.categoryIds()));
        }
        if (filter.brandId() != null) {
            specs.add((root, query, cb) -> cb.equal(root.get("brand").get("id"), filter.brandId()));
        }
        if (filter.minPrice() != null) {
            specs.add((root, query, cb) ->
                    cb.greaterThanOrEqualTo(effectivePrice(root, cb), filter.minPrice()));
        }
        if (filter.maxPrice() != null) {
            specs.add((root, query, cb) ->
                    cb.lessThanOrEqualTo(effectivePrice(root, cb), filter.maxPrice()));
        }
        if (Boolean.TRUE.equals(filter.inStock())) {
            specs.add((root, query, cb) -> cb.greaterThan(
                    cb.diff(root.<Integer>get("stockQuantity"), root.<Integer>get("reservedQuantity")),
                    0));
        }
        if (filter.minRating() != null) {
            specs.add((root, query, cb) -> cb.greaterThanOrEqualTo(
                    root.<BigDecimal>get("averageRating"), BigDecimal.valueOf(filter.minRating())));
        }
        if (filter.search() != null && !filter.search().isBlank()) {
            String pattern = "%" + filter.search().trim().toLowerCase(Locale.ROOT) + "%";
            specs.add((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.<String>get("name")), pattern),
                    cb.like(cb.lower(root.<String>get("sku")), pattern),
                    cb.like(cb.lower(root.<String>get("shortDescription")), pattern)));
        }
        if (Boolean.TRUE.equals(filter.featured())) {
            specs.add((root, query, cb) -> cb.isTrue(root.<Boolean>get("isFeatured")));
        }
        if (Boolean.TRUE.equals(filter.newArrival())) {
            specs.add((root, query, cb) -> cb.isTrue(root.<Boolean>get("isNewArrival")));
        }
        if (filter.excludeProductId() != null) {
            specs.add((root, query, cb) -> cb.notEqual(root.get("id"), filter.excludeProductId()));
        }
        return Specification.allOf(specs);
    }

    private static Expression<BigDecimal> effectivePrice(
            jakarta.persistence.criteria.Root<Product> root,
            jakarta.persistence.criteria.CriteriaBuilder cb) {
        return cb.coalesce(root.<BigDecimal>get("discountPrice"), root.<BigDecimal>get("basePrice"));
    }
}

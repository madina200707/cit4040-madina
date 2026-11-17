package com.example.shop.service;

import com.example.shop.model.entity.Category;
import com.example.shop.model.entity.Product;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public record PromoLine(Long productId, Set<Long> categoryIds, BigDecimal lineTotal) {
    public static PromoLine of(Product product, BigDecimal lineTotal) {
        Set<Long> categoryIds = new HashSet<>();
        Category category = product.getCategory();
        while (category != null && categoryIds.add(category.getId())) {
            category = category.getParentCategory();
        }
        return new PromoLine(product.getId(), categoryIds, lineTotal);
    }

    public static PromoLine amountOnly(BigDecimal amount) {
        return new PromoLine(null, Set.of(), amount);
    }
}

package com.example.shop.util;

import com.example.shop.model.entity.Product;
import com.example.shop.model.entity.ProductVariant;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class PriceCalculator {
    private PriceCalculator() {
    }

    public static BigDecimal currentPrice(Product product) {
        BigDecimal discount = product.getDiscountPrice();
        if (discount == null) {
            return product.getBasePrice();
        }
        LocalDateTime now = LocalDateTime.now();
        boolean started = product.getDiscountStartDate() == null
                || !now.isBefore(product.getDiscountStartDate());
        boolean notEnded = product.getDiscountEndDate() == null
                || !now.isAfter(product.getDiscountEndDate());
        return started && notEnded ? discount : product.getBasePrice();
    }

    public static BigDecimal currentPrice(Product product, ProductVariant variant) {
        if (variant != null && variant.getPrice() != null) {
            return variant.getPrice();
        }
        return currentPrice(product);
    }

    public static int availableQuantity(Product product, ProductVariant variant) {
        if (variant != null) {
            return variant.getStockQuantity() - variant.getReservedQuantity();
        }
        return product.getStockQuantity() - product.getReservedQuantity();
    }
}

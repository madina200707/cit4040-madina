package com.example.shop.mapper;

import com.example.shop.dto.response.*;
import com.example.shop.model.entity.*;
import com.example.shop.util.JsonConverter;
import com.example.shop.util.PriceCalculator;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Component
public class CatalogMapper {
    private final JsonConverter json;

    public CatalogMapper(JsonConverter json) {
        this.json = json;
    }

    public BrandResponse toResponse(Brand brand) {
        if (brand == null) {
            return null;
        }
        return new BrandResponse(
                brand.getId(),
                brand.getName(),
                brand.getSlug(),
                brand.getDescription(),
                brand.getLogoUrl(),
                brand.getWebsiteUrl(),
                brand.getCountry(),
                brand.getIsActive());
    }

    public CategoryResponse toResponse(Category category, List<CategoryResponse> children) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getSlug(),
                category.getDescription(),
                category.getImageUrl(),
                category.getSortOrder(),
                category.getIsActive(),
                category.getParentCategory() == null ? null : category.getParentCategory().getId(),
                children);
    }

    public ProductSummaryResponse toSummary(Product product) {
        BigDecimal currentPrice = PriceCalculator.currentPrice(product);
        int available = PriceCalculator.availableQuantity(product, null);
        return new ProductSummaryResponse(
                product.getId(),
                product.getName(),
                product.getSlug(),
                product.getShortDescription(),
                product.getSku(),
                product.getBasePrice(),
                currentPrice,
                currentPrice.compareTo(product.getBasePrice()) < 0,
                Math.max(available, 0),
                available > 0,
                product.getAverageRating(),
                product.getReviewCount(),
                product.getBrand() == null ? null : product.getBrand().getId(),
                product.getBrand() == null ? null : product.getBrand().getName(),
                product.getCategory().getId(),
                product.getCategory().getName(),
                primaryImageUrl(product),
                product.getIsFeatured(),
                product.getIsNewArrival());
    }

    public ProductResponse toResponse(Product product) {
        int available = PriceCalculator.availableQuantity(product, null);
        int threshold = product.getLowStockThreshold() == null ? 0 : product.getLowStockThreshold();
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getSlug(),
                product.getShortDescription(),
                product.getDescription(),
                product.getSku(),
                product.getBasePrice(),
                product.getDiscountPrice(),
                product.getDiscountStartDate(),
                product.getDiscountEndDate(),
                PriceCalculator.currentPrice(product),
                product.getStockQuantity(),
                Math.max(available, 0),
                available > 0,
                available > 0 && available <= threshold,
                product.getWeight(),
                product.getDimensions(),
                product.getIsActive(),
                product.getIsFeatured(),
                product.getIsNewArrival(),
                product.getAverageRating(),
                product.getReviewCount(),
                product.getSalesCount(),
                product.getMetaTitle(),
                product.getMetaDescription(),
                toResponse(product.getBrand()),
                product.getCategory().getId(),
                product.getCategory().getName(),
                product.getImages().stream()
                        .sorted(Comparator.comparing(ProductImage::getSortOrder,
                                Comparator.nullsLast(Comparator.naturalOrder())))
                        .map(this::toResponse)
                        .toList(),
                product.getSpecifications().stream()
                        .sorted(Comparator.comparing(ProductSpecification::getSortOrder,
                                Comparator.nullsLast(Comparator.naturalOrder())))
                        .map(this::toResponse)
                        .toList(),
                product.getVariants().stream()
                        .filter(variant -> Boolean.TRUE.equals(variant.getIsActive()))
                        .map(this::toResponse)
                        .toList(),
                product.getCreatedAt(),
                product.getUpdatedAt());
    }

    public ProductImageResponse toResponse(ProductImage image) {
        return new ProductImageResponse(
                image.getId(),
                image.getImageUrl(),
                image.getThumbnailUrl(),
                image.getAltText(),
                image.getIsPrimary(),
                image.getSortOrder());
    }

    public ProductSpecificationResponse toResponse(ProductSpecification specification) {
        return new ProductSpecificationResponse(
                specification.getId(),
                specification.getName(),
                specification.getValue(),
                specification.getGroupName(),
                specification.getSortOrder());
    }

    public ProductVariantResponse toResponse(ProductVariant variant) {
        int available = PriceCalculator.availableQuantity(variant.getProduct(), variant);
        return new ProductVariantResponse(
                variant.getId(),
                variant.getSku(),
                variant.getName(),
                json.toMap(variant.getAttributes()),
                PriceCalculator.currentPrice(variant.getProduct(), variant),
                Math.max(available, 0),
                available > 0,
                variant.getImageUrl(),
                variant.getIsActive());
    }

    public String primaryImageUrl(Product product) {
        return product.getImages().stream()
                .filter(image -> Boolean.TRUE.equals(image.getIsPrimary()))
                .findFirst()
                .or(() -> product.getImages().stream().findFirst())
                .map(ProductImage::getImageUrl)
                .orElse(null);
    }
}

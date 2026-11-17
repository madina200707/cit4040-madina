package com.example.shop.service.impl;

import com.example.shop.dto.request.*;
import com.example.shop.dto.response.PageResponse;
import com.example.shop.dto.response.ProductResponse;
import com.example.shop.dto.response.ProductSummaryResponse;
import com.example.shop.dto.response.ProductVariantResponse;
import com.example.shop.exception.BadRequestException;
import com.example.shop.exception.ConflictException;
import com.example.shop.exception.ResourceNotFoundException;
import com.example.shop.mapper.CatalogMapper;
import com.example.shop.model.entity.*;
import com.example.shop.repository.*;
import com.example.shop.service.ProductFilter;
import com.example.shop.service.ProductService;
import com.example.shop.util.JsonConverter;
import com.example.shop.util.SlugUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final CatalogMapper mapper;
    private final JsonConverter json;

    public ProductServiceImpl(ProductRepository productRepository,
                              ProductVariantRepository variantRepository,
                              CategoryRepository categoryRepository,
                              BrandRepository brandRepository,
                              CatalogMapper mapper,
                              JsonConverter json) {
        this.productRepository = productRepository;
        this.variantRepository = variantRepository;
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
        this.mapper = mapper;
        this.json = json;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductSummaryResponse> search(ProductFilter filter, Pageable pageable) {
        validatePriceRange(filter);
        return PageResponse.from(productRepository
                .findAll(ProductSpecifications.from(filter, true), pageable)
                .map(mapper::toSummary));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductSummaryResponse> searchInCategory(Long categoryId, ProductFilter filter,
                                                                 Pageable pageable) {
        return search(filter.withCategories(categoryWithDescendants(categoryId)), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getActiveProduct(Long id) {
        return mapper.toResponse(findActive(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductVariantResponse> getVariants(Long productId) {
        findActive(productId);
        return variantRepository.findByProductIdAndIsActiveTrueOrderByIdAsc(productId).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductSummaryResponse> getRelated(Long productId, Pageable pageable) {
        Product product = findActive(productId);
        ProductFilter filter = ProductFilter.empty()
                .withCategories(Set.of(product.getCategory().getId()))
                .excluding(productId);
        return search(filter, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductSummaryResponse> adminList(Pageable pageable) {
        return PageResponse.from(productRepository
                .findAll(ProductSpecifications.from(ProductFilter.empty(), false), pageable)
                .map(mapper::toSummary));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse adminGet(Long id) {
        return mapper.toResponse(findAny(id));
    }

    @Override
    public ProductResponse create(CreateProductRequest request) {
        String sku = request.sku().trim();
        if (productRepository.existsBySku(sku)) {
            throw new ConflictException("A product with SKU " + sku + " already exists");
        }
        validateDiscount(request.basePrice(), request.discountPrice(),
                request.discountStartDate(), request.discountEndDate());

        Product product = new Product();
        product.setName(request.name().trim());
        product.setSlug(uniqueSlug(request.name()));
        product.setSku(sku);
        product.setBasePrice(request.basePrice());
        product.setDiscountPrice(request.discountPrice());
        product.setDiscountStartDate(request.discountStartDate());
        product.setDiscountEndDate(request.discountEndDate());
        product.setStockQuantity(request.stockQuantity());
        product.setReservedQuantity(0);
        product.setLowStockThreshold(request.lowStockThreshold() == null ? 5 : request.lowStockThreshold());
        product.setShortDescription(request.shortDescription());
        product.setDescription(request.description());
        product.setWeight(request.weight());
        product.setDimensions(request.dimensions());
        product.setIsActive(true);
        product.setIsFeatured(Boolean.TRUE.equals(request.isFeatured()));
        product.setIsNewArrival(Boolean.TRUE.equals(request.isNewArrival()));
        product.setAverageRating(BigDecimal.ZERO);
        product.setReviewCount(0);
        product.setViewCount(0L);
        product.setSalesCount(0L);
        product.setMetaTitle(request.metaTitle());
        product.setMetaDescription(request.metaDescription());
        product.setCategory(findActiveCategory(request.categoryId()));
        product.setBrand(request.brandId() == null ? null : findActiveBrand(request.brandId()));
        replaceImages(product, request.images());
        replaceSpecifications(product, request.specifications());
        return mapper.toResponse(productRepository.save(product));
    }

    @Override
    public ProductResponse update(Long id, UpdateProductRequest request) {
        Product product = findAny(id);
        if (request.name() != null) {
            product.setName(request.name().trim());
        }
        if (request.sku() != null && !request.sku().trim().equals(product.getSku())) {
            String sku = request.sku().trim();
            if (productRepository.existsBySku(sku)) {
                throw new ConflictException("A product with SKU " + sku + " already exists");
            }
            product.setSku(sku);
        }
        if (request.basePrice() != null) product.setBasePrice(request.basePrice());
        if (Boolean.TRUE.equals(request.removeDiscount())) {
            product.setDiscountPrice(null);
            product.setDiscountStartDate(null);
            product.setDiscountEndDate(null);
        } else {
            if (request.discountPrice() != null) product.setDiscountPrice(request.discountPrice());
            if (request.discountStartDate() != null) product.setDiscountStartDate(request.discountStartDate());
            if (request.discountEndDate() != null) product.setDiscountEndDate(request.discountEndDate());
        }
        validateDiscount(product.getBasePrice(), product.getDiscountPrice(),
                product.getDiscountStartDate(), product.getDiscountEndDate());
        if (request.shortDescription() != null) product.setShortDescription(request.shortDescription());
        if (request.description() != null) product.setDescription(request.description());
        if (request.lowStockThreshold() != null) product.setLowStockThreshold(request.lowStockThreshold());
        if (request.weight() != null) product.setWeight(request.weight());
        if (request.dimensions() != null) product.setDimensions(request.dimensions());
        if (request.isFeatured() != null) product.setIsFeatured(request.isFeatured());
        if (request.isNewArrival() != null) product.setIsNewArrival(request.isNewArrival());
        if (request.metaTitle() != null) product.setMetaTitle(request.metaTitle());
        if (request.metaDescription() != null) product.setMetaDescription(request.metaDescription());
        if (request.categoryId() != null) product.setCategory(findActiveCategory(request.categoryId()));
        if (request.brandId() != null) product.setBrand(findActiveBrand(request.brandId()));
        if (request.images() != null) replaceImages(product, request.images());
        if (request.specifications() != null) replaceSpecifications(product, request.specifications());
        productRepository.flush();
        return mapper.toResponse(product);
    }

    @Override
    public void softDelete(Long id) {
        findAny(id).setIsActive(false);
    }

    @Override
    public ProductResponse updateStock(Long id, UpdateStockRequest request) {
        Product product = findAny(id);
        if (request.stockQuantity() < product.getReservedQuantity()) {
            throw new ConflictException("Stock cannot be lower than the "
                    + product.getReservedQuantity() + " units reserved by open orders");
        }
        product.setStockQuantity(request.stockQuantity());
        return mapper.toResponse(product);
    }

    @Override
    public ProductResponse setActive(Long id, boolean active) {
        Product product = findAny(id);
        product.setIsActive(active);
        return mapper.toResponse(product);
    }

    @Override
    public ProductVariantResponse addVariant(Long productId, CreateVariantRequest request) {
        Product product = findAny(productId);
        String sku = request.sku().trim();
        if (variantRepository.existsBySku(sku) || productRepository.existsBySku(sku)) {
            throw new ConflictException("SKU " + sku + " is already used");
        }
        ProductVariant variant = new ProductVariant();
        variant.setProduct(product);
        variant.setSku(sku);
        variant.setName(request.name().trim());
        variant.setAttributes(json.toJson(request.attributes()));
        variant.setPrice(request.price());
        variant.setStockQuantity(request.stockQuantity());
        variant.setReservedQuantity(0);
        variant.setImageUrl(request.imageUrl());
        variant.setIsActive(true);
        product.getVariants().add(variant);
        return mapper.toResponse(variantRepository.save(variant));
    }

    @Override
    public ProductVariantResponse updateVariant(Long productId, Long variantId,
                                                UpdateVariantRequest request) {
        ProductVariant variant = findVariant(productId, variantId);
        if (request.name() != null) variant.setName(request.name().trim());
        if (request.attributes() != null) variant.setAttributes(json.toJson(request.attributes()));
        if (request.price() != null) variant.setPrice(request.price());
        if (request.imageUrl() != null) variant.setImageUrl(request.imageUrl());
        if (request.isActive() != null) variant.setIsActive(request.isActive());
        if (request.stockQuantity() != null) {
            if (request.stockQuantity() < variant.getReservedQuantity()) {
                throw new ConflictException("Stock cannot be lower than the "
                        + variant.getReservedQuantity() + " units reserved by open orders");
            }
            variant.setStockQuantity(request.stockQuantity());
        }
        return mapper.toResponse(variant);
    }

    @Override
    public void deleteVariant(Long productId, Long variantId) {
        findVariant(productId, variantId).setIsActive(false);
    }

    private Product findActive(Long id) {
        return productRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("No product with id " + id));
    }

    private Product findAny(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No product with id " + id));
    }

    private ProductVariant findVariant(Long productId, Long variantId) {
        return variantRepository.findByIdAndProductId(variantId, productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No variant with id " + variantId + " for product " + productId));
    }

    private Category findActiveCategory(Long id) {
        return categoryRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("No category with id " + id));
    }

    private Brand findActiveBrand(Long id) {
        return brandRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("No brand with id " + id));
    }

    private Set<Long> categoryWithDescendants(Long categoryId) {
        findActiveCategory(categoryId);
        Map<Long, List<Long>> childrenByParent = new HashMap<>();
        for (Category category : categoryRepository.findAll()) {
            if (category.getParentCategory() != null && Boolean.TRUE.equals(category.getIsActive())) {
                childrenByParent.computeIfAbsent(category.getParentCategory().getId(),
                        key -> new ArrayList<>()).add(category.getId());
            }
        }
        Set<Long> result = new HashSet<>();
        Deque<Long> toVisit = new ArrayDeque<>();
        toVisit.add(categoryId);
        while (!toVisit.isEmpty()) {
            Long current = toVisit.poll();
            if (result.add(current)) {
                toVisit.addAll(childrenByParent.getOrDefault(current, List.of()));
            }
        }
        return result;
    }

    private String uniqueSlug(String name) {
        String base = SlugUtil.toSlug(name);
        String slug = base;
        int counter = 2;
        while (productRepository.existsBySlug(slug)) {
            slug = base + "-" + counter++;
        }
        return slug;
    }

    private void validateDiscount(BigDecimal basePrice, BigDecimal discountPrice,
                                  LocalDateTime start, LocalDateTime end) {
        if (discountPrice != null && discountPrice.compareTo(basePrice) >= 0) {
            throw new BadRequestException("Discount price must be lower than the base price");
        }
        if (start != null && end != null && end.isBefore(start)) {
            throw new BadRequestException("Discount end date must be after the start date");
        }
    }

    private void validatePriceRange(ProductFilter filter) {
        if (filter.minPrice() != null && filter.maxPrice() != null
                && filter.minPrice().compareTo(filter.maxPrice()) > 0) {
            throw new BadRequestException("minPrice cannot be greater than maxPrice");
        }
    }

    private void replaceImages(Product product, List<ProductImageRequest> images) {
        product.getImages().clear();
        if (images == null) {
            return;
        }
        boolean hasPrimary = images.stream().anyMatch(image -> Boolean.TRUE.equals(image.isPrimary()));
        int position = 0;
        for (ProductImageRequest request : images) {
            ProductImage image = new ProductImage();
            image.setProduct(product);
            image.setImageUrl(request.imageUrl());
            image.setThumbnailUrl(request.thumbnailUrl());
            image.setAltText(request.altText() != null ? request.altText() : product.getName());
            image.setIsPrimary(hasPrimary ? Boolean.TRUE.equals(request.isPrimary()) : position == 0);
            image.setSortOrder(request.sortOrder() != null ? request.sortOrder() : position);
            product.getImages().add(image);
            position++;
        }
    }

    private void replaceSpecifications(Product product, List<ProductSpecificationRequest> specs) {
        product.getSpecifications().clear();
        if (specs == null) {
            return;
        }
        int position = 0;
        for (ProductSpecificationRequest request : specs) {
            ProductSpecification specification = new ProductSpecification();
            specification.setProduct(product);
            specification.setName(request.name());
            specification.setValue(request.value());
            specification.setGroupName(request.groupName());
            specification.setSortOrder(request.sortOrder() != null ? request.sortOrder() : position);
            product.getSpecifications().add(specification);
            position++;
        }
    }
}

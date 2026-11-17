package com.example.shop.service.impl;

import com.example.shop.dto.request.CreateBrandRequest;
import com.example.shop.dto.request.UpdateBrandRequest;
import com.example.shop.dto.response.BrandResponse;
import com.example.shop.dto.response.PageResponse;
import com.example.shop.exception.ConflictException;
import com.example.shop.exception.ResourceNotFoundException;
import com.example.shop.mapper.CatalogMapper;
import com.example.shop.model.entity.Brand;
import com.example.shop.repository.BrandRepository;
import com.example.shop.service.BrandService;
import com.example.shop.util.SlugUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BrandServiceImpl implements BrandService {
    private final BrandRepository brandRepository;
    private final CatalogMapper mapper;

    public BrandServiceImpl(BrandRepository brandRepository, CatalogMapper mapper) {
        this.brandRepository = brandRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BrandResponse> getActiveBrands(Pageable pageable) {
        return PageResponse.from(brandRepository.findByIsActiveTrue(pageable).map(mapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public BrandResponse getActiveBrand(Long id) {
        return mapper.toResponse(brandRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("No brand with id " + id)));
    }

    @Override
    public BrandResponse create(CreateBrandRequest request) {
        String name = request.name().trim();
        if (brandRepository.existsByNameIgnoreCase(name)) {
            throw new ConflictException("Brand " + name + " already exists");
        }
        Brand brand = new Brand();
        brand.setName(name);
        brand.setSlug(uniqueSlug(name));
        brand.setDescription(request.description());
        brand.setLogoUrl(request.logoUrl());
        brand.setWebsiteUrl(request.websiteUrl());
        brand.setCountry(request.country());
        brand.setIsActive(true);
        return mapper.toResponse(brandRepository.save(brand));
    }

    @Override
    public BrandResponse update(Long id, UpdateBrandRequest request) {
        Brand brand = findAny(id);
        if (request.name() != null && !request.name().trim().equalsIgnoreCase(brand.getName())) {
            String name = request.name().trim();
            if (brandRepository.existsByNameIgnoreCase(name)) {
                throw new ConflictException("Brand " + name + " already exists");
            }
            brand.setName(name);
        }
        if (request.description() != null) brand.setDescription(request.description());
        if (request.logoUrl() != null) brand.setLogoUrl(request.logoUrl());
        if (request.websiteUrl() != null) brand.setWebsiteUrl(request.websiteUrl());
        if (request.country() != null) brand.setCountry(request.country());
        if (request.isActive() != null) brand.setIsActive(request.isActive());
        return mapper.toResponse(brand);
    }

    @Override
    public void softDelete(Long id) {
        findAny(id).setIsActive(false);
    }

    private Brand findAny(Long id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No brand with id " + id));
    }

    private String uniqueSlug(String name) {
        String base = SlugUtil.toSlug(name);
        String slug = base;
        int counter = 2;
        while (brandRepository.existsBySlug(slug)) {
            slug = base + "-" + counter++;
        }
        return slug;
    }
}

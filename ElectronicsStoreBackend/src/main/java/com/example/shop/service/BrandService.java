package com.example.shop.service;

import com.example.shop.dto.request.CreateBrandRequest;
import com.example.shop.dto.request.UpdateBrandRequest;
import com.example.shop.dto.response.BrandResponse;
import com.example.shop.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface BrandService {
    PageResponse<BrandResponse> getActiveBrands(Pageable pageable);

    BrandResponse getActiveBrand(Long id);

    BrandResponse create(CreateBrandRequest request);

    BrandResponse update(Long id, UpdateBrandRequest request);

    void softDelete(Long id);
}

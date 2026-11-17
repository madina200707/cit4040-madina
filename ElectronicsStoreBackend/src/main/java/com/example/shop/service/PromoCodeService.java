package com.example.shop.service;

import com.example.shop.dto.request.CreatePromoCodeRequest;
import com.example.shop.dto.request.UpdatePromoCodeRequest;
import com.example.shop.dto.request.ValidatePromoCodeRequest;
import com.example.shop.dto.response.PageResponse;
import com.example.shop.dto.response.PromoCodeResponse;
import com.example.shop.dto.response.PromoValidationResponse;
import com.example.shop.model.entity.User;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PromoCodeService {
    PromoCalculation calculate(String code, User user, List<PromoLine> lines);

    PromoValidationResponse validate(String email, ValidatePromoCodeRequest request);

    PageResponse<PromoCodeResponse> getAll(Pageable pageable);

    PromoCodeResponse getById(Long id);

    PromoCodeResponse create(String adminEmail, CreatePromoCodeRequest request);

    PromoCodeResponse update(Long id, UpdatePromoCodeRequest request);

    void deactivate(Long id);
}

package com.example.shop.controller.customer;

import com.example.shop.dto.request.ValidatePromoCodeRequest;
import com.example.shop.dto.response.PromoValidationResponse;
import com.example.shop.service.PromoCodeService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/promo")
public class PromoCodeController {
    private final PromoCodeService promoCodeService;

    public PromoCodeController(PromoCodeService promoCodeService) {
        this.promoCodeService = promoCodeService;
    }

    @PostMapping("/validate")
    public PromoValidationResponse validate(Authentication authentication,
                                            @Valid @RequestBody ValidatePromoCodeRequest request) {
        return promoCodeService.validate(authentication.getName(), request);
    }
}

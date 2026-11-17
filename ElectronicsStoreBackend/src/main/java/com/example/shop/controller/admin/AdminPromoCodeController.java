package com.example.shop.controller.admin;

import com.example.shop.dto.request.CreatePromoCodeRequest;
import com.example.shop.dto.request.UpdatePromoCodeRequest;
import com.example.shop.dto.response.PageResponse;
import com.example.shop.dto.response.PromoCodeResponse;
import com.example.shop.service.PromoCodeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/admin/promo-codes")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPromoCodeController {
    private final PromoCodeService promoCodeService;

    public AdminPromoCodeController(PromoCodeService promoCodeService) {
        this.promoCodeService = promoCodeService;
    }

    @GetMapping
    public PageResponse<PromoCodeResponse> list(@PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return promoCodeService.getAll(pageable);
    }

    @GetMapping("/{id}")
    public PromoCodeResponse one(@PathVariable Long id) {
        return promoCodeService.getById(id);
    }

    @PostMapping
    public ResponseEntity<PromoCodeResponse> create(Authentication authentication,
                                                    @Valid @RequestBody CreatePromoCodeRequest request) {
        PromoCodeResponse created = promoCodeService.create(authentication.getName(), request);
        return ResponseEntity.created(URI.create("/api/admin/promo-codes/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    public PromoCodeResponse update(@PathVariable Long id, @Valid @RequestBody UpdatePromoCodeRequest request) {
        return promoCodeService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        promoCodeService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}

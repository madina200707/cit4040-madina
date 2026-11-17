package com.example.shop.repository;

import com.example.shop.model.entity.PromoCodeUsage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromoCodeUsageRepository extends JpaRepository<PromoCodeUsage, Long> {
    long countByPromoCodeIdAndUserId(Long promoCodeId, Long userId);
}

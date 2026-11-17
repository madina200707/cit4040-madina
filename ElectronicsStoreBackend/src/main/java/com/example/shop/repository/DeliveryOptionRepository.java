package com.example.shop.repository;

import com.example.shop.model.entity.DeliveryOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeliveryOptionRepository extends JpaRepository<DeliveryOption, Long> {
    List<DeliveryOption> findByIsActiveTrueOrderByBasePriceAsc();

    Optional<DeliveryOption> findByCode(String code);

    boolean existsByCode(String code);
}

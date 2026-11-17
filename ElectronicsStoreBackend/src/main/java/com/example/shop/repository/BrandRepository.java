package com.example.shop.repository;

import com.example.shop.model.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Long> {
    Page<Brand> findByIsActiveTrue(Pageable pageable);

    Optional<Brand> findByIdAndIsActiveTrue(Long id);

    boolean existsBySlug(String slug);

    boolean existsByNameIgnoreCase(String name);
}

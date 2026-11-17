package com.example.shop.repository;

import com.example.shop.model.entity.Review;
import com.example.shop.model.enums.ReviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByProductIdAndStatus(Long productId, ReviewStatus status, Pageable pageable);

    Page<Review> findByStatus(ReviewStatus status, Pageable pageable);

    Optional<Review> findByIdAndProductId(Long id, Long productId);

    boolean existsByUserIdAndProductId(Long userId, Long productId);

    @Query("select avg(r.rating) from Review r "
            + "where r.product.id = :productId and r.status = :status")
    Double averageRating(@Param("productId") Long productId,
                         @Param("status") ReviewStatus status);

    long countByProductIdAndStatus(Long productId, ReviewStatus status);

    @Query("select r.rating, count(r) from Review r "
            + "where r.product.id = :productId and r.status = :status group by r.rating")
    List<Object[]> countByRating(@Param("productId") Long productId,
                                 @Param("status") ReviewStatus status);
}

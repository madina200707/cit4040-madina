package com.example.shop.dto.response;

import java.math.BigDecimal;
import java.util.Map;

public record ReviewSummaryResponse(
Long productId,
        BigDecimal averageRating,
        long reviewCount,
        Map<Integer, Long> ratingDistribution) {
}

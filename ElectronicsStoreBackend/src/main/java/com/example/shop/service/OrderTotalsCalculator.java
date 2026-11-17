package com.example.shop.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class OrderTotalsCalculator {
    public static final BigDecimal POINT_VALUE = new BigDecimal("0.01");
    private static final BigDecimal MAX_BONUS_SHARE = new BigDecimal("0.50");

    private OrderTotalsCalculator() {
    }

    public record Totals(BigDecimal subtotal, BigDecimal discount, BigDecimal shippingCost,
                         int bonusPointsUsed, BigDecimal totalAmount) {
    }

    public static Totals calculate(BigDecimal subtotal, BigDecimal discount,
                                   boolean freeShippingByPromo,
                                   BigDecimal deliveryBasePrice, BigDecimal freeThreshold,
                                   int requestedPoints, int availablePoints) {
        BigDecimal safeDiscount = discount == null ? BigDecimal.ZERO : discount.min(subtotal);

        BigDecimal shipping;
        if (freeShippingByPromo
                || (freeThreshold != null && subtotal.compareTo(freeThreshold) >= 0)) {
            shipping = BigDecimal.ZERO;
        } else {
            shipping = deliveryBasePrice;
        }

        int bonusUsed = cappedBonusPoints(subtotal.subtract(safeDiscount),
                requestedPoints, availablePoints);

        BigDecimal total = subtotal
                .subtract(safeDiscount)
                .add(shipping)
                .subtract(POINT_VALUE.multiply(BigDecimal.valueOf(bonusUsed)))
                .setScale(2, RoundingMode.HALF_UP);
        if (total.signum() < 0) {
            total = BigDecimal.ZERO.setScale(2);
        }
        return new Totals(
                subtotal.setScale(2, RoundingMode.HALF_UP),
                safeDiscount.setScale(2, RoundingMode.HALF_UP),
                shipping.setScale(2, RoundingMode.HALF_UP),
                bonusUsed,
                total);
    }

    static int cappedBonusPoints(BigDecimal amountAfterDiscount, int requested, int available) {
        if (requested <= 0 || available <= 0 || amountAfterDiscount.signum() <= 0) {
            return 0;
        }
        int maxByOrder = amountAfterDiscount.multiply(MAX_BONUS_SHARE)
                .divide(POINT_VALUE, 0, RoundingMode.DOWN)
                .intValue();
        return Math.min(Math.min(requested, available), maxByOrder);
    }
}

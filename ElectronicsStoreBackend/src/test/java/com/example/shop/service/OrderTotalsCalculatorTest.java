package com.example.shop.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderTotalsCalculatorTest {
    private static BigDecimal money(String value) {
        return new BigDecimal(value);
    }

    @Test
    void shippingIsChargedBelowTheFreeThreshold() {
        OrderTotalsCalculator.Totals totals = OrderTotalsCalculator.calculate(
                money("40000"), BigDecimal.ZERO, false, money("1500"), money("50000"), 0, 0);
        assertEquals(money("1500.00"), totals.shippingCost());
        assertEquals(money("41500.00"), totals.totalAmount());
    }

    @Test
    void shippingIsFreeAtTheThresholdItself() {
        OrderTotalsCalculator.Totals totals = OrderTotalsCalculator.calculate(
                money("50000"), BigDecimal.ZERO, false, money("1500"), money("50000"), 0, 0);
        assertEquals(money("0.00"), totals.shippingCost());
        assertEquals(money("50000.00"), totals.totalAmount());
    }

    @Test
    void thresholdIsCheckedOnTheSubtotalBeforeTheDiscount() {
        OrderTotalsCalculator.Totals totals = OrderTotalsCalculator.calculate(
                money("52000"), money("5000"), false, money("1500"), money("50000"), 0, 0);
        assertEquals(money("0.00"), totals.shippingCost());
        assertEquals(money("47000.00"), totals.totalAmount());
    }

    @Test
    void freeShippingPromoRemovesShipping() {
        OrderTotalsCalculator.Totals totals = OrderTotalsCalculator.calculate(
                money("20000"), BigDecimal.ZERO, true, money("4000"), null, 0, 0);
        assertEquals(money("0.00"), totals.shippingCost());
        assertEquals(money("20000.00"), totals.totalAmount());
    }

    @Test
    void bonusPointsAreCappedAtHalfOfSubtotalMinusDiscount() {
        OrderTotalsCalculator.Totals totals = OrderTotalsCalculator.calculate(
                money("10000"), money("2000"), false, money("1000"), money("50000"), 1_000_000, 1_000_000);
        assertEquals(400_000, totals.bonusPointsUsed());
        assertEquals(money("5000.00"), totals.totalAmount());
    }

    @Test
    void bonusPointsNeverExceedWhatTheCustomerHas() {
        OrderTotalsCalculator.Totals totals = OrderTotalsCalculator.calculate(
                money("10000"), BigDecimal.ZERO, false, BigDecimal.ZERO, null, 50_000, 300);
        assertEquals(300, totals.bonusPointsUsed());
        assertEquals(money("9997.00"), totals.totalAmount());
    }

    @Test
    void discountCannotMakeTheTotalNegative() {
        OrderTotalsCalculator.Totals totals = OrderTotalsCalculator.calculate(
                money("3000"), money("5000"), false, BigDecimal.ZERO, null, 0, 0);
        assertEquals(money("3000.00"), totals.discount());
        assertEquals(money("0.00"), totals.totalAmount());
    }
}

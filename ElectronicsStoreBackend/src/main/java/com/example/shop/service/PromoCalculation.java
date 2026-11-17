package com.example.shop.service;

import com.example.shop.model.entity.PromoCode;

import java.math.BigDecimal;

public record PromoCalculation(PromoCode promoCode, BigDecimal discount, boolean freeShipping) {
}

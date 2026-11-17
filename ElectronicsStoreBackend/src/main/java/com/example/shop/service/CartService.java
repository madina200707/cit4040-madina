package com.example.shop.service;

import com.example.shop.dto.request.AddToCartRequest;
import com.example.shop.dto.request.ApplyPromoCodeRequest;
import com.example.shop.dto.request.UpdateCartItemRequest;
import com.example.shop.dto.response.CartResponse;

public interface CartService {
    CartResponse getCart(String email, String sessionId);

    CartResponse addItem(String email, String sessionId, AddToCartRequest request);

    CartResponse updateItem(String email, String sessionId, Long itemId, UpdateCartItemRequest request);

    CartResponse removeItem(String email, String sessionId, Long itemId);

    void clear(String email, String sessionId);

    CartResponse applyPromoCode(String email, String sessionId, ApplyPromoCodeRequest request);

    CartResponse removePromoCode(String email, String sessionId);

    CartResponse merge(String email, String guestSessionId);
}

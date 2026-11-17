package com.example.shop.service;

import com.example.shop.dto.response.CartResponse;
import com.example.shop.dto.response.PageResponse;
import com.example.shop.dto.response.WishlistItemResponse;
import org.springframework.data.domain.Pageable;

public interface WishlistService {
    PageResponse<WishlistItemResponse> getWishlist(String email, Pageable pageable);

    WishlistItemResponse add(String email, Long productId);

    void remove(String email, Long productId);

    CartResponse moveToCart(String email, Long productId);
}

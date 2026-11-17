package com.example.shop.service;

import com.example.shop.dto.request.CreateAddressRequest;
import com.example.shop.dto.request.UpdateAddressRequest;
import com.example.shop.dto.response.AddressResponse;
import com.example.shop.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface AddressService {
    PageResponse<AddressResponse> getMyAddresses(String email, Pageable pageable);

    AddressResponse getMyAddress(String email, Long addressId);

    AddressResponse create(String email, CreateAddressRequest request);

    AddressResponse update(String email, Long addressId, UpdateAddressRequest request);

    void delete(String email, Long addressId);

    AddressResponse setDefault(String email, Long addressId);
}

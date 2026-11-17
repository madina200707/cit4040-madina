package com.example.shop.service.impl;

import com.example.shop.dto.request.CreateAddressRequest;
import com.example.shop.dto.request.UpdateAddressRequest;
import com.example.shop.dto.response.AddressResponse;
import com.example.shop.dto.response.PageResponse;
import com.example.shop.exception.ResourceNotFoundException;
import com.example.shop.mapper.UserMapper;
import com.example.shop.model.entity.Address;
import com.example.shop.model.entity.User;
import com.example.shop.repository.AddressRepository;
import com.example.shop.service.AddressService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;
    private final UserLookup userLookup;
    private final UserMapper userMapper;

    public AddressServiceImpl(AddressRepository addressRepository, UserLookup userLookup,
                              UserMapper userMapper) {
        this.addressRepository = addressRepository;
        this.userLookup = userLookup;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AddressResponse> getMyAddresses(String email, Pageable pageable) {
        User user = userLookup.byEmail(email);
        return PageResponse.from(addressRepository.findByUserId(user.getId(), pageable)
                .map(userMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public AddressResponse getMyAddress(String email, Long addressId) {
        User user = userLookup.byEmail(email);
        return userMapper.toResponse(findOwn(user, addressId));
    }

    @Override
    public AddressResponse create(String email, CreateAddressRequest request) {
        User user = userLookup.byEmail(email);
        Address address = new Address();
        address.setUser(user);
        address.setTitle(request.title());
        address.setCountry(request.country());
        address.setCity(request.city());
        address.setStreet(request.street());
        address.setBuilding(request.building());
        address.setApartment(request.apartment());
        address.setPostalCode(request.postalCode());
        address.setRecipientName(request.recipientName() != null
                ? request.recipientName()
                : user.getFirstName() + " " + user.getLastName());
        address.setRecipientPhone(request.recipientPhone() != null
                ? request.recipientPhone() : user.getPhone());
        address.setLatitude(request.latitude());
        address.setLongitude(request.longitude());

        boolean makeDefault = Boolean.TRUE.equals(request.isDefault())
                || addressRepository.countByUserId(user.getId()) == 0;
        address.setIsDefault(false);
        Address saved = addressRepository.save(address);
        if (makeDefault) {
            makeOnlyDefault(user, saved);
        }
        return userMapper.toResponse(saved);
    }

    @Override
    public AddressResponse update(String email, Long addressId, UpdateAddressRequest request) {
        User user = userLookup.byEmail(email);
        Address address = findOwn(user, addressId);
        if (request.title() != null) address.setTitle(request.title());
        if (request.country() != null) address.setCountry(request.country());
        if (request.city() != null) address.setCity(request.city());
        if (request.street() != null) address.setStreet(request.street());
        if (request.building() != null) address.setBuilding(request.building());
        if (request.apartment() != null) address.setApartment(request.apartment());
        if (request.postalCode() != null) address.setPostalCode(request.postalCode());
        if (request.recipientName() != null) address.setRecipientName(request.recipientName());
        if (request.recipientPhone() != null) address.setRecipientPhone(request.recipientPhone());
        if (request.latitude() != null) address.setLatitude(request.latitude());
        if (request.longitude() != null) address.setLongitude(request.longitude());
        if (Boolean.TRUE.equals(request.isDefault())) {
            makeOnlyDefault(user, address);
        }
        return userMapper.toResponse(address);
    }

    @Override
    public void delete(String email, Long addressId) {
        User user = userLookup.byEmail(email);
        Address address = findOwn(user, addressId);
        boolean wasDefault = Boolean.TRUE.equals(address.getIsDefault());
        addressRepository.delete(address);
        addressRepository.flush();
        if (wasDefault) {
            List<Address> rest = addressRepository.findByUserIdOrderByIdAsc(user.getId());
            if (!rest.isEmpty()) {
                rest.get(0).setIsDefault(true);
            }
        }
    }

    @Override
    public AddressResponse setDefault(String email, Long addressId) {
        User user = userLookup.byEmail(email);
        Address address = findOwn(user, addressId);
        makeOnlyDefault(user, address);
        return userMapper.toResponse(address);
    }

    private Address findOwn(User user, Long addressId) {
        return addressRepository.findByIdAndUserId(addressId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No address with id " + addressId));
    }

    private void makeOnlyDefault(User user, Address chosen) {
        for (Address address : addressRepository.findByUserIdOrderByIdAsc(user.getId())) {
            address.setIsDefault(address.getId().equals(chosen.getId()));
        }
        chosen.setIsDefault(true);
    }
}

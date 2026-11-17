package com.example.shop.service.impl;

import com.example.shop.dto.request.CreateDeliveryOptionRequest;
import com.example.shop.dto.request.DeliveryCalculationRequest;
import com.example.shop.dto.request.UpdateDeliveryOptionRequest;
import com.example.shop.dto.response.DeliveryCalculationResponse;
import com.example.shop.dto.response.DeliveryOptionResponse;
import com.example.shop.exception.BadRequestException;
import com.example.shop.exception.ConflictException;
import com.example.shop.exception.ResourceNotFoundException;
import com.example.shop.mapper.PromoCodeMapper;
import com.example.shop.model.entity.DeliveryOption;
import com.example.shop.model.enums.DeliveryMethod;
import com.example.shop.repository.DeliveryOptionRepository;
import com.example.shop.service.DeliveryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class DeliveryServiceImpl implements DeliveryService {
    private final DeliveryOptionRepository repository;
    private final PromoCodeMapper mapper;

    public DeliveryServiceImpl(DeliveryOptionRepository repository, PromoCodeMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryOptionResponse> getActiveMethods() {
        return repository.findByIsActiveTrueOrderByBasePriceAsc().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DeliveryCalculationResponse calculate(DeliveryCalculationRequest request) {
        DeliveryOption option = getActiveOption(request.deliveryMethod());
        BigDecimal subtotal = request.subtotal();
        boolean free = option.getFreeThreshold() != null
                && subtotal.compareTo(option.getFreeThreshold()) >= 0;
        BigDecimal leftForFree = option.getFreeThreshold() == null || free
                ? BigDecimal.ZERO
                : option.getFreeThreshold().subtract(subtotal);
        LocalDate today = LocalDate.now();
        return new DeliveryCalculationResponse(
                option.getCode(),
                option.getName(),
                subtotal,
                free ? BigDecimal.ZERO : option.getBasePrice(),
                free || option.getBasePrice().signum() == 0,
                leftForFree,
                today.plusDays(daysOrZero(option.getEstimatedDaysMin())),
                today.plusDays(daysOrZero(option.getEstimatedDaysMax())));
    }

    @Override
    @Transactional(readOnly = true)
    public DeliveryOption getActiveOption(DeliveryMethod method) {
        DeliveryOption option = repository.findByCode(method.name())
                .orElseThrow(() -> new BadRequestException("Delivery method " + method + " is not offered"));
        if (!Boolean.TRUE.equals(option.getIsActive())) {
            throw new BadRequestException("Delivery method " + method + " is not available right now");
        }
        return option;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryOptionResponse> getAll() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override
    public DeliveryOptionResponse create(CreateDeliveryOptionRequest request) {
        if (repository.existsByCode(request.code().name())) {
            throw new ConflictException("A delivery option for " + request.code() + " already exists");
        }
        checkDays(request.estimatedDaysMin(), request.estimatedDaysMax());
        DeliveryOption option = new DeliveryOption();
        option.setCode(request.code().name());
        option.setName(request.name().trim());
        option.setDescription(request.description());
        option.setBasePrice(request.basePrice());
        option.setFreeThreshold(request.freeThreshold());
        option.setEstimatedDaysMin(request.estimatedDaysMin());
        option.setEstimatedDaysMax(request.estimatedDaysMax());
        option.setIsActive(request.isActive() == null || request.isActive());
        return mapper.toResponse(repository.save(option));
    }

    @Override
    public DeliveryOptionResponse update(Long id, UpdateDeliveryOptionRequest request) {
        DeliveryOption option = find(id);
        if (request.name() != null) option.setName(request.name().trim());
        if (request.description() != null) option.setDescription(request.description());
        if (request.basePrice() != null) option.setBasePrice(request.basePrice());
        if (request.freeThreshold() != null) option.setFreeThreshold(request.freeThreshold());
        if (request.estimatedDaysMin() != null) option.setEstimatedDaysMin(request.estimatedDaysMin());
        if (request.estimatedDaysMax() != null) option.setEstimatedDaysMax(request.estimatedDaysMax());
        if (request.isActive() != null) option.setIsActive(request.isActive());
        checkDays(option.getEstimatedDaysMin(), option.getEstimatedDaysMax());
        return mapper.toResponse(option);
    }

    @Override
    public void deactivate(Long id) {
        find(id).setIsActive(false);
    }

    private DeliveryOption find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No delivery option with id " + id));
    }

    private void checkDays(Integer min, Integer max) {
        if (min != null && max != null && max < min) {
            throw new BadRequestException("estimatedDaysMax cannot be less than estimatedDaysMin");
        }
    }

    private static long daysOrZero(Integer days) {
        return days == null ? 0 : days;
    }
}

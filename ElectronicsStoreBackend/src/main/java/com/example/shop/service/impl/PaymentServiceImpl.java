package com.example.shop.service.impl;

import com.example.shop.dto.request.PaymentRequest;
import com.example.shop.dto.response.PaymentMethodResponse;
import com.example.shop.dto.response.PaymentResponse;
import com.example.shop.exception.*;
import com.example.shop.mapper.OrderMapper;
import com.example.shop.model.entity.Order;
import com.example.shop.model.entity.Payment;
import com.example.shop.model.entity.User;
import com.example.shop.model.enums.OrderStatus;
import com.example.shop.model.enums.PaymentMethod;
import com.example.shop.model.enums.PaymentStatus;
import com.example.shop.repository.OrderRepository;
import com.example.shop.repository.PaymentRepository;
import com.example.shop.service.GatewayResult;
import com.example.shop.service.PaymentService;
import com.example.shop.util.LuhnValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentGatewaySimulator gateway;
    private final OrderStatusManager statusManager;
    private final UserLookup userLookup;
    private final OrderMapper mapper;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              OrderRepository orderRepository,
                              PaymentGatewaySimulator gateway,
                              OrderStatusManager statusManager,
                              UserLookup userLookup,
                              OrderMapper mapper) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.gateway = gateway;
        this.statusManager = statusManager;
        this.userLookup = userLookup;
        this.mapper = mapper;
    }

    @Override
    @Transactional(noRollbackFor = PaymentFailedException.class)
    public PaymentResponse process(String email, PaymentRequest request) {
        String cardNumber = null;
        if (request.paymentMethod() == PaymentMethod.CARD) {
            if (request.cardNumber() == null || request.cardNumber().isBlank()) {
                throw new BadRequestException("Card number is required for a card payment");
            }
            cardNumber = request.cardNumber().replace(" ", "");
            if (!LuhnValidator.isValid(cardNumber)) {
                throw new BadRequestException("Card number is not valid (Luhn check failed)");
            }
        }
        if (request.paymentMethod() == PaymentMethod.BONUS_POINTS) {
            throw new BadRequestException("Paying with bonus points is not available");
        }

        User user = userLookup.byEmail(email);
        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new ResourceNotFoundException("No order with id " + request.orderId()));
        if (!order.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("This order belongs to another customer");
        }
        Payment payment = paymentRepository.findByOrderId(order.getId()).orElseGet(Payment::new);
        if (payment.getStatus() == PaymentStatus.COMPLETED || order.getStatus() == OrderStatus.PAID) {
            throw new ConflictException("This order is already paid");
        }
        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.CONFIRMED) {
            throw new ConflictException("An order with status " + order.getStatus() + " cannot be paid");
        }
        if (order.getStatus() == OrderStatus.PENDING) {
            statusManager.changeStatus(order, OrderStatus.CONFIRMED);
        }

        payment.setOrder(order);
        order.setPayment(payment);
        payment.setPaymentMethod(request.paymentMethod());
        payment.setAmount(order.getTotalAmount());
        payment.setCurrency("KZT");
        payment.setBonusPointsUsed(0);
        payment.setCardLastFourDigits(cardNumber == null ? null : cardNumber.substring(cardNumber.length() - 4));
        payment.setTransactionId("TXN-" + UUID.randomUUID().toString().replace("-", "")
                .substring(0, 16).toUpperCase(Locale.ROOT));
        payment.setErrorCode(null);
        payment.setErrorMessage(null);

        if (request.paymentMethod() == PaymentMethod.CASH_ON_DELIVERY) {
            payment.setStatus(PaymentStatus.PENDING);
            return mapper.toResponse(paymentRepository.save(payment));
        }

        GatewayResult result = gateway.charge(request.paymentMethod(), cardNumber);
        if (result.success()) {
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setPaidAt(LocalDateTime.now());
            Payment saved = paymentRepository.save(payment);
            statusManager.changeStatus(order, OrderStatus.PAID);
            return mapper.toResponse(saved);
        }
        payment.setStatus(PaymentStatus.FAILED);
        payment.setErrorCode(result.errorCode());
        payment.setErrorMessage(result.errorMessage());
        Payment saved = paymentRepository.save(payment);
        throw new PaymentFailedException(result.errorMessage(), result.errorCode(), saved.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getStatus(String email, Long orderId) {
        User user = userLookup.byEmail(email);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("No order with id " + orderId));
        if (!order.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("This order belongs to another customer");
        }
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order " + orderId + " has no payment yet"));
        return mapper.toResponse(payment);
    }

    @Override
    public List<PaymentMethodResponse> getMethods() {
        return List.of(
                new PaymentMethodResponse(PaymentMethod.CARD, "Bank card (simulated gateway)", true),
                new PaymentMethodResponse(PaymentMethod.PAYPAL, "PayPal (simulated)", true),
                new PaymentMethodResponse(PaymentMethod.CASH_ON_DELIVERY, "Cash to the courier", true),
                new PaymentMethodResponse(PaymentMethod.BONUS_POINTS, "Bonus points (not available)", false));
    }
}

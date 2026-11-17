package com.example.shop.service.impl;

import com.example.shop.model.enums.PaymentMethod;
import com.example.shop.service.GatewayResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class PaymentGatewaySimulator {
    private final long minDelayMs;
    private final long maxDelayMs;

    public PaymentGatewaySimulator(@Value("${payment.min-delay-ms:1000}") long minDelayMs,
                                   @Value("${payment.max-delay-ms:3000}") long maxDelayMs) {
        this.minDelayMs = minDelayMs;
        this.maxDelayMs = Math.max(minDelayMs, maxDelayMs);
    }

    public GatewayResult charge(PaymentMethod method, String cardNumber) {
        simulateNetworkDelay();
        if (method == PaymentMethod.CARD) {
            switch (cardNumber) {
                case "4111111111111111":
                    return GatewayResult.ok();
                case "4000000000000002":
                    return GatewayResult.declined("INSUFFICIENT_FUNDS", "Card declined: insufficient funds");
                case "4000000000000069":
                    return GatewayResult.declined("NETWORK_ERROR", "Payment network error, please try again");
                case "4000000000000077":
                    return GatewayResult.declined("SUSPECTED_FRAUD", "Payment refused: suspected fraud");
                default:
                    break;
            }
        }
        if (ThreadLocalRandom.current().nextInt(100) < 90) {
            return GatewayResult.ok();
        }
        return GatewayResult.declined("RANDOM_DECLINE", "Payment declined by the bank, please try again");
    }

    private void simulateNetworkDelay() {
        long delay = minDelayMs >= maxDelayMs
                ? minDelayMs
                : ThreadLocalRandom.current().nextLong(minDelayMs, maxDelayMs + 1);
        try {
            Thread.sleep(delay);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }
}

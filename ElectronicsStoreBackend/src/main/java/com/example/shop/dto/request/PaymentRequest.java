package com.example.shop.dto.request;

import jakarta.validation.constraints.*;
import com.example.shop.model.enums.PaymentMethod;

public record PaymentRequest(

        @NotNull(message = "Order id is required")
        Long orderId,

        @NotNull(message = "Payment method is required")
        PaymentMethod paymentMethod,

        @Pattern(regexp = "^[0-9 ]{12,23}$", message = "Card number must contain 12 to 19 digits")
        String cardNumber,

        @Size(max = 100)
        String cardHolderName,

        @Pattern(regexp = "^(0[1-9]|1[0-2])/[0-9]{2}$", message = "Expiry date must look like MM/YY")
        String expiryDate,

        @Pattern(regexp = "^[0-9]{3,4}$", message = "CVV must be 3 or 4 digits")
        String cvv) {
}

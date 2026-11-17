package com.example.shop.util;

public final class LuhnValidator {
    private LuhnValidator() {
    }

    public static boolean isValid(String cardNumber) {
        if (cardNumber == null || !cardNumber.matches("\\d{12,19}")) {
            return false;
        }
        int sum = 0;
        boolean doubleIt = false;
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int digit = cardNumber.charAt(i) - '0';
            if (doubleIt) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }
            sum += digit;
            doubleIt = !doubleIt;
        }
        return sum % 10 == 0;
    }
}

package com.example.shop.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LuhnValidatorTest {
    @ParameterizedTest
    @ValueSource(strings = {"4111111111111111", "4000000000000002", "4000000000000069",
            "4000000000000077", "5555555555554444"})
    void acceptsValidNumbers(String number) {
        assertTrue(LuhnValidator.isValid(number));
    }

    @ParameterizedTest
    @ValueSource(strings = {"4111111111111112", "1234567890123456", "abcd", "", "4111"})
    void rejectsInvalidNumbers(String number) {
        assertFalse(LuhnValidator.isValid(number));
    }
}

package com.example.shop.exception;

import com.example.shop.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Map;

public final class ErrorResponseFactory {
    private ErrorResponseFactory() {
    }

    public static ErrorResponse build(HttpStatus status, String message,
                                      String path, Map<String, String> details) {
        return new ErrorResponse(
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                status.value(),
                status.name(),
                message,
                path,
                details == null ? Collections.emptyMap() : details
        );
    }
}

package com.menditech.bank.account.util;

import com.menditech.bank.account.dto.common.ApiCommonResponse;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;

public final class ApiResponseBuilder {

    private ApiResponseBuilder() {
        throw new AssertionError("Utility class should not be instantiated");
    }

    public static <T> ApiCommonResponse<T> success(HttpStatus httpStatus, String message, @Nullable T data) {
        return buildResponse(httpStatus, message, data);
    }

    public static <T> ApiCommonResponse<T> success(HttpStatus httpStatus, String message) {
        return success(httpStatus, message, null);
    }

    public static <T> ApiCommonResponse<T> error(HttpStatus httpStatus, String message, @Nullable T data) {
        return buildResponse(httpStatus, message, data);
    }

    public static <T> ApiCommonResponse<T> error(HttpStatus httpStatus, String message) {
        return error(httpStatus, message, null);
    }

    private static <T> ApiCommonResponse<T> buildResponse(HttpStatus httpStatus, String message, T data) {
        if (httpStatus == null) {
            throw new IllegalArgumentException("HttpStatus cannot be null");
        }
        if (message == null || message.isBlank()) {
            message = httpStatus.getReasonPhrase();
        }

        return ApiCommonResponse.<T>builder()
                .httpStatus(httpStatus.value())
                .status(httpStatus.name())
                .timestamp(LocalDateTime.now())
                .message(message)
                .data(data)
                .build();
    }
}
package com.menditech.bank.customer.util;

import com.menditech.bank.customer.dto.common.ApiCommonResponse;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public final class ApiResponseBuilder {

    private ApiResponseBuilder() {
    }

    public static <T> ApiCommonResponse<T> success(HttpStatus httpStatus, String message, T data) {
        return ApiCommonResponse.<T>builder()
                .httpStatus(httpStatus.value())
                .status(httpStatus.name())
                .timestamp(LocalDateTime.now())
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiCommonResponse<T> error(HttpStatus httpStatus, String message, T data) {
        return ApiCommonResponse.<T>builder()
                .httpStatus(httpStatus.value())
                .status(httpStatus.name())
                .timestamp(LocalDateTime.now())
                .message(message)
                .data(data)
                .build();
    }
}
package com.menditech.bank.account.util;

import com.menditech.bank.account.dto.common.ApiResponse;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public final class ApiResponseBuilder {

    private ApiResponseBuilder() {
    }

    public static <T> ApiResponse<T> success(HttpStatus httpStatus, String message, T data) {
        return ApiResponse.<T>builder()
                .httpStatus(httpStatus.value())
                .status(httpStatus.name())
                .timestamp(LocalDateTime.now())
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(HttpStatus httpStatus, String message, T data) {
        return ApiResponse.<T>builder()
                .httpStatus(httpStatus.value())
                .status(httpStatus.name())
                .timestamp(LocalDateTime.now())
                .message(message)
                .data(data)
                .build();
    }
}
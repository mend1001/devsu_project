package com.menditech.bank.account.exception;

import com.menditech.bank.account.dto.common.ApiResponse;
import com.menditech.bank.account.dto.response.ApiErrorResponse;
import com.menditech.bank.account.util.ApiResponseBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<ApiErrorResponse>> handleResourceNotFound(ResourceNotFoundException ex) {
        log.warn("Account-service resource not found: {}", ex.getMessage());

        ApiErrorResponse error = ApiErrorResponse.builder()
                .errorCode("RESOURCE_NOT_FOUND")
                .details(List.of(ex.getMessage()))
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponseBuilder.error(HttpStatus.NOT_FOUND, ex.getMessage(), error));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<ApiErrorResponse>> handleBusinessException(BusinessException ex) {
        log.warn("Account-service business exception: {}", ex.getMessage());

        ApiErrorResponse error = ApiErrorResponse.builder()
                .errorCode("BUSINESS_ERROR")
                .details(List.of(ex.getMessage()))
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseBuilder.error(HttpStatus.BAD_REQUEST, ex.getMessage(), error));
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ApiResponse<ApiErrorResponse>> handleInsufficientBalance(InsufficientBalanceException ex) {
        log.warn("Account-service insufficient balance: {}", ex.getMessage());

        ApiErrorResponse error = ApiErrorResponse.builder()
                .errorCode("INSUFFICIENT_BALANCE")
                .details(List.of(ex.getMessage()))
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseBuilder.error(HttpStatus.BAD_REQUEST, ex.getMessage(), error));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<ApiErrorResponse>> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        log.warn("Account-service validation error: {}", details);

        ApiErrorResponse error = ApiErrorResponse.builder()
                .errorCode("VALIDATION_ERROR")
                .details(details)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseBuilder.error(HttpStatus.BAD_REQUEST, "Validation error", error));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<ApiErrorResponse>> handleGenericException(Exception ex) {
        log.error("Account-service unexpected error", ex);

        ApiErrorResponse error = ApiErrorResponse.builder()
                .errorCode("INTERNAL_SERVER_ERROR")
                .details(List.of(ex.getMessage()))
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseBuilder.error(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", error));
    }
}

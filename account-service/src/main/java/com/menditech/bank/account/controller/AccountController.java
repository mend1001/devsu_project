package com.menditech.bank.account.controller;

import com.menditech.bank.account.dto.common.ApiResponse;
import com.menditech.bank.account.dto.request.AccountCreateRequest;
import com.menditech.bank.account.dto.response.AccountResponse;
import com.menditech.bank.account.service.AccountService;
import com.menditech.bank.account.util.ApiResponseBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<ApiResponse<AccountResponse>> createAccount(
            @Valid @RequestBody AccountCreateRequest request
    ) {
        AccountResponse response = accountService.createAccount(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseBuilder.success(HttpStatus.CREATED, "Account created successfully", response));
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<ApiResponse<List<AccountResponse>>> getAccountsByClient(
            @PathVariable Long clientId
    ) {
        List<AccountResponse> response = accountService.getAccountsByClient(clientId);

        return ResponseEntity.ok(
                ApiResponseBuilder.success(HttpStatus.OK, "Accounts retrieved successfully", response)
        );
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<ApiResponse<AccountResponse>> getAccountByNumber(
            @PathVariable String accountNumber
    ) {
        AccountResponse response = accountService.getAccountByNumber(accountNumber);

        return ResponseEntity.ok(
                ApiResponseBuilder.success(HttpStatus.OK, "Account retrieved successfully", response)
        );
    }
}
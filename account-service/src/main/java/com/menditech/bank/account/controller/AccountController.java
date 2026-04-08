package com.menditech.bank.account.controller;

import com.menditech.bank.account.dto.common.ApiCommonResponse;
import com.menditech.bank.account.dto.request.AccountCreateRequest;
import com.menditech.bank.account.dto.request.AccountUpdateRequest;
import com.menditech.bank.account.dto.response.AccountResponse;
import com.menditech.bank.account.service.AccountService;
import com.menditech.bank.account.util.ApiResponseBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Tag(name = "Accounts", description = "Operations related to bank accounts")
public class AccountController {

    private final AccountService accountService;

    @Operation(
            summary = "Create a new bank account",
            description = "Creates a new bank account for an existing client. The account number is automatically generated based on the account type."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Account successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Account type not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping
    public ResponseEntity<ApiCommonResponse<AccountResponse>> createAccount(
            @Parameter(description = "Account creation request body", required = true)
            @Valid @RequestBody AccountCreateRequest request
    ) {
        AccountResponse response = accountService.createAccount(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseBuilder.success(HttpStatus.CREATED, "Account created successfully", response));
    }

    @Operation(
            summary = "Update an existing account",
            description = "Updates account information such as status, overdraft limit, or blocked amount. Account number and type cannot be changed."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "409", description = "Account is closed or inactive")
    })
    @PutMapping("/{accountNumber}")
    public ResponseEntity<ApiCommonResponse<AccountResponse>> updateAccount(
            @Parameter(description = "Bank account number", example = "478761", required = true)
            @PathVariable String accountNumber,
            @Parameter(description = "Account update request body", required = true)
            @Valid @RequestBody AccountUpdateRequest request
    ) {
        AccountResponse response = accountService.updateAccount(accountNumber, request);

        return ResponseEntity.ok(
                ApiResponseBuilder.success(HttpStatus.OK, "Account updated successfully", response)
        );
    }

    @Operation(
            summary = "Close/Delete an account",
            description = "Performs logical deletion of an account by closing it. The account is marked as inactive and closed, but remains in the database for historical records."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account closed successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "400", description = "Account has pending movements or non-zero balance"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @DeleteMapping("/{accountNumber}")
    public ResponseEntity<ApiCommonResponse<Void>> deleteAccount(
            @Parameter(description = "Bank account number to close", example = "478761", required = true)
            @PathVariable String accountNumber
    ) {
        accountService.deleteAccount(accountNumber);

        return ResponseEntity.ok(
                ApiResponseBuilder.success(HttpStatus.OK, "Account closed successfully", null)
        );
    }

    @Operation(
            summary = "Get accounts by client",
            description = "Returns all accounts associated with a specific client."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Accounts retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Client not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/client/{clientId}")
    public ResponseEntity<ApiCommonResponse<List<AccountResponse>>> getAccountsByClient(
            @Parameter(description = "Client identifier", example = "1")
            @PathVariable Long clientId
    ) {
        List<AccountResponse> response = accountService.getAccountsByClient(clientId);

        return ResponseEntity.ok(
                ApiResponseBuilder.success(HttpStatus.OK, "Accounts retrieved successfully", response)
        );
    }

    @Operation(
            summary = "Get account by number",
            description = "Retrieves a specific bank account using its account number."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/{accountNumber}")
    public ResponseEntity<ApiCommonResponse<AccountResponse>> getAccountByNumber(
            @Parameter(description = "Bank account number", example = "478761")
            @PathVariable String accountNumber
    ) {
        AccountResponse response = accountService.getAccountByNumber(accountNumber);

        return ResponseEntity.ok(
                ApiResponseBuilder.success(HttpStatus.OK, "Account retrieved successfully", response)
        );
    }
}
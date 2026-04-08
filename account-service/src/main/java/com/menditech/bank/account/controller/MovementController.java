package com.menditech.bank.account.controller;

import com.menditech.bank.account.dto.common.ApiCommonResponse;
import com.menditech.bank.account.dto.request.MovementCreateRequest;
import com.menditech.bank.account.dto.response.MovementResponse;
import com.menditech.bank.account.service.serviceImpl.MovementServiceImpl;
import com.menditech.bank.account.util.ApiResponseBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movements")
@RequiredArgsConstructor
@Tag(name = "Movements", description = "Operations related to account movements and transactions")
public class MovementController {

    private final MovementServiceImpl movementService;

    @Operation(
            summary = "Create account movement",
            description = "Creates a movement (deposit or withdrawal) for a specific bank account and updates the account balance accordingly."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Movement created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "409", description = "Insufficient balance for withdrawal"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping
    public ResponseEntity<ApiCommonResponse<MovementResponse>> createMovement(
            @Parameter(description = "Movement creation request body", required = true)
            @Valid @RequestBody MovementCreateRequest request
    ) {

        MovementResponse response = movementService.createMovement(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseBuilder.success(HttpStatus.CREATED, "Movement created successfully", response));
    }
}
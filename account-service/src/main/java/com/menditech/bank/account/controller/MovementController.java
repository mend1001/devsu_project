package com.menditech.bank.account.controller;

import com.menditech.bank.account.dto.common.ApiCommonResponse;
import com.menditech.bank.account.dto.request.MovementCreateRequest;
import com.menditech.bank.account.dto.response.MovementResponse;
import com.menditech.bank.account.service.MovementService;
import com.menditech.bank.account.util.ApiResponseBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movements")
@RequiredArgsConstructor
public class MovementController {

    private final MovementService movementService;

    @PostMapping
    public ResponseEntity<ApiCommonResponse<MovementResponse>> createMovement(
            @Valid @RequestBody MovementCreateRequest request
    ) {
        MovementResponse response = movementService.createMovement(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseBuilder.success(HttpStatus.CREATED, "Movement created successfully", response));
    }
}
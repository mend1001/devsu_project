package com.menditech.bank.customer.controller;

import com.menditech.bank.customer.dto.common.ApiCommonResponse;
import com.menditech.bank.customer.dto.request.LoginRequest;
import com.menditech.bank.customer.dto.response.JwtResponse;
import com.menditech.bank.customer.service.AuthService;
import com.menditech.bank.customer.util.ApiResponseBuilder;

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
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication operations for system access")
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "Authenticate client",
            description = "Authenticates a client using their client code and password. Returns a JWT token used to access protected endpoints."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "423", description = "Account locked")
    })
    @PostMapping("/login")
    public ResponseEntity<ApiCommonResponse<JwtResponse>> login(
            @Parameter(description = "Login credentials", required = true)
            @Valid @RequestBody LoginRequest request
    ) {

        JwtResponse response = authService.login(request);

        return ResponseEntity.ok(
                ApiResponseBuilder.success(HttpStatus.OK, "Login successful", response)
        );
    }
}
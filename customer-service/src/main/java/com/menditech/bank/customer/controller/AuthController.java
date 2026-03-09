package com.menditech.bank.customer.controller;

import com.menditech.bank.customer.dto.common.ApiCommonResponse;
import com.menditech.bank.customer.dto.request.LoginRequest;
import com.menditech.bank.customer.dto.response.JwtResponse;
import com.menditech.bank.customer.service.AuthService;
import com.menditech.bank.customer.util.ApiResponseBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiCommonResponse<JwtResponse>> login(@Valid @RequestBody LoginRequest request) {
        JwtResponse response = authService.login(request);
        return ResponseEntity.ok(
                ApiResponseBuilder.success(HttpStatus.OK, "Login successful", response)
        );
    }
}
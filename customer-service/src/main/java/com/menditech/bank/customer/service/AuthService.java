package com.menditech.bank.customer.service;

import com.menditech.bank.customer.dto.request.LoginRequest;
import com.menditech.bank.customer.dto.response.JwtResponse;

public interface AuthService {
    JwtResponse login(LoginRequest request);
}
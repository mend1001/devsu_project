package com.menditech.bank.customer.service;

import com.menditech.bank.customer.dto.request.LoginRequest;
import com.menditech.bank.customer.dto.response.JwtResponse;
import com.menditech.bank.customer.entity.ClientEntity;
import com.menditech.bank.customer.exception.InvalidCredentialsException;
import com.menditech.bank.customer.repository.ClientRepository;
import com.menditech.bank.customer.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final ClientRepository clientRepository;
    private final JwtService jwtService;

    @Transactional(readOnly = true)
    public JwtResponse login(LoginRequest request) {
        ClientEntity client = clientRepository.findByCode(request.getClientCode())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid client code or password"));

        if (!client.getPasswordHash().equals(request.getPassword())) {
            throw new InvalidCredentialsException("Invalid client code or password");
        }

        String token = jwtService.generateToken(client.getCode());

        return JwtResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(3600L)
                .build();
    }
}
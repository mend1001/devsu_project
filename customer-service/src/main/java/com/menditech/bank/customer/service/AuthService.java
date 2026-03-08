package com.menditech.bank.customer.service;

import com.menditech.bank.customer.dto.request.LoginRequest;
import com.menditech.bank.customer.dto.response.JwtResponse;
import com.menditech.bank.customer.entity.ClientEntity;
import com.menditech.bank.customer.exception.InvalidCredentialsException;
import com.menditech.bank.customer.repository.ClientRepository;
import com.menditech.bank.customer.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final long TOKEN_EXPIRATION_SECONDS = 3600L;

    private final ClientRepository clientRepository;
    private final JwtService jwtService;
    private final UserSessionService userSessionService;
    private final PasswordEncoder passwordEncoder;
    @Transactional
    public JwtResponse login(LoginRequest request) {
        ClientEntity client = clientRepository.findByCode(request.getClientCode())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid client code or password"));

        if (!Boolean.TRUE.equals(client.getIsActive())) {
            throw new InvalidCredentialsException("Client is inactive");
        }

        if (Boolean.TRUE.equals(client.getIsLocked())) {
            throw new InvalidCredentialsException("Client is locked");
        }

        if (!passwordEncoder.matches(request.getPassword(), client.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid client code or password");
        }

        String token = jwtService.generateToken(client.getCode());

        userSessionService.registerSession(client, token, TOKEN_EXPIRATION_SECONDS);

        return JwtResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(TOKEN_EXPIRATION_SECONDS)
                .build();
    }
}
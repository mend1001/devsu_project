package com.menditech.bank.customer.service.serviceImpl;


import com.menditech.bank.customer.dto.request.LoginRequest;
import com.menditech.bank.customer.dto.response.JwtResponse;
import com.menditech.bank.customer.entity.ClientEntity;
import com.menditech.bank.customer.exception.InvalidCredentialsException;
import com.menditech.bank.customer.repository.ClientRepository;
import com.menditech.bank.customer.security.JwtService;
import com.menditech.bank.customer.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final String TOKEN_TYPE_BEARER = "Bearer";

    @Value("${jwt.token.expiration-seconds:3600}")
    private long tokenExpirationSeconds;

    private final ClientRepository clientRepository;
    private final JwtService jwtService;
    private final UserSessionServiceImpl userSessionService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public JwtResponse login(LoginRequest request) {
        log.info("Login attempt for clientCode={}", request.getClientCode());

        ClientEntity client = findActiveClient(request.getClientCode());
        validateClientStatus(client);

        if (!validateCredentials(request, client)) {
            handleFailedLogin(client, request.getClientCode());
        }

        handleSuccessfulLogin(client);

        String token = jwtService.generateToken(client.getCode());
        userSessionService.registerSession(client, token, tokenExpirationSeconds);

        log.info("Successful login for clientCode={}", request.getClientCode());

        return buildJwtResponse(token);
    }

    private ClientEntity findActiveClient(String clientCode) {
        return clientRepository.findByCode(clientCode)
                .orElseThrow(() -> {
                    log.warn("Login attempt with non-existent clientCode={}", clientCode);
                    return new InvalidCredentialsException("Invalid client code or password");
                });
    }

    private void validateClientStatus(ClientEntity client) {
        if (Boolean.FALSE.equals(client.getIsActive())) {
            log.warn("Login attempt for inactive client: clientCode={}", client.getCode());
            throw new InvalidCredentialsException("Client is inactive");
        }

        if (Boolean.TRUE.equals(client.getIsLocked())) {
            log.warn("Login attempt for locked client: clientCode={}", client.getCode());
            throw new InvalidCredentialsException("Client is locked. Please contact support.");
        }
    }

    private boolean validateCredentials(LoginRequest request, ClientEntity client) {
        return passwordEncoder.matches(request.getPassword(), client.getPasswordHash());
    }

    private void handleFailedLogin(ClientEntity client, String clientCode) {
        int attempts = client.getFailedLoginAttempts() + 1;
        client.setFailedLoginAttempts(attempts);

        log.warn("Failed login attempt {} of {} for clientCode={}",
                attempts, MAX_FAILED_ATTEMPTS, clientCode);

        if (attempts >= MAX_FAILED_ATTEMPTS) {
            client.setIsLocked(true);
            log.error("Client locked after {} failed attempts: clientCode={}",
                    MAX_FAILED_ATTEMPTS, clientCode);
        }

        clientRepository.save(client);
        throw new InvalidCredentialsException("Invalid client code or password");
    }

    private void handleSuccessfulLogin(ClientEntity client) {
        if (client.getFailedLoginAttempts() > 0) {
            client.setFailedLoginAttempts(0);
            clientRepository.save(client);
            log.debug("Reset failed login attempts for clientCode={}", client.getCode());
        }
    }

    private JwtResponse buildJwtResponse(String token) {
        return JwtResponse.builder()
                .token(token)
                .tokenType(TOKEN_TYPE_BEARER)
                .expiresIn(tokenExpirationSeconds)
                .build();
    }
}
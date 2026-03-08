package com.menditech.bank.customer.service;

import com.menditech.bank.customer.dto.request.LoginRequest;
import com.menditech.bank.customer.dto.response.JwtResponse;
import com.menditech.bank.customer.entity.ClientEntity;
import com.menditech.bank.customer.enums.ClientStatus;
import com.menditech.bank.customer.exception.InvalidCredentialsException;
import com.menditech.bank.customer.repository.ClientRepository;
import com.menditech.bank.customer.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private ClientRepository clientRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private UserSessionService userSessionService;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldLoginSuccessfully() {
        LoginRequest request = LoginRequest.builder()
                .clientCode("CLI2722163663")
                .password("1234")
                .build();

        ClientEntity client = ClientEntity.builder()
                .id(1L)
                .code("CLI2722163663")
                .passwordHash("$2a$10$Ta/ZwTffYi5XisR/X8nwNu/4FpOFs/F9GFh0UtXJFFZGs/IzdfbV2")
                .status(ClientStatus.ACTIVE)
                .isActive(true)
                .isLocked(false)
                .failedLoginAttempts(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(clientRepository.findByCode("CLI2722163663")).thenReturn(Optional.of(client));
        when(passwordEncoder.matches("1234", "$2a$10$Ta/ZwTffYi5XisR/X8nwNu/4FpOFs/F9GFh0UtXJFFZGs/IzdfbV2")).thenReturn(true);
        when(jwtService.generateToken("CLI2722163663")).thenReturn("mock-jwt-token");

        JwtResponse result = authService.login(request);

        assertNotNull(result);
        assertEquals("mock-jwt-token", result.getToken());
        assertEquals("Bearer", result.getTokenType());
        assertEquals(3600L, result.getExpiresIn());

        verify(userSessionService).registerSession(client, "mock-jwt-token", 3600L);
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsInvalid() {
        LoginRequest request = LoginRequest.builder()
                .clientCode("CLI2722163663")
                .password("wrong-password")
                .build();

        ClientEntity client = ClientEntity.builder()
                .id(1L)
                .code("CLI2722163663")
                .passwordHash("$2a$10$Ta/ZwTffYi5XisR/X8nwNu/4FpOFs/F9GFh0UtXJFFZGs/IzdfbV2")
                .status(ClientStatus.ACTIVE)
                .isActive(true)
                .isLocked(false)
                .failedLoginAttempts(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(clientRepository.findByCode("CLI2722163663")).thenReturn(Optional.of(client));
        when(passwordEncoder.matches("wrong-password", "$2a$10$Ta/ZwTffYi5XisR/X8nwNu/4FpOFs/F9GFh0UtXJFFZGs/IzdfbV2")).thenReturn(false);

        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(request)
        );

        assertEquals("Invalid client code or password", exception.getMessage());
        verify(userSessionService, never()).registerSession(any(), anyString(), anyLong());
    }
}

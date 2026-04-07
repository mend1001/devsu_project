package com.menditech.bank.customer.service;

import com.menditech.bank.customer.dto.request.LoginRequest;
import com.menditech.bank.customer.dto.response.JwtResponse;
import com.menditech.bank.customer.entity.ClientEntity;
import com.menditech.bank.customer.enums.ClientStatus;
import com.menditech.bank.customer.exception.InvalidCredentialsException;
import com.menditech.bank.customer.repository.ClientRepository;
import com.menditech.bank.customer.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Field;
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

    @BeforeEach
    void setUp() throws Exception {
        // Inyectar el valor de @Value manualmente vía reflexión
        Field expirationField = AuthService.class.getDeclaredField("tokenExpirationSeconds");
        expirationField.setAccessible(true);
        expirationField.set(authService, 3600L);
    }

    @Test
    void shouldLoginSuccessfully() {
        // given
        String clientCode = "CLI2722163663";
        String rawPassword = "1234";
        String hashedPassword = "$2a$10$Ta/ZwTffYi5XisR/X8nwNu/4FpOFs/F9GFh0UtXJFFZGs/IzdfbV2";
        String mockToken = "mock-jwt-token";

        LoginRequest request = LoginRequest.builder()
                .clientCode(clientCode)
                .password(rawPassword)
                .build();

        ClientEntity client = ClientEntity.builder()
                .id(1L)
                .code(clientCode)
                .passwordHash(hashedPassword)
                .status(ClientStatus.ACTIVE)
                .isActive(true)
                .isLocked(false)
                .failedLoginAttempts(0)  // Empieza en 0, no se guardará
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(clientRepository.findByCode(clientCode)).thenReturn(Optional.of(client));
        when(passwordEncoder.matches(rawPassword, hashedPassword)).thenReturn(true);
        when(jwtService.generateToken(clientCode)).thenReturn(mockToken);

        // when
        JwtResponse result = authService.login(request);

        // then
        assertNotNull(result);
        assertEquals(mockToken, result.getToken());
        assertEquals("Bearer", result.getTokenType());
        assertEquals(3600L, result.getExpiresIn());

        verify(userSessionService).registerSession(client, mockToken, 3600L);
        verify(clientRepository, never()).save(client);  // ← Verificar que NO se llama
    }
    @Test
    void shouldLoginSuccessfullyAndResetFailedAttempts() {
        // given
        String clientCode = "CLI2722163663";
        String rawPassword = "1234";
        String hashedPassword = "$2a$10$Ta/ZwTffYi5XisR/X8nwNu/4FpOFs/F9GFh0UtXJFFZGs/IzdfbV2";
        String mockToken = "mock-jwt-token";

        LoginRequest request = LoginRequest.builder()
                .clientCode(clientCode)
                .password(rawPassword)
                .build();

        ClientEntity client = ClientEntity.builder()
                .id(1L)
                .code(clientCode)
                .passwordHash(hashedPassword)
                .status(ClientStatus.ACTIVE)
                .isActive(true)
                .isLocked(false)
                .failedLoginAttempts(3)  // ← Tiene intentos previos
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(clientRepository.findByCode(clientCode)).thenReturn(Optional.of(client));
        when(passwordEncoder.matches(rawPassword, hashedPassword)).thenReturn(true);
        when(jwtService.generateToken(clientCode)).thenReturn(mockToken);

        // when
        JwtResponse result = authService.login(request);

        // then
        assertNotNull(result);
        assertEquals(0, client.getFailedLoginAttempts());  // ← Verifica que se reseteó

        verify(userSessionService).registerSession(client, mockToken, 3600L);
        verify(clientRepository).save(client);  // ← Ahora SÍ se verifica
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

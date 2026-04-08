package com.menditech.bank.customer.service;

import com.menditech.bank.customer.dto.request.LoginRequest;
import com.menditech.bank.customer.dto.response.JwtResponse;
import com.menditech.bank.customer.entity.ClientEntity;
import com.menditech.bank.customer.enums.ClientStatus;
import com.menditech.bank.customer.exception.InvalidCredentialsException;
import com.menditech.bank.customer.repository.ClientRepository;
import com.menditech.bank.customer.security.JwtService;
import com.menditech.bank.customer.service.serviceImpl.AuthServiceImpl;
import com.menditech.bank.customer.service.serviceImpl.UserSessionServiceImpl;
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
    private UserSessionServiceImpl userSessionService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() throws Exception {

        Field expirationField = AuthServiceImpl.class.getDeclaredField("tokenExpirationSeconds");
        expirationField.setAccessible(true);
        expirationField.set(authService, 3600L);
    }

    // -----------------------------------------------------------------------
    // Login exitoso — failedLoginAttempts ya es 0, no se persiste
    // -----------------------------------------------------------------------
    @Test
    void shouldLoginSuccessfully() {
        // given
        String clientCode    = "CLI2722163663";
        String rawPassword   = "1234";
        String hashedPassword = "$2a$10$Ta/ZwTffYi5XisR/X8nwNu/4FpOFs/F9GFh0UtXJFFZGs/IzdfbV2";
        String mockToken     = "mock-jwt-token";

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
                .failedLoginAttempts(0)   // ya en 0 → resetFailedAttempts no persiste
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
        // failedLoginAttempts == 0 → resetFailedAttempts() no llama a save()
        verify(clientRepository, never()).save(client);
    }

    // -----------------------------------------------------------------------
    // Login exitoso — había intentos previos, se resetean y persisten
    // -----------------------------------------------------------------------
    @Test
    void shouldLoginSuccessfullyAndResetFailedAttempts() {
        // given
        String clientCode    = "CLI2722163663";
        String rawPassword   = "1234";
        String hashedPassword = "$2a$10$Ta/ZwTffYi5XisR/X8nwNu/4FpOFs/F9GFh0UtXJFFZGs/IzdfbV2";
        String mockToken     = "mock-jwt-token";

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
                .failedLoginAttempts(3)   // ← tiene intentos previos → se resetean
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
        assertEquals(0, client.getFailedLoginAttempts());

        verify(userSessionService).registerSession(client, mockToken, 3600L);
        verify(clientRepository).save(client);   // resetFailedAttempts() persiste
    }

    // -----------------------------------------------------------------------
    // Contraseña incorrecta — se lanza excepción, no se crea sesión
    // -----------------------------------------------------------------------
    @Test
    void shouldThrowExceptionWhenPasswordIsInvalid() {
        // given
        String hash = "$2a$10$Ta/ZwTffYi5XisR/X8nwNu/4FpOFs/F9GFh0UtXJFFZGs/IzdfbV2";

        LoginRequest request = LoginRequest.builder()
                .clientCode("CLI2722163663")
                .password("wrong-password")
                .build();

        ClientEntity client = ClientEntity.builder()
                .id(1L)
                .code("CLI2722163663")
                .passwordHash(hash)
                .status(ClientStatus.ACTIVE)
                .isActive(true)
                .isLocked(false)
                .failedLoginAttempts(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(clientRepository.findByCode("CLI2722163663")).thenReturn(Optional.of(client));
        when(passwordEncoder.matches("wrong-password", hash)).thenReturn(false);

        // when / then
        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(request)
        );

        assertEquals("Invalid client code or password", exception.getMessage());
        verify(userSessionService, never()).registerSession(any(), anyString(), anyLong());
        // handleFailedAttempt() llama a save() para persistir el contador
        verify(clientRepository).save(client);
    }

    // -----------------------------------------------------------------------
    // Cliente inactivo — se lanza excepción antes de verificar contraseña
    // -----------------------------------------------------------------------
    @Test
    void shouldThrowExceptionWhenClientIsInactive() {
        LoginRequest request = LoginRequest.builder()
                .clientCode("CLI0000000001")
                .password("1234")
                .build();

        ClientEntity client = ClientEntity.builder()
                .id(2L)
                .code("CLI0000000001")
                .passwordHash("hash")
                .status(ClientStatus.INACTIVE)
                .isActive(false)
                .isLocked(false)
                .failedLoginAttempts(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(clientRepository.findByCode("CLI0000000001")).thenReturn(Optional.of(client));

        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(request)
        );

        assertEquals("Client is inactive", exception.getMessage());
        verify(passwordEncoder, never()).matches(any(), any());
        verify(userSessionService, never()).registerSession(any(), any(), anyLong());
    }

    // -----------------------------------------------------------------------
    // Cliente bloqueado — se lanza excepción antes de verificar contraseña
    // -----------------------------------------------------------------------
    @Test
    void shouldThrowExceptionWhenClientIsLocked() {
        LoginRequest request = LoginRequest.builder()
                .clientCode("CLI0000000002")
                .password("1234")
                .build();

        ClientEntity client = ClientEntity.builder()
                .id(3L)
                .code("CLI0000000002")
                .passwordHash("hash")
                .status(ClientStatus.ACTIVE)
                .isActive(true)
                .isLocked(true)
                .failedLoginAttempts(5)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(clientRepository.findByCode("CLI0000000002")).thenReturn(Optional.of(client));

        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(request)
        );

        assertEquals("Client is locked. Please contact support.", exception.getMessage());
        verify(passwordEncoder, never()).matches(any(), any());
        verify(userSessionService, never()).registerSession(any(), any(), anyLong());
    }

    // -----------------------------------------------------------------------
    // Código de cliente no existe — excepción genérica (evita enumeración)
    // -----------------------------------------------------------------------
    @Test
    void shouldThrowExceptionWhenClientCodeDoesNotExist() {
        LoginRequest request = LoginRequest.builder()
                .clientCode("CLI9999999999")
                .password("1234")
                .build();

        when(clientRepository.findByCode("CLI9999999999")).thenReturn(Optional.empty());

        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(request)
        );

        assertEquals("Invalid client code or password", exception.getMessage());
        verify(passwordEncoder, never()).matches(any(), any());
    }
}
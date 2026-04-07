package com.menditech.bank.account.service;

import com.menditech.bank.account.dto.request.MovementCreateRequest;
import com.menditech.bank.account.dto.response.MovementResponse;
import com.menditech.bank.account.entity.*;
import com.menditech.bank.account.enums.AccountStatus;
import com.menditech.bank.account.enums.MovementStatus;
import com.menditech.bank.account.exception.ResourceNotFoundException;
import com.menditech.bank.account.mapper.MovementMapper;
import com.menditech.bank.account.repository.AccountRepository;
import com.menditech.bank.account.repository.MovementRepository;
import com.menditech.bank.account.repository.MovementTypeRepository;
import com.menditech.bank.account.repository.TransactionChannelRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovementServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private MovementTypeRepository movementTypeRepository;

    @Mock
    private TransactionChannelRepository transactionChannelRepository;

    @Mock
    private MovementRepository movementRepository;

    @Mock
    private MovementMapper movementMapper;

    @Mock
    private BalanceService balanceService;

    @InjectMocks
    private MovementService movementService;

    @Test
    void shouldCreateMovementSuccessfully() {
        AccountTypeEntity accountType = AccountTypeEntity.builder()
                .code("SAVINGS")
                .name("Cuenta de Ahorros")
                .build();

        AccountEntity account = AccountEntity.builder()
                .id(1L)
                .clientId(1L)
                .accountType(accountType)
                .number("478758")
                .currencyCode("COP")
                .status(AccountStatus.ACTIVE)
                .currentBalance(new BigDecimal("1000.00"))
                .availableBalance(new BigDecimal("1000.00"))
                .blockedAmount(BigDecimal.ZERO)
                .build();

        MovementTypeEntity movementType = MovementTypeEntity.builder()
                .id(1L)
                .code("DEPOSIT")
                .name("Depósito")
                .sign((short) 1)
                .build();

        TransactionChannelEntity channel = TransactionChannelEntity.builder()
                .id(1L)
                .code("WEB")
                .name("Web")
                .build();

        MovementCreateRequest request = MovementCreateRequest.builder()
                .accountNumber("478758")
                .movementTypeCode("DEPOSIT")
                .transactionChannelCode("WEB")
                .amount(new BigDecimal("500.00"))
                .externalReference("DEP001")
                .description("Test deposit")
                .notes("Movement test")
                .build();

        MovementEntity savedMovement = MovementEntity.builder()
                .id(100L)
                .account(account)
                .movementType(movementType)
                .transactionChannel(channel)
                .reference("ref-001")
                .externalReference("DEP001")
                .description("Test deposit")
                .transactionDate(LocalDateTime.now())
                .postedAt(LocalDateTime.now())
                .amount(new BigDecimal("500.00"))
                .previousBalance(new BigDecimal("1000.00"))
                .availableBalance(new BigDecimal("1500.00"))
                .currencyCode("COP")
                .status(MovementStatus.POSTED)
                .isReverted(false)
                .notes("Movement test")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        MovementResponse response = MovementResponse.builder()
                .movementId(100L)
                .accountId(1L)
                .accountNumber("478758")
                .movementTypeCode("DEPOSIT")
                .status("POSTED")
                .amount(new BigDecimal("500.00"))
                .availableBalance(new BigDecimal("1500.00"))
                .build();

        when(accountRepository.findByNumber("478758")).thenReturn(Optional.of(account));
        when(movementTypeRepository.findByCode("DEPOSIT")).thenReturn(Optional.of(movementType));
        when(transactionChannelRepository.findByCode("WEB")).thenReturn(Optional.of(channel));
        when(movementRepository.save(any(MovementEntity.class))).thenReturn(savedMovement);
        when(movementMapper.toResponse(any(MovementEntity.class))).thenReturn(response);

        MovementResponse result = movementService.createMovement(request);

        assertNotNull(result);
        assertEquals("DEPOSIT", result.getMovementTypeCode());
        assertEquals("POSTED", result.getStatus());

        verify(balanceService).applyMovement(account, movementType, new BigDecimal("500.00"));
        verify(movementRepository).save(any(MovementEntity.class));
        verify(accountRepository).save(account);
    }

    @Test
    void shouldThrowExceptionWhenAccountDoesNotExist() {
        MovementCreateRequest request = MovementCreateRequest.builder()
                .accountNumber("999999")
                .movementTypeCode("DEPOSIT")
                .amount(new BigDecimal("100.00"))
                .build();

        when(accountRepository.findByNumber("999999")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> movementService.createMovement(request)
        );

        assertEquals("Account not found: 999999", exception.getMessage());
    }
}

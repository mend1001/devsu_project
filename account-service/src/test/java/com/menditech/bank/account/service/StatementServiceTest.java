package com.menditech.bank.account.service;

import com.menditech.bank.account.dto.request.ReportRequest;
import com.menditech.bank.account.dto.response.MovementResponse;
import com.menditech.bank.account.dto.response.StatementReportResponse;
import com.menditech.bank.account.entity.AccountEntity;
import com.menditech.bank.account.entity.AccountTypeEntity;
import com.menditech.bank.account.entity.MovementEntity;
import com.menditech.bank.account.enums.AccountStatus;
import com.menditech.bank.account.mapper.MovementMapper;
import com.menditech.bank.account.repository.AccountRepository;
import com.menditech.bank.account.repository.MovementRepository;
import com.menditech.bank.account.service.serviceImpl.StatementServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatementServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private MovementRepository movementRepository;

    @Mock
    private MovementMapper movementMapper;

    @InjectMocks
    private StatementServiceImpl statementService;

    @Test
    void shouldGenerateStatementSuccessfully() {
        ReportRequest request = ReportRequest.builder()
                .clientId(1L)
                .startDate(LocalDate.of(2026, 3, 1))
                .endDate(LocalDate.of(2026, 3, 31))
                .build();

        AccountTypeEntity accountType = AccountTypeEntity.builder()
                .code("SAVINGS")
                .name("Cuenta de Ahorros")
                .build();

        AccountEntity account = AccountEntity.builder()
                .id(1L)
                .clientId(1L)
                .accountType(accountType)
                .number("478758")
                .status(AccountStatus.ACTIVE)
                .initialBalance(new BigDecimal("1000.00"))
                .currentBalance(new BigDecimal("1500.00"))
                .availableBalance(new BigDecimal("1500.00"))
                .build();

        MovementEntity movement = MovementEntity.builder()
                .id(10L)
                .account(account)
                .transactionDate(LocalDateTime.of(2026, 3, 10, 10, 0))
                .build();

        MovementResponse movementResponse = MovementResponse.builder()
                .movementId(10L)
                .accountId(1L)
                .accountNumber("478758")
                .amount(new BigDecimal("500.00"))
                .status("POSTED")
                .build();

        when(accountRepository.findByClientId(1L)).thenReturn(List.of(account));
        when(movementRepository.findByAccountClientIdAndTransactionDateBetween(
                eq(1L),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(List.of(movement));
        when(movementMapper.toResponse(movement)).thenReturn(movementResponse);

        StatementReportResponse result = statementService.generateReport(request);

        assertNotNull(result);
        assertEquals(1L, result.getClientId());
        assertEquals(1, result.getAccounts().size());
        assertEquals("478758", result.getAccounts().get(0).getAccountNumber());
        assertEquals(1, result.getAccounts().get(0).getMovements().size());
    }
}
package com.menditech.bank.account.service;

import com.menditech.bank.account.dto.request.AccountCreateRequest;
import com.menditech.bank.account.dto.response.AccountResponse;
import com.menditech.bank.account.entity.AccountEntity;
import com.menditech.bank.account.entity.AccountTypeEntity;
import com.menditech.bank.account.enums.AccountStatus;
import com.menditech.bank.account.exception.ResourceNotFoundException;
import com.menditech.bank.account.mapper.AccountMapper;
import com.menditech.bank.account.repository.AccountRepository;
import com.menditech.bank.account.repository.AccountTypeRepository;
import com.menditech.bank.account.service.serviceImpl.AccountNumberGeneratorServiceImpl;
import com.menditech.bank.account.service.serviceImpl.AccountServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountTypeRepository accountTypeRepository;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private AccountNumberGeneratorServiceImpl accountNumberGeneratorService;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    void shouldCreateAccountSuccessfully() {
        AccountCreateRequest request = AccountCreateRequest.builder()
                .clientId(1L)
                .accountTypeCode("SAVINGS")
                .iban(null)
                .currencyCode("COP")
                .initialBalance(new BigDecimal("2000.00"))
                .overdraftLimit(BigDecimal.ZERO)
                .interestRate(BigDecimal.ZERO)
                .monthlyFee(BigDecimal.ZERO)
                .minimumBalance(BigDecimal.ZERO)
                .statementDay((short) 15)
                .branchCode("BOG001")
                .alias("Main Savings")
                .isPrimary(true)
                .isActive(true)
                .build();

        AccountTypeEntity accountType = AccountTypeEntity.builder()
                .id(1L)
                .code("SAVINGS")
                .name("Cuenta de Ahorros")
                .build();

        AccountEntity savedEntity = AccountEntity.builder()
                .id(10L)
                .clientId(1L)
                .accountType(accountType)
                .number("478762")
                .status(AccountStatus.ACTIVE)
                .initialBalance(new BigDecimal("2000.00"))
                .currentBalance(new BigDecimal("2000.00"))
                .availableBalance(new BigDecimal("2000.00"))
                .build();

        AccountResponse response = AccountResponse.builder()
                .accountId(10L)
                .clientId(1L)
                .accountTypeCode("SAVINGS")
                .accountNumber("478762")
                .status("ACTIVE")
                .initialBalance(new BigDecimal("2000.00"))
                .currentBalance(new BigDecimal("2000.00"))
                .availableBalance(new BigDecimal("2000.00"))
                .build();

        when(accountTypeRepository.findByCode("SAVINGS")).thenReturn(Optional.of(accountType));
        when(accountNumberGeneratorService.generateNextAccountNumber("SAVINGS")).thenReturn("478762");
        when(accountRepository.save(any(AccountEntity.class))).thenReturn(savedEntity);
        when(accountMapper.toResponse(savedEntity)).thenReturn(response);

        AccountResponse result = accountService.createAccount(request);

        assertNotNull(result);
        assertEquals("478762", result.getAccountNumber());
        assertEquals("SAVINGS", result.getAccountTypeCode());

        verify(accountTypeRepository).findByCode("SAVINGS");
        verify(accountNumberGeneratorService).generateNextAccountNumber("SAVINGS");
        verify(accountRepository).save(any(AccountEntity.class));
        verify(accountMapper).toResponse(savedEntity);
    }

    @Test
    void shouldThrowExceptionWhenAccountTypeDoesNotExist() {
        AccountCreateRequest request = AccountCreateRequest.builder()
                .clientId(1L)
                .accountTypeCode("SAVINGS")
                .currencyCode("COP")
                .initialBalance(new BigDecimal("2000.00"))
                .overdraftLimit(BigDecimal.ZERO)
                .monthlyFee(BigDecimal.ZERO)
                .minimumBalance(BigDecimal.ZERO)
                .isPrimary(true)
                .isActive(true)
                .build();

        when(accountTypeRepository.findByCode("SAVINGS")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> accountService.createAccount(request)
        );

        assertEquals("Account type not found: SAVINGS", exception.getMessage());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void shouldReturnAccountsByClient() {
        AccountTypeEntity accountType = AccountTypeEntity.builder()
                .id(1L)
                .code("SAVINGS")
                .name("Cuenta de Ahorros")
                .build();

        AccountEntity entity = AccountEntity.builder()
                .id(1L)
                .clientId(1L)
                .accountType(accountType)
                .number("478758")
                .status(AccountStatus.ACTIVE)
                .build();

        AccountResponse response = AccountResponse.builder()
                .accountId(1L)
                .clientId(1L)
                .accountTypeCode("SAVINGS")
                .accountNumber("478758")
                .status("ACTIVE")
                .build();

        when(accountRepository.findByClientId(1L)).thenReturn(List.of(entity));
        when(accountMapper.toResponse(entity)).thenReturn(response);

        List<AccountResponse> result = accountService.getAccountsByClient(1L);

        assertEquals(1, result.size());
        assertEquals("478758", result.get(0).getAccountNumber());
    }

    @Test
    void shouldReturnAccountByNumber() {
        AccountTypeEntity accountType = AccountTypeEntity.builder()
                .id(1L)
                .code("SAVINGS")
                .name("Cuenta de Ahorros")
                .build();

        AccountEntity entity = AccountEntity.builder()
                .id(1L)
                .clientId(1L)
                .accountType(accountType)
                .number("478758")
                .status(AccountStatus.ACTIVE)
                .build();

        AccountResponse response = AccountResponse.builder()
                .accountId(1L)
                .accountNumber("478758")
                .accountTypeCode("SAVINGS")
                .status("ACTIVE")
                .build();

        when(accountRepository.findByNumber("478758")).thenReturn(Optional.of(entity));
        when(accountMapper.toResponse(entity)).thenReturn(response);

        AccountResponse result = accountService.getAccountByNumber("478758");

        assertNotNull(result);
        assertEquals("478758", result.getAccountNumber());
    }
}
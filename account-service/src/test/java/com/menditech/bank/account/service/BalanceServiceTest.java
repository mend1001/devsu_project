package com.menditech.bank.account.service;

import com.menditech.bank.account.entity.AccountEntity;
import com.menditech.bank.account.entity.MovementTypeEntity;
import com.menditech.bank.account.exception.InsufficientBalanceException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BalanceServiceTest {

    private final BalanceService balanceService = new BalanceService();

    @Test
    void shouldApplyDepositSuccessfully() {
        AccountEntity account = AccountEntity.builder()
                .currentBalance(new BigDecimal("1000.00"))
                .availableBalance(new BigDecimal("1000.00"))
                .blockedAmount(BigDecimal.ZERO)
                .build();

        MovementTypeEntity movementType = MovementTypeEntity.builder()
                .sign((short) 1)
                .build();

        balanceService.applyMovement(account, movementType, new BigDecimal("500.00"));

        assertEquals(new BigDecimal("1500.00"), account.getCurrentBalance());
        assertEquals(new BigDecimal("1500.00"), account.getAvailableBalance());
    }

    @Test
    void shouldApplyWithdrawalSuccessfully() {
        AccountEntity account = AccountEntity.builder()
                .currentBalance(new BigDecimal("1000.00"))
                .availableBalance(new BigDecimal("1000.00"))
                .blockedAmount(BigDecimal.ZERO)
                .build();

        MovementTypeEntity movementType = MovementTypeEntity.builder()
                .sign((short) -1)
                .build();

        balanceService.applyMovement(account, movementType, new BigDecimal("300.00"));

        assertEquals(new BigDecimal("700.00"), account.getCurrentBalance());
        assertEquals(new BigDecimal("700.00"), account.getAvailableBalance());
    }

    @Test
    void shouldThrowExceptionWhenBalanceIsInsufficient() {
        AccountEntity account = AccountEntity.builder()
                .currentBalance(new BigDecimal("200.00"))
                .availableBalance(new BigDecimal("200.00"))
                .blockedAmount(BigDecimal.ZERO)
                .build();

        MovementTypeEntity movementType = MovementTypeEntity.builder()
                .sign((short) -1)
                .build();

        InsufficientBalanceException exception = assertThrows(
                InsufficientBalanceException.class,
                () -> balanceService.applyMovement(account, movementType, new BigDecimal("500.00"))
        );

        assertEquals("Saldo no disponible", exception.getMessage());
    }
}
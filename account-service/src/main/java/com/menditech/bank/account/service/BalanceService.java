package com.menditech.bank.account.service;

import com.menditech.bank.account.entity.AccountEntity;
import com.menditech.bank.account.entity.MovementTypeEntity;
import com.menditech.bank.account.exception.BusinessException;
import com.menditech.bank.account.exception.InsufficientBalanceException;
import com.menditech.bank.account.service.serviceImpl.BalanceCalculator;
import com.menditech.bank.account.util.MovementSign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class BalanceService implements BalanceCalculator {

    private static final String INSUFFICIENT_BALANCE_MSG = "Insufficient balance for this operation";
    private static final String INVALID_AMOUNT_MSG = "Movement amount must be positive";

    @Override
    public void applyMovement(AccountEntity account,
                              MovementTypeEntity movementType,
                              BigDecimal amount) {
        validateInputs(account, movementType, amount);

        BigDecimal current = account.getCurrentBalance();
        BigDecimal newBalance;

        if (movementType.getSign() == MovementSign.CREDIT) {
            newBalance = current.add(amount);
            log.debug("Applying credit of {} to account {}", amount, account.getId());
        } else {
            newBalance = current.subtract(amount);
            validateSufficientFunds(account, newBalance);
            log.debug("Applying debit of {} to account {}", amount, account.getId());
        }

        updateAccountBalances(account, newBalance);
    }

    private void validateInputs(AccountEntity account,
                                MovementTypeEntity movementType,
                                BigDecimal amount) {
        if (account == null) {
            throw new BusinessException("Account cannot be null");
        }
        if (movementType == null) {
            throw new BusinessException("Movement type cannot be null");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Invalid movement amount: {}", amount);
            throw new BusinessException(INVALID_AMOUNT_MSG);
        }
    }

    private void validateSufficientFunds(AccountEntity account, BigDecimal newBalance) {
        BigDecimal overdraftLimit = account.getOverdraftLimit() != null
                ? account.getOverdraftLimit()
                : BigDecimal.ZERO;

        BigDecimal minimumAllowed = BigDecimal.ZERO.subtract(overdraftLimit);

        if (newBalance.compareTo(minimumAllowed) < 0) {
            log.warn("Insufficient balance. New balance {} would exceed overdraft limit {}",
                    newBalance, overdraftLimit);
            throw new InsufficientBalanceException(INSUFFICIENT_BALANCE_MSG);
        }
    }

    private void updateAccountBalances(AccountEntity account, BigDecimal newBalance) {
        BigDecimal blockedAmount = account.getBlockedAmount() != null
                ? account.getBlockedAmount()
                : BigDecimal.ZERO;

        account.setCurrentBalance(newBalance);
        account.setAvailableBalance(newBalance.subtract(blockedAmount));

        log.info("Balance updated for account {}. Current: {}, Available: {}",
                account.getId(), newBalance, account.getAvailableBalance());
    }
}
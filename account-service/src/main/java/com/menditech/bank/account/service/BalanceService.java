package com.menditech.bank.account.service;

import com.menditech.bank.account.entity.AccountEntity;
import com.menditech.bank.account.entity.MovementTypeEntity;
import com.menditech.bank.account.exception.InsufficientBalanceException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BalanceService {

    public void applyMovement(AccountEntity account,
                              MovementTypeEntity movementType,
                              BigDecimal amount) {

        BigDecimal current = account.getCurrentBalance();

        if (movementType.getSign() == 1) {
            current = current.add(amount);
        } else {
            BigDecimal newBalance = current.subtract(amount);

            if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                throw new InsufficientBalanceException("Saldo no disponible");
            }

            current = newBalance;
        }

        account.setCurrentBalance(current);
        account.setAvailableBalance(current.subtract(account.getBlockedAmount()));
    }
}
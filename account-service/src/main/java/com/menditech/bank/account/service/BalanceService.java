package com.menditech.bank.account.service;

import com.menditech.bank.account.entity.AccountEntity;
import com.menditech.bank.account.entity.MovementTypeEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BalanceService {

    public void applyMovement(AccountEntity account,
                              MovementTypeEntity movementType,
                              BigDecimal amount) {

        BigDecimal current = account.getCurrentBalance();

        if (movementType.getSign() == 1) {
            current = current.add(amount);
        } else {
            current = current.subtract(amount);
        }

        account.setCurrentBalance(current);
        account.setAvailableBalance(current.subtract(account.getBlockedAmount()));
    }
}

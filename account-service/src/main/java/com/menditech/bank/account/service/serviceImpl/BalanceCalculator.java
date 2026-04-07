package com.menditech.bank.account.service.serviceImpl;
import com.menditech.bank.account.entity.AccountEntity;
import com.menditech.bank.account.entity.MovementTypeEntity;

import java.math.BigDecimal;

public interface BalanceCalculator {
    void applyMovement(AccountEntity account, MovementTypeEntity movementType, BigDecimal amount);
}

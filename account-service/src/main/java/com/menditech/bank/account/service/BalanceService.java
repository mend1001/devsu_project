package com.menditech.bank.account.service;
import com.menditech.bank.account.entity.AccountEntity;
import com.menditech.bank.account.entity.MovementTypeEntity;

import java.math.BigDecimal;

public interface BalanceService {
    void applyMovement(AccountEntity account, MovementTypeEntity movementType, BigDecimal amount);
}

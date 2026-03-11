package com.menditech.bank.account.mapper;

import com.menditech.bank.account.dto.response.AccountResponse;
import com.menditech.bank.account.entity.AccountEntity;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public AccountResponse toResponse(AccountEntity account) {
        return AccountResponse.builder()
                .accountId(account.getId())
                .clientId(account.getClientId())
                .accountTypeCode(account.getAccountType() != null ? account.getAccountType().getCode() : null)
                .accountTypeName(account.getAccountType() != null ? account.getAccountType().getName() : null)
                .accountNumber(account.getNumber())
                .iban(account.getIban())
                .currencyCode(account.getCurrencyCode())
                .status(account.getStatus() != null ? account.getStatus().name() : null)
                .initialBalance(account.getInitialBalance())
                .currentBalance(account.getCurrentBalance())
                .availableBalance(account.getAvailableBalance())
                .blockedAmount(account.getBlockedAmount())
                .overdraftLimit(account.getOverdraftLimit())
                .interestRate(account.getInterestRate())
                .monthlyFee(account.getMonthlyFee())
                .minimumBalance(account.getMinimumBalance())
                .statementDay(account.getStatementDay())
                .branchCode(account.getBranchCode())
                .alias(account.getAlias())
                .isPrimary(account.getIsPrimary())
                .isActive(account.getIsActive())
                .openedAt(account.getOpenedAt())
                .closedAt(account.getClosedAt())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }
}

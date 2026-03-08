package com.menditech.bank.account.service;

import com.menditech.bank.account.dto.request.AccountCreateRequest;
import com.menditech.bank.account.dto.response.AccountResponse;
import com.menditech.bank.account.entity.AccountEntity;
import com.menditech.bank.account.entity.AccountTypeEntity;
import com.menditech.bank.account.enums.AccountStatus;
import com.menditech.bank.account.mapper.AccountMapper;
import com.menditech.bank.account.repository.AccountRepository;
import com.menditech.bank.account.repository.AccountTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountTypeRepository accountTypeRepository;
    private final AccountMapper accountMapper;

    @Transactional
    public AccountResponse createAccount(AccountCreateRequest request) {

        AccountTypeEntity type = accountTypeRepository.findByCode(request.getAccountTypeCode())
                .orElseThrow(() -> new RuntimeException("Account type not found"));

        LocalDateTime now = LocalDateTime.now();

        AccountEntity account = AccountEntity.builder()
                .clientId(request.getClientId())
                .accountType(type)
                .number(request.getAccountNumber())
                .iban(request.getIban())
                .currencyCode(request.getCurrencyCode())
                .openedAt(now)
                .status(AccountStatus.ACTIVE)
                .initialBalance(request.getInitialBalance())
                .currentBalance(request.getInitialBalance())
                .availableBalance(request.getInitialBalance())
                .blockedAmount(BigDecimal.ZERO)
                .overdraftLimit(request.getOverdraftLimit())
                .interestRate(request.getInterestRate())
                .monthlyFee(request.getMonthlyFee())
                .minimumBalance(request.getMinimumBalance())
                .statementDay(request.getStatementDay())
                .branchCode(request.getBranchCode())
                .alias(request.getAlias())
                .isPrimary(request.getIsPrimary())
                .isActive(request.getIsActive())
                .version(0L)
                .createdAt(now)
                .updatedAt(now)
                .createdBy("SYSTEM")
                .updatedBy("SYSTEM")
                .build();

        return accountMapper.toResponse(accountRepository.save(account));
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> getAccountsByClient(Long clientId) {
        return accountRepository.findByClientId(clientId)
                .stream()
                .map(accountMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccountByNumber(String number) {
        AccountEntity account = accountRepository.findByNumber(number)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        return accountMapper.toResponse(account);
    }
}
package com.menditech.bank.account.service.serviceImpl;

import com.menditech.bank.account.dto.request.AccountCreateRequest;
import com.menditech.bank.account.dto.request.AccountUpdateRequest;
import com.menditech.bank.account.dto.response.AccountResponse;
import com.menditech.bank.account.entity.AccountEntity;
import com.menditech.bank.account.entity.AccountTypeEntity;
import com.menditech.bank.account.enums.AccountStatus;
import com.menditech.bank.account.exception.BusinessException;
import com.menditech.bank.account.exception.ResourceNotFoundException;
import com.menditech.bank.account.mapper.AccountMapper;
import com.menditech.bank.account.repository.AccountRepository;
import com.menditech.bank.account.repository.AccountTypeRepository;
import com.menditech.bank.account.service.AccountNumberGenerator;
import com.menditech.bank.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private static final String SYSTEM_USER = "SYSTEM";

    private final AccountRepository accountRepository;
    private final AccountTypeRepository accountTypeRepository;
    private final AccountMapper accountMapper;
    private final AccountNumberGenerator accountNumberGenerator;

    @Override
    @Transactional
    public AccountResponse createAccount(AccountCreateRequest request) {
        log.info("Creating account for clientId: {}, type: {}", request.getClientId(), request.getAccountTypeCode());

        AccountTypeEntity type = accountTypeRepository.findByCode(request.getAccountTypeCode())
                .orElseThrow(() -> {
                    log.error("Account type not found: {}", request.getAccountTypeCode());
                    return new ResourceNotFoundException("Account type not found: " + request.getAccountTypeCode());
                });

        String generatedAccountNumber = accountNumberGenerator.generateNextAccountNumber(request.getAccountTypeCode());
        String actor = getCurrentUser();
        LocalDateTime now = LocalDateTime.now();

        AccountEntity account = AccountEntity.builder()
                .clientId(request.getClientId())
                .accountType(type)
                .number(generatedAccountNumber)
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
                .isActive(true)
                .createdAt(now)
                .updatedAt(now)
                .createdBy(actor)
                .updatedBy(actor)
                .build();

        AccountEntity saved = accountRepository.save(account);
        log.info("Account created successfully: {} for client: {}", saved.getNumber(), saved.getClientId());

        return accountMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public AccountResponse updateAccount(String accountNumber, AccountUpdateRequest request) {
        log.info("Updating account: {}", accountNumber);

        AccountEntity account = accountRepository.findByNumber(accountNumber)
                .orElseThrow(() -> {
                    log.error("Account not found for update: {}", accountNumber);
                    return new ResourceNotFoundException("Account not found: " + accountNumber);
                });

        validateAccountCanBeModified(account);

        // Actualizar campos permitidos según AccountUpdateRequest
        if (request.getIban() != null) {
            account.setIban(request.getIban());
        }
        if (request.getOverdraftLimit() != null) {
            account.setOverdraftLimit(request.getOverdraftLimit());
        }
        if (request.getInterestRate() != null) {
            account.setInterestRate(request.getInterestRate());
        }
        if (request.getMonthlyFee() != null) {
            account.setMonthlyFee(request.getMonthlyFee());
        }
        if (request.getMinimumBalance() != null) {
            account.setMinimumBalance(request.getMinimumBalance());
        }
        if (request.getStatementDay() != null) {
            account.setStatementDay(request.getStatementDay());
        }
        if (request.getBranchCode() != null) {
            account.setBranchCode(request.getBranchCode());
        }
        if (request.getAlias() != null) {
            account.setAlias(request.getAlias());
        }
        if (request.getIsPrimary() != null) {
            account.setIsPrimary(request.getIsPrimary());
        }
        if (request.getIsActive() != null) {
            account.setIsActive(request.getIsActive());
            // Sincronizar status con isActive si es necesario
            if (Boolean.FALSE.equals(request.getIsActive()) && AccountStatus.ACTIVE.equals(account.getStatus())) {
                account.setStatus(AccountStatus.INACTIVE);
            } else if (Boolean.TRUE.equals(request.getIsActive()) && AccountStatus.INACTIVE.equals(account.getStatus())) {
                account.setStatus(AccountStatus.ACTIVE);
            }
        }

        account.setUpdatedAt(LocalDateTime.now());
        account.setUpdatedBy(getCurrentUser());

        AccountEntity updated = accountRepository.save(account);
        log.info("Account updated successfully: {}", accountNumber);

        return accountMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteAccount(String accountNumber) {
        log.info("Closing account: {}", accountNumber);

        AccountEntity account = accountRepository.findByNumber(accountNumber)
                .orElseThrow(() -> {
                    log.error("Account not found for closure: {}", accountNumber);
                    return new ResourceNotFoundException("Account not found: " + accountNumber);
                });

        // Validar que la cuenta no tenga saldo pendiente
        if (account.getCurrentBalance().compareTo(BigDecimal.ZERO) != 0) {
            log.warn("Attempt to close account {} with non-zero balance: {}", accountNumber, account.getCurrentBalance());
            throw new BusinessException("Cannot close account with non-zero balance. Current balance: " + account.getCurrentBalance());
        }
        LocalDateTime now = LocalDateTime.now();

        account.setStatus(AccountStatus.CLOSED);
        account.setIsActive(false);
        account.setClosedAt(now);
        account.setUpdatedAt(now);
        account.setUpdatedBy(getCurrentUser());

        accountRepository.save(account);
        log.info("Account closed successfully: {}", accountNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountResponse> getAccountsByClient(Long clientId) {
        log.debug("Fetching accounts for clientId: {}", clientId);
        return accountRepository.findByClientId(clientId)
                .stream()
                .map(accountMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponse getAccountByNumber(String accountNumber) {
        log.debug("Fetching account by number: {}", accountNumber);
        return accountRepository.findByNumber(accountNumber)
                .map(accountMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountNumber));
    }

    private void validateAccountCanBeModified(AccountEntity account) {
        if (AccountStatus.CLOSED.equals(account.getStatus())) {
            log.warn("Attempt to modify closed account: {}", account.getNumber());
            throw new BusinessException("Cannot modify a closed account");
        }
    }

    private String getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null && auth.getName() != null) ? auth.getName() : SYSTEM_USER;
    }
}
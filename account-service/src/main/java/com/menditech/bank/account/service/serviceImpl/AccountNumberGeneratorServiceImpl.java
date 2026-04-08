package com.menditech.bank.account.service.serviceImpl;

import com.menditech.bank.account.enums.AccountTypeConfig;
import com.menditech.bank.account.repository.AccountRepository;
import com.menditech.bank.account.service.AccountNumberGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;



@Slf4j
@Service
@RequiredArgsConstructor
public class AccountNumberGeneratorServiceImpl implements AccountNumberGeneratorService {

    private final AccountRepository accountRepository;

    @Override
    @Transactional(readOnly = true)
    public String generateNextAccountNumber(String accountTypeCode) {
        if (!StringUtils.hasText(accountTypeCode)) {
            log.error("Account type code is null or empty");
            throw new IllegalArgumentException("Account type code cannot be null or empty");
        }

        log.debug("Generating next account number for type: {}", accountTypeCode);

        Long maxNumber = accountRepository.findMaxAccountNumberByAccountTypeCode(accountTypeCode);

        long nextNumber = (maxNumber == null)
                ? AccountTypeConfig.getInitialNumber(accountTypeCode)
                : maxNumber + 1;

        log.info("Generated account number {} for type {}", nextNumber, accountTypeCode);

        return String.valueOf(nextNumber);
    }
}
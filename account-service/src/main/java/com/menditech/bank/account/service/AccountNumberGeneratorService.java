package com.menditech.bank.account.service;

import com.menditech.bank.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountNumberGeneratorService {

    private final AccountRepository accountRepository;

    @Transactional(readOnly = true)
    public String generateNextAccountNumber(String accountTypeCode) {

        Long maxNumber = accountRepository.findMaxAccountNumberByAccountTypeCode(accountTypeCode);

        long nextNumber;

        if (maxNumber == null) {
            nextNumber = getInitialNumberByAccountType(accountTypeCode);
        } else {
            nextNumber = maxNumber + 1;
        }

        return String.valueOf(nextNumber);
    }

    private long getInitialNumberByAccountType(String accountTypeCode) {
        return switch (accountTypeCode.toUpperCase()) {
            case "SAVINGS" -> 478758L;
            case "CHECKING" -> 585545L;
            default -> 100000L;
        };
    }
}
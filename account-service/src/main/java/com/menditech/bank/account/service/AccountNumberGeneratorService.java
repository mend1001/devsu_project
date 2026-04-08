package com.menditech.bank.account.service;

public interface AccountNumberGeneratorService {
    String generateNextAccountNumber(String accountTypeCode);
}

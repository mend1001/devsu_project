package com.menditech.bank.account.service;

public interface AccountNumberGenerator {
    String generateNextAccountNumber(String accountTypeCode);
}

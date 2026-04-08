package com.menditech.bank.account.service.serviceImpl;

import com.menditech.bank.account.dto.request.AccountCreateRequest;
import com.menditech.bank.account.dto.request.AccountUpdateRequest;
import com.menditech.bank.account.dto.response.AccountResponse;

import java.util.List;

public interface AccountServiceImpl {
    AccountResponse createAccount(AccountCreateRequest request);
    AccountResponse updateAccount(String accountNumber, AccountUpdateRequest request);
    void deleteAccount(String accountNumber);
    List<AccountResponse> getAccountsByClient(Long clientId);
    AccountResponse getAccountByNumber(String accountNumber);
}

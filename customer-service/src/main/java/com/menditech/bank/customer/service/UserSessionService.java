package com.menditech.bank.customer.service;

import com.menditech.bank.customer.entity.ClientEntity;

public interface UserSessionService {
    void registerSession(ClientEntity client, String token, Long expiresInSeconds);
}
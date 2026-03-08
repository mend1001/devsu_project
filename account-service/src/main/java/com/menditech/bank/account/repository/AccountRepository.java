package com.menditech.bank.account.repository;

import com.menditech.bank.account.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<AccountEntity, Long> {
    Optional<AccountEntity> findByNumber(String number);
    List<AccountEntity> findByClientId(Long clientId);
}
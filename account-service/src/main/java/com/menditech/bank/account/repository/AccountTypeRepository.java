package com.menditech.bank.account.repository;

import com.menditech.bank.account.entity.AccountTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountTypeRepository extends JpaRepository<AccountTypeEntity, Long> {
    Optional<AccountTypeEntity> findByCode(String code);
}

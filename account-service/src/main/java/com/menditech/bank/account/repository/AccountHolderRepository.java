package com.menditech.bank.account.repository;

import com.menditech.bank.account.entity.AccountHolderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountHolderRepository extends JpaRepository<AccountHolderEntity, Long> {
    List<AccountHolderEntity> findByClientId(Long clientId);
}

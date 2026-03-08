package com.menditech.bank.account.repository;

import com.menditech.bank.account.entity.TransactionChannelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionChannelRepository extends JpaRepository<TransactionChannelEntity, Long> {
    Optional<TransactionChannelEntity> findByCode(String code);
}

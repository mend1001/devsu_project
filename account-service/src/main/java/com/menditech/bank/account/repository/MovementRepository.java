package com.menditech.bank.account.repository;

import com.menditech.bank.account.entity.MovementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MovementRepository extends JpaRepository<MovementEntity, Long> {
    List<MovementEntity> findByAccountId(Long accountId);
    List<MovementEntity> findByAccountClientIdAndTransactionDateBetween(
            Long clientId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );
}

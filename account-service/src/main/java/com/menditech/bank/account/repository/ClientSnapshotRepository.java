package com.menditech.bank.account.repository;

import com.menditech.bank.account.entity.ClientSnapshotEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientSnapshotRepository extends JpaRepository<ClientSnapshotEntity, Long> {
    Optional<ClientSnapshotEntity> findByClientId(Long clientId);
}

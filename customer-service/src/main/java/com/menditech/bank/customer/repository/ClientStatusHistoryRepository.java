package com.menditech.bank.customer.repository;

import com.menditech.bank.customer.entity.ClientStatusHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientStatusHistoryRepository extends JpaRepository<ClientStatusHistoryEntity, Long> {
}

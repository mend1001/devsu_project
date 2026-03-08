package com.menditech.bank.customer.repository;

import com.menditech.bank.customer.entity.ClientEventOutboxEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientEventOutboxRepository extends JpaRepository<ClientEventOutboxEntity, Long> {

    List<ClientEventOutboxEntity> findByStatus(String status);

}
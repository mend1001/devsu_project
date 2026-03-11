package com.menditech.bank.customer.repository;

import com.menditech.bank.customer.entity.ClientEntity;
import com.menditech.bank.customer.enums.ClientStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<ClientEntity, Long> {

    Optional<ClientEntity> findByCode(String code);

    Optional<ClientEntity> findByPersonId(Long personId);

    boolean existsByCode(String code);

    long countByStatus(ClientStatus status);

    @Query(value = "SELECT nextval('customer_service.client_code_seq')", nativeQuery = true)
    Long getNextClientCodeSequence();
}
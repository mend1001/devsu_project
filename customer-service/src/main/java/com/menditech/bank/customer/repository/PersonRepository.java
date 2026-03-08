package com.menditech.bank.customer.repository;

import com.menditech.bank.customer.entity.PersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<PersonEntity, Long> {
    Optional<PersonEntity> findByIdentificationNumber(String identificationNumber);
    Optional<PersonEntity> findByEmail(String email);
    boolean existsByIdentificationNumber(String identificationNumber);
    boolean existsByEmail(String email);
}
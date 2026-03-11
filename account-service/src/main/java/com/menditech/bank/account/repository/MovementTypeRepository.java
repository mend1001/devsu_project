package com.menditech.bank.account.repository;

import com.menditech.bank.account.entity.MovementTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MovementTypeRepository extends JpaRepository<MovementTypeEntity, Long> {
    Optional<MovementTypeEntity> findByCode(String code);
}
package com.menditech.bank.customer.repository;

import com.menditech.bank.customer.entity.RoleEntity;
import com.menditech.bank.customer.enums.RoleCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByCode(RoleCode code);
}
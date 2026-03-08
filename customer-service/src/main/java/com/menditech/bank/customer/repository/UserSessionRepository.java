package com.menditech.bank.customer.repository;

import com.menditech.bank.customer.entity.UserSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserSessionRepository extends JpaRepository<UserSessionEntity, Long> {

    Optional<UserSessionEntity> findBySessionToken(String sessionToken);

}

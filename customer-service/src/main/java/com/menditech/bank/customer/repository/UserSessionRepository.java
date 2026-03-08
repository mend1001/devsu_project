package com.menditech.bank.customer.repository;

import com.menditech.bank.customer.entity.UserSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserSessionRepository extends JpaRepository<UserSessionEntity, Long> {

    Optional<UserSessionEntity> findBySessionToken(String sessionToken);

    @Modifying
    @Query("""
            update UserSessionEntity us
               set us.isActive = false,
                   us.closedAt = :closedAt,
                   us.updatedAt = :updatedAt
             where us.client.id = :clientId
               and us.isActive = true
            """)
    void deactivateActiveSessionsByClientId(@Param("clientId") Long clientId,
                                            @Param("closedAt") LocalDateTime closedAt,
                                            @Param("updatedAt") LocalDateTime updatedAt);
}

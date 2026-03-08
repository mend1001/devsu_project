package com.menditech.bank.customer.entity;

import com.menditech.bank.customer.enums.ClientStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "client", schema = "customer_service")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cli_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "per_id", nullable = false, unique = true)
    private PersonEntity person;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rol_id", nullable = false)
    private RoleEntity role;

    @Column(name = "cli_code", nullable = false, length = 30, unique = true)
    private String code;

    @Column(name = "cli_password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "cli_password_salt", length = 255)
    private String passwordSalt;

    @Enumerated(EnumType.STRING)
    @Column(name = "cli_status", nullable = false, length = 20)
    private ClientStatus status;

    @Column(name = "cli_is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "cli_last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "cli_failed_login_attempts", nullable = false)
    private Integer failedLoginAttempts;

    @Column(name = "cli_is_locked", nullable = false)
    private Boolean isLocked;

    @Column(name = "cli_locked_until")
    private LocalDateTime lockedUntil;

    @Column(name = "cli_created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "cli_updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "cli_created_by", length = 60)
    private String createdBy;

    @Column(name = "cli_updated_by", length = 60)
    private String updatedBy;
}

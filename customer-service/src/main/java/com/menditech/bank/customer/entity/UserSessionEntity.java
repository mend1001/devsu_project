package com.menditech.bank.customer.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_session", schema = "customer_service")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uss_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cli_id", nullable = false)
    private ClientEntity client;

    @Column(name = "uss_session_token", nullable = false, unique = true, length = 255)
    private String sessionToken;

    @Column(name = "uss_refresh_token", unique = true, length = 255)
    private String refreshToken;

    @Column(name = "uss_login_at", nullable = false)
    private LocalDateTime loginAt;

    @Column(name = "uss_last_activity_at")
    private LocalDateTime lastActivityAt;

    @Column(name = "uss_expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "uss_ip_address", length = 64)
    private String ipAddress;

    @Column(name = "uss_user_agent", length = 255)
    private String userAgent;

    @Column(name = "uss_device_name", length = 120)
    private String deviceName;

    @Column(name = "uss_is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "uss_closed_at")
    private LocalDateTime closedAt;

    @Column(name = "uss_created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "uss_updated_at", nullable = false)
    private LocalDateTime updatedAt;
}

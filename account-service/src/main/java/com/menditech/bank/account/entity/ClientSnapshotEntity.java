package com.menditech.bank.account.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "client_snapshot", schema = "account_service")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientSnapshotEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cls_id")
    private Long id;

    @Column(name = "cli_id", nullable = false, unique = true)
    private Long clientId;

    @Column(name = "per_id")
    private Long personId;

    @Column(name = "rol_id")
    private Long roleId;

    @Column(name = "cls_client_code", length = 30)
    private String clientCode;

    @Column(name = "cls_full_name", nullable = false, length = 180)
    private String fullName;

    @Column(name = "cls_identification_number", length = 30)
    private String identificationNumber;

    @Column(name = "cls_email", length = 120)
    private String email;

    @Column(name = "cls_phone_number", length = 30)
    private String phoneNumber;

    @Column(name = "cls_status", nullable = false, length = 20)
    private String status;

    @Column(name = "cls_is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "cls_source_event", length = 50)
    private String sourceEvent;

    @Column(name = "cls_last_event_at")
    private LocalDateTime lastEventAt;

    @Column(name = "cls_created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "cls_updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
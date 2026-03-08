package com.menditech.bank.account.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "movement_type", schema = "account_service")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovementTypeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mvt_id")
    private Long id;

    @Column(name = "mvt_code", nullable = false, unique = true, length = 30)
    private String code;

    @Column(name = "mvt_name", nullable = false, length = 50)
    private String name;

    @Column(name = "mvt_sign", nullable = false)
    private Short sign;

    @Column(name = "mvt_affects_balance", nullable = false)
    private Boolean affectsBalance;

    @Column(name = "mvt_is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "mvt_created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "mvt_updated_at", nullable = false)
    private LocalDateTime updatedAt;
}

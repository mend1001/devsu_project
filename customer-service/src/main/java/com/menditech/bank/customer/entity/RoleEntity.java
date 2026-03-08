package com.menditech.bank.customer.entity;

import com.menditech.bank.customer.enums.RoleCode;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "role", schema = "customer_service")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rol_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol_code", nullable = false, length = 30, unique = true)
    private RoleCode code;

    @Column(name = "rol_name", nullable = false, length = 60)
    private String name;

    @Column(name = "rol_description", length = 255)
    private String description;

    @Column(name = "rol_is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "rol_created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "rol_updated_at", nullable = false)
    private LocalDateTime updatedAt;
}

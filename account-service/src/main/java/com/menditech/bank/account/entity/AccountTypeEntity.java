package com.menditech.bank.account.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "account_type", schema = "account_service")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountTypeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "act_id")
    private Long id;

    @Column(name = "act_code", nullable = false, unique = true, length = 20)
    private String code;

    @Column(name = "act_name", nullable = false, length = 50)
    private String name;

    @Column(name = "act_description", length = 255)
    private String description;

    @Column(name = "act_allows_overdraft", nullable = false)
    private Boolean allowsOverdraft;

    @Column(name = "act_default_currency", nullable = false, length = 3)
    private String defaultCurrency;

    @Column(name = "act_is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "act_created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "act_updated_at", nullable = false)
    private LocalDateTime updatedAt;
}

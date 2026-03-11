package com.menditech.bank.account.entity;

import com.menditech.bank.account.enums.HolderType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "account_holder", schema = "account_service")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountHolderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ach_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "acc_id", nullable = false)
    private AccountEntity account;

    @Column(name = "cli_id", nullable = false)
    private Long clientId;

    @Enumerated(EnumType.STRING)
    @Column(name = "ach_holder_type", nullable = false, length = 20)
    private HolderType holderType;

    @Column(name = "ach_is_primary", nullable = false)
    private Boolean isPrimary;

    @Column(name = "ach_is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "ach_created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "ach_updated_at", nullable = false)
    private LocalDateTime updatedAt;
}

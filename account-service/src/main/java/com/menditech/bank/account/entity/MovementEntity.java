package com.menditech.bank.account.entity;

import com.menditech.bank.account.enums.MovementStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "movement", schema = "account_service")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mov_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "acc_id", nullable = false)
    private AccountEntity account;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mvt_id", nullable = false)
    private MovementTypeEntity movementType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tch_id")
    private TransactionChannelEntity transactionChannel;

    @Column(name = "mov_reference", nullable = false, unique = true, length = 40)
    private String reference;

    @Column(name = "mov_external_reference", length = 60)
    private String externalReference;

    @Column(name = "mov_description", length = 255)
    private String description;

    @Column(name = "mov_transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    @Column(name = "mov_posted_at", nullable = false)
    private LocalDateTime postedAt;

    @Column(name = "mov_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "mov_previous_balance", nullable = false, precision = 18, scale = 2)
    private BigDecimal previousBalance;

    @Column(name = "mov_available_balance", nullable = false, precision = 18, scale = 2)
    private BigDecimal availableBalance;

    @Column(name = "mov_currency_code", nullable = false, length = 3)
    private String currencyCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "mov_status", nullable = false, length = 20)
    private MovementStatus status;

    @Column(name = "mov_is_reverted", nullable = false)
    private Boolean isReverted;

    @Column(name = "mov_reverted_at")
    private LocalDateTime revertedAt;

    @Column(name = "mov_parent_movement_id")
    private Long parentMovementId;

    @Column(name = "mov_notes", length = 255)
    private String notes;

    @Column(name = "mov_created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "mov_updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "mov_created_by", length = 60)
    private String createdBy;

    @Column(name = "mov_updated_by", length = 60)
    private String updatedBy;
}

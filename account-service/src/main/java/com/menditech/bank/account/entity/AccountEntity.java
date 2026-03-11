package com.menditech.bank.account.entity;

import com.menditech.bank.account.enums.AccountStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "account", schema = "account_service")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "acc_id")
    private Long id;

    @Column(name = "cli_id", nullable = false)
    private Long clientId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "act_id", nullable = false)
    private AccountTypeEntity accountType;

    @Column(name = "acc_number", nullable = false, unique = true, length = 20)
    private String number;

    @Column(name = "acc_iban", unique = true, length = 34)
    private String iban;

    @Column(name = "acc_currency_code", nullable = false, length = 3)
    private String currencyCode;

    @Column(name = "acc_opened_at", nullable = false)
    private LocalDateTime openedAt;

    @Column(name = "acc_closed_at")
    private LocalDateTime closedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "acc_status", nullable = false, length = 20)
    private AccountStatus status;

    @Column(name = "acc_available_balance", nullable = false, precision = 18, scale = 2)
    private BigDecimal availableBalance;

    @Column(name = "acc_current_balance", nullable = false, precision = 18, scale = 2)
    private BigDecimal currentBalance;

    @Column(name = "acc_initial_balance", nullable = false, precision = 18, scale = 2)
    private BigDecimal initialBalance;

    @Column(name = "acc_blocked_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal blockedAmount;

    @Column(name = "acc_overdraft_limit", nullable = false, precision = 18, scale = 2)
    private BigDecimal overdraftLimit;

    @Column(name = "acc_interest_rate", precision = 8, scale = 4)
    private BigDecimal interestRate;

    @Column(name = "acc_monthly_fee", nullable = false, precision = 18, scale = 2)
    private BigDecimal monthlyFee;

    @Column(name = "acc_minimum_balance", nullable = false, precision = 18, scale = 2)
    private BigDecimal minimumBalance;

    @Column(name = "acc_statement_day")
    private Short statementDay;

    @Column(name = "acc_branch_code", length = 20)
    private String branchCode;

    @Column(name = "acc_alias", length = 50)
    private String alias;

    @Column(name = "acc_is_primary", nullable = false)
    private Boolean isPrimary;

    @Column(name = "acc_is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "acc_version", nullable = false)
    private Long version;

    @Column(name = "acc_created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "acc_updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "acc_created_by", length = 60)
    private String createdBy;

    @Column(name = "acc_updated_by", length = 60)
    private String updatedBy;
}
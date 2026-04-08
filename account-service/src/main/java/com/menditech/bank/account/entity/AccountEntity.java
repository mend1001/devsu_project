package com.menditech.bank.account.entity;

import com.menditech.bank.account.enums.AccountStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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

    @NotNull(message = "Client ID is required")
    @Column(name = "cli_id", nullable = false)
    private Long clientId;

    @NotNull(message = "Account type is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "act_id", nullable = false)
    private AccountTypeEntity accountType;

    @NotBlank(message = "Account number is required")
    @Size(max = 20, message = "Account number must not exceed 20 characters")
    @Column(name = "acc_number", nullable = false, unique = true, length = 20)
    private String number;

    @Size(max = 34, message = "IBAN must not exceed 34 characters")
    @Column(name = "acc_iban", unique = true, length = 34)
    private String iban;

    @NotBlank(message = "Currency code is required")
    @Size(min = 3, max = 3, message = "Currency code must be 3 characters")
    @Column(name = "acc_currency_code", nullable = false, length = 3)
    private String currencyCode;

    @NotNull(message = "Opened date is required")
    @Column(name = "acc_opened_at", nullable = false)
    private LocalDateTime openedAt;

    @Column(name = "acc_closed_at")
    private LocalDateTime closedAt;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "acc_status", nullable = false, length = 20)
    private AccountStatus status;

    @NotNull(message = "Available balance is required")
    @PositiveOrZero(message = "Available balance cannot be negative")
    @Column(name = "acc_available_balance", nullable = false, precision = 18, scale = 2)
    private BigDecimal availableBalance;

    @NotNull(message = "Current balance is required")
    @PositiveOrZero(message = "Current balance cannot be negative")
    @Column(name = "acc_current_balance", nullable = false, precision = 18, scale = 2)
    private BigDecimal currentBalance;

    @NotNull(message = "Initial balance is required")
    @PositiveOrZero(message = "Initial balance cannot be negative")
    @Column(name = "acc_initial_balance", nullable = false, precision = 18, scale = 2)
    private BigDecimal initialBalance;

    @NotNull(message = "Blocked amount is required")
    @PositiveOrZero(message = "Blocked amount cannot be negative")
    @Column(name = "acc_blocked_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal blockedAmount;

    @NotNull(message = "Overdraft limit is required")
    @PositiveOrZero(message = "Overdraft limit cannot be negative")
    @Column(name = "acc_overdraft_limit", nullable = false, precision = 18, scale = 2)
    private BigDecimal overdraftLimit;

    @PositiveOrZero(message = "Interest rate cannot be negative")
    @Column(name = "acc_interest_rate", precision = 8, scale = 4)
    private BigDecimal interestRate;

    @NotNull(message = "Monthly fee is required")
    @PositiveOrZero(message = "Monthly fee cannot be negative")
    @Column(name = "acc_monthly_fee", nullable = false, precision = 18, scale = 2)
    private BigDecimal monthlyFee;

    @NotNull(message = "Minimum balance is required")
    @PositiveOrZero(message = "Minimum balance cannot be negative")
    @Column(name = "acc_minimum_balance", nullable = false, precision = 18, scale = 2)
    private BigDecimal minimumBalance;

    @Min(value = 1, message = "Statement day must be between 1 and 31")
    @Max(value = 31, message = "Statement day must be between 1 and 31")
    @Column(name = "acc_statement_day")
    private Short statementDay;

    @Size(max = 20, message = "Branch code must not exceed 20 characters")
    @Column(name = "acc_branch_code", length = 20)
    private String branchCode;

    @Size(max = 50, message = "Alias must not exceed 50 characters")
    @Column(name = "acc_alias", length = 50)
    private String alias;

    @NotNull(message = "Primary flag is required")
    @Column(name = "acc_is_primary", nullable = false)
    private Boolean isPrimary;

    @NotNull(message = "Active flag is required")
    @Column(name = "acc_is_active", nullable = false)
    private Boolean isActive;

    @Version
    @Column(name = "acc_version", nullable = false)
    private Long version;

    @NotNull(message = "Created date is required")
    @Column(name = "acc_created_at", nullable = false)
    private LocalDateTime createdAt;

    @NotNull(message = "Updated date is required")
    @Column(name = "acc_updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Size(max = 60, message = "Created by must not exceed 60 characters")
    @Column(name = "acc_created_by", length = 60)
    private String createdBy;

    @Size(max = 60, message = "Updated by must not exceed 60 characters")
    @Column(name = "acc_updated_by", length = 60)
    private String updatedBy;
}
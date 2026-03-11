package com.menditech.bank.account.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponse {

    private Long accountId;
    private Long clientId;
    private String accountTypeCode;
    private String accountTypeName;
    private String accountNumber;
    private String iban;
    private String currencyCode;
    private String status;
    private BigDecimal initialBalance;
    private BigDecimal currentBalance;
    private BigDecimal availableBalance;
    private BigDecimal blockedAmount;
    private BigDecimal overdraftLimit;
    private BigDecimal interestRate;
    private BigDecimal monthlyFee;
    private BigDecimal minimumBalance;
    private Short statementDay;
    private String branchCode;
    private String alias;
    private Boolean isPrimary;
    private Boolean isActive;
    private LocalDateTime openedAt;
    private LocalDateTime closedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
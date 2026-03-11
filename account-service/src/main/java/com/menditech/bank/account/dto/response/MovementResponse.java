package com.menditech.bank.account.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovementResponse {

    private Long movementId;
    private Long accountId;
    private String accountNumber;
    private String movementTypeCode;
    private String movementTypeName;
    private String transactionChannelCode;
    private String transactionChannelName;
    private String reference;
    private String externalReference;
    private String description;
    private LocalDateTime transactionDate;
    private LocalDateTime postedAt;
    private BigDecimal amount;
    private BigDecimal previousBalance;
    private BigDecimal availableBalance;
    private String currencyCode;
    private String status;
    private Boolean isReverted;
    private LocalDateTime revertedAt;
    private Long parentMovementId;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

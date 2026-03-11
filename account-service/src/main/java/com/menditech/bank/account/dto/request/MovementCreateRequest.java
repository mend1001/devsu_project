package com.menditech.bank.account.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovementCreateRequest {

    @NotBlank(message = "Account number is required")
    @Size(max = 20, message = "Account number must not exceed 20 characters")
    private String accountNumber;

    @NotBlank(message = "Movement type code is required")
    @Size(max = 30, message = "Movement type code must not exceed 30 characters")
    private String movementTypeCode;

    @Size(max = 20, message = "Transaction channel code must not exceed 20 characters")
    private String transactionChannelCode;

    @NotNull(message = "Amount is required")
    private BigDecimal amount;

    @Size(max = 60, message = "External reference must not exceed 60 characters")
    private String externalReference;

    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;

    @Size(max = 255, message = "Notes must not exceed 255 characters")
    private String notes;
}
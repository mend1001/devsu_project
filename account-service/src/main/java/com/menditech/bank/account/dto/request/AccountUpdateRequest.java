package com.menditech.bank.account.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountUpdateRequest {

    @Size(max = 34, message = "IBAN must not exceed 34 characters")
    private String iban;

    @DecimalMin(value = "0.00", message = "Overdraft limit must be greater than or equal to zero")
    private BigDecimal overdraftLimit;

    @DecimalMin(value = "0.00", message = "Interest rate must be greater than or equal to zero")
    private BigDecimal interestRate;

    @DecimalMin(value = "0.00", message = "Monthly fee must be greater than or equal to zero")
    private BigDecimal monthlyFee;

    @DecimalMin(value = "0.00", message = "Minimum balance must be greater than or equal to zero")
    private BigDecimal minimumBalance;

    @Min(value = 1, message = "Statement day must be between 1 and 31")
    @Max(value = 31, message = "Statement day must be between 1 and 31")
    private Short statementDay;

    @Size(max = 20, message = "Branch code must not exceed 20 characters")
    private String branchCode;

    @Size(max = 50, message = "Alias must not exceed 50 characters")
    private String alias;

    @NotNull(message = "Primary flag is required")
    private Boolean isPrimary;

    @NotNull(message = "Active flag is required")
    private Boolean isActive;
}

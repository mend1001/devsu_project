package com.menditech.bank.account.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatementAccountResponse {

    private Long accountId;
    private String accountNumber;
    private String accountType;
    private String status;
    private BigDecimal initialBalance;
    private BigDecimal currentBalance;
    private BigDecimal availableBalance;
    private List<MovementResponse> movements;
}
package com.menditech.bank.account.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatementReportResponse {

    private Long clientId;
    private String clientName;
    private String identificationNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<StatementAccountResponse> accounts;
}
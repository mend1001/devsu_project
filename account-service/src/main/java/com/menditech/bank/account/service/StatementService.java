package com.menditech.bank.account.service;

import com.menditech.bank.account.dto.request.ReportRequest;
import com.menditech.bank.account.dto.response.*;
import com.menditech.bank.account.entity.AccountEntity;
import com.menditech.bank.account.mapper.MovementMapper;
import com.menditech.bank.account.repository.AccountRepository;
import com.menditech.bank.account.repository.MovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatementService {

    private final AccountRepository accountRepository;
    private final MovementRepository movementRepository;
    private final MovementMapper movementMapper;

    public StatementReportResponse generateReport(ReportRequest request) {

        List<AccountEntity> accounts = accountRepository.findByClientId(request.getClientId());

        List<StatementAccountResponse> accountResponses = accounts.stream().map(account -> {

            var movements = movementRepository
                    .findByAccountClientIdAndTransactionDateBetween(
                            request.getClientId(),
                            request.getStartDate().atStartOfDay(),
                            request.getEndDate().atTime(23,59))
                    .stream()
                    .map(movementMapper::toResponse)
                    .toList();

            return StatementAccountResponse.builder()
                    .accountId(account.getId())
                    .accountNumber(account.getNumber())
                    .accountType(account.getAccountType().getName())
                    .status(account.getStatus().name())
                    .initialBalance(account.getInitialBalance())
                    .currentBalance(account.getCurrentBalance())
                    .availableBalance(account.getAvailableBalance())
                    .movements(movements)
                    .build();

        }).toList();

        return StatementReportResponse.builder()
                .clientId(request.getClientId())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .accounts(accountResponses)
                .build();
    }
}
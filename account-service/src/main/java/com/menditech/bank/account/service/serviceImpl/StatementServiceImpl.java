package com.menditech.bank.account.service.serviceImpl;

import com.menditech.bank.account.dto.request.ReportRequest;
import com.menditech.bank.account.dto.response.MovementResponse;
import com.menditech.bank.account.dto.response.StatementAccountResponse;
import com.menditech.bank.account.dto.response.StatementReportResponse;
import com.menditech.bank.account.entity.AccountEntity;
import com.menditech.bank.account.exception.BusinessException;
import com.menditech.bank.account.exception.ResourceNotFoundException;
import com.menditech.bank.account.mapper.MovementMapper;
import com.menditech.bank.account.repository.AccountRepository;
import com.menditech.bank.account.repository.MovementRepository;
import com.menditech.bank.account.service.StatementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatementServiceImpl implements StatementService {

    private static final LocalTime END_OF_DAY = LocalTime.of(23, 59, 59);

    private final AccountRepository accountRepository;
    private final MovementRepository movementRepository;
    private final MovementMapper movementMapper;

    @Override
    @Transactional(readOnly = true)
    public StatementReportResponse generateReport(ReportRequest request) {
        validateRequest(request);
        log.info("Generating statement report for clientId: {}, from {} to {}",
                request.getClientId(), request.getStartDate(), request.getEndDate());

        List<AccountEntity> accounts = accountRepository.findByClientId(request.getClientId());
        if (accounts.isEmpty()) {
            log.warn("No accounts found for clientId: {}", request.getClientId());
            throw new ResourceNotFoundException("No accounts found for clientId: " + request.getClientId());
        }

        LocalDateTime from = request.getStartDate().atStartOfDay();
        LocalDateTime to = request.getEndDate().atTime(END_OF_DAY);

        List<MovementResponse> allMovements = fetchMovements(request.getClientId(), from, to);

        Map<Long, List<MovementResponse>> movementsByAccount = groupMovementsByAccount(allMovements);

        List<StatementAccountResponse> accountResponses = accounts.stream()
                .map(account -> buildAccountResponse(account, movementsByAccount))
                .toList();

        log.info("Report generated for clientId: {}. Accounts: {}, Movements: {}",
                request.getClientId(), accounts.size(), allMovements.size());

        return StatementReportResponse.builder()
                .clientId(request.getClientId())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .accounts(accountResponses)
                .build();
    }

    private void validateRequest(ReportRequest request) {
        if (request == null) {
            throw new BusinessException("Report request cannot be null");
        }
        if (request.getClientId() == null || request.getClientId() <= 0) {
            throw new BusinessException("Valid client ID is required");
        }
        if (request.getStartDate() == null || request.getEndDate() == null) {
            throw new BusinessException("Start date and end date are required");
        }
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BusinessException("End date cannot be before start date");
        }
    }

    private List<MovementResponse> fetchMovements(Long clientId, LocalDateTime from, LocalDateTime to) {
        return movementRepository
                .findByAccountClientIdAndTransactionDateBetween(clientId, from, to)
                .stream()
                .map(movementMapper::toResponse)
                .toList();
    }

    private Map<Long, List<MovementResponse>> groupMovementsByAccount(List<MovementResponse> movements) {
        return movements.stream()
                .filter(m -> m.getAccountId() != null)
                .collect(Collectors.groupingBy(MovementResponse::getAccountId));
    }

    private StatementAccountResponse buildAccountResponse(
            AccountEntity account,
            Map<Long, List<MovementResponse>> movementsByAccount) {

        Objects.requireNonNull(account.getId(), "Account ID cannot be null");

        return StatementAccountResponse.builder()
                .accountId(account.getId())
                .accountNumber(account.getNumber())
                .accountType(account.getAccountType() != null ? account.getAccountType().getName() : "Unknown")
                .status(account.getStatus() != null ? account.getStatus().name() : "UNKNOWN")
                .initialBalance(account.getInitialBalance())
                .currentBalance(account.getCurrentBalance())
                .availableBalance(account.getAvailableBalance())
                .movements(movementsByAccount.getOrDefault(account.getId(), Collections.emptyList()))
                .build();
    }
}
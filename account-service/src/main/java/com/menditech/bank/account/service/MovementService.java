package com.menditech.bank.account.service;

import com.menditech.bank.account.dto.request.MovementCreateRequest;
import com.menditech.bank.account.dto.response.MovementResponse;
import com.menditech.bank.account.entity.*;
import com.menditech.bank.account.enums.MovementStatus;
import com.menditech.bank.account.mapper.MovementMapper;
import com.menditech.bank.account.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MovementService {

    private final AccountRepository accountRepository;
    private final MovementTypeRepository movementTypeRepository;
    private final TransactionChannelRepository transactionChannelRepository;
    private final MovementRepository movementRepository;
    private final MovementMapper movementMapper;
    private final BalanceService balanceService;

    @Transactional
    public MovementResponse createMovement(MovementCreateRequest request) {

        AccountEntity account = accountRepository.findByNumber(request.getAccountNumber())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        MovementTypeEntity movementType = movementTypeRepository.findByCode(request.getMovementTypeCode())
                .orElseThrow(() -> new RuntimeException("Movement type not found"));

        TransactionChannelEntity channel = null;

        if (request.getTransactionChannelCode() != null) {
            channel = transactionChannelRepository.findByCode(request.getTransactionChannelCode())
                    .orElseThrow(() -> new RuntimeException("Channel not found"));
        }

        BigDecimal previousBalance = account.getCurrentBalance();

        balanceService.applyMovement(account, movementType, request.getAmount());

        MovementEntity movement = MovementEntity.builder()
                .account(account)
                .movementType(movementType)
                .transactionChannel(channel)
                .reference(UUID.randomUUID().toString())
                .externalReference(request.getExternalReference())
                .description(request.getDescription())
                .transactionDate(LocalDateTime.now())
                .postedAt(LocalDateTime.now())
                .amount(request.getAmount())
                .previousBalance(previousBalance)
                .availableBalance(account.getAvailableBalance())
                .currencyCode(account.getCurrencyCode())
                .status(MovementStatus.POSTED)
                .isReverted(false)
                .notes(request.getNotes())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy("SYSTEM")
                .updatedBy("SYSTEM")
                .build();

        movementRepository.save(movement);
        accountRepository.save(account);

        return movementMapper.toResponse(movement);
    }
}

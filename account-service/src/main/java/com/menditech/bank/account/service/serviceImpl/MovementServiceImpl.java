package com.menditech.bank.account.service.serviceImpl;

import com.menditech.bank.account.dto.request.MovementCreateRequest;
import com.menditech.bank.account.dto.response.MovementResponse;
import com.menditech.bank.account.entity.AccountEntity;
import com.menditech.bank.account.entity.MovementEntity;
import com.menditech.bank.account.entity.MovementTypeEntity;
import com.menditech.bank.account.entity.TransactionChannelEntity;
import com.menditech.bank.account.enums.MovementStatus;
import com.menditech.bank.account.exception.BusinessException;
import com.menditech.bank.account.exception.ResourceNotFoundException;
import com.menditech.bank.account.mapper.MovementMapper;
import com.menditech.bank.account.repository.AccountRepository;
import com.menditech.bank.account.repository.MovementRepository;
import com.menditech.bank.account.repository.MovementTypeRepository;
import com.menditech.bank.account.repository.TransactionChannelRepository;

import com.menditech.bank.account.service.BalanceCalculator;
import com.menditech.bank.account.service.MovementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovementServiceImpl implements MovementService {

    private static final String SYSTEM_USER = "SYSTEM";

    private final AccountRepository accountRepository;
    private final MovementTypeRepository movementTypeRepository;
    private final TransactionChannelRepository transactionChannelRepository;
    private final MovementRepository movementRepository;
    private final MovementMapper movementMapper;
    private final BalanceCalculator balanceCalculator;

    @Override
    @Transactional
    public MovementResponse createMovement(MovementCreateRequest request) {
        validateRequest(request);
        log.info("Creating movement for account: {}, type: {}",
                request.getAccountNumber(), request.getMovementTypeCode());

        LocalDateTime now = LocalDateTime.now(); // ✅ Una sola instancia de tiempo
        String actor = getCurrentUser();

        AccountEntity account = findAccount(request.getAccountNumber());
        MovementTypeEntity movementType = findMovementType(request.getMovementTypeCode());
        TransactionChannelEntity channel = findChannel(request.getTransactionChannelCode());

        BigDecimal previousBalance = account.getCurrentBalance();

        balanceCalculator.applyMovement(account, movementType, request.getAmount());

        MovementEntity movement = buildMovementEntity(
                request, account, movementType, channel,
                previousBalance, now, actor
        );

        movementRepository.save(movement);
        accountRepository.save(account);

        log.info("Movement created successfully: {}, new balance: {}",
                movement.getReference(), account.getCurrentBalance());

        return movementMapper.toResponse(movement);
    }

    private void validateRequest(MovementCreateRequest request) {
        if (request == null) {
            throw new BusinessException("Movement request cannot be null");
        }
        if (!StringUtils.hasText(request.getAccountNumber())) {
            throw new BusinessException("Account number is required");
        }
        if (!StringUtils.hasText(request.getMovementTypeCode())) {
            throw new BusinessException("Movement type code is required");
        }
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Amount must be greater than zero");
        }
    }

    private AccountEntity findAccount(String accountNumber) {
        return accountRepository.findByNumber(accountNumber)
                .orElseThrow(() -> {
                    log.error("Account not found: {}", accountNumber);
                    return new ResourceNotFoundException("Account not found: " + accountNumber);
                });
    }

    private MovementTypeEntity findMovementType(String movementTypeCode) {
        return movementTypeRepository.findByCode(movementTypeCode)
                .orElseThrow(() -> {
                    log.error("Movement type not found: {}", movementTypeCode);
                    return new ResourceNotFoundException("Movement type not found: " + movementTypeCode);
                });
    }

    private TransactionChannelEntity findChannel(String channelCode) {
        if (!StringUtils.hasText(channelCode)) {
            return null;
        }
        return transactionChannelRepository.findByCode(channelCode)
                .orElseThrow(() -> {
                    log.error("Transaction channel not found: {}", channelCode);
                    return new ResourceNotFoundException("Channel not found: " + channelCode);
                });
    }

    private MovementEntity buildMovementEntity(
            MovementCreateRequest request,
            AccountEntity account,
            MovementTypeEntity movementType,
            TransactionChannelEntity channel,
            BigDecimal previousBalance,
            LocalDateTime now,
            String actor) {

        return MovementEntity.builder()
                .account(account)
                .movementType(movementType)
                .transactionChannel(channel)
                .reference(UUID.randomUUID().toString())
                .externalReference(request.getExternalReference())
                .description(request.getDescription())
                .transactionDate(now)
                .postedAt(now)
                .amount(request.getAmount())
                .previousBalance(previousBalance)
                .availableBalance(account.getAvailableBalance())
                .currencyCode(account.getCurrencyCode())
                .status(MovementStatus.POSTED)
                .isReverted(false)
                .notes(request.getNotes())
                .createdAt(now)
                .updatedAt(now)
                .createdBy(actor)
                .updatedBy(actor)
                .build();
    }

    private String getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null && auth.getName() != null) ? auth.getName() : SYSTEM_USER;
    }
}
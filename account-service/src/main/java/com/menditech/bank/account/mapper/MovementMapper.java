package com.menditech.bank.account.mapper;

import com.menditech.bank.account.dto.response.MovementResponse;
import com.menditech.bank.account.entity.MovementEntity;
import org.springframework.stereotype.Component;

@Component
public class MovementMapper {

    public MovementResponse toResponse(MovementEntity movement) {
        return MovementResponse.builder()
                .movementId(movement.getId())
                .accountId(movement.getAccount() != null ? movement.getAccount().getId() : null)
                .accountNumber(movement.getAccount() != null ? movement.getAccount().getNumber() : null)
                .movementTypeCode(movement.getMovementType() != null ? movement.getMovementType().getCode() : null)
                .movementTypeName(movement.getMovementType() != null ? movement.getMovementType().getName() : null)
                .transactionChannelCode(movement.getTransactionChannel() != null ? movement.getTransactionChannel().getCode() : null)
                .transactionChannelName(movement.getTransactionChannel() != null ? movement.getTransactionChannel().getName() : null)
                .reference(movement.getReference())
                .externalReference(movement.getExternalReference())
                .description(movement.getDescription())
                .transactionDate(movement.getTransactionDate())
                .postedAt(movement.getPostedAt())
                .amount(movement.getAmount())
                .previousBalance(movement.getPreviousBalance())
                .availableBalance(movement.getAvailableBalance())
                .currencyCode(movement.getCurrencyCode())
                .status(movement.getStatus() != null ? movement.getStatus().name() : null)
                .isReverted(movement.getIsReverted())
                .revertedAt(movement.getRevertedAt())
                .parentMovementId(movement.getParentMovementId())
                .notes(movement.getNotes())
                .createdAt(movement.getCreatedAt())
                .updatedAt(movement.getUpdatedAt())
                .build();
    }
}

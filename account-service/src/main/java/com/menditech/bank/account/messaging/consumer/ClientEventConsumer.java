package com.menditech.bank.account.messaging.consumer;

import com.menditech.bank.account.config.RabbitMqConfig;
import com.menditech.bank.account.entity.ClientSnapshotEntity;
import com.menditech.bank.account.messaging.event.ClientCreatedEvent;
import com.menditech.bank.account.repository.ClientSnapshotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ClientEventConsumer {

    private final ClientSnapshotRepository clientSnapshotRepository;

    @RabbitListener(queues = RabbitMqConfig.CLIENT_CREATED_QUEUE)
    public void handleClientCreated(ClientCreatedEvent event) {
        ClientSnapshotEntity snapshot = ClientSnapshotEntity.builder()
                .clientId(event.getClientId())
                .personId(event.getPersonId())
                .roleId(event.getRoleId())
                .clientCode(event.getClientCode())
                .fullName(event.getFullName())
                .identificationNumber(event.getIdentificationNumber())
                .email(event.getEmail())
                .phoneNumber(event.getPhoneNumber())
                .status(event.getStatus())
                .isActive(event.getIsActive())
                .sourceEvent("client.created")
                .lastEventAt(event.getEventDate())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        clientSnapshotRepository.save(snapshot);
    }
}
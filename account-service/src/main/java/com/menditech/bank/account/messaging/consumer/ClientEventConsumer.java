package com.menditech.bank.account.messaging.consumer;

import com.menditech.bank.account.config.RabbitMqConfig;
import com.menditech.bank.account.entity.ClientSnapshotEntity;
import com.menditech.bank.account.messaging.event.ClientCreatedEvent;
import com.menditech.bank.account.repository.ClientSnapshotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientEventConsumer {

    private final ClientSnapshotRepository clientSnapshotRepository;

    @RabbitListener(queues = RabbitMqConfig.CLIENT_CREATED_QUEUE)
    public void handleClientCreated(ClientCreatedEvent event) {
        log.info("Received client.created event for clientId={}, clientCode={}",
                event.getClientId(), event.getClientCode());

        ClientSnapshotEntity snapshot = clientSnapshotRepository.findByClientId(event.getClientId())
                .orElseGet(() -> ClientSnapshotEntity.builder()
                        .clientId(event.getClientId())
                        .createdAt(LocalDateTime.now())
                        .build());

        boolean isNew = snapshot.getId() == null;

        snapshot.setPersonId(event.getPersonId());
        snapshot.setRoleId(event.getRoleId());
        snapshot.setClientCode(event.getClientCode());
        snapshot.setFullName(event.getFullName());
        snapshot.setIdentificationNumber(event.getIdentificationNumber());
        snapshot.setEmail(event.getEmail());
        snapshot.setPhoneNumber(event.getPhoneNumber());
        snapshot.setStatus(event.getStatus());
        snapshot.setIsActive(event.getIsActive());
        snapshot.setSourceEvent("client.created");
        snapshot.setLastEventAt(event.getEventDate());
        snapshot.setUpdatedAt(LocalDateTime.now());

        clientSnapshotRepository.save(snapshot);

        if (isNew) {
            log.info("Client snapshot created for clientId={}", event.getClientId());
        } else {
            log.info("Client snapshot updated for clientId={}", event.getClientId());
        }
    }
}
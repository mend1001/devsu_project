package com.menditech.bank.account.messaging.consumer;

import com.menditech.bank.account.config.RabbitMqConfig;
import com.menditech.bank.account.entity.ClientSnapshotEntity;
import com.menditech.bank.account.messaging.event.ClientCreatedEvent;
import com.menditech.bank.account.messaging.event.ClientUpdatedEvent;
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

        try {
            upsertSnapshot(event, "client.created");
            log.info("Client snapshot processed successfully for clientId={} from created event",
                    event.getClientId());
        } catch (Exception ex) {
            log.error("Error processing client.created event for clientId={}, clientCode={}. Error: {}",
                    event.getClientId(), event.getClientCode(), ex.getMessage(), ex);
            throw ex;
        }
    }

    @RabbitListener(queues = RabbitMqConfig.CLIENT_UPDATED_QUEUE)
    public void handleClientUpdated(ClientUpdatedEvent event) {
        log.info("Received client.updated event for clientId={}, clientCode={}",
                event.getClientId(), event.getClientCode());

        try {
            upsertSnapshot(event, "client.updated");
            log.info("Client snapshot processed successfully for clientId={} from updated event",
                    event.getClientId());
        } catch (Exception ex) {
            log.error("Error processing client.updated event for clientId={}, clientCode={}. Error: {}",
                    event.getClientId(), event.getClientCode(), ex.getMessage(), ex);
            throw ex;
        }
    }

    private void upsertSnapshot(Object event, String sourceEvent) {
        Long clientId = extractClientId(event);
        String clientCode = extractClientCode(event);

        ClientSnapshotEntity snapshot = clientSnapshotRepository.findByClientId(clientId)
                .orElseGet(() -> {
                    log.debug("Creating new snapshot for clientId={}", clientId);
                    return ClientSnapshotEntity.builder()
                            .clientId(clientId)
                            .createdAt(LocalDateTime.now())
                            .build();
                });

        boolean isNew = snapshot.getId() == null;

        mapCommonFields(snapshot, event);

        snapshot.setSourceEvent(sourceEvent);
        snapshot.setLastEventAt(LocalDateTime.now());
        snapshot.setUpdatedAt(LocalDateTime.now());

        clientSnapshotRepository.save(snapshot);

        if (isNew) {
            log.debug("Created new client snapshot for clientId={}", clientId);
        } else {
            log.debug("Updated existing client snapshot for clientId={}", clientId);
        }
    }

    private Long extractClientId(Object event) {
        if (event instanceof ClientCreatedEvent) {
            return ((ClientCreatedEvent) event).getClientId();
        } else if (event instanceof ClientUpdatedEvent) {
            return ((ClientUpdatedEvent) event).getClientId();
        }
        throw new IllegalArgumentException("Unknown event type: " + event.getClass());
    }

    private String extractClientCode(Object event) {
        if (event instanceof ClientCreatedEvent) {
            return ((ClientCreatedEvent) event).getClientCode();
        } else if (event instanceof ClientUpdatedEvent) {
            return ((ClientUpdatedEvent) event).getClientCode();
        }
        throw new IllegalArgumentException("Unknown event type: " + event.getClass());
    }

    private void mapCommonFields(ClientSnapshotEntity snapshot, Object event) {
        if (event instanceof ClientCreatedEvent) {
            ClientCreatedEvent e = (ClientCreatedEvent) event;
            snapshot.setPersonId(e.getPersonId());
            snapshot.setRoleId(e.getRoleId());
            snapshot.setClientCode(e.getClientCode());
            snapshot.setFullName(e.getFullName());
            snapshot.setIdentificationNumber(e.getIdentificationNumber());
            snapshot.setEmail(e.getEmail());
            snapshot.setPhoneNumber(e.getPhoneNumber());
            snapshot.setStatus(e.getStatus());
            snapshot.setIsActive(e.getIsActive());
        } else if (event instanceof ClientUpdatedEvent) {
            ClientUpdatedEvent e = (ClientUpdatedEvent) event;
            snapshot.setPersonId(e.getPersonId());
            snapshot.setRoleId(e.getRoleId());
            snapshot.setClientCode(e.getClientCode());
            snapshot.setFullName(e.getFullName());
            snapshot.setIdentificationNumber(e.getIdentificationNumber());
            snapshot.setEmail(e.getEmail());
            snapshot.setPhoneNumber(e.getPhoneNumber());
            snapshot.setStatus(e.getStatus());
            snapshot.setIsActive(e.getIsActive());
        }
    }
}
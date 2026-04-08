package com.menditech.bank.customer.messaging.producer;

import com.menditech.bank.customer.config.RabbitMqConfig;
import com.menditech.bank.customer.exception.BusinessException;
import com.menditech.bank.customer.messaging.event.ClientCreatedEvent;
import com.menditech.bank.customer.messaging.event.ClientUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishClientCreated(ClientCreatedEvent event) {
        log.info("Publishing client.created event for clientId={}", event.getClientId());
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMqConfig.BANK_EXCHANGE,
                    RabbitMqConfig.CLIENT_CREATED_ROUTING_KEY,
                    event
            );
            log.info("Successfully published client.created event for clientId={}", event.getClientId());
        } catch (Exception ex) {
            log.error("Failed to publish client.created event for clientId={}. Error: {}",
                    event.getClientId(), ex.getMessage(), ex);
            throw new BusinessException("Failed to publish client created event: " + ex.getMessage());
        }
    }

    public void publishClientUpdated(ClientUpdatedEvent event) {
        log.info("Publishing client.updated event for clientId={}", event.getClientId());
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMqConfig.BANK_EXCHANGE,
                    RabbitMqConfig.CLIENT_UPDATED_ROUTING_KEY,
                    event
            );
            log.info("Successfully published client.updated event for clientId={}", event.getClientId());
        } catch (Exception ex) {
            log.error("Failed to publish client.updated event for clientId={}. Error: {}",
                    event.getClientId(), ex.getMessage(), ex);
            throw new BusinessException("Failed to publish client updated event: " + ex.getMessage());
        }
    }
}

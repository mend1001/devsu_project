package com.menditech.bank.customer.messaging.producer;

import com.menditech.bank.customer.config.RabbitMqConfig;
import com.menditech.bank.customer.messaging.event.ClientCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClientEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishClientCreated(ClientCreatedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.BANK_EXCHANGE,
                RabbitMqConfig.CLIENT_CREATED_ROUTING_KEY,
                event
        );
    }
}

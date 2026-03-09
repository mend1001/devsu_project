package com.menditech.bank.account.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String BANK_EXCHANGE = "bank.exchange";
    public static final String CLIENT_CREATED_QUEUE = "client.created.queue";
    public static final String CLIENT_CREATED_ROUTING_KEY = "client.created";

    @Bean
    public DirectExchange bankExchange() {
        return new DirectExchange(BANK_EXCHANGE);
    }

    @Bean
    public Queue clientCreatedQueue() {
        return new Queue(CLIENT_CREATED_QUEUE, true);
    }

    @Bean
    public Binding clientCreatedBinding(Queue clientCreatedQueue, DirectExchange bankExchange) {
        return BindingBuilder.bind(clientCreatedQueue)
                .to(bankExchange)
                .with(CLIENT_CREATED_ROUTING_KEY);
    }
}
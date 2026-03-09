package com.menditech.bank.customer.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String BANK_EXCHANGE = "bank.exchange";

    public static final String CLIENT_CREATED_QUEUE = "client.created.queue";
    public static final String CLIENT_CREATED_ROUTING_KEY = "client.created";

    public static final String CLIENT_UPDATED_QUEUE = "client.updated.queue";
    public static final String CLIENT_UPDATED_ROUTING_KEY = "client.updated";

    public static final String BANK_DLX = "bank.dlx";
    public static final String CLIENT_CREATED_DLQ = "client.created.dlq";
    public static final String CLIENT_CREATED_DLQ_ROUTING_KEY = "client.created.dlq";
    public static final String CLIENT_UPDATED_DLQ = "client.updated.dlq";
    public static final String CLIENT_UPDATED_DLQ_ROUTING_KEY = "client.updated.dlq";

    @Bean
    public DirectExchange bankExchange() {
        return new DirectExchange(BANK_EXCHANGE);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(BANK_DLX);
    }

    @Bean
    public Queue clientCreatedQueue() {
        return QueueBuilder.durable(CLIENT_CREATED_QUEUE)
                .withArgument("x-dead-letter-exchange", BANK_DLX)
                .withArgument("x-dead-letter-routing-key", CLIENT_CREATED_DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue clientUpdatedQueue() {
        return QueueBuilder.durable(CLIENT_UPDATED_QUEUE)
                .withArgument("x-dead-letter-exchange", BANK_DLX)
                .withArgument("x-dead-letter-routing-key", CLIENT_UPDATED_DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue clientCreatedDlq() {
        return QueueBuilder.durable(CLIENT_CREATED_DLQ).build();
    }

    @Bean
    public Queue clientUpdatedDlq() {
        return QueueBuilder.durable(CLIENT_UPDATED_DLQ).build();
    }

    @Bean
    public Binding clientCreatedBinding(Queue clientCreatedQueue, DirectExchange bankExchange) {
        return BindingBuilder.bind(clientCreatedQueue).to(bankExchange).with(CLIENT_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding clientUpdatedBinding(Queue clientUpdatedQueue, DirectExchange bankExchange) {
        return BindingBuilder.bind(clientUpdatedQueue).to(bankExchange).with(CLIENT_UPDATED_ROUTING_KEY);
    }

    @Bean
    public Binding clientCreatedDlqBinding(Queue clientCreatedDlq, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(clientCreatedDlq).to(deadLetterExchange).with(CLIENT_CREATED_DLQ_ROUTING_KEY);
    }

    @Bean
    public Binding clientUpdatedDlqBinding(Queue clientUpdatedDlq, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(clientUpdatedDlq).to(deadLetterExchange).with(CLIENT_UPDATED_DLQ_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter);
        return rabbitTemplate;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }
}

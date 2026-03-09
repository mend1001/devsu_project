package com.menditech.bank.customer.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
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

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory,
            MessageConverter jsonMessageConverter
    ) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter);
        return rabbitTemplate;
    }
}

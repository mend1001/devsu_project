package com.menditech.bank.account.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.core.QueueBuilder;
import org.aopalliance.aop.Advice;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.aopalliance.aop.Advice;

@Configuration
public class RabbitMqConfig {

    public static final String BANK_EXCHANGE = "bank.exchange";
    public static final String CLIENT_CREATED_QUEUE = "client.created.queue";
    public static final String CLIENT_CREATED_ROUTING_KEY = "client.created";

    public static final String BANK_DLX = "bank.dlx";
    public static final String CLIENT_CREATED_DLQ = "client.created.dlq";
    public static final String CLIENT_CREATED_DLQ_ROUTING_KEY = "client.created.dlq";

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
    public Queue clientCreatedDlq() {
        return QueueBuilder.durable(CLIENT_CREATED_DLQ).build();
    }

    @Bean
    public Binding clientCreatedBinding(Queue clientCreatedQueue, DirectExchange bankExchange) {
        return BindingBuilder.bind(clientCreatedQueue)
                .to(bankExchange)
                .with(CLIENT_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding clientCreatedDlqBinding(Queue clientCreatedDlq, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(clientCreatedDlq)
                .to(deadLetterExchange)
                .with(CLIENT_CREATED_DLQ_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter jsonMessageConverter
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter);
        factory.setDefaultRequeueRejected(false);
        factory.setAdviceChain(retryInterceptor());
        return factory;
    }

    @Bean
    public Advice retryInterceptor() {
        return RetryInterceptorBuilder.stateless()
                .maxRetries(2)
                .recoverer(new RejectAndDontRequeueRecoverer())
                .build();
    }
}
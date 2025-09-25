package com.iprody.xpayment.adapter.app.checkstate;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class RabbitMqPaymentRetryConfig {

    @Value("${app.rabbitmq.queue-name}")
    private String queueName;

    @Value("${app.rabbitmq.delayed-exchange-name}")
    private String delayedExchangeName;

    @Value("${app.rabbitmq.dlx-exchange-name}")
    private String dlxExchangeName;

    @Value("${app.rabbitmq.dlx-routing-key}")
    private String dlxRoutingKey;

    @Bean
    public MessageConverter jakson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue xpaymentQueue() {
        return QueueBuilder.durable(queueName)
            .withArgument("x-dead-letter-exchange", dlxExchangeName)
            .withArgument("x-dead-letter-routing-key", dlxRoutingKey)
            .build();
    }

    @Bean
    public CustomExchange delayedExchange() {
        return new CustomExchange(delayedExchangeName, "x-delayed-message", true, false,
            Map.of("x-delayed-type", "direct"));
    }

    @Bean
    public Binding queueBinding(Queue xpaymentQueue, CustomExchange delayedExchange) {
        return BindingBuilder
            .bind(xpaymentQueue)
            .to(delayedExchange)
            .with(queueName)
            .noargs();
    }
}

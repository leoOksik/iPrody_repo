package com.iprody.xpayment.adapter.app.checkstate;

import com.iprody.xpayment.adapter.app.checkstate.handler.PaymentStatusCheckHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PaymentStateCheckListener {
    private final RabbitTemplate rabbitTemplate;
    private final String exchangeName;
    private final String routingKey;
    private final String dlxExchangeName;
    private final String dlxRoutingKey;
    private final PaymentStatusCheckHandler paymentStatusCheckHandler;

    @Value("${app.rabbitmq.max-retries:60}")
    private int maxRetries;

    @Value("${app.rabbitmq.interval-ms:60000}")
    private long intervalMs;

    @Autowired
    public PaymentStateCheckListener(
        RabbitTemplate rabbitTemplate,
        @Value("${app.rabbitmq.delayed-exchange-name}") String exchangeName,
        @Value("${app.rabbitmq.queue-name}") String routingKey,
        @Value("${app.rabbitmq.dlx-exchange-name}") String dlxExchangeName,
        @Value("${app.rabbitmq.dlx-routing-key}") String dlxRoutingKey,
        PaymentStatusCheckHandler paymentStatusCheckHandler
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchangeName = exchangeName;
        this.routingKey = routingKey;
        this.dlxExchangeName = dlxExchangeName;
        this.dlxRoutingKey = dlxRoutingKey;
        this.paymentStatusCheckHandler = paymentStatusCheckHandler;
    }

    @RabbitListener(queues = "${app.rabbitmq.queue-name}")
    public void handle(PaymentCheckStateMessage message, Message raw) {
        final MessageProperties props = raw.getMessageProperties();
        final int retryCount = (int) props.getHeaders().getOrDefault("x-retry-count", 0);

        final boolean paid = paymentStatusCheckHandler.handle(message.getChargeGuid());

        if (paid) {
            log.info("Payment {} processed", message.getPaymentGuid());
            return;
        }

        log.info("Payment {} retry {}/{}", message.getPaymentGuid(), retryCount, maxRetries);

        if (retryCount < maxRetries) {
            final PaymentCheckStateMessage newMessage = new PaymentCheckStateMessage(
                message.getChargeGuid(), message.getPaymentGuid(),
                message.getAmount(), message.getCurrency()
            );
            rabbitTemplate.convertAndSend(exchangeName, routingKey, newMessage, m -> {
                    m.getMessageProperties().setHeader("x-delay", intervalMs);
                    m.getMessageProperties().setHeader("x-retry-count", retryCount + 1);
                    return m;
                }
            );
        } else {
            log.info("Payment {} after {} retries -> dlx ({}:{})",
                message.getPaymentGuid(), retryCount, dlxExchangeName, dlxRoutingKey);
            rabbitTemplate.convertAndSend(dlxExchangeName, dlxRoutingKey, message, m -> {
                    m.getMessageProperties().setHeader("x-retry-count", retryCount);
                    m.getMessageProperties().setHeader("x-final-status", "TIMEOUT");
                    m.getMessageProperties().setHeader("x-original-queue", props.getConsumerQueue());
                    return m;
                }
            );
        }
    }
}

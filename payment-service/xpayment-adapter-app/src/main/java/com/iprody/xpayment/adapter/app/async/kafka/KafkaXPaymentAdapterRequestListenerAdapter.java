package com.iprody.xpayment.adapter.app.async.kafka;

import com.iprody.xpayment.adapter.app.async.AsyncListener;
import com.iprody.xpayment.adapter.app.async.MessageHandler;
import com.iprody.xpayment.adapter.app.async.XPaymentAdapterRequestMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class KafkaXPaymentAdapterRequestListenerAdapter implements AsyncListener<XPaymentAdapterRequestMessage> {
    private static final Logger log = LoggerFactory.getLogger(KafkaXPaymentAdapterRequestListenerAdapter.class
    );
    private final MessageHandler<XPaymentAdapterRequestMessage> handler;
    private final KafkaTemplate<String, XPaymentAdapterRequestMessage> templateDlt;

    public KafkaXPaymentAdapterRequestListenerAdapter(
        MessageHandler<XPaymentAdapterRequestMessage> handler,
        KafkaTemplate<String, XPaymentAdapterRequestMessage> templateDlt) {

        this.handler = handler;
        this.templateDlt = templateDlt;
    }

    @Override
    public void onMessage(XPaymentAdapterRequestMessage message) {
        handler.handle(message);
    }

    @KafkaListener(topics = "${app.kafka.topics.xpayment-adapter.request}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ConsumerRecord<String, XPaymentAdapterRequestMessage> record, Acknowledgment ack) {
        try {
            log.info("Received XPayment Adapter request: paymentGuid={}, partition={}, offset={}",
                record.value().getPaymentGuid(), record.partition(), record.offset());
            if (isCorrectRecord(record)) {
                onMessage(record.value());
            }
            else {
                templateDlt.send("xpayment-adapter.requests-dlt", record.value());
            }
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error handling XPayment Adapter request for paymentGuid = {}", record.value().getPaymentGuid(), e);
            throw e;
        }
    }

    private boolean isCorrectRecord(ConsumerRecord<String, XPaymentAdapterRequestMessage> record) {
        BigDecimal amount = record.value().getAmount();
        String currency = record.value().getCurrency();

        if (amount == null || amount.signum() < 0 || currency == null || currency.isEmpty()) {
            return false;
        }
        for (Currency curr : Currency.values()) {
            if (curr.name().equals(currency)) {
                return amount.scale() == curr.getValue();
            }
        }
        return false;
    }
}

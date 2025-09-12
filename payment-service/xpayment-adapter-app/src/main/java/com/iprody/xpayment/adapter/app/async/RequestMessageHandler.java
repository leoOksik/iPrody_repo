package com.iprody.xpayment.adapter.app.async;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class RequestMessageHandler implements MessageHandler<XPaymentAdapterRequestMessage> {
    private final AsyncSender<XPaymentAdapterResponseMessage> sender;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Autowired
    public RequestMessageHandler(AsyncSender<XPaymentAdapterResponseMessage> sender) {
        this.sender = sender;
    }

    @Override
    public void handle(XPaymentAdapterRequestMessage message) {

        scheduler.schedule(() -> {
            XPaymentAdapterResponseMessage responseMessage = new
                XPaymentAdapterResponseMessage();
            responseMessage.setMessageId(message.getMessageId());
            responseMessage.setPaymentGuid(message.getPaymentGuid());
            responseMessage.setAmount(message.getAmount());
            responseMessage.setCurrency(message.getCurrency());

            if(message.getAmount().remainder(BigDecimal.valueOf(2)).compareTo(BigDecimal.ZERO) == 0) {
                responseMessage.setStatus(XPaymentAdapterStatus.SUCCEEDED);
            }
            else {
                responseMessage.setStatus(XPaymentAdapterStatus.CANCELLED);
            }
            responseMessage.setTransactionRefId(UUID.randomUUID());
            responseMessage.setOccurredAt(OffsetDateTime.now());
            sender.send(responseMessage);
        }, 3, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdown();
    }
}

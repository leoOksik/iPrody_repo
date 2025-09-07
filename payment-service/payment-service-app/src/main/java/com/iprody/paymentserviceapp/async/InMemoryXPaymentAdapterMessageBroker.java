package com.iprody.paymentserviceapp.async;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Slf4j
@Service
public class InMemoryXPaymentAdapterMessageBroker implements AsyncSender<XPaymentAdapterRequestMessage> {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    private final AsyncListener<XPaymentAdapterResponseMessage> resultListener;

    @Override
    public void send(XPaymentAdapterRequestMessage request) {
        final UUID txId = UUID.randomUUID();
        log.info("Start scheduler: messageId = {}, paymentGuid = {}, txId = {},",
            request.getMessageId(), request.getPaymentGuid(), txId);

        scheduler.schedule(() -> emit(request, txId, true), 0, TimeUnit.SECONDS);
        scheduler.schedule(() -> emit(request, txId, false), 30, TimeUnit.SECONDS);
    }

    private void emit(XPaymentAdapterRequestMessage request, UUID txId, boolean isInProcessing) {
        final XPaymentAdapterResponseMessage result = new XPaymentAdapterResponseMessage();

        log.info("Request message: {}", request.toString());

        result.setMessageId(UUID.randomUUID());
        result.setPaymentGuid(request.getPaymentGuid());
        result.setOccurredAt(OffsetDateTime.now());
        result.setAmount(request.getAmount());
        result.setCurrency(request.getCurrency());

        if (isInProcessing) {
            result.setStatus(XPaymentAdapterStatus.PROCESSING);
            log.info("Response (in processing): paymentGuid = {}, status= {}",
                result.getPaymentGuid(), result.getStatus());
        } else {
            if (request.getAmount().remainder(BigDecimal.valueOf(2)).compareTo(BigDecimal.ZERO) == 0) {
                result.setStatus(XPaymentAdapterStatus.SUCCEEDED);
            } else {
                result.setStatus(XPaymentAdapterStatus.CANCELLED);
            }
        }
        result.setTransactionRefId(txId);

        log.info("Response message: {}", result);

        resultListener.onMessage(result);
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdownNow();
    }
}

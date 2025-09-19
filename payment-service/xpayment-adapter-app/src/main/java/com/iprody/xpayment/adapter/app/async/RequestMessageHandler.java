package com.iprody.xpayment.adapter.app.async;

import com.iprody.xpayment.adapter.app.api.XPaymentProviderGateway;
import com.iprody.xpayment.adapter.app.dto.CreateChargeRequestDto;
import com.iprody.xpayment.app.api.model.ChargeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class RequestMessageHandler implements MessageHandler<XPaymentAdapterRequestMessage> {

    private final XPaymentProviderGateway xPaymentProviderGateway;
    private final AsyncSender<XPaymentAdapterResponseMessage> asyncSender;

    @Override
    public void handle(XPaymentAdapterRequestMessage message) {
        log.info("Payment request received paymentGuid - {}, amount - {}, currency - {}",
            message.getPaymentGuid(), message.getAmount(), message.getCurrency());

        final CreateChargeRequestDto createChargeRequestDto = new CreateChargeRequestDto();
        createChargeRequestDto.setAmount(message.getAmount());
        createChargeRequestDto.setCurrency(message.getCurrency());
        createChargeRequestDto.setOrder(message.getPaymentGuid());

        try {
            final ChargeResponse chargeResponse = xPaymentProviderGateway.createCharge(createChargeRequestDto);
            log.info("****Payment request with paymentGuid - {} is sent for payment processing. Current status - ",
                chargeResponse.getStatus());
            final XPaymentAdapterResponseMessage responseMessage = new XPaymentAdapterResponseMessage();

            responseMessage.setMessageId(message.getMessageId());
            responseMessage.setPaymentGuid(chargeResponse.getOrder());
            responseMessage.setTransactionRefId(chargeResponse.getId());
            responseMessage.setAmount(chargeResponse.getAmount());
            responseMessage.setCurrency(chargeResponse.getCurrency());
            responseMessage.setStatus(XPaymentAdapterStatus.valueOf(chargeResponse.getStatus()));
            responseMessage.setOccurredAt(OffsetDateTime.now());
            asyncSender.send(responseMessage);
        } catch (RestClientException ex) {

            log.error("Error in time of sending payment request with paymentGuid - {}", message.getPaymentGuid(), ex);
            final XPaymentAdapterResponseMessage responseMessage = new XPaymentAdapterResponseMessage();

            responseMessage.setPaymentGuid(message.getPaymentGuid());
            responseMessage.setAmount(message.getAmount());
            responseMessage.setCurrency(message.getCurrency());
            responseMessage.setStatus(XPaymentAdapterStatus.CANCELLED);
            responseMessage.setOccurredAt(OffsetDateTime.now());
            asyncSender.send(responseMessage);
        }
    }
}
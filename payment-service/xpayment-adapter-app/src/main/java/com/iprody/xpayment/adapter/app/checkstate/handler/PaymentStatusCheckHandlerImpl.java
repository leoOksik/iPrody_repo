package com.iprody.xpayment.adapter.app.checkstate.handler;

import com.iprody.xpayment.adapter.app.api.XPaymentProviderGateway;
import com.iprody.xpayment.adapter.app.async.AsyncSender;
import com.iprody.xpayment.adapter.app.async.XPaymentAdapterResponseMessage;
import com.iprody.xpayment.adapter.app.async.XPaymentAdapterStatus;
import com.iprody.xpayment.app.api.model.ChargeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

@Component
@RequiredArgsConstructor
public class PaymentStatusCheckHandlerImpl implements PaymentStatusCheckHandler {

    private final XPaymentProviderGateway xPaymentProviderGateway;
    private final AsyncSender<XPaymentAdapterResponseMessage> asyncSender;

    @Override
    public boolean handle(UUID chargeGuid) {

        final ChargeResponse chargeResponse = xPaymentProviderGateway.retrieveCharge(chargeGuid);

        final String status = chargeResponse.getStatus();

        if (requireNonNull(status).equalsIgnoreCase(XPaymentAdapterStatus.SUCCEEDED.toString()) ||
            requireNonNull(status).equalsIgnoreCase(XPaymentAdapterStatus.CANCELLED.toString())) {

            final XPaymentAdapterResponseMessage responseMessage = new  XPaymentAdapterResponseMessage();

            responseMessage.setMessageId(UUID.randomUUID());
            responseMessage.setPaymentGuid(chargeResponse.getOrder());
            responseMessage.setTransactionRefId(chargeResponse.getId());
            responseMessage.setAmount(chargeResponse.getAmount());
            responseMessage.setCurrency(chargeResponse.getCurrency());
            responseMessage.setStatus(XPaymentAdapterStatus.valueOf(status.toUpperCase()));
            responseMessage.setOccurredAt(OffsetDateTime.now());
            asyncSender.send(responseMessage);

            return true;
        }
        return false;
    }
}

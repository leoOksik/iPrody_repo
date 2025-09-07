package com.iprody.paymentserviceapp.async;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
public class XPaymentAdapterRequestMessage implements Message {

    private UUID messageId = UUID.randomUUID();
    private UUID paymentGuid;
    private OffsetDateTime occurredAt;
    private BigDecimal amount;
    private String currency;

    @Override
    public UUID getMessageId() {
        return messageId;
    }

    @Override
    public OffsetDateTime getOccurredAt() {
        return occurredAt;
    }
}

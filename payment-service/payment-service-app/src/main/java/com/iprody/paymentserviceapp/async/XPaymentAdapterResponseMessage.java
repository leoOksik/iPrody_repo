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
public class XPaymentAdapterResponseMessage implements Message {

    private UUID messageId;
    private UUID paymentGuid;
    private OffsetDateTime occurredAt;
    private BigDecimal amount;
    private String currency;
    private UUID transactionRefId;
    private XPaymentAdapterStatus status;

    @Override
    public UUID getMessageId() {
        return messageId;
    }

    @Override
    public OffsetDateTime getOccurredAt() {
        return occurredAt;
    }
}

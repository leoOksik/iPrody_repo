package com.iprody.xpayment.adapter.app.checkstate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentCheckStateMessage {
    private UUID chargeGuid;
    private UUID paymentGuid;
    private BigDecimal amount;
    private String currency;
}

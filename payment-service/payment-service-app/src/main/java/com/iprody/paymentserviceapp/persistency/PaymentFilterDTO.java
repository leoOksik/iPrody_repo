package com.iprody.paymentserviceapp.persistency;

import com.iprody.paymentserviceapp.persistence.entity.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
public class PaymentFilterDTO {
    private String currency;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private Instant createdAtBefore;
    private Instant createdAtAfter;
    private PaymentStatus paymentStatus;
}

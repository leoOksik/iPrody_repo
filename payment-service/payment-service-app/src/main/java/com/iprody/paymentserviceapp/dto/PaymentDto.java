package com.iprody.paymentserviceapp.dto;

import com.iprody.paymentserviceapp.persistence.entity.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentDto {
    private UUID guid;
    private UUID inquiryRefId;
    private BigDecimal amount;
    private String currency;
    private UUID transactionRefId;
    private PaymentStatus status;
    private String note;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

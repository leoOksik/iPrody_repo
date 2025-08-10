package com.iprody.paymentserviceapp.dto;

import com.iprody.paymentserviceapp.persistence.entity.PaymentStatus;
import jakarta.validation.constraints.Null;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;


import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentDto {

    @Null(message = "GUID must be auto generated")
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

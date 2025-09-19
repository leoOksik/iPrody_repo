package com.iprody.xpayment.adapter.app.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@ToString
public class CreateChargeRequestDto {
    private UUID order;
    private BigDecimal amount;
    private String currency;
}

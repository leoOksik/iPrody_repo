package com.iprody.xpayment.adapter.app.async.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Currency {
    USD(2),
    EUR(2),
    JOD(3),
    JPY(0);

    private final int value;
}

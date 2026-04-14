package com.iprody.xpayment.adapter.app.checkstate.handler;

import java.util.UUID;

public interface PaymentStatusCheckHandler {
    boolean handle(UUID chargeGuid);
}

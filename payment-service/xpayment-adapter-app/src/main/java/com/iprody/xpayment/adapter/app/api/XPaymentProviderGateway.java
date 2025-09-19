package com.iprody.xpayment.adapter.app.api;

import com.iprody.xpayment.adapter.app.dto.CreateChargeRequestDto;
import com.iprody.xpayment.app.api.model.ChargeResponse;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

public interface XPaymentProviderGateway {
    ChargeResponse createCharge(CreateChargeRequestDto createChargeRequestDto) throws RestClientException;
    ChargeResponse retrieveCharge(UUID id) throws RestClientException;
}

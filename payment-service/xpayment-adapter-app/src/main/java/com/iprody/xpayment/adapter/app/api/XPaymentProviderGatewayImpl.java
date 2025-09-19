package com.iprody.xpayment.adapter.app.api;

import com.iprody.xpayment.adapter.app.dto.CreateChargeRequestDto;
import com.iprody.xpayment.adapter.app.mapper.XPaymentProviderAPIMapper;
import com.iprody.xpayment.app.api.client.DefaultApi;
import com.iprody.xpayment.app.api.model.ChargeResponse;
import com.iprody.xpayment.app.api.model.CreateChargeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class XPaymentProviderGatewayImpl implements XPaymentProviderGateway {

    private final DefaultApi defaultApi;
    private final XPaymentProviderAPIMapper mapper;

    @Override
    public ChargeResponse createCharge(CreateChargeRequestDto createChargeRequestDto) throws RestClientException {
        final CreateChargeRequest createChargeRequest = mapper.toCreateChargeRequest(createChargeRequestDto);
        return defaultApi.createCharge(createChargeRequest);
    }

    @Override
    public ChargeResponse retrieveCharge(UUID id) throws RestClientException {
        return defaultApi.retrieveCharge(id);
    }
}

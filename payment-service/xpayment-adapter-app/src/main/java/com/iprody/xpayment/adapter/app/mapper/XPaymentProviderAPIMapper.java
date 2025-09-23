package com.iprody.xpayment.adapter.app.mapper;

import com.iprody.xpayment.adapter.app.dto.CreateChargeRequestDto;
import com.iprody.xpayment.app.api.model.CreateChargeRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedSourcePolicy = ReportingPolicy.ERROR,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface XPaymentProviderAPIMapper {

    CreateChargeRequest toCreateChargeRequest(CreateChargeRequestDto createChargeRequestDto);
}

package com.iprody.paymentserviceapp.mapper;

import com.iprody.paymentserviceapp.dto.PaymentDto;
import com.iprody.paymentserviceapp.persistence.entity.Payment;
import com.iprody.paymentserviceapp.persistence.entity.PaymentStatus;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class PaymentMapperTest {

    private final PaymentMapper mapper = Mappers.getMapper(PaymentMapper.class);

    @Test
    void shouldMapEntityToDto() {
        //given
        final Payment payment = new Payment();
        final UUID guid = UUID.randomUUID();
        payment.setGuid(guid);
        payment.setAmount(BigDecimal.valueOf(456.44));
        payment.setCurrency("USD");
        payment.setTransactionRefId(UUID.randomUUID());
        payment.setStatus(PaymentStatus.DECLINED);
        payment.setNote("note");
        payment.setCreatedAt(OffsetDateTime.now().minusHours(1));
        payment.setUpdatedAt(OffsetDateTime.now());

        //when
        PaymentDto paymentDto = mapper.toDto(payment);

        //then
        assertThat(paymentDto).isNotNull();
        assertThat(paymentDto.getGuid()).isEqualTo(payment.getGuid());
        assertThat(paymentDto.getAmount()).isEqualTo(payment.getAmount());
        assertThat(paymentDto.getCurrency()).isEqualTo(payment.getCurrency());
        assertThat(paymentDto.getTransactionRefId()).isEqualTo(payment.getTransactionRefId());
        assertThat(paymentDto.getStatus()).isEqualTo(payment.getStatus());
        assertThat(paymentDto.getNote()).isEqualTo(payment.getNote());
        assertThat(paymentDto.getCreatedAt()).isEqualTo(payment.getCreatedAt());
        assertThat(paymentDto.getUpdatedAt()).isEqualTo(payment.getUpdatedAt());
    }

    @Test
    void shouldMapDtoToEntity() {
        //given
        final UUID guid = UUID.randomUUID();
        final UUID inquiryRefId = UUID.randomUUID();
        final UUID transactionRefId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now().minusHours(2);
        OffsetDateTime updatedAt = OffsetDateTime.now();
        final PaymentDto paymentDto = new PaymentDto(
                guid,
                inquiryRefId,
                new BigDecimal("307.44"),
                "EUR",
                transactionRefId,
                PaymentStatus.RECEIVED,
                "note",
                createdAt,
                updatedAt
        );

        //when
        Payment entity = mapper.toEntity(paymentDto);

        //then
        assertThat(entity).isNotNull();
        assertThat(entity.getGuid()).isEqualTo(paymentDto.getGuid());
        assertThat(entity.getAmount()).isEqualTo(paymentDto.getAmount());
        assertThat(entity.getCurrency()).isEqualTo(paymentDto.getCurrency());
        assertThat(entity.getTransactionRefId()).isEqualTo(paymentDto.getTransactionRefId());
        assertThat(entity.getStatus()).isEqualTo(paymentDto.getStatus());
        assertThat(entity.getNote()).isEqualTo(paymentDto.getNote());
        assertThat(entity.getCreatedAt()).isEqualTo(paymentDto.getCreatedAt());
        assertThat(entity.getUpdatedAt()).isEqualTo(paymentDto.getUpdatedAt());
    }
}

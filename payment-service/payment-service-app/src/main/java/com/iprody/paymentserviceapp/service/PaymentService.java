package com.iprody.paymentserviceapp.service;

import com.iprody.paymentserviceapp.dto.PaymentDto;
import com.iprody.paymentserviceapp.persistence.PaymentFilterDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface PaymentService {
    List<PaymentDto> getPayments();

    List<PaymentDto> search(PaymentFilterDTO paymentFilter);

    PaymentDto create(PaymentDto dto);

    PaymentDto getPayment(UUID guid);

    Page<PaymentDto> searchPaged(PaymentFilterDTO paymentFilter, Pageable pageable);

    PaymentDto update(UUID guid, PaymentDto paymentDto);

    void delete(UUID guid);

    PaymentDto updateNote(UUID guid, String note);

}

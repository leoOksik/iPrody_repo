package com.iprody.paymentserviceapp.service;

import com.iprody.paymentserviceapp.dto.PaymentDto;
import com.iprody.paymentserviceapp.persistence.PaymentFilterDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface PaymentService {
    List<PaymentDto> getPayments();

    PaymentDto getPayment(UUID guid);

    List<PaymentDto> search(PaymentFilterDTO paymentFilter);

    Page<PaymentDto> searchPaged(PaymentFilterDTO paymentFilter, Pageable pageable);

}

package com.iprody.paymentserviceapp.service;

import com.iprody.paymentserviceapp.persistence.entity.Payment;
import com.iprody.paymentserviceapp.persistency.PaymentFilterDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface PaymentService {
    List<Payment> getPayments();

    Payment getPayment(UUID guid);

    List<Payment> search(PaymentFilterDTO paymentFilter);

    Page<Payment> searchPaged(PaymentFilterDTO paymentFilter, Pageable pageable);

}

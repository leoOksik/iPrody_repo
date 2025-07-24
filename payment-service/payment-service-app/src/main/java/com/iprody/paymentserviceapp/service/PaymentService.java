package com.iprody.paymentserviceapp.service;

import com.iprody.paymentserviceapp.persistence.entity.Payment;

import java.util.List;
import java.util.UUID;

public interface PaymentService {
    List<Payment> getPayments();

    Payment getPayment(UUID guid);
}

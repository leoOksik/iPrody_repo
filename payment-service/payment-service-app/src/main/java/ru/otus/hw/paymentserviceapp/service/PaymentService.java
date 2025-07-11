package ru.otus.hw.paymentserviceapp.service;

import ru.otus.hw.paymentserviceapp.model.Payment;

import java.util.List;

public interface PaymentService {
    List<Payment> getPayments();

    Payment getPayment(long id);
}

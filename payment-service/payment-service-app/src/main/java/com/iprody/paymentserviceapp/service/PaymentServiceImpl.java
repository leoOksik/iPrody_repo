package com.iprody.paymentserviceapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.iprody.paymentserviceapp.persistence.entity.Payment;
import com.iprody.paymentserviceapp.persistency.PaymentRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Override
    public List<Payment> getPayments() {
        return paymentRepository.findAll();
    }

    @Override
    public Payment getPayment(UUID guid) {
        return paymentRepository.findById(guid)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + guid));
    }
}

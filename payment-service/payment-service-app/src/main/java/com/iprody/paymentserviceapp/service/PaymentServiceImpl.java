package com.iprody.paymentserviceapp.service;

import com.iprody.paymentserviceapp.persistency.PaymentFilterDTO;
import com.iprody.paymentserviceapp.persistency.PaymentFilterFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Override
    public List<Payment> search(PaymentFilterDTO paymentFilter) {
        return paymentRepository.findAll(PaymentFilterFactory.fromFilter(paymentFilter));
    }

    @Override
    public Page<Payment> searchPaged(PaymentFilterDTO paymentFilter, Pageable pageable) {
        return paymentRepository.findAll(PaymentFilterFactory.fromFilter(paymentFilter), pageable);
    }
}

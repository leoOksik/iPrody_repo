package com.iprody.paymentserviceapp.service;

import com.iprody.paymentserviceapp.dto.PaymentDto;
import com.iprody.paymentserviceapp.mapper.PaymentMapper;
import com.iprody.paymentserviceapp.persistence.PaymentFilterDTO;
import com.iprody.paymentserviceapp.persistence.PaymentFilterFactory;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.iprody.paymentserviceapp.persistence.PaymentRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    @Override
    public List<PaymentDto> getPayments() {
        return paymentRepository.findAll().stream().map(paymentMapper::toDto).toList();
    }

    @Override
    public PaymentDto getPayment(UUID guid) {
        return paymentRepository.findById(guid)
            .map(paymentMapper::toDto)
            .orElseThrow(() -> new EntityNotFoundException("Payment not found: " + guid));
    }

    @Override
    public List<PaymentDto> search(PaymentFilterDTO paymentFilter) {
        return paymentRepository.findAll(PaymentFilterFactory.fromFilter(paymentFilter))
            .stream().map(paymentMapper::toDto).toList();
    }

    @Override
    public Page<PaymentDto> searchPaged(PaymentFilterDTO paymentFilter, Pageable pageable) {
        return paymentRepository.findAll(PaymentFilterFactory.fromFilter(paymentFilter), pageable)
            .map(paymentMapper::toDto);
    }
}

package com.iprody.paymentserviceapp.service;

import com.iprody.paymentserviceapp.dto.PaymentDto;
import com.iprody.paymentserviceapp.mapper.PaymentMapper;
import com.iprody.paymentserviceapp.persistence.PaymentFilterDTO;
import com.iprody.paymentserviceapp.persistence.PaymentFilterFactory;
import com.iprody.paymentserviceapp.persistence.PaymentRepository;
import com.iprody.paymentserviceapp.persistence.entity.Payment;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
    public List<PaymentDto> search(PaymentFilterDTO paymentFilter) {
        return paymentRepository.findAll(PaymentFilterFactory.fromFilter(paymentFilter))
            .stream().map(paymentMapper::toDto).toList();
    }

    @Override
    public PaymentDto create(PaymentDto dto) {
        if (dto.getGuid() != null) {
            throw new IllegalArgumentException("GUID must be generated automatically");
        }
        final Payment entity = paymentMapper.toEntity(dto);
        entity.setGuid(UUID.randomUUID());
        final Payment saved = paymentRepository.save(entity);
        return paymentMapper.toDto(saved);
    }

    @Override
    public PaymentDto getPayment(UUID guid) {
        return paymentRepository.findById(guid)
            .map(paymentMapper::toDto)
            .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + guid));
    }

    @Override
    public Page<PaymentDto> searchPaged(PaymentFilterDTO paymentFilter, Pageable pageable) {
        return paymentRepository.findAll(PaymentFilterFactory.fromFilter(paymentFilter), pageable)
            .map(paymentMapper::toDto);
    }

    @Override
    public PaymentDto update(UUID guid, PaymentDto paymentDto) {
        if (!paymentRepository.existsById(guid)) {
            throw new EntityNotFoundException("Payment not found: " + guid);
        }
        final Payment updatedPayment = paymentMapper.toEntity(paymentDto);
        updatedPayment.setGuid(guid);
        final Payment savedPayment = paymentRepository.save(updatedPayment);
        return paymentMapper.toDto(savedPayment);
    }

    @Override
    public void delete(UUID guid) {
        if (!paymentRepository.existsById(guid)) {
            throw new EntityNotFoundException("Payment not found: " + guid);
        }
        paymentRepository.deleteById(guid);
    }

    @Override
    public PaymentDto updateNote(UUID guid, String note) {
        final Payment payment = paymentRepository.findById(guid)
            .orElseThrow(() -> new EntityNotFoundException("Payment not found: " + guid));

        payment.setNote(note);
        final Payment savedPayment = paymentRepository.save(payment);
        return paymentMapper.toDto(savedPayment);
    }
}

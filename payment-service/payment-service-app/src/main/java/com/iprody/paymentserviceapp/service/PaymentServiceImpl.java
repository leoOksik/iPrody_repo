package com.iprody.paymentserviceapp.service;

import com.iprody.paymentserviceapp.async.AsyncSender;
import com.iprody.paymentserviceapp.async.XPaymentAdapterRequestMessage;
import com.iprody.paymentserviceapp.dto.PaymentDto;
import com.iprody.paymentserviceapp.exception.EntityNotFoundException;
import com.iprody.paymentserviceapp.exception.OperationError;
import com.iprody.paymentserviceapp.mapper.PaymentMapper;
import com.iprody.paymentserviceapp.mapper.XPaymentAdapterMapper;
import com.iprody.paymentserviceapp.persistence.PaymentFilterDTO;
import com.iprody.paymentserviceapp.persistence.PaymentFilterFactory;
import com.iprody.paymentserviceapp.persistence.PaymentRepository;
import com.iprody.paymentserviceapp.persistence.entity.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final XPaymentAdapterMapper xPaymentAdapterMapper;
    private final AsyncSender<XPaymentAdapterRequestMessage> sender;

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
        final Payment entity = paymentMapper.toEntity(dto);
        entity.setGuid(UUID.randomUUID());
        entity.setCreatedAt(OffsetDateTime.now());
        entity.setUpdatedAt(OffsetDateTime.now());
        entity.setInquiryRefId(UUID.randomUUID());
        final Payment saved = paymentRepository.save(entity);
        final PaymentDto resultDto = paymentMapper.toDto(saved);

        final XPaymentAdapterRequestMessage requestMessage = xPaymentAdapterMapper
            .toXPaymentAdapterRequestMessage(entity);

        sender.send(requestMessage);
        return resultDto;
    }

    @Override
    public PaymentDto getPayment(UUID guid) {
        return paymentRepository.findById(guid)
            .map(paymentMapper::toDto)
            .orElseThrow(() -> new EntityNotFoundException(Payment.class, guid, OperationError.FIND_BY_ID_OP));
    }

    @Override
    public Page<PaymentDto> searchPaged(PaymentFilterDTO paymentFilter, Pageable pageable) {
        return paymentRepository.findAll(PaymentFilterFactory.fromFilter(paymentFilter), pageable)
            .map(paymentMapper::toDto);
    }

    @Override
    public PaymentDto update(UUID guid, PaymentDto paymentDto) {
        if (!paymentRepository.existsById(guid)) {
            throw new EntityNotFoundException(Payment.class, guid, OperationError.UPDATE_OP);
        }
        final Payment updatedPayment = paymentMapper.toEntity(paymentDto);
        updatedPayment.setGuid(guid);
        final Payment savedPayment = paymentRepository.save(updatedPayment);
        return paymentMapper.toDto(savedPayment);
    }

    @Override
    public void delete(UUID guid) {
        if (!paymentRepository.existsById(guid)) {
            throw new EntityNotFoundException(Payment.class, guid, OperationError.DELETE_OP);
        }
        paymentRepository.deleteById(guid);
    }

    @Override
    public PaymentDto updateNote(UUID guid, String note) {
        final Payment payment = paymentRepository.findById(guid)
            .orElseThrow(() -> new EntityNotFoundException(Payment.class, guid, OperationError.UPDATE_OP));

        payment.setNote(note);
        final Payment savedPayment = paymentRepository.save(payment);
        return paymentMapper.toDto(savedPayment);
    }
}

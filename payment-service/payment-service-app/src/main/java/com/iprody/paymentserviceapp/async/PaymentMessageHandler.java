package com.iprody.paymentserviceapp.async;

import com.iprody.paymentserviceapp.exception.EntityNotFoundException;
import com.iprody.paymentserviceapp.exception.OperationError;
import com.iprody.paymentserviceapp.persistence.PaymentRepository;
import com.iprody.paymentserviceapp.persistence.entity.Payment;
import com.iprody.paymentserviceapp.persistence.entity.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentMessageHandler implements MessageHandler<XPaymentAdapterResponseMessage> {
    private final PaymentRepository paymentRepository;

    @Override
    public void handle(XPaymentAdapterResponseMessage message) {
        log.info("New response message with id {}, status {}",
            message.getMessageId(), message.getStatus());

        final UUID paymentId = message.getPaymentGuid();

        final Payment payment = paymentRepository.findById(paymentId).orElseThrow(() ->
        new EntityNotFoundException(Payment.class, paymentId, OperationError.FIND_BY_ID_OP));

        log.info("Payment fields before update: status = {}; transactionRefId = {}; updatedAt={}",
            payment.getStatus(), payment.getTransactionRefId(), payment.getUpdatedAt());

        payment.setStatus(PaymentStatus.valueOf(message.getStatus().name()));
        payment.setTransactionRefId(message.getTransactionRefId());
        payment.setUpdatedAt(message.getOccurredAt());

        final Payment updatedPayment = paymentRepository.save(payment);

        log.info("Updated payment fields: status = {}; transactionRefId = {}; updatedAt={}",
            updatedPayment.getStatus(), updatedPayment.getTransactionRefId(), updatedPayment.getUpdatedAt());
    }
}

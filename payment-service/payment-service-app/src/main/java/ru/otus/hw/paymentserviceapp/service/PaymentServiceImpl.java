package ru.otus.hw.paymentserviceapp.service;

import org.springframework.stereotype.Service;
import ru.otus.hw.paymentserviceapp.model.Payment;

import java.util.*;


@Service
public class PaymentServiceImpl implements PaymentService {

    private final Map<Long, Payment> payments;

    public PaymentServiceImpl() {
        payments = new HashMap<>();
        addPayment();
    }

    @Override
    public List<Payment> getPayments() {
        return new ArrayList<>(payments.values());
    }

    @Override
    public Payment getPayment(long id) {
        return payments.get(id);
    }

    public void addPayment() {
        final Random random = new Random();

        for (int i = 1; i < 6; i++) {
            final double value = Math.round((random.nextDouble() * 1000) * 100.0) / 100.0;
            payments.put((long) i, new Payment(i, value));
        }
    }
}

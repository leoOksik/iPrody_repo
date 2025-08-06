package com.iprody.paymentserviceapp.persistence;

import com.iprody.paymentserviceapp.persistence.entity.Payment;
import org.springframework.data.jpa.domain.Specification;

public final class PaymentFilterFactory {
    private static final Specification<Payment> EMPTY = ((root, query, criteriaBuilder) -> null);

    public static Specification<Payment> fromFilter(PaymentFilterDTO paymentFilter) {
        Specification<Payment> spec = EMPTY;

        if (paymentFilter.getCurrency() != null) {
            spec = spec.and(PaymentSpecifications.hasCurrency(paymentFilter.getCurrency()));
        }

        if (paymentFilter.getMinAmount() != null && paymentFilter.getMaxAmount() != null) {
            spec = spec.and(PaymentSpecifications.amountBetween(
                    paymentFilter.getMinAmount(), paymentFilter.getMaxAmount()));
        } else {
            if (paymentFilter.getMinAmount() != null) {
                spec = spec.and(PaymentSpecifications.minAmount(paymentFilter.getMinAmount()));
            }
            if (paymentFilter.getMaxAmount() != null) {
                spec = spec.and(PaymentSpecifications.maxAmount(paymentFilter.getMaxAmount()));
            }
        }

        if (paymentFilter.getCreatedAtAfter() != null && paymentFilter.getCreatedAtBefore() != null) {
            spec = spec.and(PaymentSpecifications.createdBetween(
                    paymentFilter.getCreatedAtAfter(), paymentFilter.getCreatedAtBefore()));
        } else {
            if (paymentFilter.getCreatedAtAfter() != null) {
                spec = spec.and(PaymentSpecifications.createdAtAfter(paymentFilter.getCreatedAtAfter()));
            }
            if (paymentFilter.getCreatedAtBefore() != null) {
                spec = spec.and(PaymentSpecifications.createdAtBefore(paymentFilter.getCreatedAtBefore()));
            }
        }

        if (paymentFilter.getPaymentStatus() != null) {
            spec = spec.and(PaymentSpecifications.hasStatus(paymentFilter.getPaymentStatus()));
        }
        return spec;
    }
}

package com.iprody.paymentserviceapp.persistency;

import org.springframework.data.jpa.repository.JpaRepository;
import com.iprody.paymentserviceapp.persistence.entity.Payment;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID>, JpaSpecificationExecutor<Payment> {
}

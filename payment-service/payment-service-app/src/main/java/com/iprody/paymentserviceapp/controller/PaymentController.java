package com.iprody.paymentserviceapp.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import com.iprody.paymentserviceapp.persistence.entity.Payment;
import com.iprody.paymentserviceapp.service.PaymentService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/payments")
@AllArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("")
    public List<Payment> getPayments() {
        return paymentService.getPayments();
    }

    @GetMapping("/{guid}")
    public Payment getPayment(@PathVariable UUID guid) {
        return paymentService.getPayment(guid);
    }
}
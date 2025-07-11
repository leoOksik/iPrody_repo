package ru.otus.hw.paymentserviceapp.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.otus.hw.paymentserviceapp.model.Payment;
import ru.otus.hw.paymentserviceapp.service.PaymentService;

import java.util.List;

@RestController
@RequestMapping("/payments")
@AllArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("")
    public List<Payment> getPayments() {
        return paymentService.getPayments();
    }

    @GetMapping("/{id}")
    public Payment getPayment(@PathVariable long id) {
        return paymentService.getPayment(id);
    }
}

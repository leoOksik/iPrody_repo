package com.iprody.paymentserviceapp.controller;

import com.iprody.paymentserviceapp.dto.PaymentDto;
import com.iprody.paymentserviceapp.dto.PaymentNoteUpdateDto;
import com.iprody.paymentserviceapp.persistence.PaymentFilterDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import com.iprody.paymentserviceapp.service.PaymentService;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@AllArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("all")
    public List<PaymentDto> getPayments() {
        return paymentService.getPayments();
    }

    @GetMapping("/{guid}")
    @ResponseStatus(HttpStatus.OK)
    public PaymentDto getPayment(@PathVariable UUID guid) {
        return paymentService.getPayment(guid);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public Page<PaymentDto> searchPayments(
        @ModelAttribute PaymentFilterDTO paymentFilter,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size,
        @RequestParam(defaultValue = "amount") String sortBy,
        @RequestParam(defaultValue = "desc") String direction) {

        if (!sortBy.equals("createdAt") && !sortBy.equals("amount")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Only sort by amount or createdAt");
        }
        if (!direction.equals("desc") && !direction.equals("asc")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Only desc or asc sorting");
        }

        final Sort sort;
        if (direction.equalsIgnoreCase("desc")) {
            sort = Sort.by(sortBy).descending();
        } else {
            sort = Sort.by(sortBy).ascending();
        }
        return paymentService.searchPaged(paymentFilter, PageRequest.of(page, size, sort));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentDto create(@RequestBody @Valid PaymentDto dto) {
        return paymentService.create(dto);
    }

    @PutMapping("/{guid}")
    @ResponseStatus(HttpStatus.OK)
    public PaymentDto update(@PathVariable UUID guid, @RequestBody PaymentDto dto) {
        return paymentService.update(guid, dto);
    }

    @DeleteMapping("/{guid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID guid) {
        paymentService.delete(guid);
    }

    @PatchMapping("/{guid}/note")
    @ResponseStatus(HttpStatus.OK)
    public PaymentDto updateNote(@PathVariable UUID guid, @RequestBody @Valid PaymentNoteUpdateDto noteDto) {
        return paymentService.updateNote(guid, noteDto.getNote());
    }
}

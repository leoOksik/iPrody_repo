package com.iprody.paymentserviceapp.controller;

import com.iprody.paymentserviceapp.dto.PaymentDto;
import com.iprody.paymentserviceapp.dto.PaymentNoteUpdateDto;
import com.iprody.paymentserviceapp.persistence.PaymentFilterDTO;
import com.iprody.paymentserviceapp.service.PaymentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@AllArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('admin', 'reader')")
    public List<PaymentDto> getPayments() {
        log.info("GET all payments");
        final List<PaymentDto> payments = paymentService.getPayments();
        log.debug("Sending response payments list with size: {}", payments.size());
        return payments;
    }

    @GetMapping("/{guid}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('admin', 'reader')")
    public PaymentDto getPayment(@PathVariable UUID guid) {
        log.info("GET payment by id: {}", guid);
        final PaymentDto paymentDto = paymentService.getPayment(guid);
        log.debug("Sending response PaymentDto: {}", paymentDto);
        return paymentDto;
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('admin', 'reader')")
    public Page<PaymentDto> searchPayments(
        @ModelAttribute PaymentFilterDTO paymentFilter,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size,
        @RequestParam(defaultValue = "amount") String sortBy,
        @RequestParam(defaultValue = "desc") String direction) {

        log.info("GET payments by filter: {}. Sorting by field {} with direction sort {}",
            paymentFilter, sortBy, direction);

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

        final Page<PaymentDto> paymentDtoPages = paymentService.searchPaged(paymentFilter,
            PageRequest.of(page, size, sort));

        log.debug("Sending response payments list with total elements = {} and total pages = {}",
            paymentDtoPages.getTotalElements(), paymentDtoPages.getTotalPages());

        return paymentDtoPages;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('admin')")
    public PaymentDto create(@RequestBody @Valid PaymentDto dto) {
        log.info("Create payment with request data -> {}", dto);
        final PaymentDto paymentDto = paymentService.create(dto);
        log.debug("Created payment with id: {}", paymentDto.getGuid());
        return paymentDto;
    }

    @PutMapping("/{guid}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('admin')")
    public PaymentDto update(@PathVariable UUID guid, @RequestBody PaymentDto dto) {
        log.info("Update payment with request id = {} and data -> {}", guid, dto);
        final PaymentDto paymentDto = paymentService.update(guid, dto);
        log.debug("Updated payment with id: {}", paymentDto.getGuid());
        return paymentDto;
    }

    @DeleteMapping("/{guid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('admin')")
    public void delete(@PathVariable UUID guid) {
        log.info("Delete payment with request id: {}", guid);
        paymentService.delete(guid);
        log.debug("Deleted payment with id: {}", guid);
    }

    @PatchMapping("/{guid}/note")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('admin')")
    public PaymentDto updateNote(@PathVariable UUID guid, @RequestBody @Valid PaymentNoteUpdateDto noteDto) {
        log.info("Update payment note with request id = {} and data -> {}", guid, noteDto);
        final PaymentDto paymentDto = paymentService.updateNote(guid, noteDto.getNote());
        log.debug("Updated payment note with id = {}. Set new note -> {}", paymentDto.getGuid(), paymentDto.getNote());
        return paymentDto;
    }
}

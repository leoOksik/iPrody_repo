package com.iprody.paymentserviceapp.service;

import com.iprody.paymentserviceapp.dto.PaymentDto;
import com.iprody.paymentserviceapp.mapper.PaymentMapper;
import com.iprody.paymentserviceapp.persistence.PaymentFilterDTO;
import com.iprody.paymentserviceapp.persistence.PaymentRepository;
import com.iprody.paymentserviceapp.persistence.entity.Payment;
import com.iprody.paymentserviceapp.persistence.entity.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private PaymentMapper paymentMapper;
    @InjectMocks
    private PaymentServiceImpl paymentService;

    Payment payment;
    PaymentDto paymentDto;
    UUID guid;
    UUID inquiryRefId;
    UUID transactionRefId;
    OffsetDateTime createdAt;
    OffsetDateTime updatedAt;

    @BeforeEach
    void setUp() {
        guid = UUID.randomUUID();
        inquiryRefId = UUID.randomUUID();
        transactionRefId = UUID.randomUUID();
        createdAt = OffsetDateTime.now().minusHours(1);
        updatedAt = OffsetDateTime.now();
        payment = new Payment();

        payment.setGuid(guid);
        payment.setInquiryRefId(inquiryRefId);
        payment.setAmount(BigDecimal.valueOf(456.44));
        payment.setCurrency("USD");
        payment.setTransactionRefId(transactionRefId);
        payment.setStatus(PaymentStatus.DECLINED);
        payment.setNote("note");
        payment.setCreatedAt(createdAt);
        payment.setUpdatedAt(updatedAt);

        paymentDto = new PaymentDto();
        paymentDto.setGuid(payment.getGuid());
        paymentDto.setInquiryRefId(payment.getInquiryRefId());
        paymentDto.setAmount(payment.getAmount());
        paymentDto.setCurrency(payment.getCurrency());
        paymentDto.setTransactionRefId(payment.getTransactionRefId());
        paymentDto.setStatus(payment.getStatus());
        paymentDto.setNote(payment.getNote());
        paymentDto.setCreatedAt(payment.getCreatedAt());
        paymentDto.setUpdatedAt(payment.getUpdatedAt());

    }

    @Test
    void shouldReturnPaymentById() {
        //given
        when(paymentRepository.findById(guid)).thenReturn(Optional.of(payment));
        when(paymentMapper.toDto(payment)).thenReturn(paymentDto);

        //when
        PaymentDto result = paymentService.getPayment(guid);

        //then
        assertEquals(guid, result.getGuid());
        assertEquals(inquiryRefId, result.getInquiryRefId());
        assertEquals(BigDecimal.valueOf(456.44), result.getAmount());
        assertEquals("USD", result.getCurrency());
        assertEquals(transactionRefId, result.getTransactionRefId());
        assertEquals(PaymentStatus.DECLINED, result.getStatus());
        assertEquals("note", result.getNote());
        assertEquals(createdAt, result.getCreatedAt());
        assertEquals(updatedAt, result.getUpdatedAt());
    }

    @ParameterizedTest
    @MethodSource("statusProvider")
    void shouldMapDifferentPaymentStatuses(PaymentStatus status) {
        //given
        payment.setStatus(status);
        paymentDto.setStatus(status);
        when(paymentRepository.findById(guid)).thenReturn(Optional.of(payment));
        when(paymentMapper.toDto(payment)).thenReturn(paymentDto);

        //when
        PaymentDto result = paymentService.getPayment(guid);

        //then
        assertEquals(status, result.getStatus());
    }

    static Stream<PaymentStatus> statusProvider() {
        return Stream.of(
                PaymentStatus.RECEIVED,
                PaymentStatus.PENDING,
                PaymentStatus.APPROVED,
                PaymentStatus.DECLINED,
                PaymentStatus.NOT_SENT
        );
    }

    @ParameterizedTest
    @MethodSource("filterProvider")
    void shouldSearchPaymentsByFilterField(PaymentFilterDTO filter) {
        // given
        when(paymentRepository.findAll(any(Specification.class))).thenReturn(List.of(payment));
        when(paymentMapper.toDto(payment)).thenReturn(paymentDto);

        // when
        PaymentDto result = paymentService.search(filter).get(0);

        // then
        assertThat(result).isNotNull();
        if (filter.getCurrency() != null) {
            assertThat(result.getCurrency()).isEqualTo(filter.getCurrency());
        }
        if (filter.getMinAmount() != null) {
            assertThat(result.getAmount()).isGreaterThanOrEqualTo(filter.getMinAmount());
        }
        if (filter.getMaxAmount() != null) {
            assertThat(result.getAmount()).isLessThanOrEqualTo(filter.getMaxAmount());
        }
        if (filter.getCreatedAtAfter() != null) {
            assertThat(result.getCreatedAt().toInstant()).isAfter(filter.getCreatedAtAfter());
        }
        if (filter.getCreatedAtBefore() != null) {
            assertThat(result.getCreatedAt().toInstant()).isBefore(filter.getCreatedAtBefore());
        }
    }

    static Stream<PaymentFilterDTO> filterProvider() {
        return Stream.of(
                PaymentFilterDTO.builder().currency("USD").build(),
                PaymentFilterDTO.builder().minAmount(BigDecimal.valueOf(167.77)).build(),
                PaymentFilterDTO.builder().maxAmount(BigDecimal.valueOf(467.77)).build(),
                PaymentFilterDTO.builder().createdAtAfter(Instant.now().minusSeconds(5400)).build(),
                PaymentFilterDTO.builder().createdAtBefore(Instant.now().plusSeconds(5400)).build()
        );
    }

    @ParameterizedTest
    @MethodSource("sortProvider")
    void shouldSortPaymentsByChooseFields(Sort sortBy,
                                          Comparator<Payment> comparatorEntity,
                                          Comparator<PaymentDto> comparatorDto) {
        // given
        Payment payment2 = new Payment();
        payment2.setAmount(BigDecimal.valueOf(756.4));
        payment2.setCreatedAt(OffsetDateTime.now().plusHours(4));

        PaymentDto paymentDto2 = new PaymentDto();
        paymentDto2.setAmount(payment2.getAmount());
        paymentDto2.setCreatedAt(payment2.getCreatedAt());

        List<Payment> paymentList = List.of(payment, payment2);
        List<PaymentDto> paymentDtoList = List.of(paymentDto, paymentDto2);
        List<PaymentDto> expectedList = paymentDtoList.stream().sorted(comparatorDto).toList();

        Pageable pageable = PageRequest.of(0, 1, sortBy);

        when(paymentRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(
                new PageImpl<>(paymentList.stream().sorted(comparatorEntity).toList(), pageable, paymentList.size()));

        when(paymentMapper.toDto(payment)).thenReturn(paymentDto);
        when(paymentMapper.toDto(payment2)).thenReturn(paymentDto2);

        // when
        Page<PaymentDto> result = paymentService.searchPaged(PaymentFilterDTO.builder().build(), pageable);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).usingRecursiveComparison().isEqualTo(expectedList);
    }

    static Stream<Arguments> sortProvider() {
        return Stream.of(
                Arguments.of(Sort.by("amount").ascending(),
                        Comparator.comparing(Payment::getAmount), Comparator.comparing(PaymentDto::getAmount)),
                Arguments.of(Sort.by("amount").descending(),
                        Comparator.comparing(Payment::getAmount).reversed(), Comparator.comparing(PaymentDto::getAmount).reversed()),
                Arguments.of(Sort.by("createdAt").ascending(),
                        Comparator.comparing(Payment::getCreatedAt), Comparator.comparing(PaymentDto::getCreatedAt)),
                Arguments.of(Sort.by("createdAt").descending(),
                        Comparator.comparing(Payment::getCreatedAt).reversed(), Comparator.comparing(PaymentDto::getCreatedAt).reversed())
        );
    }

    @Test
    void shouldFilterWithPagination() {
        // given
        Payment payment2 = new Payment();
        payment2.setAmount(BigDecimal.valueOf(756.4));
        payment2.setCreatedAt(OffsetDateTime.now().plusHours(4));

        PaymentDto paymentDto2 = new PaymentDto();
        paymentDto2.setAmount(payment2.getAmount());
        paymentDto2.setCreatedAt(payment2.getCreatedAt());

        List<Payment> paymentList = List.of(payment, payment2);
        Pageable pageable = PageRequest.of(0, 25);

        when(paymentRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(paymentList, pageable, paymentList.size()));
        when(paymentMapper.toDto(payment)).thenReturn(paymentDto);
        when(paymentMapper.toDto(payment2)).thenReturn(paymentDto2);

        // when
        Page<PaymentDto> resultPage = paymentService.searchPaged(PaymentFilterDTO.builder().build(), pageable);

        // then
        assertThat(resultPage).isNotNull();
        assertThat(resultPage.getNumber()).isEqualTo(0);
        assertThat(resultPage.getSize()).isEqualTo(25);
        assertThat(resultPage.getTotalElements()).isEqualTo(2);
        assertThat(resultPage.getTotalPages()).isEqualTo(1);
    }
}
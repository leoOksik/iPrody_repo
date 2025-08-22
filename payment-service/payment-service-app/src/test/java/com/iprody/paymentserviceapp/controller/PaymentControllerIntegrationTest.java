package com.iprody.paymentserviceapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iprody.paymentserviceapp.AbstractPostgresIntegrationTest;
import com.iprody.paymentserviceapp.TestJwtFactory;
import com.iprody.paymentserviceapp.dto.PaymentDto;
import com.iprody.paymentserviceapp.exception.OperationError;
import com.iprody.paymentserviceapp.persistence.PaymentRepository;
import com.iprody.paymentserviceapp.persistence.entity.Payment;
import com.iprody.paymentserviceapp.persistence.entity.PaymentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.lessThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@AutoConfigureMockMvc
class PaymentControllerIntegrationTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("Check getting payments for different roles")
    @ParameterizedTest
    @MethodSource("roleProvider")
    void shouldReturnOnlyLiquibasePayments(String username, String role) throws Exception {
        mockMvc.perform(get("/api/payments/all")
                .with(TestJwtFactory.jwtWithRole(username, role))
                .param("page", "0")
                .param("size", "10")
                .accept(MediaType.APPLICATION_JSON))

            .andExpect(status().isOk())
            .andExpect(jsonPath("$[?(@.guid=='00000000-0000-0000-0000-000000000001')]").exists())
            .andExpect(jsonPath("$[?(@.guid=='00000000-0000-0000-0000-000000000002')]").exists())
            .andExpect(jsonPath("$[?(@.guid=='00000000-0000-0000-0000-000000000003')]").exists());

    }

    @DisplayName("Check getting filtering payments for different roles")
    @ParameterizedTest
    @MethodSource("roleProvider")
    void shouldReturnFilteringLiquibasePayments(String username, String role) throws Exception {
        mockMvc.perform(get("/api/payments/search")
                .with(TestJwtFactory.jwtWithRole(username, role))
                .param("page", "0")
                .param("size", "2")
                .param("maxAmount", "60.00")
                .param("currency", "EUR" )
                .accept(MediaType.APPLICATION_JSON))

            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[?(@.guid=='00000000-0000-0000-0000-000000000002')]").exists())
            .andExpect(jsonPath("$.totalElements").value(1))
            .andExpect(jsonPath("$.pageable.pageNumber").value(0))
            .andExpect(jsonPath("$.content.length()").value(1))
            .andExpect(jsonPath("$.content[0].currency").value("EUR"))
            .andExpect(jsonPath("$.content[0].amount").value(lessThan(60.00)));
    }

    @DisplayName("Check getting payment by id for different roles")
    @ParameterizedTest
    @MethodSource("roleProvider")
    void shouldReturnPaymentById(String username, String role) throws Exception {
        UUID existingId = UUID.fromString("00000000-0000-0000-0000-000000000002");

        mockMvc.perform(get("/api/payments/" + existingId)
                .with(TestJwtFactory.jwtWithRole(username, role))
                .accept(MediaType.APPLICATION_JSON))

            .andExpect(status().isOk())
            .andExpect(jsonPath("$.guid").value(existingId.toString()))
            .andExpect(jsonPath("$.currency").value("EUR"))
            .andExpect(jsonPath("$.amount").value(50.00));
    }

    @DisplayName("Check returning 404 error code for different roles if payment not found by id (OperationError.FIND_BY_ID_OP")
    @ParameterizedTest
    @MethodSource("roleProvider")
    void shouldReturn404ForNonexistentPaymentForFindOperationById(String username, String role) throws Exception {
        UUID nonexistentId = UUID.randomUUID();

        mockMvc.perform(get("/api/payments/" + nonexistentId)
                .with(TestJwtFactory.jwtWithRole(username, role))
                .accept(MediaType.APPLICATION_JSON))

            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.errorCode").value(404))
            .andExpect(jsonPath("$.errorMessage")
                .value("Entity %s with id %s not found. Operation -> %s"
                    .formatted(Payment.class.getSimpleName(), nonexistentId, OperationError.FIND_BY_ID_OP)))
            .andExpect(jsonPath("$.timestamp").exists());
    }

    @DisplayName("Check saving new payment in db")
    @Test
    void shouldCreatePaymentAndVerifyInDatabase() throws Exception {
        PaymentDto dto = new PaymentDto();
        dto.setInquiryRefId(UUID.randomUUID());
        dto.setAmount(new BigDecimal("123.45"));
        dto.setCurrency("EUR");
        dto.setStatus(PaymentStatus.PENDING);
        dto.setCreatedAt(OffsetDateTime.now());
        dto.setUpdatedAt(OffsetDateTime.now());
        String json = objectMapper.writeValueAsString(dto);

        String response = mockMvc.perform(post("/api/payments")
                .with(TestJwtFactory.jwtWithRole("user-admin-test", "admin"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))

            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.guid").exists())
            .andExpect(jsonPath("$.currency").value("EUR"))
            .andExpect(jsonPath("$.amount").value(123.45))
            .andReturn()
            .getResponse()
            .getContentAsString();

        PaymentDto created = objectMapper.readValue(response, PaymentDto.class);
        Optional<Payment> saved = paymentRepository.findById(created.getGuid());

        assertThat(saved).isPresent();
        assertThat(saved.get().getCurrency()).isEqualTo("EUR");
        assertThat(saved.get().getAmount()).isEqualByComparingTo("123.45");
    }

    @DisplayName("Check updating payment in db by id")
    @Test
    void shouldUpdatePaymentAndVerifyInDatabase() throws Exception {

        UUID existingId = UUID.fromString("00000000-0000-0000-0000-000000000003");
        Optional<Payment> existingPayment = paymentRepository.findById(existingId);

        assertThat(existingPayment).isPresent();

        Payment payment = existingPayment.get();
        PaymentDto updatedDto = new PaymentDto();
        updatedDto.setGuid(payment.getGuid());
        updatedDto.setInquiryRefId(payment.getInquiryRefId());
        updatedDto.setAmount(new BigDecimal("111.11"));
        updatedDto.setCurrency("USD");
        updatedDto.setTransactionRefId(payment.getTransactionRefId());
        updatedDto.setStatus(payment.getStatus());
        updatedDto.setNote("new Note");
        updatedDto.setCreatedAt(payment.getCreatedAt());
        updatedDto.setUpdatedAt(OffsetDateTime.now());

        String json = objectMapper.writeValueAsString(updatedDto);

        String response = mockMvc.perform(put("/api/payments/" + existingId)
                .with(TestJwtFactory.jwtWithRole("user-admin-test", "admin"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))

            .andExpect(status().isOk())
            .andExpect(jsonPath("$.guid").exists())
            .andExpect(jsonPath("$.currency").value("USD"))
            .andExpect(jsonPath("$.amount").value(111.11))
            .andExpect(jsonPath("$.note").value("new Note"))
            .andReturn()
            .getResponse()
            .getContentAsString();

        PaymentDto updated = objectMapper.readValue(response, PaymentDto.class);
        Optional<Payment> saved = paymentRepository.findById(updated.getGuid());

        assertThat(saved).isPresent();
        assertThat(saved.get().getCurrency()).isEqualTo("USD");
        assertThat(saved.get().getAmount()).isEqualByComparingTo("111.11");
        assertThat(saved.get().getNote()).isEqualTo("new Note");

    }

    @DisplayName("Check returning 404 error code if payment not found by id (OperationError.UPDATE_OP)")
    @Test
    void shouldReturn404ForNonexistentPaymentForUpdateOperation() throws Exception {
        UUID nonexistentId = UUID.randomUUID();

        mockMvc.perform(put("/api/payments/" + nonexistentId)
                .with(TestJwtFactory.jwtWithRole("user-admin-test", "admin"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
                .accept(MediaType.APPLICATION_JSON))

            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.errorCode").value(404))
            .andExpect(jsonPath("$.errorMessage")
                .value("Entity %s with id %s not found. Operation -> %s"
                    .formatted(Payment.class.getSimpleName(), nonexistentId, OperationError.UPDATE_OP)))
            .andExpect(jsonPath("$.timestamp").exists());
    }

    @DisplayName("Check updating note in payment and saving new state in db ")
    @Test
    void shouldPatchUpdatePaymentAndVerifyInDatabase() throws Exception {
        UUID existingId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        Optional<Payment> payment = paymentRepository.findById(existingId);

        assertThat(payment).isPresent();
        payment.get().setNote("testUpdateNote");
        String json = objectMapper.writeValueAsString(payment);

        String response = mockMvc.perform(patch("/api/payments/" + existingId + "/note")
                .with(TestJwtFactory.jwtWithRole("user-admin-test", "admin"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))

            .andExpect(status().isOk())
            .andExpect(jsonPath("$.note").value("testUpdateNote"))
            .andReturn()
            .getResponse()
            .getContentAsString();

        PaymentDto updatedNotePayment = objectMapper.readValue(response, PaymentDto.class);
        Optional<Payment> savedNote = paymentRepository.findById(updatedNotePayment.getGuid());

        assertThat(savedNote).isPresent();
        assertThat(savedNote.get().getNote()).isEqualTo("testUpdateNote");
    }

    @DisplayName("Check returning 404 error code if payment not found by id (Patch - OperationError.UPDATE_OP)")
    @Test
    void shouldReturn404ForNonexistentPaymentForPatchUpdateOperation() throws Exception {
        UUID nonexistentId = UUID.randomUUID();
        String json = "{\"note\":\"testNote\"}";

        mockMvc.perform(patch("/api/payments/" + nonexistentId + "/note")
                .with(TestJwtFactory.jwtWithRole("user-admin-test", "admin"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))

            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.errorCode").value(404))
            .andExpect(jsonPath("$.errorMessage")
                .value("Entity %s with id %s not found. Operation -> %s"
                    .formatted(Payment.class.getSimpleName(), nonexistentId, OperationError.UPDATE_OP)))
            .andExpect(jsonPath("$.timestamp").exists());
    }

    @DisplayName("Check deleting payment in db by id")
    @Test
    @Transactional
    void shouldDeletePaymentAndVerifyInDatabase() throws Exception {
        UUID existingId = UUID.fromString("00000000-0000-0000-0000-000000000003");
        Optional<Payment> existingPayment = paymentRepository.findById(existingId);

        assertThat(existingPayment).isPresent();

        mockMvc.perform(delete("/api/payments/" + existingId)
                .with(TestJwtFactory.jwtWithRole("user-admin-test", "admin")))
            .andExpect(status().isNoContent());

        Optional<Payment> deletedPayment = paymentRepository.findById(existingId);

        assertThat(deletedPayment).isNotPresent();
    }

    @DisplayName("Check returning 404 error code if payment not found by id (OperationError.DELETE_OP)")
    @Test
    void shouldReturn404ForNonexistentPaymentForDeleteOperation() throws Exception {
        UUID nonexistentId = UUID.randomUUID();

        mockMvc.perform(delete("/api/payments/" + nonexistentId)
                .with(TestJwtFactory.jwtWithRole("user-admin-test", "admin"))
                .accept(MediaType.APPLICATION_JSON))

            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.errorCode").value(404))
            .andExpect(jsonPath("$.errorMessage")
                .value("Entity %s with id %s not found. Operation -> %s"
                    .formatted(Payment.class.getSimpleName(), nonexistentId, OperationError.DELETE_OP)))
            .andExpect(jsonPath("$.timestamp").exists());
    }

    static Stream<Arguments> roleProvider() {
        return Stream.of(
            Arguments.of("user-admin-test", "admin"),
            Arguments.of("user-reader-test", "reader")
        );
    }
}

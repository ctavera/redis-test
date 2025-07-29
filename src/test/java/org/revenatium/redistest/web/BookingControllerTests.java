package org.revenatium.redistest.web;

import org.junit.jupiter.api.Test;
import org.revenatium.redistest.BaseTests;
import org.revenatium.redistest.domain.PaymentRequest;
import org.springframework.http.MediaType;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BookingControllerTests extends BaseTests {

    @Test
    void payItineraryTest() throws Exception {
        // given
        String itineraryUuid = UUID.randomUUID().toString();
        PaymentRequest paymentRequest = getValidPaymentRequest();

        // when then
        mockMvc.perform(post(baseUrl + "/{id}/payment", itineraryUuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(paymentRequest)))
                .andExpect(status().isOk());

        // then: segunda llamada (misma reserva y mismos datos)
        mockMvc.perform(post(baseUrl + "/{id}/payment", itineraryUuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(paymentRequest)))
                .andExpect(status().isOk());

        String cacheKey = itineraryUuid + " con monto: " + paymentRequest.getAmount() + " y gateway: " + paymentRequest.getPaymentGateway();
        assertThat(outContent.toString()).contains("Procesando reserva: " + cacheKey);
        if (isRedisAvailable) {
            assertThat(outContent.toString()).contains("Reserva duplicada : " + itineraryUuid);
        } else {
            assertThat(outContent.toString()).doesNotContain("Reserva duplicada : " + itineraryUuid);
        }
    }

    @Test
    void concurrentPayItinerariesTest() throws Exception {
        // given
        String itineraryUuid = UUID.randomUUID().toString();
        PaymentRequest paymentRequest = getValidPaymentRequest();

        Runnable task = () -> {
            try {
                mockMvc.perform(post(baseUrl + "/{id}/payment", itineraryUuid)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(paymentRequest)))
                        .andExpect(status().isOk());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };

        CompletableFuture<Void> future1 = CompletableFuture.runAsync(task);
        CompletableFuture<Void> future2 = CompletableFuture.runAsync(task);

        CompletableFuture.allOf(future1, future2).join();

        String cacheKey = itineraryUuid + " con monto: " + paymentRequest.getAmount() + " y gateway: " + paymentRequest.getPaymentGateway();
        assertThat(outContent.toString()).contains("Procesando reserva: " + cacheKey);
        if (isRedisAvailable) {
            assertThat(outContent.toString()).contains("Reserva duplicada : " + itineraryUuid);
        } else {
            assertThat(outContent.toString()).doesNotContain("Reserva duplicada : " + itineraryUuid);
        }
    }
}

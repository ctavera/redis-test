package org.revenatium.redistest.web;

import lombok.RequiredArgsConstructor;
import org.revenatium.redistest.domain.PaymentRequest;
import org.revenatium.redistest.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/{id}/payment")
    ResponseEntity<String> payItinerary(
            @PathVariable UUID id,
            @RequestBody PaymentRequest paymentRequest) {

        Boolean success = bookingService.isAValidPayment(id.toString(), paymentRequest);
        String result;
        if (Boolean.TRUE.equals(success)) {
            try {
                System.out.println("Procesando reserva: " + id + " con monto: " + paymentRequest.getAmount() + " y gateway: " + paymentRequest.getPaymentGateway() + " : " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

                // Processing payment

                result = "Processed OK";
            } catch (Exception e) {
                System.out.println("Error procesando reserva: " + id + " con monto: " + paymentRequest.getAmount() + " y gateway: " + paymentRequest.getPaymentGateway() + " : " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                result = "Process ERROR: Reservation error";
                bookingService.clearCache(id.toString(), paymentRequest);
            }
        } else {
            System.out.println("Reserva duplicada : " + id + " : " +LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

            result = "Process ERROR: Duplicate reservation";
        }

        return ResponseEntity.ok(result);
    }
}

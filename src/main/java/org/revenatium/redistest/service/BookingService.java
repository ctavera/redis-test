package org.revenatium.redistest.service;


import org.revenatium.redistest.domain.PaymentRequest;

public interface BookingService {

    Boolean isAValidPayment(String itineraryId, PaymentRequest paymentRequest);

    void clearCache(String itineraryId, PaymentRequest paymentRequest);
}

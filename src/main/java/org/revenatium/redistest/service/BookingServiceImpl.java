package org.revenatium.redistest.service;

import lombok.RequiredArgsConstructor;
import org.revenatium.redistest.domain.PaymentRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@RequiredArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public Boolean isAValidPayment(String itineraryId, PaymentRequest paymentRequest) {

        String cacheKey = itineraryId + "::" + paymentRequest.getAmount() + "::" + paymentRequest.getPaymentGateway();

        try {
            Boolean success = redisTemplate.opsForValue().setIfAbsent(cacheKey, "LOCKED", Duration.ofSeconds(10));

            return success;
        } catch (Exception e) {
            // Redis down
            System.err.println("Redis no disponible. Operando en modo degradado. Permitiendo el pago para " + cacheKey);

            return true;
        }
    }

    @Override
    public void clearCache(String itineraryId, PaymentRequest paymentRequest) {
        String cacheKey = itineraryId + "::" + paymentRequest.getAmount() + "::" + paymentRequest.getPaymentGateway();

        redisTemplate.delete(cacheKey);
    }
}

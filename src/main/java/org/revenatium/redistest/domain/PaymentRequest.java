package org.revenatium.redistest.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PaymentRequest {

    Double amount;
    PaymentGateway paymentGateway;
}

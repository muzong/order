package com.orderpay.payment.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentNo {
    private String value;

    public static PaymentNo of(String value) {
        return PaymentNo.builder().value(value).build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentNo paymentNo = (PaymentNo) o;
        return Objects.equals(value, paymentNo.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}

package com.orderpay.order.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Money {
    private BigDecimal amount;

    public static Money of(BigDecimal amount) {
        return Money.builder().amount(amount).build();
    }

    public Money add(Money other) {
        return Money.of(this.amount.add(other.amount));
    }

    public Money multiply(int quantity) {
        return Money.of(this.amount.multiply(BigDecimal.valueOf(quantity)));
    }

    public BigDecimal getValue() {
        return this.amount;
    }
}

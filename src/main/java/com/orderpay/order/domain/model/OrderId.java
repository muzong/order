package com.orderpay.order.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderId {
    private Long value;

    public static OrderId of(Long value) {
        return OrderId.builder().value(value).build();
    }
}

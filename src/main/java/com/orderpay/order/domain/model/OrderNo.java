package com.orderpay.order.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderNo {
    private String value;

    public static OrderNo of(String value) {
        return OrderNo.builder().value(value).build();
    }
}

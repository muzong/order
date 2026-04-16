package com.orderpay.inventory.domain.model;

import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class StockId implements Serializable {
    private Long value;
}

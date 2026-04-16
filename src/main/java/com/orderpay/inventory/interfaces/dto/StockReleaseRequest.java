package com.orderpay.inventory.interfaces.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockReleaseRequest {
    private String lockId;
}

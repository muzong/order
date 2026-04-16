package com.orderpay.inventory.interfaces.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockLockRequest {
    private String skuId;
    private Integer quantity;
    private String lockId;
}

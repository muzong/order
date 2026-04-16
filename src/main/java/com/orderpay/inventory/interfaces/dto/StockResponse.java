package com.orderpay.inventory.interfaces.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockResponse {
    private String skuId;
    private Integer totalStock;
    private Integer lockedStock;
    private Integer availableStock;
    private String lockId;
}

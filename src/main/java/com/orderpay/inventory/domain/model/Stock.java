package com.orderpay.inventory.domain.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Stock {
    private StockId id;
    private SkuId skuId;
    private Integer totalStock;
    private Integer lockedStock;
    private Integer availableStock;
}

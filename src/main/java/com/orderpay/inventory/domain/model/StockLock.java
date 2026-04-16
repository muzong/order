package com.orderpay.inventory.domain.model;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockLock {
    private String lockId;
    private SkuId skuId;
    private Integer lockedQty;
    private LocalDateTime expireAt;
}

package com.orderpay.inventory.infrastructure.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("stock_lock")
public class StockLockDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String lockId;
    private String skuId;
    private Integer lockedQty;
    private Long expireAt;
    private Long createdAt;
}

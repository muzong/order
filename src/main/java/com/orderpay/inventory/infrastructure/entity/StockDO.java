package com.orderpay.inventory.infrastructure.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("inventory")
public class StockDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String skuId;
    private Integer totalStock;
    private Integer lockedStock;
    private Integer availableStock;
    private Integer isDeleted;
    private Long createBy;
    private Long updateBy;
    @TableField(fill = FieldFill.INSERT)
    private Long createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updatedAt;
}

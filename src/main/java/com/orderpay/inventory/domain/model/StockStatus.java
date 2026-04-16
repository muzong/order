package com.orderpay.inventory.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StockStatus {
    NORMAL("正常", 0),
    LOCKED("已锁定", 1),
    DEPLETED("已售罄", 2);

    private final String desc;
    private final Integer code;
}

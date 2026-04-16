package com.orderpay.order.domain.model;

public enum OrderStatus {
    PENDING_PAYMENT("待支付", 0),
    PAID("已支付", 1),
    CANCELLED("已取消", 2),
    SHIPPED("已发货", 3),
    COMPLETED("已完成", 4);

    private final String desc;
    private final int code;

    OrderStatus(String desc, int code) {
        this.desc = desc;
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public int getCode() {
        return code;
    }
}

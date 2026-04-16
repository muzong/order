package com.orderpay.payment.domain.model;

public enum PaymentStatus {
    PENDING("处理中", 0),
    SUCCESS("支付成功", 1),
    FAILED("支付失败", 2);

    private final String desc;
    private final int code;

    PaymentStatus(String desc, int code) {
        this.desc = desc;
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public int getCode() {
        return code;
    }

    public static PaymentStatus fromCode(int code) {
        for (PaymentStatus status : PaymentStatus.values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("无效的支付状态码: " + code);
    }
}

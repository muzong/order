package com.orderpay.payment.interfaces.dto;

import lombok.Data;

@Data
public class PayOrderRequest {
    private String orderNo;
    private Long userId;
}

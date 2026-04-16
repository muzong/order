package com.orderpay.payment.interfaces.dto;

import lombok.Data;

@Data
public class PaymentResponse {
    private String paymentNo;
    private String orderNo;
    private Double amount;
    private Integer status;
}

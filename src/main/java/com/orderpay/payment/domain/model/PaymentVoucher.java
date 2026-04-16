package com.orderpay.payment.domain.model;

import com.orderpay.order.domain.model.Money;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentVoucher {
    private PaymentVoucherId id;
    private PaymentNo paymentNo;
    private com.orderpay.order.domain.model.OrderId orderId;
    private Money amount;
    private PaymentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isDeleted;
    private String createBy;
    private String updateBy;
}

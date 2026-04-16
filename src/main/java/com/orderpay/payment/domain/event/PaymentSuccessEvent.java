package com.orderpay.payment.domain.event;

import com.orderpay.order.domain.model.OrderId;
import com.orderpay.payment.domain.model.PaymentNo;
import com.orderpay.payment.domain.model.PaymentVoucherId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentSuccessEvent {
    private PaymentVoucherId voucherId;
    private PaymentNo paymentNo;
    private OrderId orderId;
    private BigDecimal amount;
    private LocalDateTime createdAt;
}

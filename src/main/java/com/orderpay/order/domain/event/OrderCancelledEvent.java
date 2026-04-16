package com.orderpay.order.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCancelledEvent {
    private Long orderId;
    private String orderNo;
    private String cancelReason;
    private LocalDateTime cancelledAt;
}

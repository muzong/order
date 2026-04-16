package com.orderpay.order.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    private OrderId id;
    private OrderNo orderNo;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    private BigDecimal freight;
    private BigDecimal discountAmount;
    private OrderStatus status;
    private String userId;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private List<OrderItem> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime payTimeoutAt;
    private Boolean isDeleted;
    private String createBy;
    private String updateBy;
}

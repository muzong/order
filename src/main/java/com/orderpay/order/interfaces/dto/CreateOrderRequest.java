package com.orderpay.order.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderRequest {
    private String userId;
    private List<OrderItemRequest> items;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class OrderItemRequest {
    private String skuId;
    private String productName;
    private Double unitPrice;
    private Integer quantity;
}
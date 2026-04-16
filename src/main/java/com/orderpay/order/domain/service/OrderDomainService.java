package com.orderpay.order.domain.service;

import com.orderpay.order.domain.model.Order;
import com.orderpay.order.domain.model.OrderId;
import com.orderpay.order.domain.model.OrderItem;

import java.util.List;

public interface OrderDomainService {
    OrderId placeOrder(String userId, List<OrderItem> items, String receiverName, String receiverPhone, String receiverAddress);
    void cancel(Order order);
    void confirmPayment(OrderId orderId);
    void shipOrder(OrderId orderId);
    void completeOrder(OrderId orderId);
}

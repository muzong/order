package com.orderpay.order.application.service;

import com.orderpay.order.domain.model.OrderId;
import com.orderpay.order.interfaces.dto.CreateOrderRequest;
import com.orderpay.order.interfaces.dto.OrderResponse;

public interface OrderApplicationService {
    OrderResponse createOrder(CreateOrderRequest request);
    OrderId placeOrder(String userId, String receiverName, String receiverPhone, String receiverAddress);
    void cancelOrder(Long orderId);
    void confirmPayment(Long orderId);
    void shipOrder(Long orderId);
    void completeOrder(Long orderId);
    OrderResponse queryOrder(Long orderId);
}

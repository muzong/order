package com.orderpay.order.domain.repository;

import com.orderpay.order.domain.model.Order;
import com.orderpay.order.domain.model.OrderId;
import com.orderpay.order.domain.model.OrderNo;

import java.util.Optional;

public interface OrderRepository {
    Optional<Order> findById(OrderId id);
    Optional<Order> findByOrderNo(OrderNo orderNo);
    Order save(Order order);
    void update(Order order);
    void delete(Order order);
}

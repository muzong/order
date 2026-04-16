package com.orderpay.order.domain.service.impl;

import com.orderpay.order.domain.model.*;
import com.orderpay.order.domain.service.OrderDomainService;
import com.orderpay.order.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderDomainServiceImpl implements OrderDomainService {
    
    private final OrderRepository orderRepository;
    
    @Override
    @Transactional
    public OrderId placeOrder(String userId, List<OrderItem> items, String receiverName, String receiverPhone, String receiverAddress) {
        log.info("开始创建订单, userId: {}, items: {}", userId, items);
        
        java.math.BigDecimal totalAmount = items.stream()
                .map(item -> item.getPrice() == null ? java.math.BigDecimal.ZERO : item.getPrice().multiply(java.math.BigDecimal.valueOf(item.getQuantity())))
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        
        java.math.BigDecimal freight = java.math.BigDecimal.ZERO;
        java.math.BigDecimal discountAmount = java.math.BigDecimal.ZERO;
        java.math.BigDecimal payAmount = totalAmount.add(freight).subtract(discountAmount);
        
        String orderNo = generateOrderNo();
        
        Order order = Order.builder()
                .id(OrderId.of(System.currentTimeMillis()))
                .orderNo(OrderNo.of(orderNo))
                .totalAmount(totalAmount)
                .payAmount(payAmount)
                .freight(freight)
                .discountAmount(discountAmount)
                .status(OrderStatus.PENDING_PAYMENT)
                .userId(userId)
                .receiverName(receiverName)
                .receiverPhone(receiverPhone)
                .receiverAddress(receiverAddress)
                .items(items)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();
        
        orderRepository.save(order);
        
        log.info("订单创建成功, orderId: {}, orderNo: {}", order.getId().getValue(), orderNo);
        
        return order.getId();
    }
    
    private String generateOrderNo() {
        return "ORDER" + System.currentTimeMillis() + (int)((Math.random() * 9000) + 1000);
    }
    
    @Override
    @Transactional
    public void cancel(Order order) {
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.update(order);
    }
    
    @Override
    @Transactional
    public void confirmPayment(OrderId orderId) {
        log.info("开始确认支付, orderId: {}", orderId.getValue());
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在, orderId: " + orderId.getValue()));
        
        if (!order.getStatus().equals(OrderStatus.PENDING_PAYMENT)) {
            throw new IllegalStateException("订单状态不正确, 当前状态: " + order.getStatus().getDesc());
        }
        
        order.setStatus(OrderStatus.PAID);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.update(order);
        
        log.info("支付确认成功, orderId: {}", orderId.getValue());
    }
    
    @Override
    @Transactional
    public void shipOrder(OrderId orderId) {
        log.info("开始发货, orderId: {}", orderId.getValue());
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在, orderId: " + orderId.getValue()));
        
        if (!order.getStatus().equals(OrderStatus.PAID)) {
            throw new IllegalStateException("订单状态不正确, 当前状态: " + order.getStatus().getDesc());
        }
        
        order.setStatus(OrderStatus.SHIPPED);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.update(order);
        
        log.info("发货成功, orderId: {}", orderId.getValue());
    }
    
    @Override
    @Transactional
    public void completeOrder(OrderId orderId) {
        log.info("开始完成订单, orderId: {}", orderId.getValue());
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在, orderId: " + orderId.getValue()));
        
        if (!order.getStatus().equals(OrderStatus.SHIPPED)) {
            throw new IllegalStateException("订单状态不正确, 当前状态: " + order.getStatus().getDesc());
        }
        
        order.setStatus(OrderStatus.COMPLETED);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.update(order);
        
        log.info("订单完成成功, orderId: {}", orderId.getValue());
    }
}

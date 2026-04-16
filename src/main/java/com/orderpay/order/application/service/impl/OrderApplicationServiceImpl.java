package com.orderpay.order.application.service.impl;

import com.orderpay.inventory.application.service.InventoryApplicationService;
import com.orderpay.inventory.interfaces.dto.StockLockRequest;
import com.orderpay.order.application.service.OrderApplicationService;
import com.orderpay.order.domain.event.OrderCreatedEvent;
import com.orderpay.order.domain.model.*;
import com.orderpay.order.domain.repository.OrderRepository;
import com.orderpay.order.domain.service.OrderDomainService;
import com.orderpay.order.interfaces.dto.CreateOrderRequest;
import com.orderpay.order.interfaces.dto.OrderItemRequest;
import com.orderpay.order.interfaces.dto.OrderResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderApplicationServiceImpl implements OrderApplicationService {

    private final OrderRepository orderRepository;
    private final OrderDomainService orderDomainService;
    private final InventoryApplicationService inventoryApplicationService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        String orderNo = "ORDER_" + System.currentTimeMillis();

        Money totalAmount = Money.of(java.math.BigDecimal.ZERO);
        List<OrderItem> items = new ArrayList<>();
        for (var item : request.getItems()) {
            Money itemAmount = Money.of(java.math.BigDecimal.valueOf(item.getUnitPrice())).multiply(item.getQuantity());
            items.add(OrderItem.builder().id(item.getSkuId()).productId(item.getSkuId()).productName(item.getProductName()).price(item.getUnitPrice() == null ? java.math.BigDecimal.ZERO : java.math.BigDecimal.valueOf(item.getUnitPrice())).quantity(item.getQuantity()).build());
            totalAmount = totalAmount.add(itemAmount);
        }

        String lockId = "LOCK_" + System.currentTimeMillis();
        StockLockRequest lockRequest = StockLockRequest.builder().skuId(request.getItems().get(0).getSkuId()).quantity(request.getItems().stream().mapToInt(OrderItemRequest::getQuantity).sum()).lockId("LOCK_" + System.currentTimeMillis()).build();

        inventoryApplicationService.lock(lockRequest);

        Order order = Order.builder().orderNo(OrderNo.of(orderNo)).userId(request.getUserId()).totalAmount(totalAmount).status(OrderStatus.PENDING_PAYMENT).items(items).payTimeoutAt(LocalDateTime.now().plusMinutes(15)).createdAt(LocalDateTime.now()).build();

        orderRepository.save(order);

        eventPublisher.publishEvent(OrderCreatedEvent.builder().orderId(order.getId().getValue()).orderNo(orderNo).userId(request.getUserId()).createdAt(LocalDateTime.now()).build());

        return OrderResponse.builder().orderNo(orderNo).totalAmount(totalAmount.getAmount().doubleValue()).status(order.getStatus().getCode()).build();
    }

    @Override
    @Transactional
    public OrderId placeOrder(String userId, String receiverName, String receiverPhone, String receiverAddress) {
        Order order = Order.builder().id(OrderId.of(System.currentTimeMillis())).orderNo(OrderNo.of("ORDER_" + System.currentTimeMillis())).userId(userId).receiverName(receiverName).receiverPhone(receiverPhone).receiverAddress(receiverAddress).status(OrderStatus.PENDING_PAYMENT).totalAmount(Money.of(java.math.BigDecimal.ZERO)).payAmount(Money.of(java.math.BigDecimal.ZERO)).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).isDeleted(false).build();
        orderRepository.save(order);
        return order.getId();
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        Optional<Order> orderOptional = orderRepository.findById(OrderId.of(orderId));
        if (!orderOptional.isPresent()) {
            throw new com.orderpay.order.domain.exception.BusinessException("订单不存在");
        }

        Order order = orderOptional.get();
        orderDomainService.cancel(order);
        orderRepository.update(order);
    }

    @Override
    @Transactional
    public void confirmPayment(Long orderId) {
        Optional<Order> orderOptional = orderRepository.findById(OrderId.of(orderId));
        if (!orderOptional.isPresent()) {
            throw new com.orderpay.order.domain.exception.BusinessException("订单不存在");
        }

        Order order = orderOptional.get();
        order.setStatus(OrderStatus.PAID);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.update(order);
    }

    @Override
    @Transactional
    public void shipOrder(Long orderId) {
        Optional<Order> orderOptional = orderRepository.findById(OrderId.of(orderId));
        if (!orderOptional.isPresent()) {
            throw new com.orderpay.order.domain.exception.BusinessException("订单不存在");
        }

        Order order = orderOptional.get();
        order.setStatus(OrderStatus.SHIPPED);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.update(order);
    }

    @Override
    @Transactional
    public void completeOrder(Long orderId) {
        Optional<Order> orderOptional = orderRepository.findById(OrderId.of(orderId));
        if (!orderOptional.isPresent()) {
            throw new com.orderpay.order.domain.exception.BusinessException("订单不存在");
        }

        Order order = orderOptional.get();
        order.setStatus(OrderStatus.COMPLETED);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.update(order);
    }
}

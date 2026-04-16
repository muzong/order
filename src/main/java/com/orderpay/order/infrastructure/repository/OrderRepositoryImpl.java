package com.orderpay.order.infrastructure.repository;

import com.orderpay.order.domain.model.Order;
import com.orderpay.order.domain.model.OrderId;
import com.orderpay.order.domain.model.OrderNo;
import com.orderpay.order.domain.repository.OrderRepository;
import com.orderpay.order.infrastructure.convert.OrderConvert;
import com.orderpay.order.infrastructure.entity.OrderDO;
import com.orderpay.order.infrastructure.entity.OrderItemDO;
import com.orderpay.order.infrastructure.mapper.OrderItemMapper;
import com.orderpay.order.infrastructure.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class OrderRepositoryImpl implements OrderRepository {
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final OrderConvert orderConvert;
    
    public OrderRepositoryImpl(OrderMapper orderMapper, OrderItemMapper orderItemMapper, OrderConvert orderConvert) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.orderConvert = orderConvert;
    }
    
    @Override
    public Optional<Order> findById(OrderId id) {
        OrderDO orderDO = orderMapper.selectById(id.getValue());
        if (orderDO == null) {
            return Optional.empty();
        }
        
        List<OrderItemDO> itemDOs = orderItemMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<OrderItemDO>()
                        .eq("order_id", orderDO.getId())
                        .eq("is_deleted", false)
        );
        
        Order order = orderConvert.toDomain(orderDO);
        order.setItems(orderConvert.toDomainList(itemDOs));
        return Optional.of(order);
    }
    
    @Override
    public Optional<Order> findByOrderNo(OrderNo orderNo) {
        OrderDO orderDO = orderMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<OrderDO>()
                        .eq("order_no", orderNo.getValue())
                        .eq("is_deleted", false)
        );
        
        if (orderDO == null) {
            return Optional.empty();
        }
        
        List<OrderItemDO> itemDOs = orderItemMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<OrderItemDO>()
                        .eq("order_id", orderDO.getId())
                        .eq("is_deleted", false)
        );
        
        Order order = orderConvert.toDomain(orderDO);
        order.setItems(orderConvert.toDomainList(itemDOs));
        return Optional.of(order);
    }
    
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Order save(Order order) {
        OrderDO orderDO = orderConvert.toDO(order);
        orderMapper.insert(orderDO);
        return order;
    }
    
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update(Order order) {
        OrderDO orderDO = orderConvert.toDO(order);
        orderMapper.updateById(orderDO);
    }
    
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(Order order) {
        OrderDO orderDO = orderConvert.toDO(order);
        orderMapper.deleteById(orderDO.getId());
    }
}

package com.orderpay.order.infrastructure.convert;

import com.orderpay.order.domain.model.*;
import com.orderpay.order.infrastructure.entity.OrderDO;
import com.orderpay.order.infrastructure.entity.OrderItemDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface OrderConvert {
    OrderConvert INSTANCE = Mappers.getMapper(OrderConvert.class);
    
    @Mapping(target = "id", source = "id")
    @Mapping(target = "orderNo.value", source = "orderNo")
    @Mapping(target = "totalAmount", source = "totalAmount")
    @Mapping(target = "payAmount", source = "payAmount")
    @Mapping(target = "freight", source = "freight")
    @Mapping(target = "discountAmount", source = "discountAmount")
    @Mapping(target = "status.code", source = "status")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "receiverName", source = "receiverName")
    @Mapping(target = "receiverPhone", source = "receiverPhone")
    @Mapping(target = "receiverAddress", source = "receiverAddress")
    @Mapping(target = "items", source = "items")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "isDeleted", source = "isDeleted")
    @Mapping(target = "createBy", source = "createBy")
    @Mapping(target = "updateBy", source = "updateBy")
    OrderDO toDO(Order order);
    
    Order toDomain(OrderDO orderDO);
    
    @Mapping(target = "id", source = "id")
    @Mapping(target = "productId", source = "skuId")
    @Mapping(target = "name", source = "productName")
    @Mapping(target = "price", source = "unitPrice")
    @Mapping(target = "quantity", source = "quantity")
    OrderItemDO toDO(OrderItem orderItem);
    
    OrderItem toDomain(OrderItemDO orderItemDO);
    
    List<OrderItemDO> toDOList(List<OrderItem> orderItems);
    
    List<OrderItem> toDomainList(List<OrderItemDO> orderItemDOs);
}

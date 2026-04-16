package com.orderpay.order.interfaces.controller;

import com.orderpay.common.result.Result;
import com.orderpay.order.application.service.OrderApplicationService;
import com.orderpay.order.interfaces.dto.CancelOrderRequest;
import com.orderpay.order.interfaces.dto.CreateOrderRequest;
import com.orderpay.order.interfaces.dto.OrderResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderApplicationService orderApplicationService;
    
    @PostMapping
    public Result<OrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        OrderResponse response = orderApplicationService.createOrder(request);
        return Result.success(response);
    }
    
    @PutMapping("/{orderId}/cancel")
    public Result<Void> cancelOrder(@PathVariable Long orderId, @RequestBody CancelOrderRequest request) {
        orderApplicationService.cancelOrder(orderId);
        return Result.success();
    }
    
    @GetMapping("/{orderId}")
    public Result<OrderResponse> queryOrder(@PathVariable Long orderId) {
        OrderResponse response = orderApplicationService.queryOrder(orderId);
        return Result.success(response);
    }
}

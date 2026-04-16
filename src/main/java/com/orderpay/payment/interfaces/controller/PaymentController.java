package com.orderpay.payment.interfaces.controller;

import com.orderpay.common.result.Result;
import com.orderpay.payment.interfaces.dto.PayOrderRequest;
import com.orderpay.payment.interfaces.dto.PaymentCallbackRequest;
import com.orderpay.payment.interfaces.dto.PaymentResponse;
import com.orderpay.payment.application.service.PaymentApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/payment")
public class PaymentController {

    private final PaymentApplicationService paymentApplicationService;

    @Autowired
    public PaymentController(PaymentApplicationService paymentApplicationService) {
        this.paymentApplicationService = paymentApplicationService;
    }

    @PostMapping("/pay")
    public Result<PaymentResponse> pay(@RequestBody PayOrderRequest request) {
        log.info("收到支付请求: orderNo={}, userId={}", request.getOrderNo(), request.getUserId());
        PaymentResponse response = paymentApplicationService.pay(request);
        return Result.success(response);
    }

    @PostMapping("/callback")
    public Result<Void> handleCallback(@RequestBody PaymentCallbackRequest request) {
        log.info("收到支付回调: paymentNo={}, orderNo={}, amount={}, status={}", 
                request.getPaymentNo(), request.getOrderNo(), request.getAmount(), request.getStatus());
        try {
            paymentApplicationService.handlePaymentCallback(request);
            return Result.success("支付结果处理成功");
        } catch (Exception e) {
            return Result.fail(3003, e.getMessage());
        }
    }
}

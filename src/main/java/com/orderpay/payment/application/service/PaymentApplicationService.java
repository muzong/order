package com.orderpay.payment.application.service;

import com.orderpay.payment.interfaces.dto.PayOrderRequest;
import com.orderpay.payment.interfaces.dto.PaymentCallbackRequest;
import com.orderpay.payment.interfaces.dto.PaymentResponse;

public interface PaymentApplicationService {
    PaymentResponse pay(PayOrderRequest request);
    
    void handlePaymentCallback(PaymentCallbackRequest request);
}

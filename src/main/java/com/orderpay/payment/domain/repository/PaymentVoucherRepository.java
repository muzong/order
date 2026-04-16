package com.orderpay.payment.domain.repository;

import com.orderpay.order.domain.model.OrderId;
import com.orderpay.payment.domain.model.PaymentNo;
import com.orderpay.payment.domain.model.PaymentStatus;
import com.orderpay.payment.domain.model.PaymentVoucher;
import com.orderpay.payment.domain.model.PaymentVoucherId;

import java.util.Optional;

public interface PaymentVoucherRepository {
    Optional<PaymentVoucher> findById(PaymentVoucherId id);
    
    Optional<PaymentVoucher> findByPaymentNo(PaymentNo paymentNo);
    
    Optional<PaymentVoucher> findByOrderId(OrderId orderId);
    
    PaymentVoucher save(PaymentVoucher voucher);
    
    void update(PaymentVoucher voucher);
    
    boolean existsByPaymentNo(PaymentNo paymentNo);
}

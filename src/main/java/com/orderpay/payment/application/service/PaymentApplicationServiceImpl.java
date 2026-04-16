package com.orderpay.payment.application.service;

import com.orderpay.common.exception.BusinessException;
import com.orderpay.order.domain.model.Money;
import com.orderpay.order.domain.model.Order;
import com.orderpay.order.domain.model.OrderNo;
import com.orderpay.order.domain.repository.OrderRepository;
import com.orderpay.payment.domain.event.PaymentFailedEvent;
import com.orderpay.payment.domain.event.PaymentSuccessEvent;
import com.orderpay.payment.domain.model.PaymentNo;
import com.orderpay.payment.domain.model.PaymentStatus;
import com.orderpay.payment.domain.model.PaymentVoucher;
import com.orderpay.payment.domain.repository.PaymentVoucherRepository;
import com.orderpay.payment.interfaces.dto.PayOrderRequest;
import com.orderpay.payment.interfaces.dto.PaymentCallbackRequest;
import com.orderpay.payment.interfaces.dto.PaymentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
public class PaymentApplicationServiceImpl implements PaymentApplicationService {

    private final PaymentVoucherRepository paymentVoucherRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public PaymentApplicationServiceImpl(PaymentVoucherRepository paymentVoucherRepository, OrderRepository orderRepository) {
        this.paymentVoucherRepository = paymentVoucherRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaymentResponse pay(PayOrderRequest request) {
        log.info("发起支付请求: orderNo={}, userId={}", request.getOrderNo(), request.getUserId());

        if (request.getOrderNo() == null || request.getOrderNo().trim().isEmpty()) {
            throw new BusinessException(3001, "订单号不能为空");
        }

        Optional<Order> optionalOrder = orderRepository.findByOrderNo(OrderNo.of(request.getOrderNo()));
        if (!optionalOrder.isPresent()) {
            throw new BusinessException(3001, "订单不存在或订单号为空");
        }

        Order order = optionalOrder.get();
        if (order.getStatus() == null) {
            throw new BusinessException(3001, "订单状态无效");
        }

        if (order.getStatus().getCode() == 2) {
            throw new BusinessException(3002, "订单已取消，不可支付");
        }

        if (order.getStatus().getCode() == 1) {
            throw new BusinessException(3002, "订单已支付");
        }

        Optional<PaymentVoucher> optionalVoucher = paymentVoucherRepository.findByOrderId(order.getId());
        PaymentVoucher paymentVoucher;
        if (optionalVoucher.isPresent()) {
            paymentVoucher = optionalVoucher.get();
            if (paymentVoucher.getStatus() == PaymentStatus.SUCCESS) {
                throw new BusinessException(3002, "订单已支付");
            }
            paymentVoucher.setStatus(PaymentStatus.PENDING);
            paymentVoucher.setUpdatedAt(LocalDateTime.now());
            paymentVoucher.setUpdateBy(request.getUserId().toString());
            paymentVoucherRepository.update(paymentVoucher);
        } else {
            String paymentNo = generatePaymentNo();
            PaymentNo payNo = PaymentNo.of(paymentNo);
            
            paymentVoucher = PaymentVoucher.builder()
                    .paymentNo(payNo)
                    .orderId(order.getId())
                    .amount(order.getPayAmount() != null ? Money.of(order.getPayAmount()) : Money.of(order.getTotalAmount()))
                    .status(PaymentStatus.PENDING)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .isDeleted(false)
                    .createBy(request.getUserId().toString())
                    .updateBy(request.getUserId().toString())
                    .build();
            
            paymentVoucherRepository.save(paymentVoucher);
        }

        PaymentResponse response = new PaymentResponse();
        response.setPaymentNo(paymentVoucher.getPaymentNo().getValue());
        response.setOrderNo(order.getOrderNo().getValue());
        response.setAmount(paymentVoucher.getAmount().getAmount().doubleValue());
        response.setStatus(paymentVoucher.getStatus().getCode());

        log.info("支付凭证生成成功: paymentNo={}, orderId={}", paymentVoucher.getPaymentNo().getValue(), order.getId().getValue());
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handlePaymentCallback(PaymentCallbackRequest request) {
        log.info("处理支付回调: paymentNo={}, orderNo={}, amount={}, status={}", 
                request.getPaymentNo(), request.getOrderNo(), request.getAmount(), request.getStatus());

        if (request.getPaymentNo() == null || request.getPaymentNo().trim().isEmpty()) {
            throw new BusinessException(3001, "支付流水号不能为空");
        }

        if (request.getOrderNo() == null || request.getOrderNo().trim().isEmpty()) {
            throw new BusinessException(3001, "订单号不能为空");
        }

        Optional<Order> optionalOrder = orderRepository.findByOrderNo(OrderNo.of(request.getOrderNo()));
        if (!optionalOrder.isPresent()) {
            throw new BusinessException(3001, "订单不存在或订单号为空");
        }

        Order order = optionalOrder.get();

        if (order.getStatus() != null && order.getStatus().getCode() == 2) {
            throw new BusinessException(3004, "订单已关闭，支付回调无效");
        }

        Optional<PaymentVoucher> optionalVoucher = paymentVoucherRepository.findByPaymentNo(PaymentNo.of(request.getPaymentNo()));
        if (!optionalVoucher.isPresent()) {
            throw new BusinessException(3001, "支付凭证不存在");
        }

        PaymentVoucher paymentVoucher = optionalVoucher.get();

        Money orderMoney = order.getPayAmount() != null ? Money.of(order.getPayAmount()) : Money.of(order.getTotalAmount());
        if (!orderMoney.getAmount().equals(BigDecimal.valueOf(request.getAmount()))) {
            paymentVoucher.setStatus(PaymentStatus.FAILED);
            paymentVoucher.setUpdatedAt(LocalDateTime.now());
            paymentVoucher.setUpdateBy("system");
            paymentVoucherRepository.update(paymentVoucher);
            throw new BusinessException(3003, "支付金额与订单金额不符");
        }

        PaymentStatus callbackStatus = PaymentStatus.fromCode(request.getStatus());
        paymentVoucher.setStatus(callbackStatus);
        paymentVoucher.setUpdatedAt(LocalDateTime.now());
        paymentVoucher.setUpdateBy("system");

        if (callbackStatus == PaymentStatus.SUCCESS) {
            PaymentSuccessEvent event = PaymentSuccessEvent.builder()
                    .voucherId(paymentVoucher.getId())
                    .paymentNo(paymentVoucher.getPaymentNo())
                    .orderId(paymentVoucher.getOrderId())
                    .amount(paymentVoucher.getAmount().getAmount())
                    .createdAt(LocalDateTime.now())
                    .build();
            paymentVoucherRepository.update(paymentVoucher);
            log.info("支付成功: paymentNo={}, orderId={}", paymentVoucher.getPaymentNo().getValue(), order.getId().getValue());
        } else if (callbackStatus == PaymentStatus.FAILED) {
            PaymentFailedEvent event = PaymentFailedEvent.builder()
                    .voucherId(paymentVoucher.getId())
                    .paymentNo(paymentVoucher.getPaymentNo())
                    .orderId(paymentVoucher.getOrderId())
                    .amount(paymentVoucher.getAmount().getAmount())
                    .failReason("支付失败")
                    .createdAt(LocalDateTime.now())
                    .build();
            paymentVoucherRepository.update(paymentVoucher);
            log.info("支付失败: paymentNo={}, orderId={}", paymentVoucher.getPaymentNo().getValue(), order.getId().getValue());
        }
    }

    private String generatePaymentNo() {
        return "PAY" + System.currentTimeMillis();
    }
}

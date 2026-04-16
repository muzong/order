package com.orderpay.payment.infrastructure.repository;

import com.orderpay.common.exception.BusinessException;
import com.orderpay.order.domain.model.OrderId;
import com.orderpay.payment.domain.model.*;
import com.orderpay.payment.domain.repository.PaymentVoucherRepository;
import com.orderpay.payment.infrastructure.convert.PaymentVoucherConvert;
import com.orderpay.payment.infrastructure.entity.PaymentVoucherDO;
import com.orderpay.payment.infrastructure.mapper.PaymentVoucherMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class PaymentVoucherRepositoryImpl implements PaymentVoucherRepository {

    private final PaymentVoucherMapper paymentVoucherMapper;
    private final PaymentVoucherConvert paymentVoucherConvert;

    public PaymentVoucherRepositoryImpl(PaymentVoucherMapper paymentVoucherMapper, PaymentVoucherConvert paymentVoucherConvert) {
        this.paymentVoucherMapper = paymentVoucherMapper;
        this.paymentVoucherConvert = paymentVoucherConvert;
    }

    @Override
    public Optional<PaymentVoucher> findById(PaymentVoucherId id) {
        PaymentVoucherDO paymentVoucherDO = paymentVoucherMapper.selectById(id.getValue());
        if (paymentVoucherDO == null) {
            return Optional.empty();
        }
        return Optional.of(paymentVoucherConvert.toDomain(paymentVoucherDO));
    }

    @Override
    public Optional<PaymentVoucher> findByPaymentNo(PaymentNo paymentNo) {
        PaymentVoucherDO paymentVoucherDO = paymentVoucherMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<PaymentVoucherDO>()
                        .eq("payment_no", paymentNo.getValue())
                        .eq("is_deleted", false)
        );
        if (paymentVoucherDO == null) {
            return Optional.empty();
        }
        return Optional.of(paymentVoucherConvert.toDomain(paymentVoucherDO));
    }

    @Override
    public Optional<PaymentVoucher> findByOrderId(OrderId orderId) {
        PaymentVoucherDO paymentVoucherDO = paymentVoucherMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<PaymentVoucherDO>()
                        .eq("order_id", orderId.getValue())
                        .eq("is_deleted", false)
        );
        if (paymentVoucherDO == null) {
            return Optional.empty();
        }
        return Optional.of(paymentVoucherConvert.toDomain(paymentVoucherDO));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public PaymentVoucher save(PaymentVoucher voucher) {
        PaymentVoucherDO paymentVoucherDO = paymentVoucherConvert.toDO(voucher);
        paymentVoucherMapper.insert(paymentVoucherDO);
        voucher.setId(PaymentVoucherId.of(paymentVoucherDO.getId()));
        return voucher;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update(PaymentVoucher voucher) {
        PaymentVoucherDO paymentVoucherDO = paymentVoucherConvert.toDO(voucher);
        paymentVoucherMapper.updateById(paymentVoucherDO);
    }

    @Override
    public boolean existsByPaymentNo(PaymentNo paymentNo) {
        Integer count = paymentVoucherMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<PaymentVoucherDO>()
                        .eq("payment_no", paymentNo.getValue())
                        .eq("is_deleted", false)
        );
        return count != null && count > 0;
    }
}

package com.orderpay.payment.infrastructure.convert;

import com.orderpay.order.domain.model.Money;
import com.orderpay.order.domain.model.OrderId;
import com.orderpay.payment.domain.model.*;
import com.orderpay.payment.infrastructure.entity.PaymentVoucherDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface PaymentVoucherConvert {
    PaymentVoucherConvert INSTANCE = Mappers.getMapper(PaymentVoucherConvert.class);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "paymentNo.value", source = "paymentNo")
    @Mapping(target = "orderId.value", source = "orderId")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "status.code", source = "status")
    PaymentVoucherDO toDO(PaymentVoucher voucher);
    
    PaymentVoucher toDomain(PaymentVoucherDO paymentVoucherDO);
    
    List<PaymentVoucherDO> toDOList(List<PaymentVoucher> vouchers);
    
    List<PaymentVoucher> toDomainList(List<PaymentVoucherDO> paymentVoucherDOs);
}

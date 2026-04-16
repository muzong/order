package com.orderpay.payment.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orderpay.payment.infrastructure.entity.PaymentVoucherDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PaymentVoucherMapper extends BaseMapper<PaymentVoucherDO> {
}

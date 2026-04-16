package com.orderpay.common.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class MoneyUtil {
    private static final DecimalFormat DF = new DecimalFormat("0.00");

    public static String format(BigDecimal amount) {
        return amount == null ? "0.00" : DF.format(amount);
    }

    public static BigDecimal add(BigDecimal... amounts) {
        BigDecimal sum = BigDecimal.ZERO;
        for (BigDecimal amount : amounts) {
            if (amount != null) {
                sum = sum.add(amount);
            }
        }
        return sum;
    }

    public static BigDecimal multiply(BigDecimal amount, int quantity) {
        if (amount == null) {
            return BigDecimal.ZERO;
        }
        return amount.multiply(BigDecimal.valueOf(quantity));
    }
}
package com.example.payment.infrastructure.persistence;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class MoneyHelper {

    private MoneyHelper() {
    }

    public static long toCent(BigDecimal yuan) {
        if (yuan == null) {
            return 0L;
        }
        return yuan.multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP).longValueExact();
    }

    public static BigDecimal toYuan(long cent) {
        return BigDecimal.valueOf(cent).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }
}


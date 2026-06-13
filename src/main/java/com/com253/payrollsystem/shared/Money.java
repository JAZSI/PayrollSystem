package com.com253.payrollsystem.shared;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Money rounding — 2 decimals, HALF_UP (centavo precision). */
public final class Money {

    private Money() {
    }

    public static double round2(double amount) {
        return BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}

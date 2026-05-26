package com.com253.payrollsystem.domain.service.tax;

public final class WithholdingTax {

    private WithholdingTax() {}

    public static double annualTax(double annualIncome) {
        if (annualIncome <= 250_000) {
            return 0.0;
        } else if (annualIncome <= 400_000) {
            return (annualIncome - 250_000) * 0.15;
        } else if (annualIncome <= 800_000) {
            return 22_500 + (annualIncome - 400_000) * 0.20;
        } else if (annualIncome <= 2_000_000) {
            return 102_500 + (annualIncome - 800_000) * 0.25;
        } else if (annualIncome <= 8_000_000) {
            return 402_500 + (annualIncome - 2_000_000) * 0.30;
        }
        return 2_202_500 + (annualIncome - 8_000_000) * 0.35;
    }
}

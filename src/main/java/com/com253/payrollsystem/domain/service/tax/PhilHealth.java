package com.com253.payrollsystem.domain.service.tax;

public final class PhilHealth {

    private PhilHealth() {}

    public static double monthlyContribution(double monthlyRate) {
        double monthlyContribution = monthlyRate * 0.055;
        if (monthlyContribution < 500.00) {
            monthlyContribution = 500.00;
        } else if (monthlyContribution > 2750.00) {
            monthlyContribution = 2750.00;
        }
        return monthlyContribution;
    }
}

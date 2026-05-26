package com.com253.payrollsystem.domain.service.tax;

public final class Pagibig {

    private Pagibig() {}

    public static double monthlyContribution(double monthlyRate) {
        double monthlyContribution;
        if (monthlyRate < 1500.00) {
            monthlyContribution = monthlyRate * 0.01;
        } else {
            monthlyContribution = monthlyRate * 0.02;
        }
        if (monthlyContribution > 100.00) {
            monthlyContribution = 100.00;
        }
        return monthlyContribution;
    }
}

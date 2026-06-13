package com.com253.payrollsystem.shared.domain.tax;

/** Pag-IBIG monthly contribution (1-2%, capped at 100). */
public final class Pagibig {

    private Pagibig() {
    }

    public static double monthlyContribution(double monthlyRate) {
        double monthlyContribution = monthlyRate < 1500.00
                ? monthlyRate * 0.01
                : monthlyRate * 0.02;
        if (monthlyContribution > 100.00) {
            monthlyContribution = 100.00;
        }

        return monthlyContribution;
    }

    /** Employer share: 2% of monthly rate, capped at 100. */
    public static double employerShare(double monthlyRate) {
        return Math.min(monthlyRate * 0.02, 100.00);
    }
}

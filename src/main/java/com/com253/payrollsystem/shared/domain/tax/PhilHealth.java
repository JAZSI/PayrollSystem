package com.com253.payrollsystem.shared.domain.tax;

/** PhilHealth monthly contribution (5.5%, floor 500, ceiling 2750). */
public final class PhilHealth {

    private PhilHealth() {
    }

    public static double monthlyContribution(double monthlyRate) {
        double monthlyContribution = monthlyRate * 0.055;
        if (monthlyContribution < 500.00) {
            monthlyContribution = 500.00;
        } else if (monthlyContribution > 2750.00) {
            monthlyContribution = 2750.00;
        }

        return monthlyContribution;
    }

    /** Employer share: half of the total monthly contribution. */
    public static double employerShare(double monthlyRate) {
        return monthlyContribution(monthlyRate) / 2.0;
    }
}

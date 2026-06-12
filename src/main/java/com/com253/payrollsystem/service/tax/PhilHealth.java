package com.com253.payrollsystem.service.tax;

/**
 * Service for resolving PhilHealth monthly contribution from salary.
 */
public final class PhilHealth {

    private PhilHealth() {
    }

    /**
     * Computes PhilHealth monthly contribution (before cutoff deductions).
     *
     * @param monthlyRate monthly salary basis
     * @return PhilHealth monthly contribution
     */
    public static double monthlyContribution(double monthlyRate) {
        double monthlyContribution = monthlyRate * 0.055;  // 5.5% total

        // Apply floor and ceiling
        if (monthlyContribution < 500.00) {
            monthlyContribution = 500.00;
        } else if (monthlyContribution > 2750.00) {
            monthlyContribution = 2750.00;
        }

        return monthlyContribution;
    }
}

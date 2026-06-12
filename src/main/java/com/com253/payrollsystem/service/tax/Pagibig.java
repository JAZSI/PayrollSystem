package com.com253.payrollsystem.service.tax;

/**
 * Service for resolving Pag-IBIG monthly contribution from salary.
 */
public final class Pagibig {

    private Pagibig() {
    }

    /**
     * Computes Pag-IBIG monthly contribution (before cutoff deductions).
     *
     * @param monthlyRate monthly salary basis
     * @return Pag-IBIG monthly contribution
     */
    public static double monthlyContribution(double monthlyRate) {
        double monthlyContribution;

        if (monthlyRate < 1500.00) {
            monthlyContribution = monthlyRate * 0.01;  // 1% for lower salaries
        } else {
            monthlyContribution = monthlyRate * 0.02;  // 2% for 1,500 and above
        }

        if (monthlyContribution > 100.00) {
            monthlyContribution = 100.00;
        }

        return monthlyContribution;
    }
}

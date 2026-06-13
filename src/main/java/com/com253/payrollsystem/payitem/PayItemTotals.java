package com.com253.payrollsystem.payitem;

/** Active pay-item totals for one employee, split for the payroll engine. */
public record PayItemTotals(
        double taxableAllowances,
        double nonTaxableAllowances,
        double otherDeductions) {

    public static PayItemTotals empty() {
        return new PayItemTotals(0.0, 0.0, 0.0);
    }
}

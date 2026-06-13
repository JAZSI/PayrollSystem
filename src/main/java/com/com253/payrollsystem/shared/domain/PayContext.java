package com.com253.payrollsystem.shared.domain;

/** Per-cut-off inputs beyond attendance: loans, leave coverage, allowances, deductions. */
public record PayContext(
        double loanAmount,
        int coveredLeaveDays,
        double taxableAllowances,
        double nonTaxableAllowances,
        double otherDeductions) {

    /** Loans + leave only; no allowances or other deductions. */
    public static PayContext of(double loanAmount, int coveredLeaveDays) {
        return new PayContext(loanAmount, coveredLeaveDays, 0.0, 0.0, 0.0);
    }

    public double totalAllowances() {
        return taxableAllowances + nonTaxableAllowances;
    }
}

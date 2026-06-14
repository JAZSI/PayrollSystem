package com.com253.payrollsystem.shared.domain.tax;

/**
 * Statutory contribution evaluation seam. The default {@link #HARDCODED} reproduces the
 * built-in 2026 constants exactly; a DB-backed implementation can supply effective-dated
 * tables without changing the pure payroll math.
 */
public interface ContributionTables {

    double sssEmployeeMonthly(double salary);

    double sssEmployerMonthly(double salary);

    double sssEcMonthly(double salary);

    double philhealthTotalMonthly(double monthlyRate);

    double pagibigEmployeeMonthly(double monthlyRate);

    double pagibigEmployerMonthly(double monthlyRate);

    double annualWithholdingTax(double annualTaxable);

    /** Built-in constants — the behavior before effective-dated tables existed. */
    ContributionTables HARDCODED = new HardcodedContributionTables();
}

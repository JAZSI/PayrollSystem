package com.com253.payrollsystem.report.dto;

/** Statutory totals for a cut-off (employee + employer share per agency). */
public record RemittanceReport(
        String period,
        double sssEmployee,
        double sssEmployer,
        double sssEc,
        double philhealthEmployee,
        double philhealthEmployer,
        double pagibigEmployee,
        double pagibigEmployer,
        double tax,
        double grandTotal) {
}

package com.com253.payrollsystem.payroll;

/** Lifecycle of a batch payroll run: draft → approved → locked (posted). */
public enum PayrollRunStatus {
    DRAFT,
    APPROVED,
    LOCKED
}

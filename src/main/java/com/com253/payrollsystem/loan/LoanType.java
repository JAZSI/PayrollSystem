package com.com253.payrollsystem.loan;

/** Kind of loan or cash advance carried by an employee. */
public enum LoanType {
    SSS("SSS Loan"),
    PAGIBIG("Pag-IBIG Loan"),
    COMPANY("Company Loan"),
    CASH_ADVANCE("Cash Advance");

    private final String label;

    LoanType(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}

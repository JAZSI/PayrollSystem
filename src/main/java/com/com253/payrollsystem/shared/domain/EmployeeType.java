package com.com253.payrollsystem.shared.domain;

/** Employee classification with display label and leave eligibility. */
public enum EmployeeType {
    REGULAR("Regular", true),
    PROBATIONARY("Probationary", true),
    CONTRACTUAL("Contractual", false),
    PART_TIMER("PartTimer", false);

    private final String label;
    private final boolean leaveEligible;

    EmployeeType(String label, boolean leaveEligible) {
        this.label = label;
        this.leaveEligible = leaveEligible;
    }

    public String getLabel() { return label; }
    public boolean isLeaveEligible() { return leaveEligible; }
}

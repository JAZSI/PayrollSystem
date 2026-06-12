package com.com253.payrollsystem.model;

/**
 * Employee classification. Single source of truth for the type discriminator
 * (previously scattered as the magic strings "Regular", "Probationary",
 * "Contractual", "PartTimer").
 *
 * <p>Each constant carries its display label (used on the payslip) and whether
 * the type is leave-eligible.
 */
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

    /**
     * Gets the human-readable label shown on the payslip.
     *
     * @return display label (e.g. "Regular", "PartTimer")
     */
    public String getLabel() {
        return label;
    }

    /**
     * Indicates whether this type is eligible for leave credits.
     *
     * @return true if leave-eligible; otherwise false
     */
    public boolean isLeaveEligible() {
        return leaveEligible;
    }
}

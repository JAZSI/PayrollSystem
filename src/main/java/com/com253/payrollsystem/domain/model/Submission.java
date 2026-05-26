package com.com253.payrollsystem.domain.model;

public record Submission(
    int id,
    String employeeId,
    double leaveDays,
    double otHours,
    double loanDeduction,
    Submission.Status status,
    String submittedAt
) {
    public enum Status {
        PENDING,
        APPROVED,
        REJECTED
    }
}
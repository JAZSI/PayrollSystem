package com.com253.payrollsystem.app.service;

import com.com253.payrollsystem.app.port.EmployeeRepositoryPort;
import com.com253.payrollsystem.infrastructure.persistence.sqlite.EmployeeRepository;
import com.com253.payrollsystem.domain.model.Employee;

public final class SubmissionValidator {

    private SubmissionValidator() {}

    public static void validateSubmissionParams(EmployeeRepositoryPort employeeRepository,
                                                String employeeId,
                                                double leaveDays,
                                                double otHours,
                                                double loanDeduction) throws Exception {
        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new IllegalArgumentException("employeeId is required");
        }

        Employee emp = employeeRepository.findById(employeeId).orElse(null);
        if (emp == null) {
            throw new IllegalArgumentException("employee not found: " + employeeId);
        }

        if (Double.isNaN(leaveDays) || leaveDays < 0.0 || leaveDays > 365.0) {
            throw new IllegalArgumentException("leaveDays must be between 0 and 365");
        }

        if (Double.isNaN(otHours) || otHours < 0.0 || otHours > 500.0) {
            throw new IllegalArgumentException("otHours must be between 0 and 500");
        }

        if (Double.isNaN(loanDeduction) || loanDeduction < 0.0) {
            throw new IllegalArgumentException("loanDeduction must be >= 0");
        }

        if (loanDeduction > emp.getLoanBalance().getBalance()) {
            // allow; will be capped later during applyDeductions
        }
    }
}

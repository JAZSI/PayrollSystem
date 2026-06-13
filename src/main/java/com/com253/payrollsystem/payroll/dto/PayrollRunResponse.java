package com.com253.payrollsystem.payroll.dto;

import com.com253.payrollsystem.payroll.PayrollRunStatus;

import java.util.List;

/** Batch run; payslips populated for detail, null for list. */
public record PayrollRunResponse(
        Long id,
        String cutoffPeriod,
        PayrollRunStatus status,
        int employeeCount,
        double totalGross,
        double totalDeductions,
        double totalNet,
        String createdAt,
        List<PayslipResponse> payslips) {
}

package com.com253.payrollsystem.dashboard.dto;

import com.com253.payrollsystem.payroll.dto.PayrollRunResponse;

/** Aggregate KPIs for the dashboard landing page. */
public record DashboardResponse(
        long activeEmployees,
        long totalEmployees,
        long totalPayslips,
        long totalRuns,
        long draftRuns,
        long approvedRuns,
        long lockedRuns,
        PayrollRunResponse latestRun) {
}

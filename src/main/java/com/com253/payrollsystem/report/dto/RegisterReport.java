package com.com253.payrollsystem.report.dto;

import java.util.List;

public record RegisterReport(
        String period,
        List<RegisterRow> rows,
        double totalGross,
        double totalDeductions,
        double totalNet) {
}

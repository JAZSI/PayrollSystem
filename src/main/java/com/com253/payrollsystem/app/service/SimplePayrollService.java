package com.com253.payrollsystem.app.service;

import com.com253.payrollsystem.domain.model.AttendanceRecord;
import com.com253.payrollsystem.domain.model.Employee;
import com.com253.payrollsystem.domain.model.Employee.EmployeeType;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class SimplePayrollService {
    // dont use this this is for ui testing only --- IGNORE ---
    public SimplePayrollResult calculate(Employee employee, List<AttendanceRecord> attendance, LocalDate from, LocalDate to) {
        double totalHours = 0.0;
        double overtimeHours = 0.0;
        double undertimeHours = 0.0;
        int workingDays = 0;

        for (AttendanceRecord record : attendance) {
            Double timeIn = record.getTimeIn();
            Double timeOut = record.getTimeOut();
            if (timeIn == null || timeOut == null) {
                continue;
            }

            workingDays++;
            double dailyHours = Math.max(0.0, timeOut - timeIn);
            totalHours += dailyHours;

            if (dailyHours > 8.0) {
                overtimeHours += (dailyHours - 8.0);
            } else {
                undertimeHours += (8.0 - dailyHours);
            }
        }

        long totalDays = ChronoUnit.DAYS.between(from, to) + 1;
        int absences = (int) Math.max(0, totalDays - workingDays);

        double hourlyRate = employee.getEmployeeType() == EmployeeType.PARTTIMER
                ? employee.getHourlyRate()
                : employee.getMonthlyRate() / 26.0 / 8.0;
        double basicPay = employee.getEmployeeType() == EmployeeType.PARTTIMER
                ? totalHours * hourlyRate
                : employee.getMonthlyRate() / 2.0;
        double overtimePay = overtimeHours * hourlyRate * 1.5;
        double grossPay = basicPay + overtimePay;
        double sss = grossPay * 0.05;
        double philhealth = grossPay * 0.03;
        double pagibig = grossPay * 0.02;
        double tax = grossPay * 0.10;
        double loan = Math.min(employee.getLoanBalance().getBalance(), grossPay * 0.10);
        double netPay = grossPay - sss - philhealth - pagibig - tax - loan;

        return new SimplePayrollResult(
                employee.getName(),
                basicPay,
                overtimePay,
                grossPay,
                sss,
                philhealth,
                pagibig,
                tax,
                loan,
                netPay,
                totalHours,
                overtimeHours,
                undertimeHours,
                absences);
    }

    public record SimplePayrollResult(
            String employeeName,
            double basicPay,
            double overtimePay,
            double grossPay,
            double sssDeduction,
            double philhealthDeduction,
            double pagibigDeduction,
            double taxDeduction,
            double loanDeduction,
            double netPay,
            double totalHours,
            double overtimeHours,
            double undertimeHours,
            int absences) {
    }
}
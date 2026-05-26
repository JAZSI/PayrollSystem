package com.com253.payrollsystem.domain.service;

import com.com253.payrollsystem.domain.model.Employee;
import com.com253.payrollsystem.domain.model.PayrollEntry;
import com.com253.payrollsystem.domain.model.PayrollSettings;
import com.com253.payrollsystem.domain.model.TimeRecord;

public class PayrollCalculator {

    public static double computeHoursWorked(TimeRecord record, PayrollSettings settings) {
        return WorkHoursCalculator.computeHoursWorked(record, settings);
    }

    public static double computeTotalHours(TimeRecord[] records, PayrollSettings settings) {
        return WorkHoursCalculator.computeTotalHours(records, settings);
    }

    public static double computeOvertimeHours(TimeRecord[] records, PayrollSettings settings) {
        return WorkHoursCalculator.computeOvertimeHours(records, settings);
    }

    public static double computeUndertimeHours(TimeRecord[] records, PayrollSettings settings) {
        return WorkHoursCalculator.computeUndertimeHours(records, settings);
    }

    public static int computeAbsentDays(TimeRecord[] records) {
        return WorkHoursCalculator.computeAbsentDays(records);
    }

    public static double computeGrossPay(Employee employee, TimeRecord[] records, PayrollSettings settings) {
        return EarningsCalculator.computeGrossPay(employee, records, settings);
    }

    public static double computeBasicPay(Employee employee, TimeRecord[] records, PayrollSettings settings) {
        return EarningsCalculator.computeBasicPay(employee, records, settings);
    }

    public static double computeOvertimePay(Employee employee, TimeRecord[] records, PayrollSettings settings) {
        return EarningsCalculator.computeOvertimePay(employee, records, settings);
    }

    public static double computeSSSDeduction(double salary) {
        return GovernmentDeductionCalculator.computeSSSDeduction(salary);
    }

    public static double computePhilHealthDeduction(double monthlyRate) {
        return GovernmentDeductionCalculator.computePhilHealthDeduction(monthlyRate);
    }

    public static double computePagibigDeduction(double monthlyRate) {
        return GovernmentDeductionCalculator.computePagibigDeduction(monthlyRate);
    }

    public static double computeWithholdingTax(double taxableIncome) {
        return GovernmentDeductionCalculator.computeWithholdingTax(taxableIncome);
    }

    public static double computeUndertimePenalty(double undertimeHours, double hourlyRate) {
        return PenaltyCalculator.computeUndertimePenalty(undertimeHours, hourlyRate);
    }

    public static double computeAbsencePenalty(Employee employee, int absentDays, PayrollSettings settings) {
        return PenaltyCalculator.computeAbsencePenalty(employee, absentDays, settings);
    }

    public static double computeNetPay(PayrollEntry entry) {
        return NetPayCalculator.computeNetPay(entry);
    }

    public static PayrollEntry buildPayrollEntry(Employee employee, TimeRecord[] records,
                                                 String cutOffPeriod, double loanAmount,
                                                 PayrollSettings settings) {
        return NetPayCalculator.buildPayrollEntry(employee, records, cutOffPeriod, loanAmount, settings);
    }
}

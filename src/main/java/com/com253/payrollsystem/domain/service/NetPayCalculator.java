package com.com253.payrollsystem.domain.service;

import com.com253.payrollsystem.domain.model.Employee;
import com.com253.payrollsystem.domain.model.PayrollEntry;
import com.com253.payrollsystem.domain.model.PayrollSettings;
import com.com253.payrollsystem.domain.model.TimeRecord;

public final class NetPayCalculator {

    private NetPayCalculator() {}

    public static double computeNetPay(PayrollEntry entry) {
        return entry.grossPay()
             - entry.undertimePenalty()
             - entry.absencePenalty()
             - entry.sssDeduction()
             - entry.philhealthDeduction()
             - entry.pagibigDeduction()
             - entry.taxDeduction()
             - entry.loanDeduction();
    }

    public static PayrollEntry buildPayrollEntry(Employee employee, TimeRecord[] records,
                                                 String cutOffPeriod, double loanAmount,
                                                 PayrollSettings settings) {
        double totalHours = WorkHoursCalculator.computeTotalHours(records, settings);
        double overtimeHours = WorkHoursCalculator.computeOvertimeHours(records, settings);
        double undertimeHours = WorkHoursCalculator.computeUndertimeHours(records, settings);
        int absentDays = WorkHoursCalculator.computeAbsentDays(records);

        double basicPay = EarningsCalculator.computeBasicPay(employee, records, settings);
        double nsd = EarningsCalculator.computeNSD(employee, records, settings);
        double holidayPay = EarningsCalculator.computeHolidayPay(employee, records, settings);
        double overtimePay = EarningsCalculator.computeOvertimePay(employee, records, settings);
        double grossPay = basicPay + nsd + holidayPay + overtimePay;

        double monthlyRate = employee.getEmployeeType() == Employee.EmployeeType.PARTTIMER
                ? grossPay * 2.0
                : employee.getMonthlyRate();

        double sss = GovernmentDeductionCalculator.computeSSSDeduction(monthlyRate);
        double philhealth = GovernmentDeductionCalculator.computePhilHealthDeduction(monthlyRate);
        double pagibig = GovernmentDeductionCalculator.computePagibigDeduction(monthlyRate);
        double tax = GovernmentDeductionCalculator.computeWithholdingTax(grossPay - sss - philhealth - pagibig);

        double hourlyRate = employee.getEmployeeType() == Employee.EmployeeType.PARTTIMER
                ? employee.getHourlyRate()
                : (employee.getMonthlyRate() / settings.getWorkingDaysPerMonth()) / 8.0;

        double undertimePenalty = PenaltyCalculator.computeUndertimePenalty(undertimeHours, hourlyRate);
        double absencePenalty = PenaltyCalculator.computeAbsencePenalty(employee, absentDays, settings);

        double netPay = grossPay
                - undertimePenalty
                - absencePenalty
                - sss
                - philhealth
                - pagibig
                - tax
                - loanAmount;

        return new PayrollEntry(
                employee, cutOffPeriod,
                totalHours, overtimeHours, undertimeHours, absentDays,
                basicPay, overtimePay, holidayPay, nsd, grossPay,
                sss, philhealth, pagibig, tax, loanAmount,
                undertimePenalty, absencePenalty,
                netPay);
    }
}

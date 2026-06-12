package com.com253.payrollsystem.service;
import com.com253.payrollsystem.model.Employee;
import com.com253.payrollsystem.model.EmployeeType;
import com.com253.payrollsystem.model.TimeConversions;
import com.com253.payrollsystem.model.PayrollEntry;
import com.com253.payrollsystem.model.PayrollSettings;
import com.com253.payrollsystem.model.TimeRecord;
import com.com253.payrollsystem.service.tax.Pagibig;
import com.com253.payrollsystem.service.tax.PhilHealth;
import com.com253.payrollsystem.service.tax.SSS;
import com.com253.payrollsystem.service.tax.WithholdingTax;

public class PayrollCalculator {

    // Regular day OT.
    private static final double REGULAR_DAY_OVERTIME_MULTIPLIER = 1.25;
    // Regular holiday.
    private static final double REGULAR_HOLIDAY_BASE = 2.00;
    private static final double REGULAR_HOLIDAY_OT = REGULAR_HOLIDAY_BASE * 1.30;

    // Special holiday / rest day.
    private static final double REST_DAY_BASE = 1.30;
    private static final double REST_DAY_OT = REST_DAY_BASE * 1.30;

    /** Standard paid hours in a full workday. */
    private static final double STANDARD_HOURS_PER_DAY = 8.0;
    /** Semi-monthly payroll: two cut-offs per month. */
    private static final double CUTOFFS_PER_MONTH = 2.0;
    /** Semi-monthly payroll: 24 cut-offs per year (12 months x 2). */
    private static final double CUTOFFS_PER_YEAR = 24.0;

    /**
     * Computes worked hours for one time record.
    * Lunch break is subtracted only when the shift extends past 11:00 AM.
     *
     * @param record time record for a day
     * @param settings payroll configuration values
     * @return worked hours for the day
     */
    public static double computeHoursWorked(TimeRecord record, PayrollSettings settings) {
        if (record.isAbsent()) {
            return 0.0;
        }

        double inHours  = TimeConversions.hhmmToHours(record.getTimeIn());
        double outHours = TimeConversions.hhmmToHours(record.getTimeOut());

        double effectiveStartHour = Math.max(settings.getWorkdayStartHour(), inHours);

        double hoursWorked = outHours - effectiveStartHour;
        if (outHours > settings.getLunchBreakStartHour()) {
            hoursWorked -= 1.0;
        }

        return Math.max(0.0, hoursWorked);
    }

    /**
     * Adds worked hours across all records.
     *
     * @param records daily time records
     * @param settings payroll configuration values
     * @return total worked hours
     */
    public static double computeTotalHours(TimeRecord[] records, PayrollSettings settings) {
        double total = 0.0;
        for (TimeRecord record : records) {
            total += computeHoursWorked(record, settings);
        }
        return total;
    }

    /**
     * Computes total overtime hours across records.
     *
     * @param records daily time records
     * @param settings payroll configuration values
     * @return total overtime hours
     */
    public static double computeOvertimeHours(TimeRecord[] records, PayrollSettings settings) {
        double overtimeTotal = 0.0;
        for (TimeRecord record : records) {
            if (!record.isAbsent()) {
                overtimeTotal += computeOvertimeHoursAfter(record, settings);
            }
        }
        return overtimeTotal;
    }

    /**
     * Computes total undertime hours across records.
     *
     * @param records daily time records
     * @param settings payroll configuration values
     * @return total undertime hours
     */
    public static double computeUndertimeHours(TimeRecord[] records, PayrollSettings settings) {
        double undertimeTotal = 0.0;
        for (TimeRecord record : records) {
            if (!record.isAbsent()) {
                double hoursWorked = computeHoursWorked(record, settings);
                if (hoursWorked < STANDARD_HOURS_PER_DAY) {
                    undertimeTotal += (STANDARD_HOURS_PER_DAY - hoursWorked);
                }
            }
        }
        return undertimeTotal;
    }

    /**
     * Counts how many records are marked absent.
     *
     * @param records daily time records
     * @return number of absent days
     */
    public static int computeAbsentDays(TimeRecord[] records) {
        int count = 0;
        for (TimeRecord record : records) {
            if (record.isAbsent()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Computes gross pay based on employee type, hours, and overtime.
     *
     * @param employee employee data
     * @param records daily time records
     * @param settings payroll configuration values
     * @return gross pay for the cut-off
     */
    public static double computeGrossPay(Employee employee, TimeRecord[] records, PayrollSettings settings) {
        return computeBasicPay(employee, records, settings) + computeOvertimePay(employee, records, settings);
    }

    /**
     * Computes basic pay (without overtime).
     *
     * @param employee employee data
     * @param records daily time records
     * @param settings payroll configuration values
     * @return basic pay for the cut-off
     */
    public static double computeBasicPay(Employee employee, TimeRecord[] records, PayrollSettings settings) {
        EmployeeType type = employee.getEmployeeType();

        if (type == EmployeeType.PART_TIMER) {
            double totalHours = computeTotalHours(records, settings);
            return totalHours * employee.getHourlyRate();
        }

        // Regular, Probationary, Contractual
        return employee.getMonthlyRate() / CUTOFFS_PER_MONTH;
    }

    /**
     * Computes overtime pay for the cut-off.
     *
     * @param employee employee data
     * @param records daily time records
     * @param settings payroll configuration values
     * @return overtime pay amount
     */
    public static double computeOvertimePay(Employee employee, TimeRecord[] records, PayrollSettings settings) {
        double overtimePay = 0.0;
        EmployeeType type = employee.getEmployeeType();

        if (type == EmployeeType.PART_TIMER) {
            for (TimeRecord record : records) {
                if (!record.isAbsent()) {
                    double overtimeHours = computeOvertimeHoursAfter(record, settings);
                    double overtimeMultiplier = getOvertimeMultiplier(record);
                    overtimePay += overtimeHours * employee.getHourlyRate() * (overtimeMultiplier - 1.0);
                }
            }
            return overtimePay;
        }

        // Regular, Probationary, Contractual
        double hourlyRate = computeDailyRate(employee, settings) / STANDARD_HOURS_PER_DAY;

        for (TimeRecord record : records) {
            if (!record.isAbsent()) {
                double overtimeHours = computeOvertimeHoursAfter(record, settings);
                double overtimeMultiplier = getOvertimeMultiplier(record);
                overtimePay += overtimeHours * hourlyRate * overtimeMultiplier;
            }
        }

        return overtimePay;
    }

    /**
     * Returns overtime multiplier based on holiday type.
     *
     * @param record time record for the day
     * @return overtime multiplier value
     */
    private static double getOvertimeMultiplier(TimeRecord record) {
        if (record.isRegularHoliday()) {
            return REGULAR_HOLIDAY_OT;
        }
        if (record.isRestDayHoliday()) {
            return REST_DAY_OT;
        }
        return REGULAR_DAY_OVERTIME_MULTIPLIER;
    }

    /**
     * Computes overtime hours that occur after 5:00 PM.
     *
     * @param record time record for a day
     * @param settings payroll configuration values
     * @return hours worked after 5:00 PM
     */
    private static double computeOvertimeHoursAfter(TimeRecord record, PayrollSettings settings) {
        double outHours = TimeConversions.hhmmToHours(record.getTimeOut());
        return Math.max(0.0, outHours - settings.getOvertimeStartHour());
    }

    private static double computeDailyRate(Employee employee, PayrollSettings settings) {
        return employee.getMonthlyRate() / settings.getWorkingDaysPerMonth();
    }

    /**
     * Computes SSS deduction per cut-off from monthly salary.
     *
     * @param salary monthly salary basis
     * @return SSS deduction for one cut-off
     */
    public static double computeSSSDeduction(double salary) {
        double monthlyContribution = SSS.monthlyContribution(salary);
        return monthlyContribution / CUTOFFS_PER_MONTH;
    }

    /**
     * Computes PhilHealth deduction per cut-off.
     *
     * @param monthlyRate monthly salary basis
     * @return PhilHealth deduction for one cut-off
     */
    public static double computePhilHealthDeduction(double monthlyRate) {
        double monthlyContribution = PhilHealth.monthlyContribution(monthlyRate);

        // Employee share = half of the total contribution, then split per cut-off.
        double employeeMonthlyShare = monthlyContribution / 2.0;
        return employeeMonthlyShare / CUTOFFS_PER_MONTH;
    }

    /**
     * Computes Pag-IBIG deduction per cut-off.
     *
     * @param monthlyRate monthly salary basis
     * @return Pag-IBIG deduction for one cut-off
     */
    public static double computePagibigDeduction(double monthlyRate) {
        double monthlyContribution = Pagibig.monthlyContribution(monthlyRate);

        return monthlyContribution / CUTOFFS_PER_MONTH;
    }

    /**
     * Computes withholding tax per cut-off from taxable income.
     *
     * @param taxableIncome taxable income for one cut-off
     * @return withholding tax for one cut-off
     */
    public static double computeWithholdingTax(double taxableIncome) {
        // Annualize, compute annual tax, then return the per-cut-off amount.
        double annualIncome = taxableIncome * CUTOFFS_PER_YEAR;
        double annualTax = WithholdingTax.annualTax(annualIncome);

        return annualTax / CUTOFFS_PER_YEAR;
    }

    /**
     * Computes undertime penalty using undertime hours and hourly rate.
     *
     * @param undertimeHours total undertime hours
     * @param hourlyRate hourly rate used for penalty
     * @return undertime penalty amount
     */
    public static double computeUndertimePenalty(double undertimeHours, double hourlyRate) {
        return undertimeHours * hourlyRate;
    }

    /**
     * Computes absence penalty based on employee type and leave credits.
     *
     * @param employee employee data
     * @param absentDays total absent days
     * @param settings payroll configuration values
     * @return absence penalty amount
     */
    public static double computeAbsencePenalty(Employee employee, int absentDays, PayrollSettings settings) {
        if (absentDays <= 0) {
            return 0.0;
        }

        int leaveCredits = Math.max(0, settings.getLeaveCreditsFor(employee));
        int chargeableDays = Math.max(0, absentDays - leaveCredits);

        if (chargeableDays == 0) {
            return 0.0;
        }

        return chargeableDays * computeDailyRate(employee, settings);
    }

    /**
     * Computes net pay by subtracting deductions and penalties from gross pay.
     *
     * @param entry payroll entry with computed values
     * @return final net pay
     */
    public static double computeNetPay(PayrollEntry entry) {
        return entry.getGrossPay()
             - entry.getUndertimePenalty()
             - entry.getAbsencePenalty()
             - entry.getSssDeduction()
             - entry.getPhilhealthDeduction()
             - entry.getPagibigDeduction()
             - entry.getTaxDeduction()
             - entry.getLoanDeduction();
    }

    /**
     * Builds a complete payroll entry from employee input and time records.
     *
     * @param employee employee data
     * @param records daily time records
     * @param cutOffPeriod selected cut-off period
     * @param loanAmount loan deduction amount
      * @param settings payroll configuration values
     * @return populated payroll entry
     */
    public static PayrollEntry buildPayrollEntry(Employee employee,
                                                 TimeRecord[] records,
                                                 String cutOffPeriod,
                                                                 double loanAmount,
                                                                 PayrollSettings settings) {
        PayrollEntry entry = new PayrollEntry(employee, cutOffPeriod);

        // --- Attendance summary ---
          double totalHours    = computeTotalHours(records, settings);
          double overtimeHours = computeOvertimeHours(records, settings);
          double undertimeHours = computeUndertimeHours(records, settings);
        int    absentDays    = computeAbsentDays(records);

        entry.setTotalHoursWorked(totalHours);
        entry.setOvertimeHours(overtimeHours);
        entry.setUndertimeHours(undertimeHours);
        entry.setAbsentDays(absentDays);

        // --- Earnings ---
        double basicPay = computeBasicPay(employee, records, settings);
        double overtimePay = computeOvertimePay(employee, records, settings);
        double grossPay = basicPay + overtimePay;

        entry.setBasicPay(basicPay);
        entry.setOvertimePay(overtimePay);
        entry.setGrossPay(grossPay);

        // --- Government-mandated deductions ---
        double monthlyRate = employee.getMonthlyRate();
        if (employee.getEmployeeType() == EmployeeType.PART_TIMER) {
            // Approximate a monthly basis from one cut-off's gross pay.
            monthlyRate = grossPay * CUTOFFS_PER_MONTH;
        }

        double sss        = computeSSSDeduction(monthlyRate);
        double philhealth = computePhilHealthDeduction(monthlyRate);
        double pagibig    = computePagibigDeduction(monthlyRate);

        entry.setSssDeduction(sss);
        entry.setPhilhealthDeduction(philhealth);
        entry.setPagibigDeduction(pagibig);

        // --- Withholding tax ---
        double taxableIncome = grossPay - sss - philhealth - pagibig;
        double tax = computeWithholdingTax(taxableIncome);
        entry.setTaxDeduction(tax);

        // --- Loan ---
        entry.setLoanDeduction(loanAmount);

        // --- Penalties ---
        double dailyRate  = computeDailyRate(employee, settings);
        double hourlyRate = dailyRate / STANDARD_HOURS_PER_DAY;

        double undertimePenalty = computeUndertimePenalty(undertimeHours, hourlyRate);
        double absencePenalty   = computeAbsencePenalty(employee, absentDays, settings);

        entry.setUndertimePenalty(undertimePenalty);
        entry.setAbsencePenalty(absencePenalty);

        // --- Net pay ---
        double netPay = computeNetPay(entry);
        entry.setNetPay(netPay);

        return entry;
    }
}
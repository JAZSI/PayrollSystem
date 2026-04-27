package com.com253.payrollsystem.Service;
import com.com253.payrollsystem.Model.Employee;
import com.com253.payrollsystem.Model.PayrollEntry;
import com.com253.payrollsystem.Model.PayrollSettings;
import com.com253.payrollsystem.Model.TimeRecord;
import com.com253.payrollsystem.Service.Tax.Pagibig;
import com.com253.payrollsystem.Service.Tax.PhilHealth;
import com.com253.payrollsystem.Service.Tax.SSS;
import com.com253.payrollsystem.Service.Tax.WithholdingTax;

public class PayrollCalculator {

    // Regular day OT.
    private static final double REGULAR_DAY_OVERTIME_MULTIPLIER = 1.25;
    // Regular holiday.
    private static final double REGULAR_HOLIDAY_BASE = 2.00;
    private static final double REGULAR_HOLIDAY_OT = REGULAR_HOLIDAY_BASE * 1.30;

    // Special holiday / rest day.
    private static final double REST_DAY_BASE = 1.30;
    private static final double REST_DAY_OT = REST_DAY_BASE * 1.30;

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

        int timeIn  = record.getTimeIn();
        int timeOut = record.getTimeOut();

        // Convert HHMM to decimal hours
        // e.g. 800  →  8 + 00/60  = 8.0
        //      930  →  9 + 30/60  = 9.5
        //      1700 → 17 + 00/60  = 17.0
        double inHours  = (timeIn  / 100) + (timeIn  % 100) / 60.0;
        double outHours = (timeOut / 100) + (timeOut % 100) / 60.0;

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
                overtimeTotal += computeovertimehoursafter(record, settings);
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
                if (hoursWorked < 8.0) {
                    undertimeTotal += (8.0 - hoursWorked);
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
        String type = employee.getEmployeeType();

        if (type.equals("PartTimer")) {
            double totalHours = computeTotalHours(records, settings);
            return totalHours * employee.getHourlyRate();
        }

        // Regular, Probationary, Contractual
        return employee.getMonthlyRate() / 2.0;
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
        String type = employee.getEmployeeType();

        if (type.equals("PartTimer")) {
            for (TimeRecord record : records) {
                if (!record.isAbsent()) {
                    double overtimeHours = computeovertimehoursafter(record, settings);
                    double overtimeMultiplier = getOvertimeMultiplier(record);
                    overtimePay += overtimeHours * employee.getHourlyRate() * (overtimeMultiplier - 1.0);
                }
            }
            return overtimePay;
        }

        // Regular, Probationary, Contractual
        double hourlyRate = computeDailyRate(employee, settings) / 8.0;

        for (TimeRecord record : records) {
            if (!record.isAbsent()) {
                double overtimeHours = computeovertimehoursafter(record, settings);
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
    private static double computeovertimehoursafter(TimeRecord record, PayrollSettings settings) {
        double outHours = (record.getTimeOut() / 100) + (record.getTimeOut() % 100) / 60.0;
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
        return monthlyContribution / 2.0;
    }

    /**
     * Computes PhilHealth deduction per cut-off.
     *
     * @param monthlyRate monthly salary basis
     * @return PhilHealth deduction for one cut-off
     */
    public static double computePhilHealthDeduction(double monthlyRate) {
        double monthlyContribution = PhilHealth.monthlyContribution(monthlyRate);

        // Employee share = half; deducted per cut-off = another half
        // Employee monthly share = monthlyContribution / 2
        // Per cut-off = monthlyContribution / 2 / 2 = monthlyContribution / 4
        return monthlyContribution / 4.0;
    }

    /**
     * Computes Pag-IBIG deduction per cut-off.
     *
     * @param monthlyRate monthly salary basis
     * @return Pag-IBIG deduction for one cut-off
     */
    public static double computePagibigDeduction(double monthlyRate) {
        double monthlyContribution = Pagibig.monthlyContribution(monthlyRate);

        return monthlyContribution / 2.0;  // per cut-off
    }

    /**
     * Computes withholding tax per cut-off from taxable income.
     *
     * @param taxableIncome taxable income for one cut-off
     * @return withholding tax for one cut-off
     */
    public static double computeWithholdingTax(double taxableIncome) {
        // Annualize (24 cut-offs per year)
        double annualIncome = taxableIncome * 24.0;
        double annualTax = WithholdingTax.annualTax(annualIncome);

        // Return per-cut-off amount (Semi-monthly)
        return annualTax / 24.0;
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
        if (employee.getEmployeeType().equals("PartTimer")) {
            monthlyRate = grossPay * 2.0;
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
        double hourlyRate = dailyRate / 8.0;

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
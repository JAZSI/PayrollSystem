package com.com253.payrollsystem.shared.domain;

import com.com253.payrollsystem.shared.domain.tax.ContributionTables;
import com.com253.payrollsystem.shared.domain.tax.Pagibig;
import com.com253.payrollsystem.shared.domain.tax.PhilHealth;
import com.com253.payrollsystem.shared.domain.tax.SSS;
import com.com253.payrollsystem.shared.domain.tax.WithholdingTax;

import com.com253.payrollsystem.shared.Money;

/** Pure payroll computation engine (no Spring/DB dependencies). */
public class PayrollCalculator {

    private static final double REGULAR_DAY_OVERTIME_MULTIPLIER = 1.25;
    private static final double REGULAR_HOLIDAY_OT = 2.00 * 1.30;
    private static final double REST_DAY_OT = 1.30 * 1.30;

    private static final double STANDARD_HOURS_PER_DAY = 8.0;
    private static final double CUTOFFS_PER_MONTH = 2.0;
    private static final double CUTOFFS_PER_YEAR = 24.0;

    private static final double NIGHT_DIFF_RATE = 0.10;
    private static final double NIGHT_WINDOW_START = 22.0;
    private static final double NIGHT_WINDOW_END = 6.0;

    // ----------------------------- Attendance -----------------------------

    /** Worked hours for a day; subtracts lunch when shift passes 11:00. */
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

    public static double computeTotalHours(TimeRecord[] records, PayrollSettings settings) {
        double total = 0.0;
        for (TimeRecord record : records) {
            total += computeHoursWorked(record, settings);
        }
        return total;
    }

    public static double computeOvertimeHours(TimeRecord[] records, PayrollSettings settings) {
        double overtimeTotal = 0.0;
        for (TimeRecord record : records) {
            if (!record.isAbsent()) {
                overtimeTotal += computeOvertimeHoursAfter(record, settings);
            }
        }
        return overtimeTotal;
    }

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

    public static int computeAbsentDays(TimeRecord[] records) {
        int count = 0;
        for (TimeRecord record : records) {
            if (record.isAbsent()) {
                count++;
            }
        }
        return count;
    }

    // ------------------------------ Earnings ------------------------------

    public static double computeGrossPay(Employee employee, TimeRecord[] records, PayrollSettings settings) {
        return computeBasicPay(employee, records, settings) + computeOvertimePay(employee, records, settings);
    }

    /** Hours x rate for part-timers; half the monthly rate otherwise. */
    public static double computeBasicPay(Employee employee, TimeRecord[] records, PayrollSettings settings) {
        if (employee.getEmployeeType() == EmployeeType.PART_TIMER) {
            return computeTotalHours(records, settings) * employee.getHourlyRate();
        }
        return employee.getMonthlyRate() / CUTOFFS_PER_MONTH;
    }

    /** OT hours x hourly rate x holiday multiplier, summed over the cut-off. */
    public static double computeOvertimePay(Employee employee, TimeRecord[] records, PayrollSettings settings) {
        double overtimePay = 0.0;

        if (employee.getEmployeeType() == EmployeeType.PART_TIMER) {
            for (TimeRecord record : records) {
                if (!record.isAbsent()) {
                    double overtimeHours = computeOvertimeHoursAfter(record, settings);
                    double multiplier = getOvertimeMultiplier(record);
                    overtimePay += overtimeHours * employee.getHourlyRate() * (multiplier - 1.0);
                }
            }
            return overtimePay;
        }

        double hourlyRate = computeDailyRate(employee, settings) / STANDARD_HOURS_PER_DAY;
        for (TimeRecord record : records) {
            if (!record.isAbsent()) {
                double overtimeHours = computeOvertimeHoursAfter(record, settings);
                double multiplier = getOvertimeMultiplier(record);
                overtimePay += overtimeHours * hourlyRate * multiplier;
            }
        }
        return overtimePay;
    }

    private static double getOvertimeMultiplier(TimeRecord record) {
        if (record.isRegularHoliday()) {
            return REGULAR_HOLIDAY_OT;
        }
        if (record.isRestDayHoliday()) {
            return REST_DAY_OT;
        }
        return REGULAR_DAY_OVERTIME_MULTIPLIER;
    }

    private static double computeOvertimeHoursAfter(TimeRecord record, PayrollSettings settings) {
        double outHours = TimeConversions.hhmmToHours(record.getTimeOut());
        return Math.max(0.0, outHours - settings.getOvertimeStartHour());
    }

    private static double computeDailyRate(Employee employee, PayrollSettings settings) {
        return employee.getMonthlyRate() / settings.getWorkingDaysPerMonth();
    }

    /** Base hourly rate: hourly for part-timers, daily/8 otherwise. */
    private static double hourlyRateOf(Employee employee, PayrollSettings settings) {
        if (employee.getEmployeeType() == EmployeeType.PART_TIMER) {
            return employee.getHourlyRate();
        }
        return computeDailyRate(employee, settings) / STANDARD_HOURS_PER_DAY;
    }

    // --------------------------- Night differential ---------------------------

    /** Worked hours falling in the 22:00–06:00 window for one day. */
    public static double computeNightHours(TimeRecord record) {
        if (record.isAbsent()) {
            return 0.0;
        }
        double in = TimeConversions.hhmmToHours(record.getTimeIn());
        double out = TimeConversions.hhmmToHours(record.getTimeOut());
        return overlap(in, out, NIGHT_WINDOW_START, 24.0) + overlap(in, out, 0.0, NIGHT_WINDOW_END);
    }

    public static double computeNightHours(TimeRecord[] records) {
        double total = 0.0;
        for (TimeRecord record : records) {
            total += computeNightHours(record);
        }
        return total;
    }

    /** Night differential: +10% of hourly rate per night hour worked. */
    public static double computeNightDiffPay(Employee employee, TimeRecord[] records, PayrollSettings settings) {
        return computeNightHours(records) * hourlyRateOf(employee, settings) * NIGHT_DIFF_RATE;
    }

    private static double overlap(double start, double end, double windowStart, double windowEnd) {
        return Math.max(0.0, Math.min(end, windowEnd) - Math.max(start, windowStart));
    }

    // ----------------------------- Deductions -----------------------------

    public static double computeSSSDeduction(double salary) {
        return SSS.monthlyContribution(salary) / CUTOFFS_PER_MONTH;
    }

    /** Employee share (half), split per cut-off. */
    public static double computePhilHealthDeduction(double monthlyRate) {
        double employeeMonthlyShare = PhilHealth.monthlyContribution(monthlyRate) / 2.0;
        return employeeMonthlyShare / CUTOFFS_PER_MONTH;
    }

    public static double computePagibigDeduction(double monthlyRate) {
        return Pagibig.monthlyContribution(monthlyRate) / CUTOFFS_PER_MONTH;
    }

    /** Annualize, apply TRAIN brackets, return per cut-off. */
    public static double computeWithholdingTax(double taxableIncome) {
        double annualTax = WithholdingTax.annualTax(taxableIncome * CUTOFFS_PER_YEAR);
        return annualTax / CUTOFFS_PER_YEAR;
    }

    // ----------------------------- Penalties ------------------------------

    public static double computeUndertimePenalty(double undertimeHours, double hourlyRate) {
        return undertimeHours * hourlyRate;
    }

    /** Charges absent days not covered by approved leave at the daily rate. */
    public static double computeAbsencePenalty(Employee employee, int absentDays,
                                               int coveredLeaveDays, PayrollSettings settings) {
        if (absentDays <= 0) {
            return 0.0;
        }
        int chargeableDays = Math.max(0, absentDays - Math.max(0, coveredLeaveDays));
        return chargeableDays == 0 ? 0.0 : chargeableDays * computeDailyRate(employee, settings);
    }

    public static double computeNetPay(PayrollEntry entry) {
        return entry.getGrossPay()
             - entry.getUndertimePenalty()
             - entry.getAbsencePenalty()
             - entry.getSssDeduction()
             - entry.getPhilhealthDeduction()
             - entry.getPagibigDeduction()
             - entry.getTaxDeduction()
             - entry.getLoanDeduction()
             - entry.getOtherDeductions();
    }

    // ------------------------------- Build --------------------------------

    /** Back-compat: loan + flat settings leave credits, no allowances or other deductions. */
    public static PayrollEntry buildPayrollEntry(Employee employee, TimeRecord[] records,
                                                 String cutOffPeriod, double loanAmount,
                                                 PayrollSettings settings) {
        return buildPayrollEntry(employee, records, cutOffPeriod, settings,
                PayContext.of(loanAmount, Math.max(0, settings.getLeaveCreditsFor(employee))));
    }

    /** Uses the built-in statutory tables. */
    public static PayrollEntry buildPayrollEntry(Employee employee, TimeRecord[] records,
                                                 String cutOffPeriod, PayrollSettings settings,
                                                 PayContext ctx) {
        return buildPayrollEntry(employee, records, cutOffPeriod, settings, ctx,
                ContributionTables.HARDCODED);
    }

    /** Computes a full payslip; money rounded to centavo (HALF_UP). */
    public static PayrollEntry buildPayrollEntry(Employee employee, TimeRecord[] records,
                                                 String cutOffPeriod, PayrollSettings settings,
                                                 PayContext ctx, ContributionTables tables) {
        PayrollEntry entry = new PayrollEntry(employee, cutOffPeriod);

        // --------------------------- Attendance ---------------------------
        double totalHours = computeTotalHours(records, settings);
        double overtimeHours = computeOvertimeHours(records, settings);
        double undertimeHours = computeUndertimeHours(records, settings);
        int absentDays = computeAbsentDays(records);
        entry.setTotalHoursWorked(totalHours);
        entry.setOvertimeHours(overtimeHours);
        entry.setUndertimeHours(undertimeHours);
        entry.setAbsentDays(absentDays);

        // ---------------------------- Earnings ----------------------------
        double basicPay = computeBasicPay(employee, records, settings);
        double overtimePay = computeOvertimePay(employee, records, settings);
        double nightDiffPay = computeNightDiffPay(employee, records, settings);
        double earnedPay = basicPay + overtimePay + nightDiffPay;
        double allowances = ctx.totalAllowances();
        double grossPay = earnedPay + allowances;
        entry.setBasicPay(Money.round2(basicPay));
        entry.setOvertimePay(Money.round2(overtimePay));
        entry.setNightDiffPay(Money.round2(nightDiffPay));
        entry.setAllowances(Money.round2(allowances));
        entry.setGrossPay(Money.round2(grossPay));

        // -------------------------- Deductions ----------------------------
        double monthlyRate = employee.getMonthlyRate();
        if (employee.getEmployeeType() == EmployeeType.PART_TIMER) {
            monthlyRate = earnedPay * CUTOFFS_PER_MONTH; // approximate monthly basis
        }
        double sss = tables.sssEmployeeMonthly(monthlyRate) / CUTOFFS_PER_MONTH;
        double philhealth = tables.philhealthTotalMonthly(monthlyRate) / 2.0 / CUTOFFS_PER_MONTH;
        double pagibig = tables.pagibigEmployeeMonthly(monthlyRate) / CUTOFFS_PER_MONTH;
        entry.setSssDeduction(Money.round2(sss));
        entry.setPhilhealthDeduction(Money.round2(philhealth));
        entry.setPagibigDeduction(Money.round2(pagibig));

        // --------------- Employer share (info only, per cut-off) ----------
        entry.setEmployerSss(Money.round2(tables.sssEmployerMonthly(monthlyRate) / CUTOFFS_PER_MONTH));
        entry.setEmployerPhilhealth(Money.round2(tables.philhealthTotalMonthly(monthlyRate) / 2.0 / CUTOFFS_PER_MONTH));
        entry.setEmployerPagibig(Money.round2(tables.pagibigEmployerMonthly(monthlyRate) / CUTOFFS_PER_MONTH));
        entry.setEmployerEc(Money.round2(tables.sssEcMonthly(monthlyRate) / CUTOFFS_PER_MONTH));

        double taxableIncome = earnedPay + ctx.taxableAllowances();
        double tax = tables.annualWithholdingTax(
                (taxableIncome - sss - philhealth - pagibig) * CUTOFFS_PER_YEAR) / CUTOFFS_PER_YEAR;
        entry.setTaxDeduction(Money.round2(tax));
        entry.setLoanDeduction(Money.round2(ctx.loanAmount()));
        entry.setOtherDeductions(Money.round2(ctx.otherDeductions()));

        // --------------------------- Penalties ----------------------------
        double hourlyRate = computeDailyRate(employee, settings) / STANDARD_HOURS_PER_DAY;
        entry.setUndertimePenalty(Money.round2(computeUndertimePenalty(undertimeHours, hourlyRate)));
        entry.setAbsencePenalty(Money.round2(
                computeAbsencePenalty(employee, absentDays, ctx.coveredLeaveDays(), settings)));

        // ----------------------------- Net --------------------------------
        entry.setNetPay(Money.round2(computeNetPay(entry)));
        return entry;
    }
}

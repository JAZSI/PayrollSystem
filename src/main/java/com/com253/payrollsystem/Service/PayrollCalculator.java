package com.com253.payrollsystem.Service;

import com.com253.payrollsystem.Model.Employee;
import com.com253.payrollsystem.Model.Employee.EmployeeType;
import com.com253.payrollsystem.Model.PayrollEntry;
import com.com253.payrollsystem.Model.PayrollSettings;
import com.com253.payrollsystem.Model.TimeRecord;
import com.com253.payrollsystem.Service.Tax.Pagibig;
import com.com253.payrollsystem.Service.Tax.PhilHealth;
import com.com253.payrollsystem.Service.Tax.SSS;
import com.com253.payrollsystem.Service.Tax.WithholdingTax;

/**
 * Static utility class for all payroll computation.
 * All methods are pure functions — no side effects, no state.
 */
public class PayrollCalculator {

    // Regular day OT: 25% additional on top of normal rate.
    private static final double REGULAR_DAY_OT_MULTIPLIER = 1.25;

    // Regular holiday: 200% for all hours worked, plus 30% extra on OT hours.
    private static final double REGULAR_HOLIDAY_MULTIPLIER = 2.00;
    private static final double REGULAR_HOLIDAY_OT_PREMIUM = 0.30;

    // Special non-working / rest day: 130% for all hours, plus 30% extra on OT hours.
    private static final double SPECIAL_DAY_MULTIPLIER = 1.30;
    private static final double SPECIAL_DAY_OT_PREMIUM = 0.30;

    // Night shift differential: 10% additional on hours between 10 PM and 6 AM.
    private static final double NSD_RATE = 0.10;

    private static final double STANDARD_HOURS_PER_DAY = 8.0;

    // Minimum hours required to receive pay for the day.
    // Working less than this results in 0 hours credited (no pay, no penalty).
    private static final double MINIMUM_PAID_HOURS = 1.0;

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

        // Handle midnight-crossing shifts (e.g., 22:00 to 06:00)
        if (outHours <= inHours) {
            outHours += 24.0;
        }

        double effectiveStartHour = Math.max(settings.getWorkdayStartHour(), inHours);

        double hoursWorked = outHours - effectiveStartHour;
        if (outHours > settings.getLunchBreakStartHour()) {
            hoursWorked -= 1.0;
        }

        // Short shifts (less than minimum paid hours) receive no pay and are not penalized.
        hoursWorked = Math.max(0.0, hoursWorked);
        if (hoursWorked < MINIMUM_PAID_HOURS) {
            return 0.0;
        }

        return hoursWorked;
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
                overtimeTotal += computeOvertimeHoursForRecord(record, settings);
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
                // Skip short-shift records (computeHoursWorked returns 0 for < 1 hour).
                if (hoursWorked == 0.0) {
                    continue;
                }
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
        return computeBasicPay(employee, records, settings)
             + computeNSD(employee, records, settings)
             + computeHolidayPay(employee, records, settings)
             + computeOvertimePay(employee, records, settings);
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
        if (employee.getEmployeeType() == EmployeeType.PARTTIMER) {
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
        double hourlyRate = getHourlyRate(employee, settings);

        for (TimeRecord record : records) {
            if (record.isAbsent()) {
                continue;
            }

            double overtimeHours = computeOvertimeHoursForRecord(record, settings);
            double multiplier = computeOtMultiplier(record);

            // multiplier - 1.0 gives the extra premium on top of the base rate.
            // For regular days: 1.25 - 1.0 = +25%.
            // For holiday OT: 1.60 - 1.0 = +60% on top of the holiday base;
            //                 1.39 - 1.0 = +39% on top of the special day base.
            // The holiday base itself is already paid via computeHolidayPay().
            overtimePay += overtimeHours * hourlyRate * (multiplier - 1.0);
        }
        return overtimePay;
    }

    /**
     * Returns the overtime multipler for a given time record.
     * For holidays, this is the EXTRA premium (e.g. 0.30) not the base multiplier.
     * The holiday base pay is handled separately in computeHolidayPay().
     *
     * @param record time record for the day
     * @return multiplier value
     */
    private static double computeOtMultiplier(TimeRecord record) {
        if (record.isRegularHoliday()) {
            // 200% base + 30% OT = 260% total rate for holiday OT hours.
            // The 200% base is already paid via basicPay + holidayPay.
            // The extra 60% comes from here: 2.0 * 0.30 = 0.60, so (1.60 - 1.0) = 0.60.
            return 1.0 + (REGULAR_HOLIDAY_MULTIPLIER * REGULAR_HOLIDAY_OT_PREMIUM);
        }
        if (record.isRestDayHoliday()) {
            // 130% base + 30% OT = 169% total rate for holiday OT hours.
            // The 130% base is already paid via basicPay + holidayPay.
            // The extra 39% comes from here: 1.30 * 0.30 = 0.39, so (1.39 - 1.0) = 0.39.
            return 1.0 + (SPECIAL_DAY_MULTIPLIER * SPECIAL_DAY_OT_PREMIUM);
        }
        return REGULAR_DAY_OT_MULTIPLIER; // 1.25 → (1.25 - 1.0) = 0.25
    }

    /**
     * Computes overtime hours that occur after the overtime start threshold.
     *
     * @param record   time record for a day
     * @param settings payroll configuration values
     * @return hours worked beyond the overtime start hour
     */
    private static double computeOvertimeHoursForRecord(TimeRecord record, PayrollSettings settings) {
        double outHours = (record.getTimeOut() / 100) + (record.getTimeOut() % 100) / 60.0;
        return Math.max(0.0, outHours - settings.getOvertimeStartHour());
    }

    /**
     * Gets the hourly rate for an employee.
     * For PartTimer this is the direct hourly rate; for others it's derived from daily rate.
     *
     * @param employee employee data
     * @param settings payroll configuration values
     * @return hourly rate
     */
    private static double getHourlyRate(Employee employee, PayrollSettings settings) {
        if (employee.getEmployeeType() == EmployeeType.PARTTIMER) {
            return employee.getHourlyRate();
        }
        return computeDailyRate(employee, settings) / STANDARD_HOURS_PER_DAY;
    }

    private static double computeDailyRate(Employee employee, PayrollSettings settings) {
        if (employee.getEmployeeType() == EmployeeType.PARTTIMER) {
            return employee.getHourlyRate() * STANDARD_HOURS_PER_DAY;
        }
        return employee.getMonthlyRate() / settings.getWorkingDaysPerMonth();
    }

    /**
     * Computes the extra pay employees receive for working on holidays.
     * For each holiday worked, hours worked get the statutory holiday multiplier
     * on top of the normal rate.
     *
     * @param employee employee data
     * @param records  daily time records
     * @param settings payroll configuration values
     * @return total holiday pay premium for the cut-off
     */
    private static double computeHolidayPay(Employee employee, TimeRecord[] records, PayrollSettings settings) {
        double holidayPay = 0.0;
        double hourlyRate = getHourlyRate(employee, settings);

        for (TimeRecord record : records) {
            if (record.isAbsent() || !record.isHoliday()) {
                continue;
            }

            double hoursWorked = computeHoursWorked(record, settings);

            if (record.isRegularHoliday()) {
                holidayPay += hoursWorked * hourlyRate * (REGULAR_HOLIDAY_MULTIPLIER - 1.0);
            } else if (record.isRestDayHoliday()) {
                holidayPay += hoursWorked * hourlyRate * (SPECIAL_DAY_MULTIPLIER - 1.0);
            }
        }
        return holidayPay;
    }

    /**
     * Computes night shift differential for hours worked between 10 PM and 6 AM.
     * Applies an additional 10% of the hourly rate on top of whatever pay
     * the NSD hours already receive (basic, holiday, or OT).
     *
     * @param employee employee data
     * @param records  daily time records
     * @param settings payroll configuration values
     * @return total night shift differential for the cut-off
     */
    private static double computeNSD(Employee employee, TimeRecord[] records, PayrollSettings settings) {
        double nsdPay = 0.0;
        double hourlyRate = getHourlyRate(employee, settings);

        // NSD window: 22:00 to 06:00 next day.
        // Treated as hours [22.0, 46.0) — the 6 AM boundary becomes 30.0 (24+6).
        final double NSD_START_HOUR = 22.0;
        final double NSD_END_HOUR   = 30.0; // 6 AM = 24 + 6

        for (TimeRecord record : records) {
            if (record.isAbsent()) {
                continue;
            }

            double timeIn  = (record.getTimeIn()  / 100) + (record.getTimeIn()  % 100) / 60.0;
            double timeOut = (record.getTimeOut() / 100) + (record.getTimeOut() % 100) / 60.0;

            // If shift crosses midnight, extend the coordinate system forward by 24.
            // e.g., 22:00 to 06:00 becomes [22, 30] in a continuous timeline.
            if (timeOut <= timeIn) {
                timeOut += 24.0;
            }

            double nsdHours = Math.max(0.0, Math.min(timeOut, NSD_END_HOUR) - Math.max(timeIn, NSD_START_HOUR));

            if (nsdHours > 0.0) {
                nsdPay += nsdHours * hourlyRate * NSD_RATE;
            }
        }
        return nsdPay;
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

        int leaveCredits = employee.isHasLeave()
                ? employee.getLeaveBalance().getTotal()
                : 0;
        
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
   * @param employee   employee data
   * @param records    daily time records
   * @param cutOffPeriod selected cut-off period
   * @param loanAmount loan deduction amount
   * @param settings   payroll configuration values
   * @return populated payroll entry
   */
    public static PayrollEntry buildPayrollEntry(Employee employee, TimeRecord[] records,
                                                 String cutOffPeriod, double loanAmount,
                                                 PayrollSettings settings) {
      // Attendance summary
      double totalHours     = computeTotalHours(records, settings);
      double overtimeHours  = computeOvertimeHours(records, settings);
      double undertimeHours = computeUndertimeHours(records, settings);
      int    absentDays     = computeAbsentDays(records);

      // Earnings
      double basicPay    = computeBasicPay(employee, records, settings);
      double nsd         = computeNSD(employee, records, settings);
      double holidayPay  = computeHolidayPay(employee, records, settings);
      double overtimePay = computeOvertimePay(employee, records, settings);
      double grossPay    = basicPay + nsd + holidayPay + overtimePay;

      // Government-mandated deductions
      double monthlyRate = employee.getMonthlyRate();
      if (employee.getEmployeeType() == EmployeeType.PARTTIMER) {
          monthlyRate = grossPay * 2.0;
      }

      double sss        = computeSSSDeduction(monthlyRate);
      double philhealth = computePhilHealthDeduction(monthlyRate);
      double pagibig    = computePagibigDeduction(monthlyRate);
      double tax        = computeWithholdingTax(grossPay - sss - philhealth - pagibig);

      // Penalties
      double hourlyRate       = getHourlyRate(employee, settings);
      double undertimePenalty = computeUndertimePenalty(undertimeHours, hourlyRate);
      double absencePenalty   = computeAbsencePenalty(employee, absentDays, settings);

      // Net pay
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
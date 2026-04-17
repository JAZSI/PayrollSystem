package com.com253.payrollsystem.Service;
import com.com253.payrollsystem.Model.Employee;
import com.com253.payrollsystem.Model.PayrollEntry;
import com.com253.payrollsystem.Model.TimeRecord;

public class PayrollCalculator {

    private static final double WORKDAY_START_HOUR = 8.0;
    private static final double LUNCH_BREAK_START_HOUR = 11.0;

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
     * @return worked hours for the day
     */
    public static double computeHoursWorked(TimeRecord record) {
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

        // Work hours only start counting from 8:00 AM onward.
        double effectiveStartHour = Math.max(WORKDAY_START_HOUR, inHours);

        double hoursWorked = outHours - effectiveStartHour;
        if (outHours > LUNCH_BREAK_START_HOUR) {
            hoursWorked -= 1.0;
        }

        return Math.max(0.0, hoursWorked);
    }

    /**
     * Adds worked hours across all records.
     *
     * @param records daily time records
     * @return total worked hours
     */
    public static double computeTotalHours(TimeRecord[] records) {
        double total = 0.0;
        for (TimeRecord record : records) {
            total += computeHoursWorked(record);
        }
        return total;
    }

    /**
     * Computes total overtime hours across records.
     *
     * @param records daily time records
     * @return total overtime hours
     */
    public static double computeOvertimeHours(TimeRecord[] records) {
        double overtimeTotal = 0.0;
        for (TimeRecord record : records) {
            if (!record.isAbsent()) {
                double hoursWorked = computeHoursWorked(record);
                if (hoursWorked > 8.0) {
                    overtimeTotal += (hoursWorked - 8.0);
                }
            }
        }
        return overtimeTotal;
    }

    /**
     * Computes total undertime hours across records.
     *
     * @param records daily time records
     * @return total undertime hours
     */
    public static double computeUndertimeHours(TimeRecord[] records) {
        double undertimeTotal = 0.0;
        for (TimeRecord record : records) {
            if (!record.isAbsent()) {
                double hoursWorked = computeHoursWorked(record);
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
     * @return gross pay for the cut-off
     */
    public static double computeGrossPay(Employee employee, TimeRecord[] records) {
        return computeBasicPay(employee, records) + computeOvertimePay(employee, records);
    }

    /**
     * Computes basic pay (without overtime).
     *
     * @param employee employee data
     * @param records daily time records
     * @return basic pay for the cut-off
     */
    public static double computeBasicPay(Employee employee, TimeRecord[] records) {
        String type = employee.getEmployeeType();

        if (type.equals("PartTimer")) {
            double totalHours = computeTotalHours(records);
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
     * @return overtime pay amount
     */
    public static double computeOvertimePay(Employee employee, TimeRecord[] records) {
        double overtimePay = 0.0;
        String type = employee.getEmployeeType();

        if (type.equals("PartTimer")) {
            for (TimeRecord record : records) {
                if (!record.isAbsent()) {
                    double overtimeHours = Math.max(0.0, computeHoursWorked(record) - 8.0);
                    double overtimeMultiplier = getOvertimeMultiplier(record);
                    overtimePay += overtimeHours * employee.getHourlyRate() * (overtimeMultiplier - 1.0);
                }
            }
            return overtimePay;
        }

        // Regular, Probationary, Contractual
        double hourlyRate = employee.computeDailyRate() / 8.0;

        for (TimeRecord record : records) {
            if (!record.isAbsent()) {
                double overtimeHours = Math.max(0.0, computeHoursWorked(record) - 8.0);
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
     * Computes SSS deduction per cut-off from monthly salary.
     *
     * @param salary monthly salary basis
     * @return SSS deduction for one cut-off
     */
    public static double computeSSSDeduction(double salary) {
        double contribution;

        if      (salary < 5_250)  contribution = 250;
        else if (salary < 5_750)  contribution = 275;
        else if (salary < 6_250)  contribution = 300;
        else if (salary < 6_750)  contribution = 325;
        else if (salary < 7_250)  contribution = 350;
        else if (salary < 7_750)  contribution = 375;
        else if (salary < 8_250)  contribution = 400;
        else if (salary < 8_750)  contribution = 425;
        else if (salary < 9_250)  contribution = 450;
        else if (salary < 9_750)  contribution = 475;
        else if (salary < 10_250) contribution = 500;
        else if (salary < 10_750) contribution = 525;
        else if (salary < 11_250) contribution = 550;
        else if (salary < 11_750) contribution = 575;
        else if (salary < 12_250) contribution = 600;
        else if (salary < 12_750) contribution = 625;
        else if (salary < 13_250) contribution = 650;
        else if (salary < 13_750) contribution = 675;
        else if (salary < 14_250) contribution = 700;
        else if (salary < 14_750) contribution = 725;
        else if (salary < 15_250) contribution = 750;
        else if (salary < 15_750) contribution = 775;
        else if (salary < 16_250) contribution = 800;
        else if (salary < 16_750) contribution = 825;
        else if (salary < 17_250) contribution = 850;
        else if (salary < 17_750) contribution = 875;
        else if (salary < 18_250) contribution = 900;
        else if (salary < 18_750) contribution = 925;
        else if (salary < 19_250) contribution = 950;
        else if (salary < 19_750) contribution = 975;
        else if (salary < 20_250) contribution = 1_000;
        else if (salary < 20_750) contribution = 1_025;
        else if (salary < 21_250) contribution = 1_050;
        else if (salary < 21_750) contribution = 1_075;
        else if (salary < 22_250) contribution = 1_100;
        else if (salary < 22_750) contribution = 1_125;
        else if (salary < 23_250) contribution = 1_150;
        else if (salary < 23_750) contribution = 1_175;
        else if (salary < 24_250) contribution = 1_200;
        else if (salary < 24_750) contribution = 1_225;
        else if (salary < 25_250) contribution = 1_250;
        else if (salary < 25_750) contribution = 1_275;
        else if (salary < 26_250) contribution = 1_300;
        else if (salary < 26_750) contribution = 1_325;
        else if (salary < 27_250) contribution = 1_350;
        else if (salary < 27_750) contribution = 1_375;
        else if (salary < 28_250) contribution = 1_400;
        else if (salary < 28_750) contribution = 1_425;
        else if (salary < 29_250) contribution = 1_450;
        else if (salary < 29_750) contribution = 1_475;
        else if (salary < 30_250) contribution = 1_500;
        else if (salary < 30_750) contribution = 1_525;
        else if (salary < 31_250) contribution = 1_550;
        else if (salary < 31_750) contribution = 1_575;
        else if (salary < 32_250) contribution = 1_600;
        else if (salary < 32_750) contribution = 1_625;
        else if (salary < 33_250) contribution = 1_650;
        else if (salary < 33_750) contribution = 1_675;
        else if (salary < 34_250) contribution = 1_700;
        else if (salary < 34_750) contribution = 1_725;
        else                      contribution = 1_750; // cap at 35,000+

        // Per cutoff (semi-monthly)
        return contribution / 2.0;
    }

    /**
     * Computes PhilHealth deduction per cut-off.
     *
     * @param monthlyRate monthly salary basis
     * @return PhilHealth deduction for one cut-off
     */
    public static double computePhilHealthDeduction(double monthlyRate) {
        double monthlyContribution = monthlyRate * 0.055;  // 5.5% total

        // Apply floor and ceiling
        if (monthlyContribution < 500.00) {
            monthlyContribution = 500.00;
        } else if (monthlyContribution > 2750.00) {
            monthlyContribution = 2750.00;
        }

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
        double monthlyContribution;

        if (monthlyRate < 1500.00) {
            monthlyContribution = monthlyRate * 0.01;  // 1% for lower salaries
        } else {
            monthlyContribution = monthlyRate * 0.02;  // 2% for 1,500 and above
        }

        if (monthlyContribution > 100.00) {
            monthlyContribution = 100.00;
        }

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

        double annualTax;

        if (annualIncome <= 250_000) {
            annualTax = 0.0;
        } else if (annualIncome <= 400_000) {
            annualTax = (annualIncome - 250_000) * 0.15;
        } else if (annualIncome <= 800_000) {
            annualTax = 22_500 + (annualIncome - 400_000) * 0.20;
        } else if (annualIncome <= 2_000_000) {
            annualTax = 102_500 + (annualIncome - 800_000) * 0.25;
        } else if (annualIncome <= 8_000_000) {
            annualTax = 402_500 + (annualIncome - 2_000_000) * 0.30;
        } else {
            annualTax = 2_202_500 + (annualIncome - 8_000_000) * 0.35;
        }

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
     * @return absence penalty amount
     */
    public static double computeAbsencePenalty(Employee employee, int absentDays) {
        if (employee.getEmployeeType().equals("PartTimer")) {
            return 0.0;
        }

        if (employee.isHasLeave() && absentDays <= 5) {
            // All absent days are covered by leave credits — no penalty
            return 0.0;
        }

        // Contractual or PartTimer (hasLeave == false), or absences exceed leave credits
        int chargeableDays = absentDays;
        if (employee.isHasLeave() && absentDays > 5) {
            // Only days beyond the 5-credit allowance are charged
            chargeableDays = absentDays - 5;
        }

        return chargeableDays * employee.computeDailyRate();
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
     * @return populated payroll entry
     */
    public static PayrollEntry buildPayrollEntry(Employee employee,
                                                 TimeRecord[] records,
                                                 String cutOffPeriod,
                                                 double loanAmount) {
        PayrollEntry entry = new PayrollEntry(employee, cutOffPeriod);

        // --- Attendance summary ---
        double totalHours    = computeTotalHours(records);
        double overtimeHours = computeOvertimeHours(records);
        double undertimeHours = computeUndertimeHours(records);
        int    absentDays    = computeAbsentDays(records);

        entry.setTotalHoursWorked(totalHours);
        entry.setOvertimeHours(overtimeHours);
        entry.setUndertimeHours(undertimeHours);
        entry.setAbsentDays(absentDays);

        // --- Earnings ---
        double basicPay = computeBasicPay(employee, records);
        double overtimePay = computeOvertimePay(employee, records);
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
        double dailyRate  = employee.computeDailyRate();
        double hourlyRate = dailyRate / 8.0;

        double undertimePenalty = computeUndertimePenalty(undertimeHours, hourlyRate);
        double absencePenalty   = computeAbsencePenalty(employee, absentDays);

        entry.setUndertimePenalty(undertimePenalty);
        entry.setAbsencePenalty(absencePenalty);

        // --- Net pay ---
        double netPay = computeNetPay(entry);
        entry.setNetPay(netPay);

        return entry;
    }
}
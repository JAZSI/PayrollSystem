package com.com253.payrollsystem.Model;

/**
 * Represents a snapshot of a computed payroll result for a specific employee
 * and cutoff period. Immutable — all fields set once at construction.
 */
public class PayrollEntry {
    private final Employee employee;
    private final String cutOffPeriod;
    private final double totalHoursWorked;
    private final double overtimeHours;
    private final double undertimeHours;
    private final int absentDays;
    private final double basicPay;
    private final double overtimePay;
    private final double holidayPay;
    private final double nightShiftDifferential;
    private final double grossPay;
    private final double sssDeduction;
    private final double philhealthDeduction;
    private final double pagibigDeduction;
    private final double taxDeduction;
    private final double loanDeduction;
    private final double undertimePenalty;
    private final double absencePenalty;
    private final double netPay;

    /**
     * Creates a payroll entry with all computed values.
     *
     * @param employee            payroll employee reference
     * @param cutOffPeriod        payroll cutoff period
     * @param totalHoursWorked    total hours worked in the period
     * @param overtimeHours       overtime hours worked
     * @param undertimeHours      undertime hours
     * @param absentDays          days absent
     * @param basicPay            basic salary for the cutoff
     * @param overtimePay         overtime pay amount
     * @param holidayPay          additional pay for working on holidays
     * @param nightShiftDifferential additional pay for hours between 10 PM and 6 AM
     * @param grossPay            total earnings before deductions
     * @param sssDeduction        SSS contribution deduction
     * @param philhealthDeduction PhilHealth contribution deduction
     * @param pagibigDeduction    Pag-IBIG contribution deduction
     * @param taxDeduction        withholding tax deduction
     * @param loanDeduction       loan amortization deduction
     * @param undertimePenalty    undertime penalty amount
     * @param absencePenalty      absence penalty amount
     * @param netPay              final take-home pay
     */
    public PayrollEntry(Employee employee, String cutOffPeriod,
            double totalHoursWorked, double overtimeHours, double undertimeHours,
            int absentDays, double basicPay, double overtimePay, double holidayPay,
            double nightShiftDifferential, double grossPay, double sssDeduction,
            double philhealthDeduction, double pagibigDeduction, double taxDeduction,
            double loanDeduction, double undertimePenalty, double absencePenalty,
            double netPay) {
        this.employee = employee;
        this.cutOffPeriod = cutOffPeriod;
        this.totalHoursWorked = totalHoursWorked;
        this.overtimeHours = overtimeHours;
        this.undertimeHours = undertimeHours;
        this.absentDays = absentDays;
        this.basicPay = basicPay;
        this.overtimePay = overtimePay;
        this.holidayPay = holidayPay;
        this.nightShiftDifferential = nightShiftDifferential;
        this.grossPay = grossPay;
        this.sssDeduction = sssDeduction;
        this.philhealthDeduction = philhealthDeduction;
        this.pagibigDeduction = pagibigDeduction;
        this.taxDeduction = taxDeduction;
        this.loanDeduction = loanDeduction;
        this.undertimePenalty = undertimePenalty;
        this.absencePenalty = absencePenalty;
        this.netPay = netPay;
    }

    /**
     * Gets the employee reference.
     *
     * @return employee
     */
    public Employee getEmployee() {
        return employee;
    }

    /**
     * Gets the cutoff period.
     *
     * @return cutoff period
     */
    public String getCutOffPeriod() {
        return cutOffPeriod;
    }

    /**
     * Gets total hours worked.
     *
     * @return total hours worked
     */
    public double getTotalHoursWorked() {
        return totalHoursWorked;
    }

    /**
     * Gets overtime hours.
     *
     * @return overtime hours
     */
    public double getOvertimeHours() {
        return overtimeHours;
    }

    /**
     * Gets undertime hours.
     *
     * @return undertime hours
     */
    public double getUndertimeHours() {
        return undertimeHours;
    }

    /**
     * Gets absent days.
     *
     * @return absent day count
     */
    public int getAbsentDays() {
        return absentDays;
    }

    /**
     * Gets basic pay.
     *
     * @return basic pay
     */
    public double getBasicPay() {
        return basicPay;
    }

    /**
     * Gets overtime pay.
     *
     * @return overtime pay
     */
    public double getOvertimePay() {
        return overtimePay;
    }

    /**
     * Gets additional pay for working on holidays.
     *
     * @return holiday pay
     */
    public double getHolidayPay() {
        return holidayPay;
    }

    /**
     * Gets night shift differential (10% additional pay for hours between
     * 10 PM and 6 AM).
     *
     * @return night shift differential
     */
    public double getNightShiftDifferential() {
        return nightShiftDifferential;
    }

    /**
     * Gets gross pay.
     *
     * @return gross pay
     */
    public double getGrossPay() {
        return grossPay;
    }

    /**
     * Gets SSS deduction.
     *
     * @return SSS deduction
     */
    public double getSssDeduction() {
        return sssDeduction;
    }

    /**
     * Gets PhilHealth deduction.
     *
     * @return PhilHealth deduction
     */
    public double getPhilhealthDeduction() {
        return philhealthDeduction;
    }

    /**
     * Gets Pag-IBIG deduction.
     *
     * @return Pag-IBIG deduction
     */
    public double getPagibigDeduction() {
        return pagibigDeduction;
    }

    /**
     * Gets tax deduction.
     *
     * @return tax deduction
     */
    public double getTaxDeduction() {
        return taxDeduction;
    }

    /**
     * Gets loan deduction.
     *
     * @return loan deduction
     */
    public double getLoanDeduction() {
        return loanDeduction;
    }

    /**
     * Gets undertime penalty.
     *
     * @return undertime penalty
     */
    public double getUndertimePenalty() {
        return undertimePenalty;
    }

    /**
     * Gets absence penalty.
     *
     * @return absence penalty
     */
    public double getAbsencePenalty() {
        return absencePenalty;
    }

    /**
     * Gets net pay.
     *
     * @return net pay
     */
    public double getNetPay() {
        return netPay;
    }
}
package com.com253.payrollsystem.Model;

/**
 * Immutable snapshot of a stored payroll record retrieved for reporting.
 * Corresponds to one row in the payroll_entries table.
 */
public class PayrollReportEntry {

    private final int id;
    private final String employeeId;
    private final String employeeName;
    private final String cutOffPeriod;
    private final double totalHours;
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
    private final String createdAt;

    /**
     * Creates a report entry from a database row.
     *
     * @param id                      database row id
     * @param employeeId              employee identifier
     * @param employeeName            employee display name
     * @param cutOffPeriod            cutoff period label
     * @param totalHours              total hours worked
     * @param overtimeHours           overtime hours worked
     * @param undertimeHours          undertime hours
     * @param absentDays              days absent
     * @param basicPay                basic salary earned
     * @param overtimePay             overtime pay earned
     * @param holidayPay              holiday premium earned
     * @param nightShiftDifferential  night shift differential
     * @param grossPay                total earnings
     * @param sssDeduction            SSS contribution
     * @param philhealthDeduction     PhilHealth contribution
     * @param pagibigDeduction        Pag-IBIG contribution
     * @param taxDeduction            withholding tax
     * @param loanDeduction           loan amortization
     * @param undertimePenalty        undertime penalty
     * @param absencePenalty          absence penalty
     * @param netPay                  take-home pay
     * @param createdAt               timestamp of record creation
     */
    public PayrollReportEntry(int id, String employeeId, String employeeName,
            String cutOffPeriod, double totalHours, double overtimeHours,
            double undertimeHours, int absentDays, double basicPay,
            double overtimePay, double holidayPay, double nightShiftDifferential,
            double grossPay, double sssDeduction, double philhealthDeduction,
            double pagibigDeduction, double taxDeduction, double loanDeduction,
            double undertimePenalty, double absencePenalty, double netPay,
            String createdAt) {
        this.id = id;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.cutOffPeriod = cutOffPeriod;
        this.totalHours = totalHours;
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
        this.createdAt = createdAt;
    }

    /**
     * Convenience factory that builds a report entry from a PayrollEntry
     * and the employee's display name.
     *
     * @param entry    computed payroll entry
     * @return        report snapshot ready for saving or exporting
     */
    public static PayrollReportEntry fromPayrollEntry(PayrollEntry entry) {
        return new PayrollReportEntry(
            0,
            entry.getEmployee().getEmployeeId(),
            entry.getEmployee().getName(),
            entry.getCutOffPeriod(),
            entry.getTotalHoursWorked(),
            entry.getOvertimeHours(),
            entry.getUndertimeHours(),
            entry.getAbsentDays(),
            entry.getBasicPay(),
            entry.getOvertimePay(),
            entry.getHolidayPay(),
            entry.getNightShiftDifferential(),
            entry.getGrossPay(),
            entry.getSssDeduction(),
            entry.getPhilhealthDeduction(),
            entry.getPagibigDeduction(),
            entry.getTaxDeduction(),
            entry.getLoanDeduction(),
            entry.getUndertimePenalty(),
            entry.getAbsencePenalty(),
            entry.getNetPay(),
            null
        );
    }

    /**
     * Gets the database row id.
     *
     * @return id
     */
    public int getId() { return id; }

    /**
     * Gets the employee identifier.
     *
     * @return employee id
     */
    public String getEmployeeId() { return employeeId; }

    /**
     * Gets the employee display name.
     *
     * @return employee name
     */
    public String getEmployeeName() { return employeeName; }

    /**
     * Gets the cutoff period label.
     *
     * @return cutoff period
     */
    public String getCutOffPeriod() { return cutOffPeriod; }

    /**
     * Gets total hours worked in the period.
     *
     * @return total hours
     */
    public double getTotalHours() { return totalHours; }

    /**
     * Gets overtime hours worked.
     *
     * @return overtime hours
     */
    public double getOvertimeHours() { return overtimeHours; }

    /**
     * Gets undertime hours.
     *
     * @return undertime hours
     */
    public double getUndertimeHours() { return undertimeHours; }

    /**
     * Gets days absent.
     *
     * @return absent day count
     */
    public int getAbsentDays() { return absentDays; }

    /**
     * Gets basic pay.
     *
     * @return basic pay
     */
    public double getBasicPay() { return basicPay; }

    /**
     * Gets overtime pay.
     *
     * @return overtime pay
     */
    public double getOvertimePay() { return overtimePay; }

    /**
     * Gets additional pay for working on holidays.
     *
     * @return holiday pay
     */
    public double getHolidayPay() { return holidayPay; }

    /**
     * Gets night shift differential.
     *
     * @return NSD
     */
    public double getNightShiftDifferential() { return nightShiftDifferential; }

    /**
     * Gets gross pay.
     *
     * @return gross pay
     */
    public double getGrossPay() { return grossPay; }

    /**
     * Gets SSS deduction.
     *
     * @return SSS deduction
     */
    public double getSssDeduction() { return sssDeduction; }

    /**
     * Gets PhilHealth deduction.
     *
     * @return PhilHealth deduction
     */
    public double getPhilhealthDeduction() { return philhealthDeduction; }

    /**
     * Gets Pag-IBIG deduction.
     *
     * @return Pag-IBIG deduction
     */
    public double getPagibigDeduction() { return pagibigDeduction; }

    /**
     * Gets tax deduction.
     *
     * @return tax deduction
     */
    public double getTaxDeduction() { return taxDeduction; }

    /**
     * Gets loan deduction.
     *
     * @return loan deduction
     */
    public double getLoanDeduction() { return loanDeduction; }

    /**
     * Gets undertime penalty.
     *
     * @return undertime penalty
     */
    public double getUndertimePenalty() { return undertimePenalty; }

    /**
     * Gets absence penalty.
     *
     * @return absence penalty
     */
    public double getAbsencePenalty() { return absencePenalty; }

    /**
     * Gets net pay.
     *
     * @return net pay
     */
    public double getNetPay() { return netPay; }

    /**
     * Gets the record creation timestamp.
     *
     * @return created at timestamp
     */
    public String getCreatedAt() { return createdAt; }

    /**
     * Sum of all deductions for this entry.
     * Includes all government contributions, taxes, and penalties.
     *
     * @return total deductions
     */
    public double getTotalDeductions() {
        return sssDeduction + philhealthDeduction + pagibigDeduction
             + taxDeduction + loanDeduction + undertimePenalty + absencePenalty;
    }
}
package com.com253.payrollsystem.Model;

/**
 * Represents a payroll entry for a specific cutoff period.
 */
public class PayrollEntry {
    private Employee employee;
    private String cutOffPeriod;
    private double totalHoursWorked;
    private double overtimeHours;
    private double undertimeHours;
    private int absentDays;
    private double basicPay;
    private double overtimePay;
    private double grossPay;
    private double sssDeduction;
    private double philhealthDeduction;
    private double pagibigDeduction;
    private double taxDeduction;
    private double loanDeduction;
    private double undertimePenalty;
    private double absencePenalty;
    private double netPay;

    /**
     * Creates a payroll entry with default numeric values.
     *
     * @param employee payroll employee reference
     * @param cutOffPeriod payroll cutoff period
     */
    public PayrollEntry(Employee employee, String cutOffPeriod) {
        this.employee = employee;
        this.cutOffPeriod = cutOffPeriod;
        this.totalHoursWorked = 0.0;
        this.overtimeHours = 0.0;
        this.undertimeHours = 0.0;
        this.absentDays = 0;
        this.basicPay = 0.0;
        this.overtimePay = 0.0;
        this.grossPay = 0.0;
        this.sssDeduction = 0.0;
        this.philhealthDeduction = 0.0;
        this.pagibigDeduction = 0.0;
        this.taxDeduction = 0.0;
        this.loanDeduction = 0.0;
        this.undertimePenalty = 0.0;
        this.absencePenalty = 0.0;
        this.netPay = 0.0;
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

    /**
     * Sets total hours worked.
     *
     * @param totalHoursWorked total hours worked
     */
    public void setTotalHoursWorked(double totalHoursWorked) {
        this.totalHoursWorked = totalHoursWorked;
    }

    /**
     * Sets overtime hours.
     *
     * @param overtimeHours overtime hours
     */
    public void setOvertimeHours(double overtimeHours) {
        this.overtimeHours = overtimeHours;
    }

    /**
     * Sets undertime hours.
     *
     * @param undertimeHours undertime hours
     */
    public void setUndertimeHours(double undertimeHours) {
        this.undertimeHours = undertimeHours;
    }

    /**
     * Sets absent days.
     *
     * @param absentDays absent day count
     */
    public void setAbsentDays(int absentDays) {
        this.absentDays = absentDays;
    }

    /**
     * Sets basic pay.
     *
     * @param basicPay basic pay
     */
    public void setBasicPay(double basicPay) {
        this.basicPay = basicPay;
    }

    /**
     * Sets overtime pay.
     *
     * @param overtimePay overtime pay
     */
    public void setOvertimePay(double overtimePay) {
        this.overtimePay = overtimePay;
    }

    /**
     * Sets gross pay.
     *
     * @param grossPay gross pay
     */
    public void setGrossPay(double grossPay) {
        this.grossPay = grossPay;
    }

    /**
     * Sets SSS deduction.
     *
     * @param sssDeduction SSS deduction
     */
    public void setSssDeduction(double sssDeduction) {
        this.sssDeduction = sssDeduction;
    }

    /**
     * Sets PhilHealth deduction.
     *
     * @param philhealthDeduction PhilHealth deduction
     */
    public void setPhilhealthDeduction(double philhealthDeduction) {
        this.philhealthDeduction = philhealthDeduction;
    }

    /**
     * Sets Pag-IBIG deduction.
     *
     * @param pagibigDeduction Pag-IBIG deduction
     */
    public void setPagibigDeduction(double pagibigDeduction) {
        this.pagibigDeduction = pagibigDeduction;
    }

    /**
     * Sets tax deduction.
     *
     * @param taxDeduction tax deduction
     */
    public void setTaxDeduction(double taxDeduction) {
        this.taxDeduction = taxDeduction;
    }

    /**
     * Sets loan deduction.
     *
     * @param loanDeduction loan deduction
     */
    public void setLoanDeduction(double loanDeduction) {
        this.loanDeduction = loanDeduction;
    }

    /**
     * Sets undertime penalty.
     *
     * @param undertimePenalty undertime penalty
     */
    public void setUndertimePenalty(double undertimePenalty) {
        this.undertimePenalty = undertimePenalty;
    }

    /**
     * Sets absence penalty.
     *
     * @param absencePenalty absence penalty
     */
    public void setAbsencePenalty(double absencePenalty) {
        this.absencePenalty = absencePenalty;
    }

    /**
     * Sets net pay.
     *
     * @param netPay net pay
     */
    public void setNetPay(double netPay) {
        this.netPay = netPay;
    }
}

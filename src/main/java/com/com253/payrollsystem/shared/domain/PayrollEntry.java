package com.com253.payrollsystem.shared.domain;

/** Payroll result for one cut-off. */
public class PayrollEntry {

    private final Employee employee;
    private final String cutOffPeriod;

    // ----------------------------- Attendance -----------------------------
    private double totalHoursWorked;
    private double overtimeHours;
    private double undertimeHours;
    private int absentDays;

    // ------------------------------ Earnings ------------------------------
    private double basicPay;
    private double overtimePay;
    private double nightDiffPay;
    private double allowances;
    private double grossPay;

    // ----------------------------- Deductions -----------------------------
    private double sssDeduction;
    private double philhealthDeduction;
    private double pagibigDeduction;
    private double taxDeduction;
    private double loanDeduction;
    private double otherDeductions;

    // ----------------------------- Penalties ------------------------------
    private double undertimePenalty;
    private double absencePenalty;

    // -------------------- Employer share (info only) ----------------------
    private double employerSss;
    private double employerPhilhealth;
    private double employerPagibig;
    private double employerEc;

    private double netPay;

    public PayrollEntry(Employee employee, String cutOffPeriod) {
        this.employee = employee;
        this.cutOffPeriod = cutOffPeriod;
    }

    public Employee getEmployee() { return employee; }
    public String getCutOffPeriod() { return cutOffPeriod; }

    public double getTotalHoursWorked() { return totalHoursWorked; }
    public void setTotalHoursWorked(double v) { this.totalHoursWorked = v; }

    public double getOvertimeHours() { return overtimeHours; }
    public void setOvertimeHours(double v) { this.overtimeHours = v; }

    public double getUndertimeHours() { return undertimeHours; }
    public void setUndertimeHours(double v) { this.undertimeHours = v; }

    public int getAbsentDays() { return absentDays; }
    public void setAbsentDays(int v) { this.absentDays = v; }

    public double getBasicPay() { return basicPay; }
    public void setBasicPay(double v) { this.basicPay = v; }

    public double getOvertimePay() { return overtimePay; }
    public void setOvertimePay(double v) { this.overtimePay = v; }

    public double getNightDiffPay() { return nightDiffPay; }
    public void setNightDiffPay(double v) { this.nightDiffPay = v; }

    public double getAllowances() { return allowances; }
    public void setAllowances(double v) { this.allowances = v; }

    public double getGrossPay() { return grossPay; }
    public void setGrossPay(double v) { this.grossPay = v; }

    public double getSssDeduction() { return sssDeduction; }
    public void setSssDeduction(double v) { this.sssDeduction = v; }

    public double getPhilhealthDeduction() { return philhealthDeduction; }
    public void setPhilhealthDeduction(double v) { this.philhealthDeduction = v; }

    public double getPagibigDeduction() { return pagibigDeduction; }
    public void setPagibigDeduction(double v) { this.pagibigDeduction = v; }

    public double getTaxDeduction() { return taxDeduction; }
    public void setTaxDeduction(double v) { this.taxDeduction = v; }

    public double getLoanDeduction() { return loanDeduction; }
    public void setLoanDeduction(double v) { this.loanDeduction = v; }

    public double getOtherDeductions() { return otherDeductions; }
    public void setOtherDeductions(double v) { this.otherDeductions = v; }

    public double getUndertimePenalty() { return undertimePenalty; }
    public void setUndertimePenalty(double v) { this.undertimePenalty = v; }

    public double getAbsencePenalty() { return absencePenalty; }
    public void setAbsencePenalty(double v) { this.absencePenalty = v; }

    public double getEmployerSss() { return employerSss; }
    public void setEmployerSss(double v) { this.employerSss = v; }

    public double getEmployerPhilhealth() { return employerPhilhealth; }
    public void setEmployerPhilhealth(double v) { this.employerPhilhealth = v; }

    public double getEmployerPagibig() { return employerPagibig; }
    public void setEmployerPagibig(double v) { this.employerPagibig = v; }

    public double getEmployerEc() { return employerEc; }
    public void setEmployerEc(double v) { this.employerEc = v; }

    public double getNetPay() { return netPay; }
    public void setNetPay(double v) { this.netPay = v; }
}

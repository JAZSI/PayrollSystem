package com.com253.payrollsystem.payroll;

import com.com253.payrollsystem.shared.domain.EmployeeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/** A saved payroll computation (full breakdown). Mirrors the domain PayrollEntry. */
@Entity
@Table(name = "payslips")
public class PayslipEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false, length = 40)
    private String employeeId;

    @Column(name = "employee_name", nullable = false, length = 120)
    private String employeeName;

    @Enumerated(EnumType.STRING)
    @Column(name = "employee_type", nullable = false, length = 20)
    private EmployeeType employeeType;

    @Column(name = "cutoff_period", nullable = false, length = 20)
    private String cutoffPeriod;

    /** Optional link to a batch payroll run (null for single-employee runs). */
    @Column(name = "run_id")
    private Long runId;

    @Column(name = "total_hours", nullable = false)
    private double totalHours;
    @Column(name = "overtime_hours", nullable = false)
    private double overtimeHours;
    @Column(name = "undertime_hours", nullable = false)
    private double undertimeHours;
    @Column(name = "absent_days", nullable = false)
    private int absentDays;

    @Column(name = "basic_pay", nullable = false)
    private double basicPay;
    @Column(name = "overtime_pay", nullable = false)
    private double overtimePay;
    @Column(name = "night_diff_pay", nullable = false)
    private double nightDiffPay;
    @Column(nullable = false)
    private double allowances;
    @Column(name = "gross_pay", nullable = false)
    private double grossPay;

    @Column(nullable = false)
    private double sss;
    @Column(nullable = false)
    private double philhealth;
    @Column(nullable = false)
    private double pagibig;
    @Column(nullable = false)
    private double tax;
    @Column(nullable = false)
    private double loan;
    @Column(name = "other_deductions", nullable = false)
    private double otherDeductions;

    @Column(name = "undertime_penalty", nullable = false)
    private double undertimePenalty;
    @Column(name = "absence_penalty", nullable = false)
    private double absencePenalty;

    @Column(name = "employer_sss", nullable = false)
    private double employerSss;
    @Column(name = "employer_philhealth", nullable = false)
    private double employerPhilhealth;
    @Column(name = "employer_pagibig", nullable = false)
    private double employerPagibig;
    @Column(name = "employer_ec", nullable = false)
    private double employerEc;

    @Column(name = "net_pay", nullable = false)
    private double netPay;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public PayslipEntity() {
        // JPA / mapper
    }

    public Long getId() { return id; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String v) { this.employeeId = v; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String v) { this.employeeName = v; }

    public EmployeeType getEmployeeType() { return employeeType; }
    public void setEmployeeType(EmployeeType v) { this.employeeType = v; }

    public String getCutoffPeriod() { return cutoffPeriod; }
    public void setCutoffPeriod(String v) { this.cutoffPeriod = v; }

    public Long getRunId() { return runId; }
    public void setRunId(Long v) { this.runId = v; }

    public double getTotalHours() { return totalHours; }
    public void setTotalHours(double v) { this.totalHours = v; }

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

    public double getSss() { return sss; }
    public void setSss(double v) { this.sss = v; }

    public double getPhilhealth() { return philhealth; }
    public void setPhilhealth(double v) { this.philhealth = v; }

    public double getPagibig() { return pagibig; }
    public void setPagibig(double v) { this.pagibig = v; }

    public double getTax() { return tax; }
    public void setTax(double v) { this.tax = v; }

    public double getLoan() { return loan; }
    public void setLoan(double v) { this.loan = v; }

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

    public LocalDateTime getCreatedAt() { return createdAt; }
}

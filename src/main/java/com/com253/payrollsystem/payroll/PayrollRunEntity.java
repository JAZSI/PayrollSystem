package com.com253.payrollsystem.payroll;

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

/** A batch payroll run for one cut-off period, aggregating many payslips. */
@Entity
@Table(name = "payroll_runs")
public class PayrollRunEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cutoff_period", nullable = false, length = 20)
    private String cutoffPeriod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PayrollRunStatus status = PayrollRunStatus.DRAFT;

    @Column(name = "employee_count", nullable = false)
    private int employeeCount;

    @Column(name = "total_gross", nullable = false)
    private double totalGross;

    @Column(name = "total_deductions", nullable = false)
    private double totalDeductions;

    @Column(name = "total_net", nullable = false)
    private double totalNet;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    protected PayrollRunEntity() {
        // JPA
    }

    public PayrollRunEntity(String cutoffPeriod) {
        this.cutoffPeriod = cutoffPeriod;
        this.status = PayrollRunStatus.DRAFT;
    }

    public Long getId() { return id; }

    public String getCutoffPeriod() { return cutoffPeriod; }

    public PayrollRunStatus getStatus() { return status; }
    public void setStatus(PayrollRunStatus status) { this.status = status; }

    public int getEmployeeCount() { return employeeCount; }
    public void setEmployeeCount(int v) { this.employeeCount = v; }

    public double getTotalGross() { return totalGross; }
    public void setTotalGross(double v) { this.totalGross = v; }

    public double getTotalDeductions() { return totalDeductions; }
    public void setTotalDeductions(double v) { this.totalDeductions = v; }

    public double getTotalNet() { return totalNet; }
    public void setTotalNet(double v) { this.totalNet = v; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}

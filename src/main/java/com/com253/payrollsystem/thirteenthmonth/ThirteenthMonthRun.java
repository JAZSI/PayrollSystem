package com.com253.payrollsystem.thirteenthmonth;

import com.com253.payrollsystem.payroll.PayrollRunStatus;
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

/** A 13th-month computation for a calendar year (reuses the payroll run lifecycle). */
@Entity
@Table(name = "thirteenth_month_runs")
public class ThirteenthMonthRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int year;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private PayrollRunStatus status = PayrollRunStatus.DRAFT;

    @Column(name = "employee_count", nullable = false)
    private int employeeCount;

    @Column(name = "total_amount", nullable = false)
    private double totalAmount;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    protected ThirteenthMonthRun() {
        // JPA
    }

    public ThirteenthMonthRun(int year) {
        this.year = year;
        this.status = PayrollRunStatus.DRAFT;
    }

    public Long getId() { return id; }
    public int getYear() { return year; }

    public PayrollRunStatus getStatus() { return status; }
    public void setStatus(PayrollRunStatus status) { this.status = status; }

    public int getEmployeeCount() { return employeeCount; }
    public void setEmployeeCount(int v) { this.employeeCount = v; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double v) { this.totalAmount = v; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}

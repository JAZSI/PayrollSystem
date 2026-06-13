package com.com253.payrollsystem.thirteenthmonth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/** One employee's 13th-month amount within a run. */
@Entity
@Table(name = "thirteenth_month_entries")
public class ThirteenthMonthEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "run_id", nullable = false)
    private Long runId;

    @Column(name = "employee_id", nullable = false, length = 40)
    private String employeeId;

    @Column(name = "employee_name", nullable = false, length = 120)
    private String employeeName;

    @Column(name = "total_basic", nullable = false)
    private double totalBasic;

    @Column(nullable = false)
    private double amount;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    protected ThirteenthMonthEntry() {
        // JPA
    }

    public ThirteenthMonthEntry(Long runId, String employeeId, String employeeName,
                                double totalBasic, double amount) {
        this.runId = runId;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.totalBasic = totalBasic;
        this.amount = amount;
    }

    public Long getId() { return id; }
    public Long getRunId() { return runId; }
    public String getEmployeeId() { return employeeId; }
    public String getEmployeeName() { return employeeName; }
    public double getTotalBasic() { return totalBasic; }
    public double getAmount() { return amount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}

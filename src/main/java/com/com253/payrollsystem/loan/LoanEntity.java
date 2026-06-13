package com.com253.payrollsystem.loan;

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

/** An employee loan that amortizes per cut-off until the balance reaches zero. */
@Entity
@Table(name = "loans")
public class LoanEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false, length = 40)
    private String employeeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LoanType type;

    @Column(nullable = false)
    private double principal;

    @Column(name = "per_cutoff_amount", nullable = false)
    private double perCutoffAmount;

    @Column(nullable = false)
    private double balance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private LoanStatus status;

    @Column(name = "start_period", length = 40)
    private String startPeriod;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    protected LoanEntity() {
        // JPA
    }

    public LoanEntity(String employeeId, LoanType type, double principal,
                      double perCutoffAmount, String startPeriod) {
        this.employeeId = employeeId;
        this.type = type;
        this.principal = principal;
        this.perCutoffAmount = perCutoffAmount;
        this.balance = principal;
        this.status = LoanStatus.ACTIVE;
        this.startPeriod = startPeriod;
    }

    public Long getId() { return id; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String v) { this.employeeId = v; }

    public LoanType getType() { return type; }
    public void setType(LoanType v) { this.type = v; }

    public double getPrincipal() { return principal; }
    public void setPrincipal(double v) { this.principal = v; }

    public double getPerCutoffAmount() { return perCutoffAmount; }
    public void setPerCutoffAmount(double v) { this.perCutoffAmount = v; }

    public double getBalance() { return balance; }
    public void setBalance(double v) { this.balance = v; }

    public LoanStatus getStatus() { return status; }
    public void setStatus(LoanStatus v) { this.status = v; }

    public String getStartPeriod() { return startPeriod; }
    public void setStartPeriod(String v) { this.startPeriod = v; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}

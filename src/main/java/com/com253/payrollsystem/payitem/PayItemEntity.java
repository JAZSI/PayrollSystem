package com.com253.payrollsystem.payitem;

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

/** A recurring or one-off allowance/deduction applied to an employee's pay. */
@Entity
@Table(name = "pay_items")
public class PayItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false, length = 40)
    private String employeeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private PayItemKind kind;

    @Column(nullable = false, length = 60)
    private String name;

    @Column(nullable = false)
    private double amount;

    @Column(nullable = false)
    private boolean taxable;

    @Column(nullable = false)
    private boolean recurring;

    @Column(nullable = false)
    private boolean active;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    protected PayItemEntity() {
        // JPA
    }

    public PayItemEntity(String employeeId, PayItemKind kind, String name, double amount,
                         boolean taxable, boolean recurring) {
        this.employeeId = employeeId;
        this.kind = kind;
        this.name = name;
        this.amount = amount;
        this.taxable = taxable;
        this.recurring = recurring;
        this.active = true;
    }

    public Long getId() { return id; }
    public String getEmployeeId() { return employeeId; }

    public PayItemKind getKind() { return kind; }
    public void setKind(PayItemKind v) { this.kind = v; }

    public String getName() { return name; }
    public void setName(String v) { this.name = v; }

    public double getAmount() { return amount; }
    public void setAmount(double v) { this.amount = v; }

    public boolean isTaxable() { return taxable; }
    public void setTaxable(boolean v) { this.taxable = v; }

    public boolean isRecurring() { return recurring; }
    public void setRecurring(boolean v) { this.recurring = v; }

    public boolean isActive() { return active; }
    public void setActive(boolean v) { this.active = v; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}

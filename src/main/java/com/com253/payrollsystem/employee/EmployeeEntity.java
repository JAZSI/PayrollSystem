package com.com253.payrollsystem.employee;

import com.com253.payrollsystem.shared.domain.EmployeeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/** JPA employee record (separate from the domain model). */
@Entity
@Table(name = "employees")
public class EmployeeEntity {

    @Id
    @Column(length = 40)
    private String id;

    @Column(name = "full_name", nullable = false, length = 120)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EmployeeType type;

    @Column(name = "monthly_rate", nullable = false)
    private double monthlyRate;

    @Column(name = "hourly_rate", nullable = false)
    private double hourlyRate;

    @Column(nullable = false)
    private boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    protected EmployeeEntity() {
        // JPA
    }

    public EmployeeEntity(String id, String fullName, EmployeeType type,
                          double monthlyRate, double hourlyRate, boolean active) {
        this.id = id;
        this.fullName = fullName;
        this.type = type;
        this.monthlyRate = monthlyRate;
        this.hourlyRate = hourlyRate;
        this.active = active;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public EmployeeType getType() { return type; }
    public void setType(EmployeeType type) { this.type = type; }

    public double getMonthlyRate() { return monthlyRate; }
    public void setMonthlyRate(double monthlyRate) { this.monthlyRate = monthlyRate; }

    public double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(double hourlyRate) { this.hourlyRate = hourlyRate; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}

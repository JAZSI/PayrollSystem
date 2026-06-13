package com.com253.payrollsystem.leave;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** A category of leave (paid or unpaid) with a default annual credit grant. */
@Entity
@Table(name = "leave_types")
public class LeaveType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 60)
    private String name;

    @Column(nullable = false)
    private boolean paid;

    @Column(name = "default_annual_credits", nullable = false)
    private int defaultAnnualCredits;

    protected LeaveType() {
        // JPA
    }

    public LeaveType(String name, boolean paid, int defaultAnnualCredits) {
        this.name = name;
        this.paid = paid;
        this.defaultAnnualCredits = defaultAnnualCredits;
    }

    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String v) { this.name = v; }

    public boolean isPaid() { return paid; }
    public void setPaid(boolean v) { this.paid = v; }

    public int getDefaultAnnualCredits() { return defaultAnnualCredits; }
    public void setDefaultAnnualCredits(int v) { this.defaultAnnualCredits = v; }
}

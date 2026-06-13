package com.com253.payrollsystem.leave;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** An employee's leave credits for one type in one year. */
@Entity
@Table(name = "leave_balances")
public class LeaveBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false, length = 40)
    private String employeeId;

    @Column(name = "leave_type_id", nullable = false)
    private Long leaveTypeId;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    private int credits;

    @Column(nullable = false)
    private int used;

    protected LeaveBalance() {
        // JPA
    }

    public LeaveBalance(String employeeId, Long leaveTypeId, int year, int credits) {
        this.employeeId = employeeId;
        this.leaveTypeId = leaveTypeId;
        this.year = year;
        this.credits = credits;
        this.used = 0;
    }

    public Long getId() { return id; }
    public String getEmployeeId() { return employeeId; }
    public Long getLeaveTypeId() { return leaveTypeId; }
    public int getYear() { return year; }

    public int getCredits() { return credits; }
    public void setCredits(int v) { this.credits = v; }

    public int getUsed() { return used; }
    public void setUsed(int v) { this.used = v; }

    public int getRemaining() { return credits - used; }
}

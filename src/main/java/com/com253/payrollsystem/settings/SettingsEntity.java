package com.com253.payrollsystem.settings;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Single-row payroll settings (id = 1). */
@Entity
@Table(name = "payroll_settings")
public class SettingsEntity {

    @Id
    private Integer id = 1;

    @Column(name = "working_days", nullable = false)
    private int workingDays;

    @Column(name = "workday_start_hour", nullable = false)
    private double workdayStartHour;

    @Column(name = "overtime_start_hour", nullable = false)
    private double overtimeStartHour;

    @Column(name = "lunch_start_hour", nullable = false)
    private double lunchStartHour;

    @Column(name = "leave_regular", nullable = false)
    private int leaveRegular;

    @Column(name = "leave_probationary", nullable = false)
    private int leaveProbationary;

    @Column(name = "leave_contractual", nullable = false)
    private int leaveContractual;

    @Column(name = "leave_part_timer", nullable = false)
    private int leavePartTimer;

    protected SettingsEntity() {
        // JPA
    }

    public SettingsEntity(int workingDays, double workdayStartHour, double overtimeStartHour,
                          double lunchStartHour, int leaveRegular, int leaveProbationary,
                          int leaveContractual, int leavePartTimer) {
        this.id = 1;
        this.workingDays = workingDays;
        this.workdayStartHour = workdayStartHour;
        this.overtimeStartHour = overtimeStartHour;
        this.lunchStartHour = lunchStartHour;
        this.leaveRegular = leaveRegular;
        this.leaveProbationary = leaveProbationary;
        this.leaveContractual = leaveContractual;
        this.leavePartTimer = leavePartTimer;
    }

    public Integer getId() { return id; }

    public int getWorkingDays() { return workingDays; }
    public void setWorkingDays(int v) { this.workingDays = v; }

    public double getWorkdayStartHour() { return workdayStartHour; }
    public void setWorkdayStartHour(double v) { this.workdayStartHour = v; }

    public double getOvertimeStartHour() { return overtimeStartHour; }
    public void setOvertimeStartHour(double v) { this.overtimeStartHour = v; }

    public double getLunchStartHour() { return lunchStartHour; }
    public void setLunchStartHour(double v) { this.lunchStartHour = v; }

    public int getLeaveRegular() { return leaveRegular; }
    public void setLeaveRegular(int v) { this.leaveRegular = v; }

    public int getLeaveProbationary() { return leaveProbationary; }
    public void setLeaveProbationary(int v) { this.leaveProbationary = v; }

    public int getLeaveContractual() { return leaveContractual; }
    public void setLeaveContractual(int v) { this.leaveContractual = v; }

    public int getLeavePartTimer() { return leavePartTimer; }
    public void setLeavePartTimer(int v) { this.leavePartTimer = v; }
}

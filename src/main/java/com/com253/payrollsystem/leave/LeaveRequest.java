package com.com253.payrollsystem.leave;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** A leave application spanning a date range, counted in working days. */
@Entity
@Table(name = "leave_requests")
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false, length = 40)
    private String employeeId;

    @Column(name = "leave_type_id", nullable = false)
    private Long leaveTypeId;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private int days;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private LeaveStatus status;

    @Column(length = 240)
    private String reason;

    @Column(name = "decided_by", length = 60)
    private String decidedBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    protected LeaveRequest() {
        // JPA
    }

    public LeaveRequest(String employeeId, Long leaveTypeId, LocalDate startDate,
                        LocalDate endDate, int days, String reason) {
        this.employeeId = employeeId;
        this.leaveTypeId = leaveTypeId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.days = days;
        this.reason = reason;
        this.status = LeaveStatus.PENDING;
    }

    public Long getId() { return id; }
    public String getEmployeeId() { return employeeId; }
    public Long getLeaveTypeId() { return leaveTypeId; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public int getDays() { return days; }

    public LeaveStatus getStatus() { return status; }
    public void setStatus(LeaveStatus v) { this.status = v; }

    public String getReason() { return reason; }

    public String getDecidedBy() { return decidedBy; }
    public void setDecidedBy(String v) { this.decidedBy = v; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}

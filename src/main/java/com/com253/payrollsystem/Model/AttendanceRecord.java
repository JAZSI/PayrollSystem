package com.com253.payrollsystem.Model;

import java.time.LocalDate;

/**
 * Represents a single day's attendance entry for an employee.
 */
public class AttendanceRecord {
    
    private final String employeeId;
    private final LocalDate recordDate;
    private final Double timeIn;
    private final Double timeOut;

    /**
     * Creates an attendance record for a specific employee and date.
     *
     * @param employeeId the employee this record belongs to
     * @param recordDate the date of the attendance entry
     * @param timeIn     clock-in time in decimal hours, or null if not clocked in
     * @param timeOut    clock-out time in decimal hours, or null if not clocked out
     */
    public AttendanceRecord(String employeeId, LocalDate recordDate,
                            Double timeIn, Double timeOut) {
        this.employeeId = employeeId;
        this.recordDate = recordDate;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
    }
    
    /**
     * Gets the employee identifier.
     *
     * @return employee ID
     */
    public String getEmployeeId() {
        return employeeId;
    }

    /**
     * Gets the date of this record.
     *
     * @return record date
     */
    public LocalDate getRecordDate() {
        return recordDate;
    }
    
    /**
     * Gets the clock-in time in decimal hours.
     *
     * @return time in, or null if absent
     */
    public Double timeIn() {
        return timeIn;
    }

    /**
     * Gets the clock-out time in decimal hours.
     *
     * @return time out, or null if not yet clocked out
     */
    public Double timeOut() {
        return timeOut;
    }
    
    /**
     * Indicates whether the employee was absent for this day.
     *
     * @return true if both time in and time out are null
     */
    public boolean isAbsent() {
        return timeIn == null && timeOut == null;
    }
}

package com.com253.payrollsystem.Model;

/**
 * Represents a single leave usage transaction in the ledger.
 */
public class LeaveTransaction {

    public enum LeaveType {
        SICK,
        VACATION,
        EMERGENCY
    }

    private final int id;
    private final String employeeId;
    private final LeaveType leaveType;
    private final int days;
    private final String cutOffPeriod;
    private final String createdAt;

    /**
     * Creates a leave transaction record.
     *
     * @param id            transaction identifier
     * @param employeeId    employee identifier
     * @param leaveType     type of leave consumed
     * @param days          number of days used
     * @param cutOffPeriod  cutoff period in which this was deducted
     * @param createdAt     timestamp of the transaction
     */
    public LeaveTransaction(int id, String employeeId, LeaveType leaveType,
                           int days, String cutOffPeriod, String createdAt) {
        this.id = id;
        this.employeeId = employeeId;
        this.leaveType = leaveType;
        this.days = days;
        this.cutOffPeriod = cutOffPeriod;
        this.createdAt = createdAt;
    }

    /**
     * Gets the transaction identifier.
     *
     * @return id
     */
    public int getId() { 
        return id; 
    }

    /**
     * Gets the employee identifier.
     *
     * @return employee id
     */
    public String getEmployeeId() { 
        return employeeId; 
    }

    /**
     * Gets the type of leave consumed.
     *
     * @return leave type
     */
    public LeaveType getLeaveType() { 
        return leaveType; 
    }

    /**
     * Gets the number of leave days used.
     *
     * @return days
     */
    public int getDays() { 
        return days; 
    }

    /**
     * Gets the cutoff period label.
     *
     * @return cutoff period
     */
    public String getCutOffPeriod() { 
        return cutOffPeriod; 
    }

    /**
     * Gets the creation timestamp.
     *
     * @return created at
     */
    public String getCreatedAt() { 
        return createdAt; 
    }
}
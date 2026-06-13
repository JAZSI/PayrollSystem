package com.com253.payrollsystem.attendance;

import com.com253.payrollsystem.shared.domain.HolidayType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** A single day's attendance for an employee in a cut-off period. */
@Entity
@Table(name = "time_records")
public class TimeRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false, length = 40)
    private String employeeId;

    @Column(name = "cutoff_period", nullable = false, length = 20)
    private String cutoffPeriod;

    @Column(name = "day_number", nullable = false)
    private int dayNumber;

    @Column(name = "time_in", nullable = false)
    private int timeIn;

    @Column(name = "time_out", nullable = false)
    private int timeOut;

    @Column(nullable = false)
    private boolean absent;

    @Enumerated(EnumType.STRING)
    @Column(name = "holiday_type", nullable = false, length = 24)
    private HolidayType holidayType = HolidayType.NONE;

    protected TimeRecordEntity() {
        // JPA
    }

    public TimeRecordEntity(String employeeId, String cutoffPeriod, int dayNumber,
                            int timeIn, int timeOut, boolean absent, HolidayType holidayType) {
        this.employeeId = employeeId;
        this.cutoffPeriod = cutoffPeriod;
        this.dayNumber = dayNumber;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        this.absent = absent;
        this.holidayType = holidayType == null ? HolidayType.NONE : holidayType;
    }

    public Long getId() { return id; }
    public String getEmployeeId() { return employeeId; }
    public String getCutoffPeriod() { return cutoffPeriod; }
    public int getDayNumber() { return dayNumber; }
    public int getTimeIn() { return timeIn; }
    public void setTimeIn(int timeIn) { this.timeIn = timeIn; }

    public int getTimeOut() { return timeOut; }
    public void setTimeOut(int timeOut) { this.timeOut = timeOut; }

    public boolean isAbsent() { return absent; }
    public void setAbsent(boolean absent) { this.absent = absent; }

    public HolidayType getHolidayType() { return holidayType; }
    public void setHolidayType(HolidayType holidayType) { this.holidayType = holidayType; }
}

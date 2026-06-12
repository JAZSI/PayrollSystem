package com.com253.payrollsystem.model;

/**
 * Represents a daily time record entry.
 */
public class TimeRecord {

    private int dayNumber;
    private int timeIn;
    private int timeOut;
    private boolean isAbsent;
    private HolidayType holidayType;

    /**
     * Creates a time record entry with a holiday type.
     *
     * @param dayNumber day number in the period
     * @param timeIn time-in value
     * @param timeOut time-out value
     * @param isAbsent absence indicator
     * @param holidayType holiday type for the day (null is treated as {@link HolidayType#NONE})
     */
    public TimeRecord(int dayNumber, int timeIn, int timeOut, boolean isAbsent, HolidayType holidayType) {
        this.dayNumber = dayNumber;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        this.isAbsent = isAbsent;
        this.holidayType = holidayType == null ? HolidayType.NONE : holidayType;
    }

    /**
     * Gets the day number.
     *
     * @return day number
     */
    public int getDayNumber() {
        return dayNumber;
    }

    /**
     * Gets the time-in value.
     *
     * @return time-in
     */
    public int getTimeIn() {
        return timeIn;
    }

    /**
     * Gets the time-out value.
     *
     * @return time-out
     */
    public int getTimeOut() {
        return timeOut;
    }

    /**
     * Indicates whether the employee is absent.
     *
     * @return true if absent; otherwise false
     */
    public boolean isAbsent() {
        return isAbsent;
    }

    /**
     * Indicates whether the day is a holiday.
     *
     * @return true if holiday; otherwise false
     */
    public boolean isHoliday() {
        return holidayType != HolidayType.NONE;
    }

    /**
     * Indicates whether the day is a regular holiday.
     *
     * @return true if regular holiday; otherwise false
     */
    public boolean isRegularHoliday() {
        return holidayType == HolidayType.REGULAR_HOLIDAY;
    }

    /**
     * Indicates whether the day is a special holiday or rest day.
     *
     * @return true if special holiday/rest day; otherwise false
     */
    public boolean isRestDayHoliday() {
        return holidayType == HolidayType.SPECIAL_OR_REST_DAY;
    }

    /**
     * Gets the holiday type for the day.
     *
     * @return holiday type
     */
    public HolidayType getHolidayType() {
        return holidayType;
    }
}

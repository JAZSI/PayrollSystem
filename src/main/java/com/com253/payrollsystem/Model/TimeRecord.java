package com.com253.payrollsystem.Model;

/**
 * Represents a daily time record entry.
 */
public class TimeRecord {
    public static final String HOLIDAY_NONE = "NONE";
    public static final String HOLIDAY_REGULAR = "REGULAR_HOLIDAY";
    public static final String HOLIDAY_REST_DAY = "SPECIAL_OR_REST_DAY";

    private int dayNumber;
    private int timeIn;
    private int timeOut;
    private boolean isAbsent;
    private String holidayType;

    /**
     * Creates a time record entry with a holiday type.
     *
     * @param dayNumber day number in the period
     * @param timeIn time-in value
     * @param timeOut time-out value
     * @param isAbsent absence indicator
     * @param holidayType holiday type for the day
     */
    public TimeRecord(int dayNumber, int timeIn, int timeOut, boolean isAbsent, String holidayType) {
        this.dayNumber = dayNumber;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        this.isAbsent = isAbsent;
        this.holidayType = normalizeHolidayType(holidayType);
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
        return !HOLIDAY_NONE.equals(holidayType);
    }

    /**
     * Indicates whether the day is a regular holiday.
     *
     * @return true if regular holiday; otherwise false
     */
    public boolean isRegularHoliday() {
        return HOLIDAY_REGULAR.equals(holidayType);
    }

    /**
     * Indicates whether the day is a special holiday or rest day.
     *
     * @return true if special holiday/rest day; otherwise false
     */
    public boolean isRestDayHoliday() {
        return HOLIDAY_REST_DAY.equals(holidayType);
    }

    /**
     * Gets the holiday type code for the day.
     *
     * @return holiday type code
     */
    public String getHolidayType() {
        return holidayType;
    }

    private String normalizeHolidayType(String holidayType) {
        if (HOLIDAY_REGULAR.equals(holidayType) || HOLIDAY_REST_DAY.equals(holidayType)) {
            return holidayType;
        }
        return HOLIDAY_NONE;
    }
}

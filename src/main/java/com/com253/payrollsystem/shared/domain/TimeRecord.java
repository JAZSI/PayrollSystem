package com.com253.payrollsystem.shared.domain;

/** One day's attendance entry. */
public class TimeRecord {

    private final int dayNumber;
    private final int timeIn;
    private final int timeOut;
    private final boolean isAbsent;
    private final HolidayType holidayType;

    public TimeRecord(int dayNumber, int timeIn, int timeOut, boolean isAbsent, HolidayType holidayType) {
        this.dayNumber = dayNumber;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        this.isAbsent = isAbsent;
        this.holidayType = holidayType == null ? HolidayType.NONE : holidayType;
    }

    public int getDayNumber() { return dayNumber; }
    public int getTimeIn() { return timeIn; }
    public int getTimeOut() { return timeOut; }
    public boolean isAbsent() { return isAbsent; }
    public HolidayType getHolidayType() { return holidayType; }

    public boolean isHoliday() { return holidayType != HolidayType.NONE; }
    public boolean isRegularHoliday() { return holidayType == HolidayType.REGULAR_HOLIDAY; }
    public boolean isRestDayHoliday() { return holidayType == HolidayType.SPECIAL_OR_REST_DAY; }
}

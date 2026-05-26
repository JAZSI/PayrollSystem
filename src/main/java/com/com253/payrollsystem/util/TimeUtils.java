package com.com253.payrollsystem.util;

/**
 * Small time conversion utilities used across payroll services.
 */
public final class TimeUtils {

    private TimeUtils() {}

    /**
     * Converts a fractional hour value (e.g. 8.5) to HHMM integer (e.g. 850).
     */
    public static int toHHMM(double time) {
        int hours = (int) time;
        int minutes = (int) ((time - hours) * 60);
        return hours * 100 + minutes;
    }
}

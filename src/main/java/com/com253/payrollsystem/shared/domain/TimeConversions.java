package com.com253.payrollsystem.shared.domain;

/** HHMM time helpers (e.g. 800 = 08:00). */
public final class TimeConversions {

    private TimeConversions() {
    }

    /** HHMM to decimal hours (930 -> 9.5). */
    public static double hhmmToHours(int hhmm) {
        return (hhmm / 100) + (hhmm % 100) / 60.0;
    }
}

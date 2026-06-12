package com.com253.payrollsystem.model;

/**
 * Conversions for the {@code int} HHMM time representation used throughout the
 * payroll flow (e.g. {@code 800} = 08:00, {@code 1700} = 17:00).
 *
 * <p>Interim home for logic that was duplicated across the CLI and the
 * calculator. A future refactor (see .docs/04-refactoring-roadmap.md, Stage A)
 * replaces the {@code int} HHMM representation with {@link java.time.LocalTime}.
 */
public final class TimeConversions {

    private TimeConversions() {
    }

    /**
     * Converts an HHMM value to decimal hours.
     * <p>Examples: {@code 800 -> 8.0}, {@code 930 -> 9.5}, {@code 1700 -> 17.0}.
     *
     * @param hhmm time in HHMM format
     * @return the time expressed as decimal hours
     */
    public static double hhmmToHours(int hhmm) {
        return (hhmm / 100) + (hhmm % 100) / 60.0;
    }
}

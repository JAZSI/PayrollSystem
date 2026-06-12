package com.com253.payrollsystem.model;

/**
 * Holiday classification for a day's time record. Single source of truth for the
 * holiday discriminator (previously the magic strings "NONE", "REGULAR_HOLIDAY",
 * "SPECIAL_OR_REST_DAY" on {@link TimeRecord}).
 */
public enum HolidayType {
    /** An ordinary working day (no holiday premium). */
    NONE,
    /** A regular holiday. */
    REGULAR_HOLIDAY,
    /** A special non-working holiday or rest day. */
    SPECIAL_OR_REST_DAY
}

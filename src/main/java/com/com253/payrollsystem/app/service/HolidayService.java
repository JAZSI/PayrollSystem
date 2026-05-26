package com.com253.payrollsystem.app.service;

import com.com253.payrollsystem.domain.model.HolidayType;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashSet;
import java.util.Set;

/**
 * Simple hardcoded holiday service. Rather than parsing an external document,
 * this implementation computes a curated list of common holidays for the
 * provided year. It includes fixed-date holidays and computed ones like
 * Maundy Thursday / Good Friday (based on Easter) and the last-Monday
 * National Heroes' Day.
 */
public class HolidayService {
    private final int year;
    private final Set<LocalDate> regular = new HashSet<>();
    private final Set<LocalDate> special = new HashSet<>();

    public HolidayService() {
        this(LocalDate.now().getYear());
    }

    public HolidayService(int year) {
        this.year = year;
        populate();
    }

    public HolidayType getHolidayType(LocalDate date) {
        if (regular.contains(date)) return HolidayType.REGULAR_HOLIDAY;
        if (special.contains(date)) return HolidayType.SPECIAL_OR_REST_DAY;
        return HolidayType.NONE;
    }

    public boolean isHoliday(LocalDate date) {
        return getHolidayType(date) != HolidayType.NONE;
    }

    private void populate() {
        // Fixed regular holidays
        regular.add(LocalDate.of(year, Month.JANUARY, 1));   // New Year's Day
        regular.add(LocalDate.of(year, Month.JUNE, 12));     // Independence Day
        regular.add(LocalDate.of(year, Month.NOVEMBER, 30)); // Bonifacio Day
        regular.add(LocalDate.of(year, Month.DECEMBER, 25)); // Christmas Day
        regular.add(LocalDate.of(year, Month.DECEMBER, 30)); // Rizal Day

        // Fixed special / common rest days
        special.add(LocalDate.of(year, Month.MAY, 1));       // Labor Day (treat as special in some configs)
        special.add(LocalDate.of(year, Month.NOVEMBER, 1));  // All Saints' Day (common observance)

        // Computed: Maundy Thursday and Good Friday (based on Easter Sunday)
        LocalDate easter = computeEasterSunday(year);
        if (easter != null) {
            special.add(easter.minusDays(3)); // Maundy Thursday
            special.add(easter.minusDays(2)); // Good Friday
        }

        // Computed: National Heroes' Day - last Monday of August
        regular.add(lastWeekdayOfMonth(year, Month.AUGUST, DayOfWeek.MONDAY));
    }

    private LocalDate lastWeekdayOfMonth(int year, Month month, DayOfWeek dow) {
        LocalDate d = LocalDate.of(year, month, month.length(java.time.Year.isLeap(year)));
        while (d.getDayOfWeek() != dow) d = d.minusDays(1);
        return d;
    }

    /**
     * Compute Easter Sunday for a given year using Anonymous Gregorian algorithm.
     * Returns the LocalDate for Easter Sunday.
     */
    private static LocalDate computeEasterSunday(int y) {
        int a = y % 19;
        int b = y / 100;
        int c = y % 100;
        int d = b / 4;
        int e = b % 4;
        int f = (b + 8) / 25;
        int g = (b - f + 1) / 3;
        int h = (19 * a + b - d - g + 15) % 30;
        int i = c / 4;
        int k = c % 4;
        int l = (32 + 2 * e + 2 * i - h - k) % 7;
        int m = (a + 11 * h + 22 * l) / 451;
        int month = (h + l - 7 * m + 114) / 31; // 3=March, 4=April
        int day = ((h + l - 7 * m + 114) % 31) + 1;
        return LocalDate.of(y, month, day);
    }
}

package com.com253.payrollsystem.attendance;

import com.com253.payrollsystem.shared.domain.HolidayType;
import com.com253.payrollsystem.holiday.HolidayEntity;
import com.com253.payrollsystem.holiday.HolidayRepository;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;

/** Resolves a date's holiday type from the DB (weekends = rest day). */
@Component
public class HolidayCalendar {

    private final HolidayRepository repository;

    public HolidayCalendar(HolidayRepository repository) {
        this.repository = repository;
    }

    /** The holiday type for a date: a listed holiday, else rest day on weekends, else NONE. */
    public HolidayType typeFor(LocalDate date) {
        return repository.findByDate(date)
                .map(HolidayEntity::getType)
                .orElseGet(() -> isWeekend(date) ? HolidayType.SPECIAL_OR_REST_DAY : HolidayType.NONE);
    }

    /** A human-readable label for a date (holiday name, "Rest Day", or null for a normal day). */
    public String nameFor(LocalDate date) {
        return repository.findByDate(date)
                .map(HolidayEntity::getName)
                .orElseGet(() -> isWeekend(date) ? "Rest Day" : null);
    }

    private static boolean isWeekend(LocalDate date) {
        return date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY;
    }
}

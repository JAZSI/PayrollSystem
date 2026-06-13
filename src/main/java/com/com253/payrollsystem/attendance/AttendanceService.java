package com.com253.payrollsystem.attendance;

import com.com253.payrollsystem.payroll.PeriodLockGuard;

import com.com253.payrollsystem.employee.EmployeeRepository;
import com.com253.payrollsystem.shared.domain.WorkingDayCalculator;
import com.com253.payrollsystem.attendance.dto.DayCalendarResponse;
import com.com253.payrollsystem.attendance.dto.SaveAttendanceRequest;
import com.com253.payrollsystem.attendance.dto.TimeRecordRequest;
import com.com253.payrollsystem.attendance.dto.TimeRecordResponse;
import com.com253.payrollsystem.shared.error.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;

/** Manages attendance (time records) for an employee and cut-off period. */
@Service
@Transactional
public class AttendanceService {

    private final TimeRecordRepository repository;
    private final EmployeeRepository employeeRepository;
    private final PeriodLockGuard periodLockGuard;
    private final HolidayCalendar holidayCalendar;

    public AttendanceService(TimeRecordRepository repository, EmployeeRepository employeeRepository,
                             PeriodLockGuard periodLockGuard, HolidayCalendar holidayCalendar) {
        this.repository = repository;
        this.employeeRepository = employeeRepository;
        this.periodLockGuard = periodLockGuard;
        this.holidayCalendar = holidayCalendar;
    }

    /** Weekday day-numbers for a cut-off in the given month (reuses the domain calculator). */
    @Transactional(readOnly = true)
    public int[] workingDays(String period, int year, int month) {
        return WorkingDayCalculator.getWorkingDays(period, YearMonth.of(year, month));
    }

    /** Working days with their automatically-determined holiday classification. */
    @Transactional(readOnly = true)
    public List<DayCalendarResponse> calendar(String period, int year, int month) {
        return Arrays.stream(WorkingDayCalculator.getWorkingDays(period, YearMonth.of(year, month)))
                .mapToObj(day -> {
                    LocalDate date = LocalDate.of(year, month, day);
                    return new DayCalendarResponse(day, holidayCalendar.typeFor(date),
                            holidayCalendar.nameFor(date));
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TimeRecordResponse> list(String employeeId, String period) {
        return repository.findByEmployeeIdAndCutoffPeriodOrderByDayNumber(employeeId, period)
                .stream().map(AttendanceService::toResponse).toList();
    }

    /** Upserts the period's records; keeps other-day punches; auto holiday. */
    public List<TimeRecordResponse> replace(SaveAttendanceRequest req) {
        periodLockGuard.ensureNotLocked(req.cutoffPeriod());
        if (!employeeRepository.existsById(req.employeeId())) {
            throw new NotFoundException("Employee not found: " + req.employeeId());
        }
        for (TimeRecordRequest r : req.records()) {
            if (!r.absent() && r.timeOut() <= r.timeIn()) {
                throw new IllegalArgumentException(
                        "Day " + r.dayNumber() + ": time out must be later than time in");
            }
        }

        for (TimeRecordRequest r : req.records()) {
            int timeIn = r.absent() ? 0 : r.timeIn();
            int timeOut = r.absent() ? 0 : r.timeOut();
            var holidayType = holidayCalendar.typeFor(LocalDate.of(req.year(), req.month(), r.dayNumber()));

            TimeRecordEntity entity = repository
                    .findByEmployeeIdAndCutoffPeriodAndDayNumber(
                            req.employeeId(), req.cutoffPeriod(), r.dayNumber())
                    .orElseGet(() -> new TimeRecordEntity(
                            req.employeeId(), req.cutoffPeriod(), r.dayNumber(),
                            timeIn, timeOut, r.absent(), holidayType));

            entity.setTimeIn(timeIn);
            entity.setTimeOut(timeOut);
            entity.setAbsent(r.absent());
            entity.setHolidayType(holidayType);
            repository.save(entity);
        }

        return repository.findByEmployeeIdAndCutoffPeriodOrderByDayNumber(
                        req.employeeId(), req.cutoffPeriod())
                .stream().map(AttendanceService::toResponse).toList();
    }

    private static TimeRecordResponse toResponse(TimeRecordEntity e) {
        return new TimeRecordResponse(e.getId(), e.getDayNumber(), e.getTimeIn(),
                e.getTimeOut(), e.isAbsent(), e.getHolidayType());
    }
}

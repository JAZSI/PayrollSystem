package com.com253.payrollsystem.attendance;

import com.com253.payrollsystem.payroll.PeriodLockGuard;

import com.com253.payrollsystem.employee.EmployeeEntity;
import com.com253.payrollsystem.employee.EmployeeRepository;
import com.com253.payrollsystem.attendance.dto.KioskResponse;
import com.com253.payrollsystem.shared.error.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/** Kiosk time clock: clock in then out by employee id. */
@Service
@Transactional
public class KioskService {

    private static final DateTimeFormatter HH_MM = DateTimeFormatter.ofPattern("HH:mm");

    private final EmployeeRepository employeeRepository;
    private final TimeRecordRepository timeRecordRepository;
    private final HolidayCalendar holidayCalendar;
    private final PeriodLockGuard periodLockGuard;

    public KioskService(EmployeeRepository employeeRepository,
                        TimeRecordRepository timeRecordRepository,
                        HolidayCalendar holidayCalendar,
                        PeriodLockGuard periodLockGuard) {
        this.employeeRepository = employeeRepository;
        this.timeRecordRepository = timeRecordRepository;
        this.holidayCalendar = holidayCalendar;
        this.periodLockGuard = periodLockGuard;
    }

    public KioskResponse clock(String employeeId) {
        EmployeeEntity emp = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException("Employee id not found: " + employeeId));
        if (!emp.isActive()) {
            throw new IllegalStateException("Employee account is inactive.");
        }

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        int day = today.getDayOfMonth();
        String cutoff = day <= 15 ? "1st-15th" : "16th-30th";
        int nowHhmm = now.getHour() * 100 + now.getMinute();
        String time = now.format(HH_MM);

        periodLockGuard.ensureNotLocked(cutoff);

        Optional<TimeRecordEntity> existing = timeRecordRepository
                .findByEmployeeIdAndCutoffPeriodAndDayNumber(employeeId, cutoff, day);

        if (existing.isEmpty()) {
            timeRecordRepository.save(new TimeRecordEntity(
                    employeeId, cutoff, day, nowHhmm, 0, false, holidayCalendar.typeFor(today)));
            return response("CLOCK_IN", emp, time, today,
                    "Welcome, " + emp.getFullName() + "! Clocked in at " + time + ".");
        }

        TimeRecordEntity record = existing.get();
        if (record.getTimeOut() == 0) {
            record.setTimeOut(nowHhmm);
            timeRecordRepository.save(record);
            return response("CLOCK_OUT", emp, time, today,
                    "Goodbye, " + emp.getFullName() + "! Clocked out at " + time + ".");
        }

        return response("ALREADY_COMPLETE", emp, time, today,
                emp.getFullName() + ", you have already clocked out today.");
    }

    private static KioskResponse response(String action, EmployeeEntity emp, String time,
                                          LocalDate date, String message) {
        return new KioskResponse(action, emp.getId(), emp.getFullName(), time,
                date.toString(), message);
    }
}

package com.com253.payrollsystem.attendance;

import com.com253.payrollsystem.attendance.dto.DayCalendarResponse;
import com.com253.payrollsystem.attendance.dto.SaveAttendanceRequest;
import com.com253.payrollsystem.attendance.dto.TimeRecordResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** REST endpoints for attendance (time records). */
@RestController
@RequestMapping("/api/attendance")
@PreAuthorize("hasAnyRole('ADMIN','HR')")
public class AttendanceController {

    private final AttendanceService service;

    public AttendanceController(AttendanceService service) {
        this.service = service;
    }

    @GetMapping("/working-days")
    public int[] workingDays(@RequestParam String period,
                             @RequestParam int year,
                             @RequestParam int month) {
        return service.workingDays(period, year, month);
    }

    /** Working days for a period with their auto-determined holiday type. */
    @GetMapping("/calendar")
    public List<DayCalendarResponse> calendar(@RequestParam String period,
                                              @RequestParam int year,
                                              @RequestParam int month) {
        return service.calendar(period, year, month);
    }

    @GetMapping
    public List<TimeRecordResponse> list(@RequestParam String employeeId,
                                         @RequestParam String period) {
        return service.list(employeeId, period);
    }

    @PutMapping
    public List<TimeRecordResponse> save(@Valid @RequestBody SaveAttendanceRequest request) {
        return service.replace(request);
    }
}

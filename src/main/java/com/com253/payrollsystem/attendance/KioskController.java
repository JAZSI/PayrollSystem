package com.com253.payrollsystem.attendance;

import com.com253.payrollsystem.attendance.dto.KioskClockRequest;
import com.com253.payrollsystem.attendance.dto.KioskResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Public time-clock kiosk: employees punch in/out by id. */
@RestController
@RequestMapping("/api/kiosk")
public class KioskController {

    private final KioskService service;

    public KioskController(KioskService service) {
        this.service = service;
    }

    @PostMapping("/clock")
    public KioskResponse clock(@Valid @RequestBody KioskClockRequest request) {
        return service.clock(request.employeeId());
    }
}

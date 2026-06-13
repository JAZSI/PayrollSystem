package com.com253.payrollsystem.thirteenthmonth;

import com.com253.payrollsystem.shared.security.CurrentUserService;
import com.com253.payrollsystem.thirteenthmonth.dto.CreateThirteenthMonthRequest;
import com.com253.payrollsystem.thirteenthmonth.dto.MyThirteenthMonthResponse;
import com.com253.payrollsystem.thirteenthmonth.dto.ThirteenthMonthRunResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** 13th-month runs: staff compute/approve/lock; employees view their own when locked. */
@RestController
public class ThirteenthMonthController {

    private final ThirteenthMonthService service;
    private final ThirteenthMonthPdfService pdfService;
    private final CurrentUserService currentUser;

    public ThirteenthMonthController(ThirteenthMonthService service,
                                     ThirteenthMonthPdfService pdfService,
                                     CurrentUserService currentUser) {
        this.service = service;
        this.pdfService = pdfService;
        this.currentUser = currentUser;
    }

    @PostMapping("/api/thirteenth-month/runs")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public ThirteenthMonthRunResponse create(@Valid @RequestBody CreateThirteenthMonthRequest request) {
        return service.createRun(request.year());
    }

    @GetMapping("/api/thirteenth-month/runs")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public List<ThirteenthMonthRunResponse> list() {
        return service.list();
    }

    @GetMapping("/api/thirteenth-month/runs/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public ThirteenthMonthRunResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping("/api/thirteenth-month/runs/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public ThirteenthMonthRunResponse approve(@PathVariable Long id) {
        return service.approve(id);
    }

    @PostMapping("/api/thirteenth-month/runs/{id}/lock")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public ThirteenthMonthRunResponse lock(@PathVariable Long id) {
        return service.lock(id);
    }

    @GetMapping("/api/thirteenth-month/entries/{id}/pdf")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public ResponseEntity<byte[]> pdf(@PathVariable Long id) {
        ThirteenthMonthEntry entry = service.entryOrThrow(id);
        byte[] body = pdfService.render(entry, service.yearOfRun(entry.getRunId()));
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"13th-month-" + id + ".pdf\"")
                .body(body);
    }

    @GetMapping("/api/me/thirteenth-month")
    public List<MyThirteenthMonthResponse> mine() {
        String employeeId = currentUser.currentEmployeeId();
        return employeeId == null ? List.of() : service.myEntries(employeeId);
    }
}

package com.com253.payrollsystem.payroll;

import com.com253.payrollsystem.payroll.dto.CreateRunRequest;
import com.com253.payrollsystem.payroll.dto.PayrollRunResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** REST endpoints for batch payroll runs and their DRAFT → APPROVED → LOCKED lifecycle. */
@RestController
@RequestMapping("/api/payroll/runs")
@PreAuthorize("hasAnyRole('ADMIN','HR')")
public class PayrollRunController {

    private final PayrollRunService service;

    public PayrollRunController(PayrollRunService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PayrollRunResponse create(@Valid @RequestBody CreateRunRequest request) {
        return service.createRun(request.period());
    }

    @GetMapping
    public List<PayrollRunResponse> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public PayrollRunResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping("/{id}/approve")
    public PayrollRunResponse approve(@PathVariable Long id) {
        return service.approve(id);
    }

    @PostMapping("/{id}/lock")
    public PayrollRunResponse lock(@PathVariable Long id) {
        return service.lock(id);
    }
}

package com.com253.payrollsystem.loan;

import com.com253.payrollsystem.loan.dto.LoanRequest;
import com.com253.payrollsystem.loan.dto.LoanResponse;
import com.com253.payrollsystem.shared.security.CurrentUserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Loan accounts: staff manage them; employees view their own. */
@RestController
public class LoanController {

    private final LoanService service;
    private final CurrentUserService currentUser;

    public LoanController(LoanService service, CurrentUserService currentUser) {
        this.service = service;
        this.currentUser = currentUser;
    }

    @GetMapping("/api/employees/{id}/loans")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public List<LoanResponse> byEmployee(@PathVariable String id) {
        return service.findByEmployee(id);
    }

    @PostMapping("/api/loans")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public LoanResponse create(@Valid @RequestBody LoanRequest request) {
        return service.create(request);
    }

    @PutMapping("/api/loans/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public LoanResponse update(@PathVariable Long id, @Valid @RequestBody LoanRequest request) {
        return service.update(id, request);
    }

    @PostMapping("/api/loans/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public LoanResponse cancel(@PathVariable Long id) {
        return service.cancel(id);
    }

    /** Self-service: the current user's own loans. */
    @GetMapping("/api/me/loans")
    public List<LoanResponse> mine() {
        String employeeId = currentUser.currentEmployeeId();
        return employeeId == null ? List.of() : service.findByEmployee(employeeId);
    }
}

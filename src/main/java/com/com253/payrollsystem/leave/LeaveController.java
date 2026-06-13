package com.com253.payrollsystem.leave;

import com.com253.payrollsystem.leave.dto.LeaveBalanceResponse;
import com.com253.payrollsystem.leave.dto.LeaveRequestRequest;
import com.com253.payrollsystem.leave.dto.LeaveRequestResponse;
import com.com253.payrollsystem.shared.error.NotFoundException;
import com.com253.payrollsystem.shared.security.CurrentUserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/** Leave balances and requests — staff manage the queue; employees self-serve. */
@RestController
public class LeaveController {

    private final LeaveService service;
    private final CurrentUserService currentUser;

    public LeaveController(LeaveService service, CurrentUserService currentUser) {
        this.service = service;
        this.currentUser = currentUser;
    }

    // ------------------------------ Staff (ADMIN/HR) ------------------------------

    @GetMapping("/api/employees/{id}/leave-balances")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public List<LeaveBalanceResponse> balances(@PathVariable String id,
                                               @RequestParam(required = false) Integer year) {
        return service.balancesFor(id, year == null ? LocalDate.now().getYear() : year);
    }

    @GetMapping("/api/employees/{id}/leave-requests")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public List<LeaveRequestResponse> employeeRequests(@PathVariable String id) {
        return service.requestsByEmployee(id);
    }

    @GetMapping("/api/leave-requests")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public List<LeaveRequestResponse> queue(@RequestParam(required = false) LeaveStatus status) {
        return service.listRequests(status);
    }

    @PostMapping("/api/leave-requests")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public LeaveRequestResponse file(@Valid @RequestBody LeaveRequestRequest request) {
        return service.file(request.employeeId(), request);
    }

    @PostMapping("/api/leave-requests/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public LeaveRequestResponse approve(@PathVariable Long id) {
        return service.approve(id, decider());
    }

    @PostMapping("/api/leave-requests/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public LeaveRequestResponse reject(@PathVariable Long id) {
        return service.reject(id, decider());
    }

    // ------------------------------ Self-service ------------------------------

    @GetMapping("/api/me/leave-balances")
    public List<LeaveBalanceResponse> myBalances(@RequestParam(required = false) Integer year) {
        return service.balancesFor(meId(), year == null ? LocalDate.now().getYear() : year);
    }

    @GetMapping("/api/me/leave-requests")
    public List<LeaveRequestResponse> myRequests() {
        return service.requestsByEmployee(meId());
    }

    @PostMapping("/api/me/leave-requests")
    @ResponseStatus(HttpStatus.CREATED)
    public LeaveRequestResponse fileMine(@Valid @RequestBody LeaveRequestRequest request) {
        return service.file(meId(), request);
    }

    private String meId() {
        String employeeId = currentUser.currentEmployeeId();
        if (employeeId == null) {
            throw new NotFoundException("Your account is not linked to an employee record");
        }
        return employeeId;
    }

    private String decider() {
        return currentUser.current().map(u -> u.getUsername()).orElse("system");
    }
}

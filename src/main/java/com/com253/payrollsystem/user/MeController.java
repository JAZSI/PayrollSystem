package com.com253.payrollsystem.user;

import com.com253.payrollsystem.employee.EmployeeService;
import com.com253.payrollsystem.shared.security.CurrentUserService;
import com.com253.payrollsystem.employee.dto.EmployeeResponse;
import com.com253.payrollsystem.user.dto.MeResponse;
import com.com253.payrollsystem.shared.error.NotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** The current user's own account + employee details (self-service). */
@RestController
@RequestMapping("/api/me")
public class MeController {

    private final CurrentUserService currentUser;
    private final EmployeeService employeeService;

    public MeController(CurrentUserService currentUser, EmployeeService employeeService) {
        this.currentUser = currentUser;
        this.employeeService = employeeService;
    }

    @GetMapping
    public MeResponse me() {
        UserEntity user = currentUser.current()
                .orElseThrow(() -> new NotFoundException("No authenticated user"));

        EmployeeResponse employee = null;
        if (user.getEmployeeId() != null) {
            try {
                employee = employeeService.findById(user.getEmployeeId());
            } catch (NotFoundException ignored) {
                employee = null;
            }
        }
        return new MeResponse(user.getUsername(), user.getRole(), employee);
    }
}

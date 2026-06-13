package com.com253.payrollsystem.user;

import com.com253.payrollsystem.user.dto.AuthResponse;
import com.com253.payrollsystem.user.dto.LoginRequest;
import com.com253.payrollsystem.user.dto.RegisterRequest;
import com.com253.payrollsystem.user.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return service.login(request);
    }

    /** Create a new user. Restricted to ADMIN. */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        return service.register(request);
    }
}

package com.com253.payrollsystem.user;

import com.com253.payrollsystem.audit.AuditService;
import com.com253.payrollsystem.shared.security.JwtService;
import com.com253.payrollsystem.user.dto.AuthResponse;
import com.com253.payrollsystem.user.dto.LoginRequest;
import com.com253.payrollsystem.user.dto.RegisterRequest;
import com.com253.payrollsystem.user.dto.UserResponse;
import com.com253.payrollsystem.shared.error.ConflictException;
import com.com253.payrollsystem.shared.error.NotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Authentication: login (issue JWT) and admin user registration. */
@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuditService auditService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtService jwtService,
                       AuditService auditService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.auditService = auditService;
    }

    /** Verifies credentials and returns a signed JWT. Bad credentials -> 401. */
    public AuthResponse login(LoginRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password()));

        UserEntity user = userRepository.findByUsername(req.username())
                .orElseThrow(() -> new NotFoundException("User not found: " + req.username()));

        String token = jwtService.generate(user.getUsername(), user.getRole());
        auditService.recordAs(user.getUsername(), user.getRole(), "LOGIN", "User",
                user.getUsername(), "Signed in");
        return new AuthResponse(token, user.getUsername(), user.getRole(), user.getEmployeeId());
    }

    @Transactional(readOnly = true)
    public List<UserResponse> listUsers() {
        return userRepository.findAll().stream()
                .map(u -> new UserResponse(u.getUsername(), u.getRole(), u.getEmployeeId()))
                .toList();
    }

    /** Creates a new user (ADMIN only — enforced at the controller). */
    public UserResponse register(RegisterRequest req) {
        if (userRepository.existsByUsername(req.username())) {
            throw new ConflictException("Username already taken: " + req.username());
        }
        UserEntity user = userRepository.save(new UserEntity(
                req.username(),
                passwordEncoder.encode(req.password()),
                req.role(),
                req.employeeId()));
        auditService.record("CREATE", "User", user.getUsername(),
                "Created " + user.getRole() + " user");
        return new UserResponse(user.getUsername(), user.getRole(), user.getEmployeeId());
    }
}

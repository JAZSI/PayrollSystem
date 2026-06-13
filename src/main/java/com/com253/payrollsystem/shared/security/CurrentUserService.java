package com.com253.payrollsystem.shared.security;

import com.com253.payrollsystem.user.UserEntity;
import com.com253.payrollsystem.user.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/** Resolves the currently authenticated user and their role/employee link. */
@Service
public class CurrentUserService {

    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<UserEntity> current() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName() == null) {
            return Optional.empty();
        }
        return userRepository.findByUsername(auth.getName());
    }

    /** The employee id linked to the current user, or null (e.g. for ADMIN/HR users). */
    public String currentEmployeeId() {
        return current().map(UserEntity::getEmployeeId).orElse(null);
    }

    public boolean hasRole(Role role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + role.name()));
    }

    /** True for staff who can see all data (ADMIN or HR). */
    public boolean isStaff() {
        return hasRole(Role.ADMIN) || hasRole(Role.HR);
    }
}

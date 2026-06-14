package com.com253.payrollsystem.employee;

import com.com253.payrollsystem.audit.AuditService;
import com.com253.payrollsystem.user.UserEntity;
import com.com253.payrollsystem.user.UserRepository;
import com.com253.payrollsystem.shared.security.Role;
import com.com253.payrollsystem.employee.dto.EmployeeRequest;
import com.com253.payrollsystem.employee.dto.EmployeeResponse;
import com.com253.payrollsystem.shared.error.ConflictException;
import com.com253.payrollsystem.shared.error.NotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Application service for employee management (CRUD + deactivate). */
@Service
@Transactional
public class EmployeeService {

    private final EmployeeRepository repository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;

    public EmployeeService(EmployeeRepository repository, UserRepository userRepository,
                           PasswordEncoder passwordEncoder, AuditService auditService) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> findAll() {
        return repository.findAll().stream().map(EmployeeService::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public EmployeeResponse findById(String id) {
        return toResponse(getOrThrow(id));
    }

    public EmployeeResponse create(EmployeeRequest request) {
        if (repository.existsById(request.id())) {
            throw new ConflictException("Employee already exists: " + request.id());
        }
        EmployeeEntity entity = new EmployeeEntity(
                request.id(),
                request.fullName(),
                request.type(),
                request.monthlyRate(),
                request.hourlyRate(),
                true);
        entity.setBankAccount(request.bankAccount());
        EmployeeResponse saved = toResponse(repository.save(entity));

        // Optionally create a login account (username = employee id) so they can sign in.
        if (request.password() != null && !request.password().isBlank()) {
            if (userRepository.existsByUsername(request.id())) {
                throw new ConflictException("A user account already exists for id: " + request.id());
            }
            userRepository.save(new UserEntity(
                    request.id(), passwordEncoder.encode(request.password()),
                    Role.EMPLOYEE, request.id()));
        }
        auditService.record("CREATE", "Employee", request.id(), "Added " + request.fullName());
        return saved;
    }

    public EmployeeResponse update(String id, EmployeeRequest request) {
        EmployeeEntity entity = getOrThrow(id);
        entity.setFullName(request.fullName());
        entity.setType(request.type());
        entity.setMonthlyRate(request.monthlyRate());
        entity.setHourlyRate(request.hourlyRate());
        entity.setBankAccount(request.bankAccount());
        EmployeeResponse updated = toResponse(repository.save(entity));
        auditService.record("UPDATE", "Employee", id, "Updated " + request.fullName());
        return updated;
    }

    public void deactivate(String id) {
        EmployeeEntity entity = getOrThrow(id);
        entity.setActive(false);
        repository.save(entity);
        auditService.record("DEACTIVATE", "Employee", id, "Deactivated " + entity.getFullName());
    }

    private EmployeeEntity getOrThrow(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Employee not found: " + id));
    }

    private static EmployeeResponse toResponse(EmployeeEntity e) {
        return new EmployeeResponse(
                e.getId(),
                e.getFullName(),
                e.getType(),
                e.getType().getLabel(),
                e.getMonthlyRate(),
                e.getHourlyRate(),
                e.getBankAccount(),
                e.isActive(),
                e.getCreatedAt() == null ? null : e.getCreatedAt().toString());
    }
}

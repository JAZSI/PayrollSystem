package com.com253.payrollsystem.audit;

import com.com253.payrollsystem.audit.dto.AuditPage;
import com.com253.payrollsystem.audit.dto.AuditResponse;
import com.com253.payrollsystem.shared.security.CurrentUserService;
import com.com253.payrollsystem.shared.security.Role;
import com.com253.payrollsystem.user.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Append-only audit trail. Actor resolved from the security context unless given explicitly. */
@Service
@Transactional
public class AuditService {

    private final AuditRepository repository;
    private final CurrentUserService currentUser;

    public AuditService(AuditRepository repository, CurrentUserService currentUser) {
        this.repository = repository;
        this.currentUser = currentUser;
    }

    /** Records an action attributed to the current user (or "system" if none). */
    public void record(String action, String entity, String entityId, String summary) {
        UserEntity user = currentUser.current().orElse(null);
        String username = user == null ? "system" : user.getUsername();
        Role role = user == null ? null : user.getRole();
        recordAs(username, role, action, entity, entityId, summary);
    }

    /** Records an action with an explicit actor (e.g. login, before the context is set). */
    public void recordAs(String username, Role role, String action, String entity,
                         String entityId, String summary) {
        repository.save(new AuditEntry(username, role, action, entity, entityId, summary));
    }

    @Transactional(readOnly = true)
    public AuditPage find(String entity, int page, int size) {
        PageRequest pageable = PageRequest.of(Math.max(0, page), Math.min(Math.max(1, size), 200));
        Page<AuditEntry> result = (entity == null || entity.isBlank())
                ? repository.findAllByOrderByCreatedAtDesc(pageable)
                : repository.findByEntityOrderByCreatedAtDesc(entity, pageable);
        return new AuditPage(
                result.getContent().stream().map(AuditService::toResponse).toList(),
                result.getNumber(), result.getSize(),
                result.getTotalElements(), result.getTotalPages());
    }

    private static AuditResponse toResponse(AuditEntry a) {
        return new AuditResponse(a.getId(), a.getUsername(), a.getRole(), a.getAction(),
                a.getEntity(), a.getEntityId(), a.getSummary(),
                a.getCreatedAt() == null ? null : a.getCreatedAt().toString());
    }
}

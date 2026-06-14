package com.com253.payrollsystem.audit;

import com.com253.payrollsystem.shared.security.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/** An immutable record of one mutating action (append-only). */
@Entity
@Table(name = "audit_log")
public class AuditEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(length = 12)
    private Role role;

    @Column(nullable = false, length = 24)
    private String action;

    @Column(nullable = false, length = 40)
    private String entity;

    @Column(name = "entity_id", length = 60)
    private String entityId;

    @Column(length = 240)
    private String summary;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    protected AuditEntry() {
        // JPA
    }

    public AuditEntry(String username, Role role, String action, String entity,
                      String entityId, String summary) {
        this.username = username;
        this.role = role;
        this.action = action;
        this.entity = entity;
        this.entityId = entityId;
        this.summary = summary;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public Role getRole() { return role; }
    public String getAction() { return action; }
    public String getEntity() { return entity; }
    public String getEntityId() { return entityId; }
    public String getSummary() { return summary; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}

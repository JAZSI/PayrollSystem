package com.com253.payrollsystem.statutory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** A versioned, effective-dated contribution table for one agency. */
@Entity
@Table(name = "contribution_tables")
public class ContributionTableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private ContributionAgency agency;

    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;

    @Column(nullable = false)
    private boolean active;

    @Column(length = 120)
    private String note;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    protected ContributionTableEntity() {
        // JPA
    }

    public ContributionTableEntity(ContributionAgency agency, LocalDate effectiveFrom,
                                   boolean active, String note) {
        this.agency = agency;
        this.effectiveFrom = effectiveFrom;
        this.active = active;
        this.note = note;
    }

    public Long getId() { return id; }

    public ContributionAgency getAgency() { return agency; }
    public void setAgency(ContributionAgency agency) { this.agency = agency; }

    public LocalDate getEffectiveFrom() { return effectiveFrom; }
    public void setEffectiveFrom(LocalDate effectiveFrom) { this.effectiveFrom = effectiveFrom; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}

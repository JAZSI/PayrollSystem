package com.com253.payrollsystem.holiday;

import com.com253.payrollsystem.shared.domain.HolidayType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

/** An admin-managed holiday: a date with a name and pay classification. */
@Entity
@Table(name = "holidays")
public class HolidayEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private LocalDate date;

    @Column(nullable = false, length = 120)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private HolidayType type;

    protected HolidayEntity() {
        // JPA
    }

    public HolidayEntity(LocalDate date, String name, HolidayType type) {
        this.date = date;
        this.name = name;
        this.type = type;
    }

    public Long getId() { return id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public HolidayType getType() { return type; }
    public void setType(HolidayType type) { this.type = type; }
}

package com.com253.payrollsystem.holiday;

import com.com253.payrollsystem.audit.AuditService;
import com.com253.payrollsystem.holiday.dto.HolidayRequest;
import com.com253.payrollsystem.holiday.dto.HolidayResponse;
import com.com253.payrollsystem.shared.error.ConflictException;
import com.com253.payrollsystem.shared.error.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** CRUD for admin-managed holidays. */
@Service
@Transactional
public class HolidayService {

    private final HolidayRepository repository;
    private final AuditService auditService;

    public HolidayService(HolidayRepository repository, AuditService auditService) {
        this.repository = repository;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<HolidayResponse> findAll() {
        return repository.findAllByOrderByDate().stream().map(HolidayService::toResponse).toList();
    }

    public HolidayResponse create(HolidayRequest req) {
        if (repository.existsByDate(req.date())) {
            throw new ConflictException("A holiday already exists on " + req.date());
        }
        HolidayResponse created = toResponse(repository.save(new HolidayEntity(req.date(), req.name(), req.type())));
        auditService.record("CREATE", "Holiday", created.id().toString(), req.name() + " on " + req.date());
        return created;
    }

    public HolidayResponse update(Long id, HolidayRequest req) {
        HolidayEntity holiday = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Holiday not found: " + id));
        if (!holiday.getDate().equals(req.date()) && repository.existsByDate(req.date())) {
            throw new ConflictException("A holiday already exists on " + req.date());
        }
        holiday.setDate(req.date());
        holiday.setName(req.name());
        holiday.setType(req.type());
        HolidayResponse updated = toResponse(repository.save(holiday));
        auditService.record("UPDATE", "Holiday", id.toString(), req.name() + " on " + req.date());
        return updated;
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Holiday not found: " + id);
        }
        repository.deleteById(id);
        auditService.record("DELETE", "Holiday", id.toString(), "Deleted holiday " + id);
    }

    private static HolidayResponse toResponse(HolidayEntity h) {
        return new HolidayResponse(h.getId(), h.getDate().toString(), h.getName(), h.getType());
    }
}

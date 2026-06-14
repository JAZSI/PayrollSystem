package com.com253.payrollsystem.audit;

import com.com253.payrollsystem.audit.dto.AuditPage;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Read-only, paged audit trail (ADMIN). */
@RestController
@RequestMapping("/api/audit")
@PreAuthorize("hasRole('ADMIN')")
public class AuditController {

    private final AuditService service;

    public AuditController(AuditService service) {
        this.service = service;
    }

    @GetMapping
    public AuditPage list(@RequestParam(required = false) String entity,
                          @RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "25") int size) {
        return service.find(entity, page, size);
    }
}

package com.com253.payrollsystem.payroll;

import com.com253.payrollsystem.shared.security.CurrentUserService;
import com.com253.payrollsystem.payroll.dto.PayslipResponse;
import com.com253.payrollsystem.payroll.dto.RunPayrollRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Payroll runs and payslips; employees see only their own. */
@RestController
public class PayrollController {

    private final PayrollService service;
    private final PayslipPdfService pdfService;
    private final CurrentUserService currentUser;

    public PayrollController(PayrollService service, PayslipPdfService pdfService,
                             CurrentUserService currentUser) {
        this.service = service;
        this.pdfService = pdfService;
        this.currentUser = currentUser;
    }

    @PostMapping("/api/payroll/run")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public PayslipResponse run(@Valid @RequestBody RunPayrollRequest request) {
        return service.runPayroll(request);
    }

    @GetMapping("/api/payslips")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public List<PayslipResponse> history(@RequestParam(required = false) String employeeId) {
        return service.history(employeeId);
    }

    /** Self-service: the current user's own payslips (empty for users with no employee link). */
    @GetMapping("/api/payslips/me")
    public List<PayslipResponse> myPayslips() {
        String employeeId = currentUser.currentEmployeeId();
        return employeeId == null ? List.of() : service.history(employeeId);
    }

    @GetMapping("/api/payslips/{id}")
    public PayslipResponse get(@PathVariable Long id) {
        return authorize(service.get(id));
    }

    @GetMapping("/api/payslips/{id}/pdf")
    public ResponseEntity<byte[]> pdf(@PathVariable Long id) {
        PayslipResponse slip = authorize(service.get(id));
        byte[] pdf = pdfService.render(slip);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"payslip-" + id + ".pdf\"")
                .body(pdf);
    }

    /** ADMIN/HR may view any payslip; an EMPLOYEE only their own. */
    private PayslipResponse authorize(PayslipResponse slip) {
        if (currentUser.isStaff() || slip.employeeId().equals(currentUser.currentEmployeeId())) {
            return slip;
        }
        throw new AccessDeniedException("You can only view your own payslips");
    }
}

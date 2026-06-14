package com.com253.payrollsystem.report;

import com.com253.payrollsystem.report.dto.BankReport;
import com.com253.payrollsystem.report.dto.RegisterReport;
import com.com253.payrollsystem.report.dto.RemittanceReport;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

/** Finance reports + CSV exports (ADMIN/HR). */
@RestController
@RequestMapping("/api/reports")
@PreAuthorize("hasAnyRole('ADMIN','HR')")
public class ReportController {

    private final ReportService service;

    public ReportController(ReportService service) {
        this.service = service;
    }

    @GetMapping("/register")
    public RegisterReport register(@RequestParam String period) {
        return service.register(period);
    }

    @GetMapping("/remittance")
    public RemittanceReport remittance(@RequestParam String period) {
        return service.remittance(period);
    }

    @GetMapping("/bank")
    public BankReport bank(@RequestParam String period) {
        return service.bank(period);
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> export(@RequestParam String type, @RequestParam String period) {
        String csv = switch (type) {
            case "register" -> service.registerCsv(period);
            case "remittance" -> service.remittanceCsv(period);
            case "bank" -> service.bankCsv(period);
            default -> throw new IllegalArgumentException("Unknown report type: " + type);
        };
        String filename = type + "-" + period + ".csv";
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(csv.getBytes(StandardCharsets.UTF_8));
    }
}

package com.com253.payrollsystem.shared.mapping;

import com.com253.payrollsystem.shared.domain.Employee;
import com.com253.payrollsystem.shared.domain.PayrollEntry;
import com.com253.payrollsystem.shared.domain.TimeRecord;
import com.com253.payrollsystem.shared.domain.employeetypes.Contractual;
import com.com253.payrollsystem.shared.domain.employeetypes.PartTimer;
import com.com253.payrollsystem.shared.domain.employeetypes.Probationary;
import com.com253.payrollsystem.shared.domain.employeetypes.Regular;
import com.com253.payrollsystem.employee.EmployeeEntity;
import com.com253.payrollsystem.payroll.PayslipEntity;
import com.com253.payrollsystem.attendance.TimeRecordEntity;
import com.com253.payrollsystem.payroll.dto.PayslipResponse;

/** Shared conversions between persistence entities, the pure domain model, and DTOs. */
public final class PayrollMapping {

    private PayrollMapping() {
    }

    public static Employee toDomainEmployee(EmployeeEntity e) {
        return switch (e.getType()) {
            case REGULAR -> new Regular(e.getId(), e.getFullName(), e.getMonthlyRate());
            case PROBATIONARY -> new Probationary(e.getId(), e.getFullName(), e.getMonthlyRate());
            case CONTRACTUAL -> new Contractual(e.getId(), e.getFullName(), e.getMonthlyRate());
            case PART_TIMER -> new PartTimer(e.getId(), e.getFullName(), e.getHourlyRate());
        };
    }

    public static TimeRecord toDomainRecord(TimeRecordEntity e) {
        return new TimeRecord(e.getDayNumber(), e.getTimeIn(), e.getTimeOut(),
                e.isAbsent(), e.getHolidayType());
    }

    /** Builds an (unsaved) payslip entity from a computed payroll entry. */
    public static PayslipEntity toEntity(EmployeeEntity emp, PayrollEntry entry) {
        PayslipEntity s = new PayslipEntity();
        s.setEmployeeId(emp.getId());
        s.setEmployeeName(emp.getFullName());
        s.setEmployeeType(emp.getType());
        s.setCutoffPeriod(entry.getCutOffPeriod());
        s.setTotalHours(entry.getTotalHoursWorked());
        s.setOvertimeHours(entry.getOvertimeHours());
        s.setUndertimeHours(entry.getUndertimeHours());
        s.setAbsentDays(entry.getAbsentDays());
        s.setBasicPay(entry.getBasicPay());
        s.setOvertimePay(entry.getOvertimePay());
        s.setNightDiffPay(entry.getNightDiffPay());
        s.setAllowances(entry.getAllowances());
        s.setGrossPay(entry.getGrossPay());
        s.setSss(entry.getSssDeduction());
        s.setPhilhealth(entry.getPhilhealthDeduction());
        s.setPagibig(entry.getPagibigDeduction());
        s.setTax(entry.getTaxDeduction());
        s.setLoan(entry.getLoanDeduction());
        s.setOtherDeductions(entry.getOtherDeductions());
        s.setUndertimePenalty(entry.getUndertimePenalty());
        s.setAbsencePenalty(entry.getAbsencePenalty());
        s.setEmployerSss(entry.getEmployerSss());
        s.setEmployerPhilhealth(entry.getEmployerPhilhealth());
        s.setEmployerPagibig(entry.getEmployerPagibig());
        s.setEmployerEc(entry.getEmployerEc());
        s.setNetPay(entry.getNetPay());
        return s;
    }

    public static PayslipResponse toResponse(PayslipEntity s) {
        return new PayslipResponse(
                s.getId(), s.getEmployeeId(), s.getEmployeeName(),
                s.getEmployeeType(), s.getEmployeeType().getLabel(), s.getCutoffPeriod(),
                s.getTotalHours(), s.getOvertimeHours(), s.getUndertimeHours(), s.getAbsentDays(),
                s.getBasicPay(), s.getOvertimePay(), s.getNightDiffPay(), s.getAllowances(),
                s.getGrossPay(),
                s.getSss(), s.getPhilhealth(), s.getPagibig(), s.getTax(), s.getLoan(),
                s.getOtherDeductions(),
                s.getUndertimePenalty(), s.getAbsencePenalty(),
                s.getEmployerSss(), s.getEmployerPhilhealth(), s.getEmployerPagibig(), s.getEmployerEc(),
                s.getNetPay(),
                s.getCreatedAt() == null ? null : s.getCreatedAt().toString());
    }
}

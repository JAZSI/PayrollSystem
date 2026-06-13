package com.com253.payrollsystem.leave;

import com.com253.payrollsystem.employee.EmployeeRepository;
import com.com253.payrollsystem.holiday.HolidayRepository;
import com.com253.payrollsystem.leave.dto.LeaveBalanceResponse;
import com.com253.payrollsystem.leave.dto.LeaveRequestRequest;
import com.com253.payrollsystem.leave.dto.LeaveRequestResponse;
import com.com253.payrollsystem.leave.dto.LeaveTypeRequest;
import com.com253.payrollsystem.leave.dto.LeaveTypeResponse;
import com.com253.payrollsystem.shared.error.ConflictException;
import com.com253.payrollsystem.shared.error.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Leave types, per-year balances (accrued from defaults), requests, and payroll coverage. */
@Service
@Transactional
public class LeaveService {

    private final LeaveTypeRepository types;
    private final LeaveBalanceRepository balances;
    private final LeaveRequestRepository requests;
    private final EmployeeRepository employees;
    private final HolidayRepository holidays;

    public LeaveService(LeaveTypeRepository types, LeaveBalanceRepository balances,
                        LeaveRequestRepository requests, EmployeeRepository employees,
                        HolidayRepository holidays) {
        this.types = types;
        this.balances = balances;
        this.requests = requests;
        this.employees = employees;
        this.holidays = holidays;
    }

    // ------------------------------ Leave types ------------------------------

    @Transactional(readOnly = true)
    public List<LeaveTypeResponse> findAllTypes() {
        return types.findAllByOrderByName().stream().map(LeaveService::toType).toList();
    }

    public LeaveTypeResponse createType(LeaveTypeRequest req) {
        if (types.existsByName(req.name())) {
            throw new ConflictException("A leave type named '" + req.name() + "' already exists");
        }
        return toType(types.save(new LeaveType(req.name(), req.paid(), req.defaultAnnualCredits())));
    }

    public LeaveTypeResponse updateType(Long id, LeaveTypeRequest req) {
        LeaveType type = types.findById(id)
                .orElseThrow(() -> new NotFoundException("Leave type not found: " + id));
        if (!type.getName().equals(req.name()) && types.existsByName(req.name())) {
            throw new ConflictException("A leave type named '" + req.name() + "' already exists");
        }
        type.setName(req.name());
        type.setPaid(req.paid());
        type.setDefaultAnnualCredits(req.defaultAnnualCredits());
        return toType(types.save(type));
    }

    public void deleteType(Long id) {
        if (!types.existsById(id)) {
            throw new NotFoundException("Leave type not found: " + id);
        }
        types.deleteById(id);
    }

    // ------------------------------- Balances --------------------------------

    /** Returns the employee's balances for the year, accruing any missing types first. */
    public List<LeaveBalanceResponse> balancesFor(String employeeId, int year) {
        ensureBalances(employeeId, year);
        Map<Long, LeaveType> byId = typeIndex();
        return balances.findByEmployeeIdAndYear(employeeId, year).stream()
                .map(b -> toBalance(b, byId.get(b.getLeaveTypeId())))
                .filter(r -> r != null)
                .toList();
    }

    /** Creates a balance row per leave type for the year from each type's default credits. */
    public void ensureBalances(String employeeId, int year) {
        for (LeaveType type : types.findAll()) {
            balances.findByEmployeeIdAndLeaveTypeIdAndYear(employeeId, type.getId(), year)
                    .orElseGet(() -> balances.save(
                            new LeaveBalance(employeeId, type.getId(), year, type.getDefaultAnnualCredits())));
        }
    }

    // ------------------------------- Requests --------------------------------

    @Transactional(readOnly = true)
    public List<LeaveRequestResponse> listRequests(LeaveStatus status) {
        List<LeaveRequest> found = status == null
                ? requests.findAllByOrderByCreatedAtDesc()
                : requests.findByStatusOrderByCreatedAtDesc(status);
        return mapRequests(found);
    }

    @Transactional(readOnly = true)
    public List<LeaveRequestResponse> requestsByEmployee(String employeeId) {
        return mapRequests(requests.findByEmployeeIdOrderByCreatedAtDesc(employeeId));
    }

    public LeaveRequestResponse file(String employeeId, LeaveRequestRequest req) {
        if (!employees.existsById(employeeId)) {
            throw new NotFoundException("Employee not found: " + employeeId);
        }
        LeaveType type = types.findById(req.leaveTypeId())
                .orElseThrow(() -> new NotFoundException("Leave type not found: " + req.leaveTypeId()));
        if (req.endDate().isBefore(req.startDate())) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
        int days = workingDaysBetween(req.startDate(), req.endDate());
        if (days <= 0) {
            throw new IllegalArgumentException("The selected range has no working days");
        }
        LeaveRequest saved = requests.save(new LeaveRequest(
                employeeId, type.getId(), req.startDate(), req.endDate(), days, req.reason()));
        return toRequest(saved, type.getName());
    }

    public LeaveRequestResponse approve(Long id, String decidedBy) {
        LeaveRequest request = getRequestOrThrow(id);
        requirePending(request);
        LeaveType type = types.findById(request.getLeaveTypeId())
                .orElseThrow(() -> new NotFoundException("Leave type not found: " + request.getLeaveTypeId()));

        if (type.isPaid()) {
            int year = request.getStartDate().getYear();
            ensureBalances(request.getEmployeeId(), year);
            LeaveBalance balance = balances
                    .findByEmployeeIdAndLeaveTypeIdAndYear(request.getEmployeeId(), type.getId(), year)
                    .orElseThrow(() -> new NotFoundException("Leave balance not found"));
            if (balance.getRemaining() < request.getDays()) {
                throw new ConflictException("Insufficient '" + type.getName() + "' credits: "
                        + balance.getRemaining() + " left, " + request.getDays() + " requested");
            }
            balance.setUsed(balance.getUsed() + request.getDays());
            balances.save(balance);
        }

        request.setStatus(LeaveStatus.APPROVED);
        request.setDecidedBy(decidedBy);
        return toRequest(requests.save(request), type.getName());
    }

    public LeaveRequestResponse reject(Long id, String decidedBy) {
        LeaveRequest request = getRequestOrThrow(id);
        requirePending(request);
        request.setStatus(LeaveStatus.REJECTED);
        request.setDecidedBy(decidedBy);
        return toRequest(requests.save(request), typeName(request.getLeaveTypeId()));
    }

    // --------------------------- Payroll integration ---------------------------

    /** Absent days covered by approved *paid* leave whose dates fall in the cut-off. */
    @Transactional(readOnly = true)
    public int coveredDaysFor(String employeeId, String period) {
        List<Long> paidTypeIds = types.findAll().stream()
                .filter(LeaveType::isPaid).map(LeaveType::getId).toList();
        int covered = 0;
        for (LeaveRequest request : requests.findByEmployeeIdAndStatus(employeeId, LeaveStatus.APPROVED)) {
            if (!paidTypeIds.contains(request.getLeaveTypeId())) {
                continue;
            }
            for (LocalDate d = request.getStartDate(); !d.isAfter(request.getEndDate()); d = d.plusDays(1)) {
                if (isWorkingDay(d) && inPeriod(period, d.getDayOfMonth())) {
                    covered++;
                }
            }
        }
        return covered;
    }

    // ------------------------------- helpers -------------------------------

    private int workingDaysBetween(LocalDate start, LocalDate end) {
        int count = 0;
        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
            if (isWorkingDay(d)) {
                count++;
            }
        }
        return count;
    }

    private boolean isWorkingDay(LocalDate date) {
        DayOfWeek dow = date.getDayOfWeek();
        if (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY) {
            return false;
        }
        return !holidays.existsByDate(date);
    }

    private static boolean inPeriod(String period, int dayOfMonth) {
        if ("1st-15th".equals(period)) {
            return dayOfMonth <= 15;
        }
        if ("16th-30th".equals(period)) {
            return dayOfMonth >= 16;
        }
        return false;
    }

    private LeaveRequest getRequestOrThrow(Long id) {
        return requests.findById(id)
                .orElseThrow(() -> new NotFoundException("Leave request not found: " + id));
    }

    private static void requirePending(LeaveRequest request) {
        if (request.getStatus() != LeaveStatus.PENDING) {
            throw new ConflictException("Only a pending request can be decided (current: "
                    + request.getStatus() + ")");
        }
    }

    private Map<Long, LeaveType> typeIndex() {
        return types.findAll().stream().collect(Collectors.toMap(LeaveType::getId, Function.identity()));
    }

    private String typeName(Long leaveTypeId) {
        return types.findById(leaveTypeId).map(LeaveType::getName).orElse("?");
    }

    private List<LeaveRequestResponse> mapRequests(List<LeaveRequest> found) {
        Map<Long, LeaveType> byId = typeIndex();
        return found.stream()
                .map(r -> toRequest(r, byId.containsKey(r.getLeaveTypeId())
                        ? byId.get(r.getLeaveTypeId()).getName() : "?"))
                .toList();
    }

    private static LeaveTypeResponse toType(LeaveType t) {
        return new LeaveTypeResponse(t.getId(), t.getName(), t.isPaid(), t.getDefaultAnnualCredits());
    }

    private static LeaveBalanceResponse toBalance(LeaveBalance b, LeaveType type) {
        if (type == null) {
            return null;
        }
        return new LeaveBalanceResponse(b.getLeaveTypeId(), type.getName(), type.isPaid(),
                b.getYear(), b.getCredits(), b.getUsed(), b.getRemaining());
    }

    private static LeaveRequestResponse toRequest(LeaveRequest r, String typeName) {
        return new LeaveRequestResponse(
                r.getId(), r.getEmployeeId(), r.getLeaveTypeId(), typeName,
                r.getStartDate().toString(), r.getEndDate().toString(), r.getDays(),
                r.getStatus(), r.getReason(), r.getDecidedBy(),
                r.getCreatedAt() == null ? null : r.getCreatedAt().toString());
    }
}

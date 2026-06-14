package com.com253.payrollsystem.report;

import com.com253.payrollsystem.employee.EmployeeEntity;
import com.com253.payrollsystem.employee.EmployeeRepository;
import com.com253.payrollsystem.payroll.PayrollRepository;
import com.com253.payrollsystem.payroll.PayslipEntity;
import com.com253.payrollsystem.report.dto.BankReport;
import com.com253.payrollsystem.report.dto.BankRow;
import com.com253.payrollsystem.report.dto.RegisterReport;
import com.com253.payrollsystem.report.dto.RegisterRow;
import com.com253.payrollsystem.report.dto.RemittanceReport;
import com.com253.payrollsystem.shared.Money;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Read-only aggregation of saved payslips into finance reports + CSV exports. */
@Service
@Transactional(readOnly = true)
public class ReportService {

    private final PayrollRepository payslips;
    private final EmployeeRepository employees;

    public ReportService(PayrollRepository payslips, EmployeeRepository employees) {
        this.payslips = payslips;
        this.employees = employees;
    }

    // ------------------------------- Register -------------------------------

    public RegisterReport register(String period) {
        List<RegisterRow> rows = slips(period).stream().map(ReportService::toRegisterRow).toList();
        double gross = rows.stream().mapToDouble(RegisterRow::grossPay).sum();
        double deductions = rows.stream().mapToDouble(RegisterRow::totalDeductions).sum();
        double net = rows.stream().mapToDouble(RegisterRow::netPay).sum();
        return new RegisterReport(period, rows, Money.round2(gross),
                Money.round2(deductions), Money.round2(net));
    }

    // ------------------------------ Remittance ------------------------------

    public RemittanceReport remittance(String period) {
        List<PayslipEntity> slips = slips(period);
        double sssEe = sum(slips, PayslipEntity::getSss);
        double sssEr = sum(slips, PayslipEntity::getEmployerSss);
        double ec = sum(slips, PayslipEntity::getEmployerEc);
        double phEe = sum(slips, PayslipEntity::getPhilhealth);
        double phEr = sum(slips, PayslipEntity::getEmployerPhilhealth);
        double piEe = sum(slips, PayslipEntity::getPagibig);
        double piEr = sum(slips, PayslipEntity::getEmployerPagibig);
        double tax = sum(slips, PayslipEntity::getTax);
        double grand = sssEe + sssEr + ec + phEe + phEr + piEe + piEr + tax;
        return new RemittanceReport(period,
                Money.round2(sssEe), Money.round2(sssEr), Money.round2(ec),
                Money.round2(phEe), Money.round2(phEr),
                Money.round2(piEe), Money.round2(piEr),
                Money.round2(tax), Money.round2(grand));
    }

    // -------------------------------- Bank ----------------------------------

    public BankReport bank(String period) {
        Map<String, String> accountById = employees.findAll().stream()
                .collect(Collectors.toMap(EmployeeEntity::getId,
                        e -> e.getBankAccount() == null ? "" : e.getBankAccount(), (a, b) -> a));
        List<BankRow> rows = slips(period).stream()
                .map(s -> new BankRow(s.getEmployeeId(), s.getEmployeeName(),
                        accountById.getOrDefault(s.getEmployeeId(), ""), s.getNetPay()))
                .toList();
        double net = rows.stream().mapToDouble(BankRow::netPay).sum();
        return new BankReport(period, rows, Money.round2(net));
    }

    // -------------------------------- CSV -----------------------------------

    public String registerCsv(String period) {
        StringBuilder sb = new StringBuilder(
                "Employee ID,Name,Gross,SSS,PhilHealth,Pag-IBIG,Tax,Loan,Other Deductions,Penalties,Total Deductions,Net\n");
        for (RegisterRow r : register(period).rows()) {
            sb.append(csv(r.employeeId())).append(',').append(csv(r.employeeName())).append(',')
              .append(r.grossPay()).append(',').append(r.sss()).append(',').append(r.philhealth()).append(',')
              .append(r.pagibig()).append(',').append(r.tax()).append(',').append(r.loan()).append(',')
              .append(r.otherDeductions()).append(',').append(r.penalties()).append(',')
              .append(r.totalDeductions()).append(',').append(r.netPay()).append('\n');
        }
        return sb.toString();
    }

    public String remittanceCsv(String period) {
        RemittanceReport r = remittance(period);
        return "Agency,Employee Share,Employer Share\n"
                + "SSS," + r.sssEmployee() + ',' + r.sssEmployer() + '\n'
                + "SSS EC,0," + r.sssEc() + '\n'
                + "PhilHealth," + r.philhealthEmployee() + ',' + r.philhealthEmployer() + '\n'
                + "Pag-IBIG," + r.pagibigEmployee() + ',' + r.pagibigEmployer() + '\n'
                + "BIR (Withholding Tax)," + r.tax() + ",0\n"
                + "TOTAL," + "," + r.grandTotal() + '\n';
    }

    public String bankCsv(String period) {
        StringBuilder sb = new StringBuilder("Employee ID,Name,Bank Account,Net Pay\n");
        for (BankRow r : bank(period).rows()) {
            sb.append(csv(r.employeeId())).append(',').append(csv(r.employeeName())).append(',')
              .append(csv(r.bankAccount())).append(',').append(r.netPay()).append('\n');
        }
        return sb.toString();
    }

    // ------------------------------- helpers --------------------------------

    private List<PayslipEntity> slips(String period) {
        return payslips.findByCutoffPeriodOrderByEmployeeName(period);
    }

    private static double sum(List<PayslipEntity> slips, Function<PayslipEntity, Double> field) {
        return slips.stream().mapToDouble(field::apply).sum();
    }

    private static RegisterRow toRegisterRow(PayslipEntity s) {
        double penalties = s.getUndertimePenalty() + s.getAbsencePenalty();
        double totalDeductions = Money.round2(s.getGrossPay() - s.getNetPay());
        return new RegisterRow(s.getEmployeeId(), s.getEmployeeName(), s.getGrossPay(),
                s.getSss(), s.getPhilhealth(), s.getPagibig(), s.getTax(), s.getLoan(),
                s.getOtherDeductions(), Money.round2(penalties), totalDeductions, s.getNetPay());
    }

    /** Quotes a CSV field if it contains a comma, quote, or newline. */
    private static String csv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return '"' + value.replace("\"", "\"\"") + '"';
        }
        return value;
    }
}

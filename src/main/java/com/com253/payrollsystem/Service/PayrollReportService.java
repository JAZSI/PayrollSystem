package com.com253.payrollsystem.Service;

import com.com253.payrollsystem.Model.PayrollReportEntry;
import com.com253.payrollsystem.Repository.SubmissionRepository;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

/**
 * Provides payroll reporting and CSV export functionality.
 * All methods are pure — no side effects, no state.
 */
public class PayrollReportService {

    private final SubmissionRepository submissionRepository = new SubmissionRepository();

    /**
     * Retrieves all payroll entries for a specific cutoff period.
     *
     * @param cutOffPeriod selected cutoff period label
     * @return list of report entries sorted by employee name
     */
    public List<PayrollReportEntry> getReportByPeriod(String cutOffPeriod) throws SQLException {
        return submissionRepository.findByPeriod(cutOffPeriod);
    }

    /**
     * Retrieves all payroll entries across all periods.
     *
     * @return list of all report entries sorted by period then name
     */
    public List<PayrollReportEntry> getAllReports() throws SQLException {
        return submissionRepository.findAllReports();
    }

    /**
     * Computes period-wide summary totals for display.
     *
     * @param entries list of report entries for the period
     * @return          string array: [totalGross, totalDeductions, totalNet]
     */
    public double[] computePeriodTotals(List<PayrollReportEntry> entries) {
        double totalGross = 0.0;
        double totalNet   = 0.0;
        for (PayrollReportEntry e : entries) {
            totalGross += e.grossPay();
            totalNet   += e.netPay();
        }
        double totalDeductions = totalGross - totalNet;
        return new double[] { totalGross, totalDeductions, totalNet };
    }

    /**
     * Exports payroll entries to a CSV file.
     * Format: Employee ID, Name, Period, Total Hours, OT Hours, Undertime Hours,
     * Absent Days, Basic Pay, OT Pay, Holiday Pay, NSD, Gross Pay, SSS, PhilHealth,
     * Pag-IBIG, Tax, Loan, Undertime Penalty, Absence Penalty, Net Pay
     *
     * @param entries list of report entries to export
     * @param filePath destination file path
     */
    public void exportToCsv(List<PayrollReportEntry> entries, String filePath) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("Employee ID,Name,Period,Total Hours,OT Hours,Undertime Hours,"
                         + "Absent Days,Basic Pay,OT Pay,Holiday Pay,NSD,Gross Pay,"
                         + "SSS,PhilHealth,Pag-IBIG,Tax,Loan,Undertime Penalty,"
                         + "Absence Penalty,Net Pay");

            for (PayrollReportEntry e : entries) {
                writer.printf("%s,%s,%s,%.2f,%.2f,%.2f,%d,%.2f,%.2f,%.2f,%.2f,%.2f,"
                            + "%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f%n",
                    csvEscape(e.employeeId()),
                    csvEscape(e.employeeName()),
                    csvEscape(e.cutOffPeriod()),
                    e.totalHours(),
                    e.overtimeHours(),
                    e.undertimeHours(),
                    e.absentDays(),
                    e.basicPay(),
                    e.overtimePay(),
                    e.holidayPay(),
                    e.nightShiftDifferential(),
                    e.grossPay(),
                    e.sssDeduction(),
                    e.philhealthDeduction(),
                    e.pagibigDeduction(),
                    e.taxDeduction(),
                    e.loanDeduction(),
                    e.undertimePenalty(),
                    e.absencePenalty(),
                    e.netPay()
                );
            }
        }
    }

    /**
     * Escapes a string for CSV output — wraps in quotes if it contains
     * a comma, quote, or newline.
     *
     * @param value the raw string
     * @return      CSV-safe string
     */
    private String csvEscape(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
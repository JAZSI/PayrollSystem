package com.com253.payrollsystem.CLI;
import com.com253.payrollsystem.Model.Employee;
import com.com253.payrollsystem.Model.PayrollEntry;
import com.com253.payrollsystem.Model.TimeRecord;
import com.com253.payrollsystem.Model.EmployeeTypes.Contractual;
import com.com253.payrollsystem.Model.EmployeeTypes.PartTimer;
import com.com253.payrollsystem.Model.EmployeeTypes.Probationary;
import com.com253.payrollsystem.Model.EmployeeTypes.Regular;
import com.com253.payrollsystem.Service.PayrollCalculator;
import com.com253.payrollsystem.Service.WorkingDayCalculator;
import java.util.Scanner;

public class Menu {

    private static Scanner scanner = new Scanner(System.in);

    /**
     * Runs the CLI payroll menu loop.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        printHeader();

        int choice = 1;
        do {
            processPayroll();

            System.out.println("\n------------------------------------------------------------");
            System.out.println("  What would you like to do next?");
            System.out.println("  [1] Process Another Payroll");
            System.out.println("  [2] Exit");
            System.out.println("------------------------------------------------------------");
            choice = InputValidator.readIntInRange(scanner, "  Enter choice: ", 1, 2);
        } while (choice == 1);

        System.out.println("\n  Thank you for using the Payroll System. Goodbye!");
        scanner.close();
    }

    /**
     * Prints the CLI header.
     */
    static void printHeader() {
        System.out.println("============================================================");
        System.out.println("              JAVA PAYROLL MANAGEMENT SYSTEM                ");
        System.out.println("============================================================");
    }

    /**
     * Runs one full payroll processing flow.
     */
    static void processPayroll() {
        System.out.println("\n============================================================");
        System.out.println("  PAYROLL PROCESSING");
        System.out.println("============================================================");

        // 1. Collect employee data
        Employee employee = createEmployee();

        // 2. Select cut-off period
        String cutOffPeriod = readCutOffPeriod();

        // 3. Enter attendance
        System.out.println("\n  --- Time Records for " + cutOffPeriod + " ---");
        TimeRecord[] records = readAllTimeRecords(cutOffPeriod);

        // 4. Loan deduction
        double loanAmount = readLoanAmount();

        // 5. Compute payroll
        PayrollEntry entry = PayrollCalculator.buildPayrollEntry(
                employee, records, cutOffPeriod, loanAmount);

        // 6. Print payslip
        printPayslip(entry);
    }

    /**
     * Displays employee type choices and reads the user selection.
     *
     * @return selected type code
     */
    static String readEmployeeType() {
        System.out.println("\n  Employee Type:");
        System.out.println("    [R] Regular");
        System.out.println("    [P] Probationary");
        System.out.println("    [C] Contractual");
        System.out.println("    [T] Part-Timer");

        return InputValidator.readEmployeeType(scanner, "  Enter type: ");
    }

    /**
     * Collects employee details and creates the matching employee object.
     *
     * @return created employee
     */
    static Employee createEmployee() {
        System.out.println("\n  --- Employee Information ---");

        String type = readEmployeeType();

        String id = InputValidator.readEmployeeId(scanner,
            "  Employee ID: ");

        String name = InputValidator.readPersonName(scanner, "  Full Name: ");

        switch (type) {
            case "R": {
                double rate = InputValidator.readDoubleMin(scanner,
                    "  Monthly Rate (PHP): ", 1.0);
                return new Regular(id, name, rate);
            }
            case "P": {
                double rate = InputValidator.readDoubleMin(scanner,
                    "  Monthly Rate (PHP): ", 1.0);
                return new Probationary(id, name, rate);
            }
            case "C": {
                double rate = InputValidator.readDoubleMin(scanner,
                    "  Monthly Rate (PHP): ", 1.0);
                return new Contractual(id, name, rate);
            }
            case "T": {
                double rate = InputValidator.readDoubleMin(scanner,
                    "  Hourly Rate (PHP): ", 1.0);
                return new PartTimer(id, name, rate);
            }
            default:
                // Should never reach here due to validation above
                throw new IllegalStateException("Unexpected employee type: " + type);
        }
    }

    /**
     * Asks which payroll cut-off period to use.
     *
     * @return chosen cut-off label
     */
    static String readCutOffPeriod() {
        System.out.println("\n  Cut-Off Period:");
        System.out.println("    [1] 1st-15th");
        System.out.println("    [2] 16th-30th");
        int choice = InputValidator.readIntInRange(scanner, "  Enter choice: ", 1, 2);
        return (choice == 1) ? "1st-15th" : "16th-30th";
    }

    /**
     * Builds a list of working days (Monday to Friday) for a cut-off period.
     *
     * @param cutOffPeriod selected cut-off period
     * @return array of working day numbers
     */
    static int[] getWorkingDays(String cutOffPeriod) {
        return WorkingDayCalculator.getWorkingDaysForCurrentMonth(cutOffPeriod);
    }

    /**
     * Reads one day's attendance and returns a time record.
     *
     * @param dayNumber day of month
     * @return created time record for the day
     */
    static TimeRecord readTimeRecord(int dayNumber) {
        System.out.println("\n  Day " + dayNumber + ":");

        boolean isAbsent = InputValidator.readYesNo(scanner,
                "    Was the employee absent? (Y/N): ");

        if (isAbsent) {
            return new TimeRecord(dayNumber, 0, 0, true, TimeRecord.HOLIDAY_NONE);
        }

        // Read and validate time-in
        int timeIn = InputValidator.readHHMM(scanner,
                "    Time In  (HHMM, e.g. 800 for 8:00 AM): ");

        // Read and validate time-out (must be after time-in)
        int timeOut;
        while (true) {
            timeOut = InputValidator.readHHMM(scanner,
                    "    Time Out (HHMM, e.g. 1700 for 5:00 PM): ");
            if (timeOut <= timeIn) {
                System.out.println("    Time Out must be later than Time In.");
                continue;
            }
            break;
        }

        System.out.println("    Holiday Type:");
        System.out.println("      [1] Regular Day");
        System.out.println("      [2] Regular Holiday");
        System.out.println("      [3] Special Holiday / Rest Day");

        int holidayChoice = InputValidator.readIntInRange(
            scanner,
            "    Enter holiday type: ",
            1,
            3);

        String holidayType = mapHolidayType(holidayChoice);

        return new TimeRecord(dayNumber, timeIn, timeOut, false, holidayType);
    }

    private static String mapHolidayType(int holidayChoice) {
        switch (holidayChoice) {
            case 2:
                return TimeRecord.HOLIDAY_REGULAR;
            case 3:
                return TimeRecord.HOLIDAY_REST_DAY;
            default:
                return TimeRecord.HOLIDAY_NONE;
        }
    }

    /**
     * Reads attendance records for all working days in the cut-off period.
     *
     * @param cutOffPeriod selected cut-off period
     * @return array of time records
     */
    static TimeRecord[] readAllTimeRecords(String cutOffPeriod) {
        int[] workingDays = getWorkingDays(cutOffPeriod);
        TimeRecord[] records = new TimeRecord[workingDays.length];

        for (int i = 0; i < workingDays.length; i++) {
            records[i] = readTimeRecord(workingDays[i]);
        }
        return records;
    }

    /**
     * Reads the loan amount to deduct for the cut-off.
     *
     * @return loan amount, or 0.0 if no loan
     */
    static double readLoanAmount() {
        System.out.println("\n  --- Loan Deduction ---");
        boolean hasLoan = InputValidator.readYesNo(scanner,
                "  Is there a loan deduction? (Y/N): ");

        if (!hasLoan) return 0.0;

        return InputValidator.readDoubleMin(scanner, "  Loan Amount (PHP): ", 0.01);
    }

    /**
     * Prints a formatted payslip using computed payroll data.
     *
     * @param entry payroll entry to print
     */
    static void printPayslip(PayrollEntry entry) {
        Employee emp = entry.getEmployee();
        final int labelWidth = 35;
        final int valueWidth = 18;

        System.out.println("\n");
        System.out.println("============================================================");
        System.out.println("                        ABC Company                         ");
        System.out.println("                  Employee Payroll System                   ");
        System.out.println("============================================================");
        System.out.printf("  Employee ID   : %s%n",  emp.getEmployeeId());
        System.out.printf("  Name          : %s%n",  emp.getName());
        System.out.printf("  Type          : %s%n",  emp.getEmployeeType());
        System.out.printf("  Cut-Off Period: %s%n",  entry.getCutOffPeriod());
        System.out.println("------------------------------------------------------------");

        System.out.println("  ATTENDANCE SUMMARY");
        System.out.printf("  %-" + labelWidth + "s %-" + valueWidth + "s%n", "Total Hours Worked:",
            String.format("%,.2f hrs", entry.getTotalHoursWorked()));
        System.out.printf("  %-" + labelWidth + "s %-" + valueWidth + "s%n", "Overtime Hours:",
            String.format("%,.2f hrs", entry.getOvertimeHours()));
        System.out.printf("  %-" + labelWidth + "s %-" + valueWidth + "s%n", "Undertime Hours:",
            String.format("%,.2f hrs", entry.getUndertimeHours()));
        System.out.printf("  %-" + labelWidth + "s %-" + valueWidth + "s%n", "Absent Days:",
            String.format("%d day(s)", entry.getAbsentDays()));
        System.out.println("------------------------------------------------------------");

        System.out.println("  SALARY DETAILS");
        System.out.printf("  %-" + labelWidth + "s %-" + valueWidth + "s%n", "Basic Salary:",
            formatPeso(entry.getBasicPay()));
        System.out.println("  Additional:");
        System.out.printf("  %-" + labelWidth + "s %-" + valueWidth + "s%n", "Overtime:",
            formatPeso(entry.getOvertimePay()));
        System.out.println("------------------------------------------------------------");

        System.out.println("  EARNINGS");
        System.out.printf("  %-" + labelWidth + "s %-" + valueWidth + "s%n", "Gross Pay:",
                formatPeso(entry.getGrossPay()));
        System.out.println("------------------------------------------------------------");

        System.out.println("  DEDUCTIONS");
        System.out.printf("  %-" + labelWidth + "s %-" + valueWidth + "s%n", "SSS:",
                formatPeso(entry.getSssDeduction()));
        System.out.printf("  %-" + labelWidth + "s %-" + valueWidth + "s%n", "PhilHealth:",
                formatPeso(entry.getPhilhealthDeduction()));
        System.out.printf("  %-" + labelWidth + "s %-" + valueWidth + "s%n", "Pag-IBIG:",
                formatPeso(entry.getPagibigDeduction()));
        System.out.printf("  %-" + labelWidth + "s %-" + valueWidth + "s%n", "Withholding Tax:",
                formatPeso(entry.getTaxDeduction()));
        System.out.printf("  %-" + labelWidth + "s %-" + valueWidth + "s%n", "Loan Deduction:",
                formatPeso(entry.getLoanDeduction()));
        System.out.printf("  %-" + labelWidth + "s %-" + valueWidth + "s%n", "Undertime Penalty:",
                formatPeso(entry.getUndertimePenalty()));
        System.out.printf("  %-" + labelWidth + "s %-" + valueWidth + "s%n", "Absence Penalty:",
                formatPeso(entry.getAbsencePenalty()));
        System.out.println("------------------------------------------------------------");

        System.out.printf("  %-" + labelWidth + "s %-" + valueWidth + "s%n", "NET PAY:", formatPeso(entry.getNetPay()));
        System.out.println("============================================================");
    }

    /**
     * Formats a number as Philippine Peso text.
     *
     * @param amount value to format
     * @return formatted peso string
     */
    static String formatPeso(double amount) {
        return String.format("PHP %,.2f", amount);
    }

}

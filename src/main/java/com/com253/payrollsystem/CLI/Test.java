package com.com253.payrollsystem.CLI;

import com.com253.payrollsystem.Model.Employee;
import com.com253.payrollsystem.Model.PayrollEntry;
import com.com253.payrollsystem.Model.TimeRecord;
import com.com253.payrollsystem.Model.EmployeeTypes.Contractual;
import com.com253.payrollsystem.Model.EmployeeTypes.PartTimer;
import com.com253.payrollsystem.Model.EmployeeTypes.Probationary;
import com.com253.payrollsystem.Model.EmployeeTypes.Regular;
import com.com253.payrollsystem.Service.PayrollCalculator;

public class Test {

    private static final String EMPLOYEE_TYPE = "R";
    private static final String EMPLOYEE_ID = "1234-5678-90";
    private static final String EMPLOYEE_NAME = "John Christian R. Senoto";
    private static final double RATE = 30000.00;

    private static final String CUT_OFF_PERIOD = "1st-15th";
    private static final double LOAN_AMOUNT = 0.00;

    private static final TimeRecordData[] TIME_RECORD_DATA = {
        new TimeRecordData(1, 800, 1700, false, TimeRecord.HOLIDAY_NONE),
        new TimeRecordData(2, 800, 1700, false, TimeRecord.HOLIDAY_NONE),
        new TimeRecordData(3, 800, 1700, false, TimeRecord.HOLIDAY_NONE),
        new TimeRecordData(4, 800, 1700, false, TimeRecord.HOLIDAY_NONE),
        new TimeRecordData(5, 800, 1700, false, TimeRecord.HOLIDAY_NONE),
        new TimeRecordData(6, 800, 1700, false, TimeRecord.HOLIDAY_NONE),
        new TimeRecordData(7, 800, 1700, false, TimeRecord.HOLIDAY_NONE),
        new TimeRecordData(8, 800, 1700, false, TimeRecord.HOLIDAY_NONE),
        new TimeRecordData(9, 800, 1700, false, TimeRecord.HOLIDAY_NONE),
        new TimeRecordData(10, 800, 1700, false, TimeRecord.HOLIDAY_NONE),
        new TimeRecordData(11, 800, 1700, false, TimeRecord.HOLIDAY_NONE),
        new TimeRecordData(12, 800, 1700, false, TimeRecord.HOLIDAY_NONE),
        new TimeRecordData(13, 800, 1700, false, TimeRecord.HOLIDAY_NONE),
        new TimeRecordData(14, 800, 1700, false, TimeRecord.HOLIDAY_NONE),
        new TimeRecordData(15, 800, 1700, false, TimeRecord.HOLIDAY_NONE)
    };

    /**
     * Runs a full payroll flow using fixed values instead of user input.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        Employee employee = createEmployeeFromConstants();
        TimeRecord[] records = buildConstantTimeRecords();

        PayrollEntry entry = PayrollCalculator.buildPayrollEntry(
                employee,
                records,
                CUT_OFF_PERIOD,
                LOAN_AMOUNT);

        Menu.printPayslip(entry);
    }

    private static Employee createEmployeeFromConstants() {
        switch (EMPLOYEE_TYPE) {
            case "R":
                return new Regular(EMPLOYEE_ID, EMPLOYEE_NAME, RATE);
            case "P":
                return new Probationary(EMPLOYEE_ID, EMPLOYEE_NAME, RATE);
            case "C":
                return new Contractual(EMPLOYEE_ID, EMPLOYEE_NAME, RATE);
            case "T":
                return new PartTimer(EMPLOYEE_ID, EMPLOYEE_NAME, RATE);
            default:
                throw new IllegalStateException("Unexpected employee type: " + EMPLOYEE_TYPE);
        }
    }

    private static TimeRecord[] buildConstantTimeRecords() {
        TimeRecord[] records = new TimeRecord[TIME_RECORD_DATA.length];
        for (int i = 0; i < TIME_RECORD_DATA.length; i++) {
            TimeRecordData data = TIME_RECORD_DATA[i];
            records[i] = new TimeRecord(
                    data.dayNumber,
                    data.timeIn,
                    data.timeOut,
                    data.isAbsent,
                    data.holidayType);
        }
        return records;
    }

    private static final class TimeRecordData {
        private final int dayNumber;
        private final int timeIn;
        private final int timeOut;
        private final boolean isAbsent;
        private final String holidayType;

        private TimeRecordData(int dayNumber, int timeIn, int timeOut, boolean isAbsent, String holidayType) {
            this.dayNumber = dayNumber;
            this.timeIn = timeIn;
            this.timeOut = timeOut;
            this.isAbsent = isAbsent;
            this.holidayType = holidayType;
        }
    }
}

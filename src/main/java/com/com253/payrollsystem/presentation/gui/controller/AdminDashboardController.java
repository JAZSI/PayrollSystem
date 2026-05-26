package com.com253.payrollsystem.presentation.gui.controller;

import com.com253.payrollsystem.app.service.AttendanceService;
import com.com253.payrollsystem.app.service.EmployeeService;
import com.com253.payrollsystem.app.service.SimplePayrollService;
import com.com253.payrollsystem.app.service.SimplePayrollService.SimplePayrollResult;
import com.com253.payrollsystem.domain.model.AttendanceRecord;
import com.com253.payrollsystem.domain.model.Employee;
import com.com253.payrollsystem.domain.model.Employee.EmployeeType;
import com.com253.payrollsystem.domain.model.LeaveBalance;
import com.com253.payrollsystem.domain.model.LoanBalance;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class AdminDashboardController {

    private final EmployeeService employeeService = new EmployeeService();
    private final AttendanceService attendanceService = new AttendanceService();
    private final SimplePayrollService payrollService = new SimplePayrollService();
    private final ObservableList<Employee> employees = FXCollections.observableArrayList();
    private final ObservableList<AttendanceRow> attendanceRows = FXCollections.observableArrayList();
    private final ObservableList<PayrollRow> payrollRows = FXCollections.observableArrayList();

    private boolean editingEmployee;
    private String editingEmployeeId;

    @FXML private VBox viewDashboard;
    @FXML private VBox viewEmployees;
    @FXML private VBox viewAttendance;
    @FXML private VBox viewPayroll;
    @FXML private VBox viewReports;
    @FXML private VBox viewSettings;

    @FXML private TableView<Employee> employeeTable;
    @FXML private TableColumn<Employee, String> empColId;
    @FXML private TableColumn<Employee, String> empColName;
    @FXML private TableColumn<Employee, String> empColDept;
    @FXML private TableColumn<Employee, String> empColType;
    @FXML private TableColumn<Employee, String> empColRate;
    @FXML private TableColumn<Employee, String> empColLeave;
    @FXML private TableColumn<Employee, String> empColLoan;
    @FXML private TableColumn<Employee, Void> empColAction;

    @FXML private TableView<AttendanceRow> attendanceAdminTable;
    @FXML private TableColumn<AttendanceRow, String> attAdColDay;
    @FXML private TableColumn<AttendanceRow, String> attAdColDate;
    @FXML private TableColumn<AttendanceRow, String> attAdColIn;
    @FXML private TableColumn<AttendanceRow, String> attAdColOut;
    @FXML private TableColumn<AttendanceRow, String> attAdColHrs;
    @FXML private TableColumn<AttendanceRow, String> attAdColOt;
    @FXML private TableColumn<AttendanceRow, String> attAdColUt;
    @FXML private TableColumn<AttendanceRow, String> attAdColAbsent;
    @FXML private TableColumn<AttendanceRow, String> attAdColHoliday;

    @FXML private Label adminSumHours;
    @FXML private Label adminSumOT;
    @FXML private Label adminSumUT;
    @FXML private Label adminSumAbsent;

    @FXML private TableView<PayrollRow> payrollTable;
    @FXML private TableColumn<PayrollRow, String> prColName;
    @FXML private TableColumn<PayrollRow, String> prColBasic;
    @FXML private TableColumn<PayrollRow, String> prColOT;
    @FXML private TableColumn<PayrollRow, String> prColGross;
    @FXML private TableColumn<PayrollRow, String> prColSSS;
    @FXML private TableColumn<PayrollRow, String> prColPH;
    @FXML private TableColumn<PayrollRow, String> prColPIG;
    @FXML private TableColumn<PayrollRow, String> prColTax;
    @FXML private TableColumn<PayrollRow, String> prColLoan;
    @FXML private TableColumn<PayrollRow, String> prColNet;
    @FXML private TableColumn<PayrollRow, Void> prColAct;

    @FXML private VBox payrollPlaceholder;
    @FXML private VBox payrollResults;

    @FXML private StackPane modalOverlay;
    @FXML private Label modalTitle;
    @FXML private TextField employeeIdField;
    @FXML private TextField employeeNameField;
    @FXML private ComboBox<String> employeeTypeCombo;
    @FXML private ComboBox<String> employeeRateTypeCombo;
    @FXML private TextField employeeRateField;
    @FXML private TextField employeeLoanField;
    @FXML private ComboBox<String> employeeDepartmentCombo;
    @FXML private CheckBox hasLeaveCheckbox;

    public void initialize() {
        setupEmployeeTable();
        setupAttendanceTable();
        setupPayrollTable();
        showDashboard();
        loadEmployees();
    }

    public void showDashboard() { showOnly(viewDashboard); }
    public void showEmployees() { showOnly(viewEmployees); }
    public void showAttendance() { showOnly(viewAttendance); }
    public void showPayroll() { showOnly(viewPayroll); }
    public void showReports() { showOnly(viewReports); }
    public void showSettings() { showOnly(viewSettings); }

    public void handleLogout() {
        System.exit(0);
    }

    public void handleNotifications() {
        info("Notifications", "No pending notifications for the demo build.");
    }

    public void openAddEmployeeModal() {
        editingEmployee = false;
        editingEmployeeId = null;
        modalTitle.setText("Add Employee");
        employeeIdField.setDisable(false);
        clearEmployeeForm();
        modalOverlay.setVisible(true);
        modalOverlay.setManaged(true);
    }

    public void handleExport() { info("Export", "Report export is a placeholder for the demo."); }
    public void handleExportCsv() { info("CSV Export", "CSV export is a placeholder for the demo."); }
    public void handlePdfPayslips() { info("PDF Payslips", "PDF export is a placeholder for the demo."); }
    public void handleBulkExport() { info("Bulk Export", "Bulk export is a placeholder for the demo."); }
    public void toggleDay() { info("Calendar", "Working-day toggles are currently visual only."); }
    public void addHoliday() { info("Holiday Manager", "Holiday editing is currently visual only."); }
    public void deleteHoliday() { info("Holiday Manager", "Holiday deletion is currently visual only."); }
    public void addAdmin() { info("Admin Accounts", "Admin account management is currently visual only."); }

    public void closeModal() {
        modalOverlay.setVisible(false);
        modalOverlay.setManaged(false);
        clearEmployeeForm();
    }

    public void saveEmployee() {
        String employeeId = editingEmployee && editingEmployeeId != null ? editingEmployeeId : text(employeeIdField);
        String name = text(employeeNameField);
        String typeLabel = value(employeeTypeCombo, "Regular");
        String rateType = value(employeeRateTypeCombo, "Monthly Rate");
        String department = value(employeeDepartmentCombo, "Operations");

        if (employeeId.isBlank() || name.isBlank()) {
            warn("Validation", "Employee ID and name are required.");
            return;
        }

        double rate;
        double loanBalance;
        try {
            rate = parseDouble(employeeRateField, 0.0);
            loanBalance = parseDouble(employeeLoanField, 0.0);
        } catch (NumberFormatException ex) {
            warn("Validation", "Rate and loan balance must be numeric.");
            return;
        }

        EmployeeType type = parseType(typeLabel);
        boolean hasLeave = hasLeaveCheckbox.isSelected();
        LeaveBalance leaveBalance = hasLeave ? new LeaveBalance(10, 10, 5) : new LeaveBalance(0, 0, 0);
        LoanBalance loan = new LoanBalance(Math.max(0.0, loanBalance));
        double monthlyRate = "Hourly Rate".equals(rateType) ? 0.0 : rate;
        double hourlyRate = "Hourly Rate".equals(rateType) ? rate : 0.0;

        Employee employee = new Employee(employeeId, name, type, monthlyRate, hourlyRate, hasLeave, leaveBalance, loan);

        try {
            employeeService.saveEmployee(employee);
            loadEmployees();
            closeModal();
            info("Saved", "Employee saved successfully for " + department + ".");
        } catch (SQLException e) {
            error("Save Failed", e.getMessage());
        }
    }

    public void loadAttendance() {
        try {
            Employee employee = firstEmployee();
            if (employee == null) {
                warn("Attendance", "No employees available.");
                return;
            }

            DateRange range = currentDemoRange();
            List<AttendanceRecord> records = attendanceService.getAttendanceHistory(employee.getEmployeeId(), range.from(), range.to());
            List<AttendanceRow> rows = new ArrayList<>();
            double totalHours = 0.0;
            double otHours = 0.0;
            double utHours = 0.0;
            int workingDays = 0;

            for (AttendanceRecord record : records) {
                double hours = 0.0;
                double ot = 0.0;
                double ut = 0.0;
                String status = "Absent";
                String timeIn = "-";
                String timeOut = "-";

                if (record.getTimeIn() != null) {
                    timeIn = formatTime(record.getTimeIn());
                }
                if (record.getTimeOut() != null) {
                    timeOut = formatTime(record.getTimeOut());
                }
                if (record.getTimeIn() != null && record.getTimeOut() != null) {
                    hours = Math.max(0.0, record.getTimeOut() - record.getTimeIn());
                    ot = Math.max(0.0, hours - 8.0);
                    ut = Math.max(0.0, 8.0 - hours);
                    status = "Present";
                    workingDays++;
                }

                totalHours += hours;
                otHours += ot;
                utHours += ut;

                rows.add(new AttendanceRow(
                        String.valueOf(record.getRecordDate().getDayOfMonth()),
                        record.getRecordDate().format(DateTimeFormatter.ofPattern("MMM d")),
                        timeIn,
                        timeOut,
                        formatDecimal(hours),
                        formatDecimal(ot),
                        formatDecimal(ut),
                        status,
                        "None"));
            }

            attendanceRows.setAll(rows);
            attendanceAdminTable.setItems(attendanceRows);
            adminSumHours.setText(formatDecimal(totalHours));
            adminSumOT.setText(formatDecimal(otHours));
            adminSumUT.setText(formatDecimal(utHours));
            adminSumAbsent.setText(String.valueOf(Math.max(0, range.days() - workingDays)));
            showAttendance();
        } catch (SQLException e) {
            error("Attendance", e.getMessage());
        }
    }

    public void computePayroll() {
        try {
            List<Employee> allEmployees = employeeService.getAllEmployees();
            if (allEmployees.isEmpty()) {
                warn("Payroll", "No employees available.");
                return;
            }

            DateRange range = currentDemoRange();
            List<PayrollRow> rows = new ArrayList<>();
            for (Employee employee : allEmployees) {
                List<AttendanceRecord> attendance = attendanceService.getAttendanceHistory(employee.getEmployeeId(), range.from(), range.to());
                SimplePayrollResult result = payrollService.calculate(employee, attendance, range.from(), range.to());
                rows.add(new PayrollRow(
                        result.employeeName(),
                        formatMoney(result.basicPay()),
                        formatMoney(result.overtimePay()),
                        formatMoney(result.grossPay()),
                        formatMoney(result.sssDeduction()),
                        formatMoney(result.philhealthDeduction()),
                        formatMoney(result.pagibigDeduction()),
                        formatMoney(result.taxDeduction()),
                        formatMoney(result.loanDeduction()),
                        formatMoney(result.netPay())));
            }

            payrollRows.setAll(rows);
            payrollTable.setItems(payrollRows);
            payrollPlaceholder.setVisible(false);
            payrollPlaceholder.setManaged(false);
            payrollResults.setVisible(true);
            payrollResults.setManaged(true);
            showPayroll();
        } catch (SQLException e) {
            error("Payroll", e.getMessage());
        }
    }

    public void loadEmployees() {
        try {
            employees.setAll(employeeService.getAllEmployees());
            employeeTable.setItems(employees);
        } catch (SQLException e) {
            error("Employees", e.getMessage());
        }
    }

    private void setupEmployeeTable() {
        empColId.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getEmployeeId()));
        empColName.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getName()));
        empColDept.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(departmentFor(cell.getValue())));
        empColType.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getTypeName()));
        empColRate.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(formatEmployeeRate(cell.getValue())));
        empColLeave.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(String.valueOf(cell.getValue().getLeaveBalance().getTotal())));
        empColLoan.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(formatMoney(cell.getValue().getLoanBalance().getBalance())));
        empColAction.setCellFactory(actionButtons(Employee.class, this::editEmployee, this::deleteEmployee));
    }

    private void setupAttendanceTable() {
        attAdColDay.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().day()));
        attAdColDate.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().date()));
        attAdColIn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().timeIn()));
        attAdColOut.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().timeOut()));
        attAdColHrs.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().hours()));
        attAdColOt.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().ot()));
        attAdColUt.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().ut()));
        attAdColAbsent.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().status()));
        attAdColHoliday.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().holiday()));
    }

    private void setupPayrollTable() {
        prColName.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().employeeName()));
        prColBasic.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().basicPay()));
        prColOT.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().overtimePay()));
        prColGross.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().grossPay()));
        prColSSS.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().sss()));
        prColPH.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().philhealth()));
        prColPIG.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().pagibig()));
        prColTax.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().tax()));
        prColLoan.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().loan()));
        prColNet.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().net()));
        prColAct.setCellFactory(actionButtons(PayrollRow.class,
            row -> info("Payslip", "Payslip preview ready for " + row.employeeName()),
            row -> {}));
    }

    private <T> Callback<TableColumn<T, Void>, TableCell<T, Void>> actionButtons(Class<T> type, java.util.function.Consumer<T> editAction, java.util.function.Consumer<T> deleteAction) {
        return column -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox box = new HBox(6, editButton, deleteButton);

            {
                editButton.setOnAction(event -> {
                    T item = getTableView().getItems().get(getIndex());
                    editAction.accept(item);
                });
                deleteButton.setOnAction(event -> {
                    T item = getTableView().getItems().get(getIndex());
                    deleteAction.accept(item);
                });
                editButton.getStyleClass().add("btn-sm");
                deleteButton.getStyleClass().add("btn-sm");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        };
    }

    private void editEmployee(Employee employee) {
        editingEmployee = true;
        editingEmployeeId = employee.getEmployeeId();
        modalTitle.setText("Edit Employee");
        employeeIdField.setText(employee.getEmployeeId());
        employeeIdField.setDisable(true);
        employeeNameField.setText(employee.getName());
        employeeTypeCombo.setValue(employee.getTypeName());
        employeeRateTypeCombo.setValue(employee.getEmployeeType() == EmployeeType.PARTTIMER ? "Hourly Rate" : "Monthly Rate");
        employeeRateField.setText(formatDecimal(employee.getEmployeeType() == EmployeeType.PARTTIMER ? employee.getHourlyRate() : employee.getMonthlyRate()));
        employeeLoanField.setText(formatDecimal(employee.getLoanBalance().getBalance()));
        employeeDepartmentCombo.setValue(departmentFor(employee));
        hasLeaveCheckbox.setSelected(employee.isHasLeave());
        modalOverlay.setVisible(true);
        modalOverlay.setManaged(true);
    }

    private void deleteEmployee(Employee employee) {
        try {
            employeeService.deleteEmployee(employee.getEmployeeId());
            loadEmployees();
            info("Deleted", "Employee removed: " + employee.getName());
        } catch (SQLException e) {
            error("Delete Failed", e.getMessage());
        }
    }

    private Employee firstEmployee() throws SQLException {
        List<Employee> all = employeeService.getAllEmployees();
        return all.isEmpty() ? null : all.get(0);
    }

    private void showOnly(Node visibleNode) {
        List<Node> views = List.of(viewDashboard, viewEmployees, viewAttendance, viewPayroll, viewReports, viewSettings);
        for (Node view : views) {
            boolean visible = view == visibleNode;
            view.setVisible(visible);
            view.setManaged(visible);
        }
    }

    private void clearEmployeeForm() {
        employeeIdField.clear();
        employeeNameField.clear();
        employeeTypeCombo.setValue("Regular");
        employeeRateTypeCombo.setValue("Monthly Rate");
        employeeRateField.clear();
        employeeLoanField.clear();
        employeeDepartmentCombo.setValue("Operations");
        hasLeaveCheckbox.setSelected(true);
        employeeIdField.setDisable(false);
    }

    private EmployeeType parseType(String typeLabel) {
        return switch (typeLabel) {
            case "Probationary" -> EmployeeType.PROBATIONARY;
            case "Contractual" -> EmployeeType.CONTRACTUAL;
            case "Part-timer" -> EmployeeType.PARTTIMER;
            default -> EmployeeType.REGULAR;
        };
    }

    private String departmentFor(Employee employee) {
        return switch (employee.getEmployeeType()) {
            case REGULAR -> "Operations";
            case PROBATIONARY -> "HR & Admin";
            case CONTRACTUAL -> "Finance";
            case PARTTIMER -> "Sales";
        };
    }

    private String formatEmployeeRate(Employee employee) {
        return employee.getEmployeeType() == EmployeeType.PARTTIMER
                ? formatMoney(employee.getHourlyRate()) + "/hr"
                : formatMoney(employee.getMonthlyRate()) + "/mo";
    }

    private String formatMoney(double value) {
        return String.format(Locale.US, "₱%,.2f", value);
    }

    private String formatDecimal(double value) {
        return String.format(Locale.US, "%.1f", value);
    }

    private String formatTime(double time) {
        int hours = (int) time;
        int minutes = (int) Math.round((time - hours) * 100);
        return String.format(Locale.US, "%02d:%02d", hours, minutes);
    }

    private double parseDouble(TextField field, double defaultValue) {
        String text = text(field);
        return text.isBlank() ? defaultValue : Double.parseDouble(text);
    }

    private String text(TextField field) {
        return field.getText() == null ? "" : field.getText().trim();
    }

    private String value(ComboBox<String> comboBox, String defaultValue) {
        return comboBox.getValue() == null ? defaultValue : comboBox.getValue();
    }

    private void info(String title, String message) {
        showAlert(AlertType.INFORMATION, title, message);
    }

    private void warn(String title, String message) {
        showAlert(AlertType.WARNING, title, message);
    }

    private void error(String title, String message) {
        showAlert(AlertType.ERROR, title, message);
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private DateRange currentDemoRange() {
        LocalDate today = LocalDate.now();
        YearMonth month = YearMonth.of(today.getYear(), today.getMonthValue());
        if (today.getDayOfMonth() <= 15) {
            return new DateRange(month.atDay(1), month.atDay(15));
        }
        return new DateRange(month.atDay(16), month.atEndOfMonth());
    }

    private record DateRange(LocalDate from, LocalDate to) {
        int days() {
            return (int) (java.time.temporal.ChronoUnit.DAYS.between(from, to) + 1);
        }
    }

    public record AttendanceRow(String day, String date, String timeIn, String timeOut, String hours, String ot, String ut, String status, String holiday) {
    }

    public record PayrollRow(String employeeName, String basicPay, String overtimePay, String grossPay, String sss, String philhealth, String pagibig, String tax, String loan, String net) {
    }
}

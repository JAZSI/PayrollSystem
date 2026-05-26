package com.com253.payrollsystem.presentation.gui.controller;

import com.com253.payrollsystem.app.service.AttendanceService;
import com.com253.payrollsystem.app.service.EmployeeService;
import com.com253.payrollsystem.domain.model.AttendanceRecord;
import com.com253.payrollsystem.domain.model.Employee;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
/**
 * Placeholder controller for KioskTerminal.fxml
 * 
 * Once JavaFX is added as a dependency, add the following imports and enhance:
 *   import javafx.fxml.FXML;
 *   import javafx.scene.control.Label;
 */
public class KioskTerminalController {

    private final EmployeeService employeeService = new EmployeeService();
    private final AttendanceService attendanceService = new AttendanceService();

    private final StringBuilder enteredId = new StringBuilder();
    private Employee currentEmployee;

    @FXML private VBox stateIdle;
    @FXML private VBox stateActive;
    @FXML private VBox stateSuccessIn;
    @FXML private VBox stateSuccessOut;
    @FXML private VBox stateErrorNF;
    @FXML private VBox stateErrorDup;

    @FXML private Label inputValueLabel;
    @FXML private Label clockLabel;
    @FXML private Label dateLabel;
    @FXML private Label empAvatarLabel;
    @FXML private Label empNameLabel;
    @FXML private Label empIdLabel;
    @FXML private Label empTypeBadge;
    @FXML private Label successInName;
    @FXML private Label successOutName;
    @FXML private Label successInTimestamp;
    @FXML private Label successOutTimestamp;
    @FXML private Label countdownLabelIn;
    @FXML private Label countdownLabelOut;
    @FXML private Label errorNFMessage;
    @FXML private Label errorDupMessage;
    @FXML private Label errorDupDetail;
    @FXML private Button submitBtn;
    @FXML private Button tabIdle;
    @FXML private Button tabActive;
    @FXML private Button tabSuccessIn;
    @FXML private Button tabSuccessOut;
    @FXML private Button tabErrorNF;
    @FXML private Button tabErrorDup;

    private Timeline clockTimeline;

    public void initialize() {
        startClock();
        showIdle();
    }

    public void showIdle() {
        selectState(stateIdle);
        enteredId.setLength(0);
        currentEmployee = null;
        refreshInput();
    }

    public void showActive() {
        selectState(stateActive);
    }

    public void showSuccessIn() {
        selectState(stateSuccessIn);
        scheduleReturnToIdle();
    }

    public void showSuccessOut() {
        selectState(stateSuccessOut);
        scheduleReturnToIdle();
    }

    public void showErrorNotFound() {
        selectState(stateErrorNF);
        scheduleReturnToIdle();
    }

    public void showErrorDuplicate() {
        selectState(stateErrorDup);
        scheduleReturnToIdle();
    }

    public void handleBackspace() {
        if (!enteredId.isEmpty()) {
            enteredId.deleteCharAt(enteredId.length() - 1);
        }
        refreshInput();
    }

    public void handleKey0() { appendDigit('0'); }
    public void handleKey1() { appendDigit('1'); }
    public void handleKey2() { appendDigit('2'); }
    public void handleKey3() { appendDigit('3'); }
    public void handleKey4() { appendDigit('4'); }
    public void handleKey5() { appendDigit('5'); }
    public void handleKey6() { appendDigit('6'); }
    public void handleKey7() { appendDigit('7'); }
    public void handleKey8() { appendDigit('8'); }
    public void handleKey9() { appendDigit('9'); }

    public void handleSubmit() {
        String employeeId = enteredId.toString().trim();
        if (employeeId.isEmpty()) {
            showErrorNotFound();
            return;
        }

        try {
            Employee employee = employeeService.findEmployee(employeeId);
            if (employee == null) {
                currentEmployee = null;
                errorNFMessage.setText("No employee matches ID " + employeeId + ". Please check the number and try again.");
                showErrorNotFound();
                return;
            }

            currentEmployee = employee;
            updateActiveEmployee(employee);
            showActive();
            Platform.runLater(() -> processAutoAttendance(employee));
        } catch (SQLException e) {
            currentEmployee = null;
            errorNFMessage.setText(e.getMessage());
            showErrorNotFound();
        }
    }

    public void handleTimeIn() {
        processAutoAttendance(currentEmployee);
    }

    public void handleTimeOut() {
        processAutoAttendance(currentEmployee);
    }

    public void goToIdle() {
        showIdle();
    }

    private void appendDigit(char digit) {
        if (enteredId.length() < 20) {
            enteredId.append(digit);
            refreshInput();
        }
    }

    private void refreshInput() {
        String value = enteredId.isEmpty() ? "Enter employee ID…" : enteredId.toString();
        inputValueLabel.setText(value);
        inputValueLabel.getStyleClass().remove("empty");
        if (enteredId.isEmpty()) {
            inputValueLabel.getStyleClass().add("empty");
        }
    }

    private void selectState(VBox activeState) {
        VBox[] states = {stateIdle, stateActive, stateSuccessIn, stateSuccessOut, stateErrorNF, stateErrorDup};
        for (VBox state : states) {
            boolean active = state == activeState;
            state.setVisible(active);
            state.setManaged(active);
        }
    }

    private void updateActiveEmployee(Employee employee) {
        empNameLabel.setText(employee.getName());
        empIdLabel.setText(employee.getEmployeeId());
        empAvatarLabel.setText(initials(employee.getName()));
        empTypeBadge.setText(employee.getTypeName());
    }

    private void updateSuccessName(Label label, Employee employee) {
        label.setText(employee.getName());
    }

    private String initials(String name) {
        String[] parts = name == null ? new String[0] : name.trim().split("\\s+");
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            if (!part.isBlank()) {
                builder.append(Character.toUpperCase(part.charAt(0)));
            }
            if (builder.length() == 2) {
                break;
            }
        }
        return builder.length() == 0 ? "--" : builder.toString();
    }

    private void startClock() {
        updateClock();
        clockTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> updateClock()));
        clockTimeline.setCycleCount(Timeline.INDEFINITE);
        clockTimeline.play();
    }

    private void updateClock() {
        LocalTime now = LocalTime.now();
        clockLabel.setText(now.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        dateLabel.setText(LocalDate.now().getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                + ", " + LocalDate.now().format(DateTimeFormatter.ofPattern("MMM dd yyyy")));
    }

    private String formatTimestamp(LocalDate date, LocalTime time) {
        return date.format(DateTimeFormatter.ofPattern("d MMM yyyy")) + " · "
                + time.format(DateTimeFormatter.ofPattern("hh:mm:ss a", Locale.ENGLISH));
    }

    private String formatClock(Double value) {
        if (value == null) {
            return "—";
        }
        int hours = value.intValue();
        int minutes = (int) Math.round((value - hours) * 60);
        if (minutes == 60) {
            hours += 1;
            minutes = 0;
        }
        return String.format(Locale.US, "%02d:%02d", hours, minutes);
    }

    private double currentTimeDecimal() {
        LocalTime now = LocalTime.now();
        return now.getHour() + (now.getMinute() / 60.0) + (now.getSecond() / 3600.0);
    }

    private void processAutoAttendance(Employee employee) {
        if (employee == null) {
            showErrorNotFound();
            return;
        }

        LocalDate today = LocalDate.now();
        try {
            List<AttendanceRecord> records = attendanceService.getAttendanceHistory(employee.getEmployeeId(), today, today);
            AttendanceRecord existing = records.isEmpty() ? null : records.get(0);

            if (existing == null || existing.getTimeIn() == null) {
                attendanceService.clockIn(employee.getEmployeeId(), today, currentTimeDecimal());
                updateSuccessName(successInName, employee);
                successInTimestamp.setText(formatTimestamp(LocalDate.now(), LocalTime.now()));
                countdownLabelIn.setText("Returning to home in 3s…");
                showSuccessIn();
                return;
            }

            if (existing.getTimeOut() == null) {
                attendanceService.clockOut(employee.getEmployeeId(), today, currentTimeDecimal());
                updateSuccessName(successOutName, employee);
                successOutTimestamp.setText(formatTimestamp(LocalDate.now(), LocalTime.now()));
                countdownLabelOut.setText("Returning to home in 3s…");
                showSuccessOut();
                return;
            }

            errorDupMessage.setText(employee.getName() + " already has a complete attendance record today.");
            errorDupDetail.setText("Time In: " + formatClock(existing.getTimeIn()) + "   ·   Time Out: " + formatClock(existing.getTimeOut()));
            showErrorDuplicate();
        } catch (SQLException e) {
            errorNFMessage.setText(e.getMessage());
            showErrorNotFound();
        }
    }

    private void scheduleReturnToIdle() {
        PauseTransition pause = new PauseTransition(Duration.seconds(2.5));
        pause.setOnFinished(event -> goToIdle());
        pause.play();
    }

    // Add @FXML handlers for kiosk actions (clock in/out)
}

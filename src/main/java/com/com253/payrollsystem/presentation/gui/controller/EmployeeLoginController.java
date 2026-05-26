package com.com253.payrollsystem.presentation.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class EmployeeLoginController {

    @FXML
    private VBox loginScreen;

    @FXML
    private VBox dashboardScreen;

    @FXML
    private TextField loginIdField;

    @FXML
    private PasswordField loginPinField;

    @FXML
    private Label greetingName;

    @FXML
    public void initialize() {
        showLoginScreen();
    }

    @FXML
    public void handleLogin() {
        String employeeId = loginIdField.getText() == null ? "Employee" : loginIdField.getText().trim();
        if (employeeId.isEmpty()) {
            employeeId = "Employee";
        }

        if (greetingName != null) {
            greetingName.setText(employeeId);
        }

        showDashboardScreen();
        if (loginPinField != null) {
            loginPinField.clear();
        }
    }

    @FXML
    public void handleNotifications() {
        // UI-only placeholder
    }

    @FXML
    public void handleLogout() {
        showLoginScreen();
    }

    @FXML
    public void switchToFirstCutoff() {
        // UI-only placeholder
    }

    @FXML
    public void switchToSecondCutoff() {
        // UI-only placeholder
    }

    @FXML
    public void handleDownloadPdf() {
        // UI-only placeholder
    }

    @FXML
    public void handlePrint() {
        // UI-only placeholder
    }

    private void showLoginScreen() {
        if (loginScreen != null) {
            loginScreen.setVisible(true);
            loginScreen.setManaged(true);
        }
        if (dashboardScreen != null) {
            dashboardScreen.setVisible(false);
            dashboardScreen.setManaged(false);
        }
    }

    private void showDashboardScreen() {
        if (loginScreen != null) {
            loginScreen.setVisible(false);
            loginScreen.setManaged(false);
        }
        if (dashboardScreen != null) {
            dashboardScreen.setVisible(true);
            dashboardScreen.setManaged(true);
        }
    }
}
package com.com253.payrollsystem.presentation.gui.launcher;

import com.com253.payrollsystem.infrastructure.config.Database;
import java.io.IOException;
import java.io.IOException;
import java.util.Locale;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GuiLauncher extends Application {

    public enum Screen {
        ADMIN_DASHBOARD,
        EMPLOYEE_PORTAL,
        KIOSK_TERMINAL
    }

    private static Screen initialScreen = Screen.ADMIN_DASHBOARD;

    public static void launchScreen(Screen screen) {
        initialScreen = screen == null ? Screen.ADMIN_DASHBOARD : screen;
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        try {
            Database.initialize();
        } catch (Exception e) {
            throw new IOException("Failed to initialize database", e);
        }

        FXMLLoader loader = new FXMLLoader(GuiLauncher.class.getResource(resolveFxmlPath(initialScreen)));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        stage.setTitle("PayrollSystem - " + formatTitle(initialScreen));
        stage.setScene(scene);
        stage.setMinWidth(1280);
        stage.setMinHeight(820);
        stage.show();
    }

    public static void main(String[] args) {
        if (args != null && args.length > 0) {
            initialScreen = parseScreen(args[0]);
        }
        launch(args);
    }

    private static String resolveFxmlPath(Screen screen) {
        return switch (screen) {
            case EMPLOYEE_PORTAL -> "/fxml/EmployeePortal.fxml";
            case KIOSK_TERMINAL -> "/fxml/KioskTerminal.fxml";
            case ADMIN_DASHBOARD -> "/fxml/AdminDashboard.fxml";
        };
    }

    private static String formatTitle(Screen screen) {
        return switch (screen) {
            case EMPLOYEE_PORTAL -> "Employee Portal";
            case KIOSK_TERMINAL -> "Kiosk Terminal";
            case ADMIN_DASHBOARD -> "Admin Dashboard";
        };
    }

    private static Screen parseScreen(String value) {
        String normalized = value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "EMPLOYEE", "PORTAL", "2" -> Screen.EMPLOYEE_PORTAL;
            case "KIOSK", "TERMINAL", "3" -> Screen.KIOSK_TERMINAL;
            default -> Screen.ADMIN_DASHBOARD;
        };
    }
}

package com.com253.payrollsystem.presentation.launcher;

import com.com253.payrollsystem.infrastructure.config.Database;
import com.com253.payrollsystem.presentation.cli.launcher.CliRunner;
import com.com253.payrollsystem.presentation.gui.launcher.GuiLauncher;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

public final class StartupMenu {

    public void run(Scanner scanner) {
        while (true) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> new CliRunner().run(scanner);
                case "2" -> {
                    launchGui(scanner);
                    return;
                }
                case "3" -> resetDatabase(scanner);
                case "4", "exit", "quit" -> {
                    System.out.println("Goodbye");
                    return;
                }
                default -> System.out.println("Unknown choice. Please pick 1, 2, 3, or 4.");
            }
        }
    }

    private void launchGui(Scanner scanner) {
        System.out.println();
        System.out.println("=== GUI Screens ===");
        System.out.println("1) Admin Dashboard");
        System.out.println("2) Employee Portal");
        System.out.println("3) Kiosk Terminal");
        System.out.print("Choose screen: ");

        String guiChoice = scanner.nextLine().trim();
        GuiLauncher.Screen screen = switch (guiChoice) {
            case "2" -> GuiLauncher.Screen.EMPLOYEE_PORTAL;
            case "3" -> GuiLauncher.Screen.KIOSK_TERMINAL;
            default -> GuiLauncher.Screen.ADMIN_DASHBOARD;
        };

        GuiLauncher.launchScreen(screen);
    }

    private void printMenu() {
        System.out.println();
        System.out.println("=== PayrollSystem Startup ===");
        System.out.println("1) CLI");
        System.out.println("2) GUI");
        System.out.println("3) Reset database");
        System.out.println("4) Exit");
        System.out.print("Choose interface: ");
    }

    private void resetDatabase(Scanner scanner) {
        System.out.print("This will erase the local SQLite database. Type YES to continue: ");
        String confirm = scanner.nextLine().trim();
        if (!"YES".equals(confirm)) {
            System.out.println("Reset cancelled.");
            return;
        }

        try {
            Database.wipeAndReinitialize();
            System.out.println("Database reset complete.");
        } catch (SQLException | IOException e) {
            System.err.println("Database reset failed: " + e.getMessage());
        }
    }
}

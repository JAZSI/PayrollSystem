package com.com253.payrollsystem;

import com.com253.payrollsystem.cli.InputValidator;
import com.com253.payrollsystem.cli.Menu;
import java.util.Scanner;

public class Main {

    /**
     * Starts the app and lets the user choose CLI, GUI, or exit.
     * Owns the single {@link Scanner} for the whole application and closes it once.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            printLauncherMenu();

            int choice = InputValidator.readIntInRange(scanner,
                    "  Enter choice: ", 1, 3);

            switch (choice) {
                case 1 -> Menu.run(scanner);
                case 2 -> System.out.println("N/A");
                default -> System.out.println("\n  Goodbye!");
            }
        } finally {
            scanner.close();
        }
    }

    /**
     * Prints the first menu shown when the app starts.
     */
    private static void printLauncherMenu() {
        System.out.println("============================================================");
        System.out.println("              JAVA PAYROLL MANAGEMENT SYSTEM                ");
        System.out.println("============================================================");
        System.out.println("  Choose how to run the system:");
        System.out.println("    [1] CLI");
        System.out.println("    [2] GUI");
        System.out.println("    [3] Exit");
        System.out.println("------------------------------------------------------------");
    }
}
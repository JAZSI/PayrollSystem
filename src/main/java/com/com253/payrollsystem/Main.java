package com.com253.payrollsystem;

import com.com253.payrollsystem.CLI.InputValidator;
import java.util.Scanner;

public class Main {

    private static Scanner scanner = new Scanner(System.in);

    /**
     * Starts the app and lets the user choose CLI, GUI, or exit.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        printLauncherMenu();

        int choice = InputValidator.readIntInRange(scanner,
                "  Enter choice: ", 1, 3);

        switch (choice) {
            case 1:
                com.com253.payrollsystem.CLI.Menu.main(args);
                break;
            case 2:
                com.com253.payrollsystem.GUI.Menu.main(args);
                break;
            default:
                System.out.println("\n  Goodbye!");
                break;
        }

        scanner.close();
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
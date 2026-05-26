package com.com253.payrollsystem.presentation.cli.menu;

import com.com253.payrollsystem.presentation.cli.command.*;
import com.com253.payrollsystem.presentation.cli.launcher.CliContext;

public class MainMenu {

    public static void printMainMenu(CliContext ctx) {
        System.out.println();
        System.out.println("=== Main Menu ===");
        System.out.println("1) Login");
        System.out.println("2) List employees");
        System.out.println("3) Register employee");
        System.out.println("4) Clock in");
        System.out.println("5) Clock out");
        System.out.println("6) Submit payroll (employee)");
        System.out.println("7) Build payroll entry (generate)");
        System.out.println("8) View pending submissions");
        System.out.println("9) Exit");
        System.out.print("Choice: ");
    }

    public static void handleChoice(String choice, CliContext ctx) throws Exception {
        switch (choice) {
            case "1":
                new LoginCommand().execute(ctx);
                break;
            case "2":
                new ListEmployeesCommand().execute(ctx);
                break;
            case "3":
                new RegisterEmployeeCommand().execute(ctx);
                break;
            case "4":
                new ClockInCommand().execute(ctx);
                break;
            case "5":
                new ClockOutCommand().execute(ctx);
                break;
            case "6":
                new SubmitPayrollCommand().execute(ctx);
                break;
            case "7":
                new BuildPayrollEntryCommand().execute(ctx);
                break;
            case "8":
                new ViewPendingSubmissionsCommand().execute(ctx);
                break;
            default:
                System.out.println("Unknown choice: " + choice);
        }
    }
}

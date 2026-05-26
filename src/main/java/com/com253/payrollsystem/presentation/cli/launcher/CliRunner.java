package com.com253.payrollsystem.presentation.cli.launcher;

import com.com253.payrollsystem.app.service.PayrollService;
import com.com253.payrollsystem.presentation.cli.menu.MainMenu;
import java.util.Scanner;

public class CliRunner {

    public void run(Scanner scanner) {
        PayrollService payrollService = new PayrollService();
        CliContext ctx = new CliContext(payrollService, scanner);

        System.out.println("PayrollSystem CLI - welcome");
        try {
            while (true) {
                MainMenu.printMainMenu(ctx);
                String choice = scanner.nextLine().trim();
                if ("exit".equalsIgnoreCase(choice) || "9".equals(choice)) {
                    System.out.println("Goodbye");
                    break;
                }
                MainMenu.handleChoice(choice, ctx);
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

package com.com253.payrollsystem.presentation.cli.launcher;

import com.com253.payrollsystem.app.service.PayrollService;
import com.com253.payrollsystem.infrastructure.config.Database;
import com.com253.payrollsystem.presentation.cli.menu.SimulationMenu;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

public final class SimulationLauncher {

    private SimulationLauncher() {
    }

    public static void main(String[] args) {
        try {
            Database.initialize();
        } catch (SQLException | IOException e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
            return;
        }

        try (Scanner scanner = new Scanner(System.in)) {
            new SimulationMenu(new PayrollService(), scanner).run();
        }
    }
}
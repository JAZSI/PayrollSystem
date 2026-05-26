package com.com253.payrollsystem.presentation.launcher;

import com.com253.payrollsystem.infrastructure.config.Database;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

public final class AppLauncher {

    private AppLauncher() {
    }

    public static void main(String[] args) {
        try {
            Database.initialize();
        } catch (SQLException | IOException e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
            return;
        }

        try (Scanner scanner = new Scanner(System.in)) {
            new StartupMenu().run(scanner);
        }
    }
}

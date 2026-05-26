package com.com253.payrollsystem.presentation.gui.navigation;

public class Routes {

    /**
     * Screen enumeration with FXML and CSS resource paths.
     */
    public enum Screen {
        // ═══ ADMIN MODULE ═══
        ADMIN_DASHBOARD("admin/dashboard.fxml", "admin/dashboard.css", "Admin Dashboard"),
        ADMIN_EMPLOYEES("admin/employees.fxml", "admin/employees.css", "Employees"),
        ADMIN_ATTENDANCE("admin/attendance.fxml", "admin/attendance.css", "Attendance"),
        ADMIN_PAYROLL("admin/payroll.fxml", "admin/payroll.css", "Payroll Run"),
        ADMIN_REPORTS("admin/reports.fxml", "admin/reports.css", "Reports"),
        ADMIN_SETTINGS("admin/settings.fxml", "admin/settings.css", "Settings"),

        // ═══ EMPLOYEE MODULE ═══
        EMPLOYEE_LOGIN("employee/login.fxml", "employee/login.css", "Employee Login"),
        EMPLOYEE_DASHBOARD("employee/dashboard.fxml", "employee/dashboard.css", "Employee Dashboard"),
        EMPLOYEE_ATTENDANCE("employee/attendance.fxml", "employee/attendance.css", "My Attendance"),
        EMPLOYEE_PAYSLIP("employee/payslip.fxml", "employee/payslip.css", "My Payslip"),
        EMPLOYEE_LEAVE("employee/leave.fxml", "employee/leave.css", "Leave Management"),

        // ═══ KIOSK MODULE ═══
        KIOSK_IDLE("kiosk/idle.fxml", "kiosk/idle.css", "Kiosk Idle"),
        KIOSK_ACTIVE("kiosk/active.fxml", "kiosk/active.css", "Kiosk Active"),
        KIOSK_SUCCESS_IN("kiosk/success-in.fxml", "kiosk/success-in.css", "Time In Success"),
        KIOSK_SUCCESS_OUT("kiosk/success-out.fxml", "kiosk/success-out.css", "Time Out Success"),
        KIOSK_ERROR_NOT_FOUND("kiosk/error-notfound.fxml", "kiosk/error-notfound.css", "Employee Not Found"),
        KIOSK_ERROR_DUPLICATE("kiosk/error-duplicate.fxml", "kiosk/error-duplicate.css", "Already Timed In");

        private final String fxmlPath;
        private final String cssPath;
        private final String title;

        Screen(String fxmlPath, String cssPath, String title) {
            this.fxmlPath = fxmlPath;
            this.cssPath = cssPath;
            this.title = title;
        }

        /**
         * Get the FXML resource path relative to /fxml directory.
         */
        public String getFxmlPath() {
            return "/fxml/" + fxmlPath;
        }

        /**
         * Get the CSS stylesheet path relative to /css directory.
         */
        public String getCssPath() {
            return "/css/" + cssPath;
        }

        /**
         * Get the user-friendly screen title.
         */
        public String getTitle() {
            return title;
        }

        /**
         * Get the module name (admin, employee, kiosk).
         */
        public String getModule() {
            return fxmlPath.split("/")[0];
        }
    }

    /**
     * Shared stylesheet that applies to all modules.
     */
    public static final String SHARED_THEME = "/css/shared/theme.css";
    public static final String SHARED_COMPONENTS = "/css/shared/components.css";

    /**
     * Build a complete stylesheet list for a screen.
     * Returns array: [shared/theme.css, shared/components.css, module/screen.css]
     */
    public static String[] getStylesheets(Screen screen) {
        return new String[]{
            SHARED_THEME,
            SHARED_COMPONENTS,
            screen.getCssPath()
        };
    }
}

package com.com253.payrollsystem.domain.model;

public enum CutoffPeriod {
    FIRST_HALF("1st-15th", 1, 15),
    SECOND_HALF("16th-end", 16, -1);

    private final String label;
    private final int startDay;
    private final int endDay;

    CutoffPeriod(String label, int startDay, int endDay) {
        this.label = label;
        this.startDay = startDay;
        this.endDay = endDay;
    }

    public String label() {
        return label;
    }

    public int startDay() {
        return startDay;
    }

    public int endDay() {
        return endDay;
    }

    public static CutoffPeriod fromLabel(String label) {
        if (label == null) return FIRST_HALF;
        String normalized = label.trim().toLowerCase();
        if (normalized.contains("16") || normalized.contains("second") || normalized.contains("2nd") || normalized.contains("end")) {
            return SECOND_HALF;
        }
        return FIRST_HALF;
    }
}

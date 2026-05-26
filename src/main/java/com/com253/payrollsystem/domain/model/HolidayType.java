package com.com253.payrollsystem.domain.model;

public enum HolidayType {
    NONE,
    REGULAR_HOLIDAY,
    SPECIAL_OR_REST_DAY;

    public static HolidayType fromLegacyCode(String value) {
        if (REGULAR_HOLIDAY.name().equals(value)) {
            return REGULAR_HOLIDAY;
        }
        if (SPECIAL_OR_REST_DAY.name().equals(value)) {
            return SPECIAL_OR_REST_DAY;
        }
        return NONE;
    }

    public String toLegacyCode() {
        return name();
    }
}

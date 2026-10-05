package com.roomradar.entity;

/**
 * Enumeration representing college working days (Monday to Saturday).
 */
public enum DayOfWeekEnum {
    MON,
    TUE,
    WED,
    THU,
    FRI,
    SAT;

    /**
     * Parses a day string leniently (supports abbreviations and full names).
     *
     * @param value raw day string (e.g., "MON", "Monday", "mon")
     * @return matching DayOfWeekEnum
     * @throws IllegalArgumentException if the day is null, empty, or unparseable
     */
    public static DayOfWeekEnum fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Day of week must not be empty. Valid options: MON, TUE, WED, THU, FRI, SAT");
        }
        String clean = value.trim().toUpperCase();
        switch (clean) {
            case "MON":
            case "MONDAY":
                return MON;
            case "TUE":
            case "TUESDAY":
                return TUE;
            case "WED":
            case "WEDNESDAY":
                return WED;
            case "THU":
            case "THURSDAY":
                return THU;
            case "FRI":
            case "FRIDAY":
                return FRI;
            case "SAT":
            case "SATURDAY":
                return SAT;
            default:
                throw new IllegalArgumentException("Invalid day of week: '" + value + "'. Valid options are MON, TUE, WED, THU, FRI, SAT");
        }
    }
}

package com.roomradar.entity;

/**
 * Enumeration representing different room classifications on campus.
 */
public enum RoomType {
    CLASSROOM,
    LAB,
    SEMINAR_HALL;

    public static RoomType fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return RoomType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid room type: '" + value + "'. Valid options are: CLASSROOM, LAB, SEMINAR_HALL");
        }
    }
}

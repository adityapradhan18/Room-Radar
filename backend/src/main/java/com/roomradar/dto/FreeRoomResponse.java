package com.roomradar.dto;

import com.roomradar.entity.Room;
import com.roomradar.entity.RoomType;

/**
 * DTO representing an available room with its "Free Until" computed status.
 */
public class FreeRoomResponse {

    private Long id;
    private String roomNumber;
    private String building;
    private Integer floor;
    private Integer capacity;
    private RoomType roomType;

    /**
     * Start time of next class after requested window (e.g. "13:00"), or "Rest of the day".
     */
    private String freeUntil;

    /**
     * Optional calculated duration in minutes until next class starts, or null if rest of the day.
     */
    private Long minutesUntilNextClass;

    public FreeRoomResponse() {
    }

    public FreeRoomResponse(Long id, String roomNumber, String building, Integer floor,
                            Integer capacity, RoomType roomType, String freeUntil, Long minutesUntilNextClass) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.building = building;
        this.floor = floor;
        this.capacity = capacity;
        this.roomType = roomType;
        this.freeUntil = freeUntil;
        this.minutesUntilNextClass = minutesUntilNextClass;
    }

    public static FreeRoomResponse fromRoom(Room room, String freeUntil, Long minutesUntilNextClass) {
        return new FreeRoomResponse(
                room.getId(),
                room.getRoomNumber(),
                room.getBuilding(),
                room.getFloor(),
                room.getCapacity(),
                room.getRoomType(),
                freeUntil,
                minutesUntilNextClass
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public String getFreeUntil() {
        return freeUntil;
    }

    public void setFreeUntil(String freeUntil) {
        this.freeUntil = freeUntil;
    }

    public Long getMinutesUntilNextClass() {
        return minutesUntilNextClass;
    }

    public void setMinutesUntilNextClass(Long minutesUntilNextClass) {
        this.minutesUntilNextClass = minutesUntilNextClass;
    }
}

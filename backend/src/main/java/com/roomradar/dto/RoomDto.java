package com.roomradar.dto;

import com.roomradar.entity.Room;
import com.roomradar.entity.RoomType;

/**
 * Data Transfer Object for Room details.
 */
public class RoomDto {

    private Long id;
    private String roomNumber;
    private String building;
    private Integer floor;
    private Integer capacity;
    private RoomType roomType;

    public RoomDto() {
    }

    public RoomDto(Long id, String roomNumber, String building, Integer floor, Integer capacity, RoomType roomType) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.building = building;
        this.floor = floor;
        this.capacity = capacity;
        this.roomType = roomType;
    }

    public static RoomDto fromEntity(Room room) {
        if (room == null) return null;
        return new RoomDto(
                room.getId(),
                room.getRoomNumber(),
                room.getBuilding(),
                room.getFloor(),
                room.getCapacity(),
                room.getRoomType()
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
}

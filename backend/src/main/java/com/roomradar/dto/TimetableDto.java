package com.roomradar.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.roomradar.entity.DayOfWeekEnum;
import com.roomradar.entity.Timetable;
import java.time.LocalTime;

/**
 * Data Transfer Object for scheduled timetable slots.
 */
public class TimetableDto {

    private Long id;
    private Long roomId;
    private String roomNumber;
    private DayOfWeekEnum dayOfWeek;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    private String subject;
    private String faculty;
    private String batch;

    public TimetableDto() {
    }

    public TimetableDto(Long id, Long roomId, String roomNumber, DayOfWeekEnum dayOfWeek,
                        LocalTime startTime, LocalTime endTime, String subject, String faculty, String batch) {
        this.id = id;
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.subject = subject;
        this.faculty = faculty;
        this.batch = batch;
    }

    public static TimetableDto fromEntity(Timetable t) {
        if (t == null) return null;
        Long rId = t.getRoom() != null ? t.getRoom().getId() : null;
        String rNum = t.getRoom() != null ? t.getRoom().getRoomNumber() : null;
        return new TimetableDto(
                t.getId(),
                rId,
                rNum,
                t.getDayOfWeek(),
                t.getStartTime(),
                t.getEndTime(),
                t.getSubject(),
                t.getFaculty(),
                t.getBatch()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public DayOfWeekEnum getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeekEnum dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }
}

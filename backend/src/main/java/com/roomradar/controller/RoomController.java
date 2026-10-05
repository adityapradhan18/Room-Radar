package com.roomradar.controller;

import com.roomradar.dto.FreeRoomResponse;
import com.roomradar.dto.RoomDto;
import com.roomradar.dto.TimetableDto;
import com.roomradar.entity.DayOfWeekEnum;
import com.roomradar.entity.RoomType;
import com.roomradar.service.RoomService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * REST controller exposing endpoints for Room Radar.
 */
@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.OPTIONS})
public class RoomController {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    /**
     * 1. GET /api/rooms/free?day=MON&start=10:00&end=11:00
     * Finds free rooms for a specified day and time window, with optional filters.
     */
    @GetMapping("/free")
    public ResponseEntity<List<FreeRoomResponse>> getFreeRooms(
            @RequestParam("day") String dayStr,
            @RequestParam("start") String startStr,
            @RequestParam("end") String endStr,
            @RequestParam(value = "building", required = false) String building,
            @RequestParam(value = "floor", required = false) Integer floor,
            @RequestParam(value = "roomType", required = false) String roomTypeStr,
            @RequestParam(value = "minCapacity", required = false) Integer minCapacity
    ) {
        DayOfWeekEnum day = DayOfWeekEnum.fromString(dayStr);
        LocalTime start = roomService.parseTime(startStr, "start");
        LocalTime end = roomService.parseTime(endStr, "end");
        RoomType roomType = RoomType.fromString(roomTypeStr);

        List<FreeRoomResponse> freeRooms = roomService.getFreeRooms(
                day, start, end, building, floor, roomType, minCapacity
        );

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Queried-Day", day.name());
        headers.add("X-Queried-Start", start.format(TIME_FORMATTER));
        headers.add("X-Queried-End", end.format(TIME_FORMATTER));
        headers.add("X-Free-Rooms-Count", String.valueOf(freeRooms.size()));

        return ResponseEntity.ok().headers(headers).body(freeRooms);
    }

    /**
     * 2. GET /api/rooms/free-now
     * Finds free rooms for the current 1-hour window starting right now.
     */
    @GetMapping("/free-now")
    public ResponseEntity<List<FreeRoomResponse>> getFreeRoomsNow(
            @RequestParam(value = "building", required = false) String building,
            @RequestParam(value = "floor", required = false) Integer floor,
            @RequestParam(value = "roomType", required = false) String roomTypeStr,
            @RequestParam(value = "minCapacity", required = false) Integer minCapacity
    ) {
        RoomType roomType = RoomType.fromString(roomTypeStr);
        List<FreeRoomResponse> freeRooms = roomService.getFreeRoomsNow(
                building, floor, roomType, minCapacity
        );

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now().withSecond(0).withNano(0);
        LocalTime end = now.plusHours(1);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Server-Date", today.toString());
        headers.add("X-Server-Day", today.getDayOfWeek().name());
        headers.add("X-Window-Start", now.format(TIME_FORMATTER));
        headers.add("X-Window-End", end.format(TIME_FORMATTER));
        headers.add("X-Free-Rooms-Count", String.valueOf(freeRooms.size()));

        return ResponseEntity.ok().headers(headers).body(freeRooms);
    }

    /**
     * 3. GET /api/rooms
     * Lists all registered campus rooms.
     */
    @GetMapping
    public ResponseEntity<List<RoomDto>> getAllRooms() {
        List<RoomDto> rooms = roomService.getAllRooms();
        return ResponseEntity.ok(rooms);
    }

    /**
     * 4. GET /api/rooms/{id}/schedule?day=MON
     * Returns that room's timetable for the specified day.
     */
    @GetMapping("/{id}/schedule")
    public ResponseEntity<List<TimetableDto>> getRoomSchedule(
            @PathVariable("id") Long id,
            @RequestParam("day") String dayStr
    ) {
        DayOfWeekEnum day = DayOfWeekEnum.fromString(dayStr);
        List<TimetableDto> schedule = roomService.getRoomSchedule(id, day);
        return ResponseEntity.ok(schedule);
    }
}

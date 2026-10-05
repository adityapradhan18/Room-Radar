package com.roomradar.service;

import com.roomradar.dto.FreeRoomResponse;
import com.roomradar.dto.RoomDto;
import com.roomradar.dto.TimetableDto;
import com.roomradar.entity.DayOfWeekEnum;
import com.roomradar.entity.Room;
import com.roomradar.entity.RoomType;
import com.roomradar.entity.Timetable;
import com.roomradar.exception.BadRequestException;
import com.roomradar.exception.ResourceNotFoundException;
import com.roomradar.repository.RoomRepository;
import com.roomradar.repository.TimetableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service orchestrating room queries, overlap detection, and schedule calculations.
 */
@Service
@Transactional(readOnly = true)
public class RoomService {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final RoomRepository roomRepository;
    private final TimetableRepository timetableRepository;

    public RoomService(RoomRepository roomRepository, TimetableRepository timetableRepository) {
        this.roomRepository = roomRepository;
        this.timetableRepository = timetableRepository;
    }

    /**
     * Determines whether a scheduled class overlaps with a requested search window.
     *
     * Two time intervals [A_start, A_end) and [B_start, B_end) overlap if and only if:
     *     A_start < B_end AND A_end > B_start
     *
     * Example Scenarios for Requested Window 10:00 - 11:00:
     * 1. Partial Overlap (starts before, ends inside):
     *    Class 09:30 - 10:30: (09:30 < 11:00) && (10:30 > 10:00) -> TRUE (Blocked)
     * 2. Partial Overlap (starts inside, ends after):
     *    Class 10:30 - 11:30: (10:30 < 11:00) && (11:30 > 10:00) -> TRUE (Blocked)
     * 3. Fully Enclosed (class inside window):
     *    Class 10:15 - 10:45: (10:15 < 11:00) && (10:45 > 10:00) -> TRUE (Blocked)
     * 4. Enclosing (class spans past both bounds):
     *    Class 09:00 - 12:00: (09:00 < 11:00) && (12:00 > 10:00) -> TRUE (Blocked)
     * 5. Adjacent Before (ends exactly when window starts):
     *    Class 09:00 - 10:00: (09:00 < 11:00) && (10:00 > 10:00) -> FALSE (Free!)
     * 6. Adjacent After (starts exactly when window ends):
     *    Class 11:00 - 12:00: (11:00 < 11:00) && (12:00 > 10:00) -> FALSE (Free!)
     *
     * @param classStart scheduled class start time
     * @param classEnd scheduled class end time
     * @param requestedStart user requested window start time
     * @param requestedEnd user requested window end time
     * @return true if there is any overlapping period, false otherwise
     */
    public boolean isOverlapping(LocalTime classStart, LocalTime classEnd,
                                 LocalTime requestedStart, LocalTime requestedEnd) {
        if (classStart == null || classEnd == null || requestedStart == null || requestedEnd == null) {
            return false;
        }
        return classStart.isBefore(requestedEnd) && classEnd.isAfter(requestedStart);
    }

    /**
     * Finds all rooms free for a requested day and time interval, applying optional filters.
     *
     * @param day Day of the week (MON - SAT)
     * @param start Window start time
     * @param end Window end time
     * @param building Optional building filter
     * @param floor Optional floor filter
     * @param roomType Optional room type filter
     * @param minCapacity Optional minimum capacity filter
     * @return List of free rooms with computed "free until" badge
     */
    public List<FreeRoomResponse> getFreeRooms(DayOfWeekEnum day, LocalTime start, LocalTime end,
                                               String building, Integer floor,
                                               RoomType roomType, Integer minCapacity) {
        validateTimeWindow(start, end);
        if (day == null) {
            throw new BadRequestException("Day of week must be specified (MON, TUE, WED, THU, FRI, SAT)");
        }

        // Clean optional filters
        String cleanedBuilding = (building != null && !building.trim().isEmpty()) ? building.trim() : null;

        // 1. Fetch candidate rooms matching physical filters
        List<Room> candidateRooms = roomRepository.findWithFilters(cleanedBuilding, floor, roomType, minCapacity);
        if (candidateRooms.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. Fetch all scheduled classes for this day
        List<Timetable> dailyTimetable = timetableRepository.findByDayOfWeek(day);

        // Group timetable by room_id for fast lookup
        Map<Long, List<Timetable>> timetableByRoomId = dailyTimetable.stream()
                .collect(Collectors.groupingBy(t -> t.getRoom().getId()));

        List<FreeRoomResponse> freeRooms = new ArrayList<>();

        for (Room room : candidateRooms) {
            List<Timetable> roomSchedule = timetableByRoomId.getOrDefault(room.getId(), Collections.emptyList());

            // Check if ANY class overlaps with [start, end)
            boolean isBusy = roomSchedule.stream()
                    .anyMatch(t -> isOverlapping(t.getStartTime(), t.getEndTime(), start, end));

            if (!isBusy) {
                // Room is free! Compute "free until":
                // The start time of the next class in that room after the requested window on that day,
                // or "Rest of the day" if there is none.
                String freeUntil = computeFreeUntil(roomSchedule, end);
                Long minutesUntilNextClass = computeMinutesUntilNextClass(roomSchedule, end);

                freeRooms.add(FreeRoomResponse.fromRoom(room, freeUntil, minutesUntilNextClass));
            }
        }

        return freeRooms;
    }

    /**
     * Finds free rooms right now for a 1-hour window starting at current server time.
     *
     * @return List of free rooms right now
     */
    public List<FreeRoomResponse> getFreeRoomsNow(String building, Integer floor,
                                                  RoomType roomType, Integer minCapacity) {
        LocalDate today = LocalDate.now();
        java.time.DayOfWeek jDay = today.getDayOfWeek();

        // On Sunday, no regular classes are scheduled
        if (jDay == java.time.DayOfWeek.SUNDAY) {
            String cleanedBuilding = (building != null && !building.trim().isEmpty()) ? building.trim() : null;
            List<Room> rooms = roomRepository.findWithFilters(cleanedBuilding, floor, roomType, minCapacity);
            return rooms.stream()
                    .map(r -> FreeRoomResponse.fromRoom(r, "Rest of the day", null))
                    .collect(Collectors.toList());
        }

        DayOfWeekEnum day = mapDayOfWeek(jDay);
        LocalTime now = LocalTime.now().withSecond(0).withNano(0);
        LocalTime oneHourLater = now.plusHours(1);

        // Handle day boundary wrap around if past 23:00
        if (oneHourLater.isBefore(now)) {
            oneHourLater = LocalTime.of(23, 59, 59);
        }

        return getFreeRooms(day, now, oneHourLater, building, floor, roomType, minCapacity);
    }

    /**
     * Computes the "free until" label.
     * Looks for classes starting at or after the requested window end time,
     * picks the earliest one, or returns "Rest of the day".
     */
    public String computeFreeUntil(List<Timetable> roomSchedule, LocalTime requestedEnd) {
        if (roomSchedule == null || roomSchedule.isEmpty()) {
            return "Rest of the day";
        }

        Optional<LocalTime> nextClassStart = roomSchedule.stream()
                .map(Timetable::getStartTime)
                .filter(startTime -> !startTime.isBefore(requestedEnd)) // startTime >= requestedEnd
                .min(Comparator.naturalOrder());

        return nextClassStart
                .map(time -> time.format(TIME_FORMATTER))
                .orElse("Rest of the day");
    }

    /**
     * Computes duration in minutes until the next class starts.
     */
    public Long computeMinutesUntilNextClass(List<Timetable> roomSchedule, LocalTime requestedEnd) {
        if (roomSchedule == null || roomSchedule.isEmpty()) {
            return null;
        }

        Optional<LocalTime> nextClassStart = roomSchedule.stream()
                .map(Timetable::getStartTime)
                .filter(startTime -> !startTime.isBefore(requestedEnd))
                .min(Comparator.naturalOrder());

        return nextClassStart
                .map(time -> Duration.between(requestedEnd, time).toMinutes())
                .orElse(null);
    }

    /**
     * Retrieve all rooms in the system.
     */
    public List<RoomDto> getAllRooms() {
        return roomRepository.findAll().stream()
                .sorted(Comparator.comparing(Room::getBuilding)
                        .thenComparing(Room::getFloor)
                        .thenComparing(Room::getRoomNumber))
                .map(RoomDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Retrieve a specific room's timetable for a given day.
     */
    public List<TimetableDto> getRoomSchedule(Long roomId, DayOfWeekEnum day) {
        if (!roomRepository.existsById(roomId)) {
            throw new ResourceNotFoundException("Room not found with ID: " + roomId);
        }
        if (day == null) {
            throw new BadRequestException("Day of week must be specified (MON, TUE, WED, THU, FRI, SAT)");
        }

        return timetableRepository.findByRoomIdAndDayOfWeekOrderByStartTimeAsc(roomId, day)
                .stream()
                .map(TimetableDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Validates that requested window is logically coherent.
     */
    public void validateTimeWindow(LocalTime start, LocalTime end) {
        if (start == null) {
            throw new BadRequestException("Start time must be provided (e.g. 10:00)");
        }
        if (end == null) {
            throw new BadRequestException("End time must be provided (e.g. 11:00)");
        }
        if (!end.isAfter(start)) {
            throw new BadRequestException(String.format(
                    "Invalid time window: end time (%s) must be strictly after start time (%s)",
                    end.format(TIME_FORMATTER), start.format(TIME_FORMATTER)
            ));
        }
    }

    /**
     * Parses time string with helpful format tolerance (e.g., "9:00", "09:00", "09:00:00").
     */
    public LocalTime parseTime(String timeStr, String fieldName) {
        if (timeStr == null || timeStr.trim().isEmpty()) {
            throw new BadRequestException(fieldName + " time must not be empty");
        }
        String clean = timeStr.trim();
        // If single digit hour, e.g. "9:00" -> prepend zero -> "09:00"
        if (clean.matches("^\\d:\\d{2}.*")) {
            clean = "0" + clean;
        }
        try {
            if (clean.length() == 5) {
                return LocalTime.parse(clean, TIME_FORMATTER);
            }
            return LocalTime.parse(clean);
        } catch (DateTimeParseException ex) {
            throw new BadRequestException(String.format(
                    "Invalid %s format: '%s'. Expected format HH:mm (e.g. 10:00)",
                    fieldName, timeStr
            ));
        }
    }

    private DayOfWeekEnum mapDayOfWeek(java.time.DayOfWeek jDay) {
        switch (jDay) {
            case MONDAY: return DayOfWeekEnum.MON;
            case TUESDAY: return DayOfWeekEnum.TUE;
            case WEDNESDAY: return DayOfWeekEnum.WED;
            case THURSDAY: return DayOfWeekEnum.THU;
            case FRIDAY: return DayOfWeekEnum.FRI;
            case SATURDAY: return DayOfWeekEnum.SAT;
            default: return DayOfWeekEnum.MON;
        }
    }
}

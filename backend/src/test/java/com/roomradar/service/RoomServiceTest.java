package com.roomradar.service;

import com.roomradar.dto.FreeRoomResponse;
import com.roomradar.entity.DayOfWeekEnum;
import com.roomradar.entity.Room;
import com.roomradar.entity.RoomType;
import com.roomradar.entity.Timetable;
import com.roomradar.exception.BadRequestException;
import com.roomradar.repository.RoomRepository;
import com.roomradar.repository.TimetableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests verifying the time window overlap logic, free-until computations,
 * and service methods in RoomService.
 */
@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private TimetableRepository timetableRepository;

    @InjectMocks
    private RoomService roomService;

    private final LocalTime REQ_START = LocalTime.of(10, 0);
    private final LocalTime REQ_END = LocalTime.of(11, 0);

    @Nested
    @DisplayName("Core Overlap Logic Tests: classStart < reqEnd AND classEnd > reqStart")
    class OverlapLogicTests {

        @Test
        @DisplayName("Partial Overlap Case 1: Class starts before window and ends inside (09:30 - 10:30 blocks 10:00 - 11:00)")
        void testPartialOverlap_StartsBeforeEndsInside() {
            LocalTime classStart = LocalTime.of(9, 30);
            LocalTime classEnd = LocalTime.of(10, 30);

            // Logic: 09:30 < 11:00 (true) AND 10:30 > 10:00 (true) => OVERLAP!
            boolean overlaps = roomService.isOverlapping(classStart, classEnd, REQ_START, REQ_END);

            assertThat(overlaps)
                    .as("A 09:30-10:30 class must block a 10:00-11:00 request because it occupies the first 30 minutes")
                    .isTrue();
        }

        @Test
        @DisplayName("Partial Overlap Case 2: Class starts inside window and ends after (10:30 - 11:30 blocks 10:00 - 11:00)")
        void testPartialOverlap_StartsInsideEndsAfter() {
            LocalTime classStart = LocalTime.of(10, 30);
            LocalTime classEnd = LocalTime.of(11, 30);

            // Logic: 10:30 < 11:00 (true) AND 11:30 > 10:00 (true) => OVERLAP!
            boolean overlaps = roomService.isOverlapping(classStart, classEnd, REQ_START, REQ_END);

            assertThat(overlaps)
                    .as("A 10:30-11:30 class must block a 10:00-11:00 request because it occupies the last 30 minutes")
                    .isTrue();
        }

        @Test
        @DisplayName("Exact Match: Class occurs exactly during requested window (10:00 - 11:00 blocks 10:00 - 11:00)")
        void testExactOverlap() {
            LocalTime classStart = LocalTime.of(10, 0);
            LocalTime classEnd = LocalTime.of(11, 0);

            boolean overlaps = roomService.isOverlapping(classStart, classEnd, REQ_START, REQ_END);

            assertThat(overlaps).isTrue();
        }

        @Test
        @DisplayName("Enclosing Overlap: 2-hour lab wraps around requested slot (09:00 - 12:00 blocks 10:00 - 11:00)")
        void testEnclosingOverlap() {
            LocalTime classStart = LocalTime.of(9, 0);
            LocalTime classEnd = LocalTime.of(12, 0);

            boolean overlaps = roomService.isOverlapping(classStart, classEnd, REQ_START, REQ_END);

            assertThat(overlaps).isTrue();
        }

        @Test
        @DisplayName("Internal Overlap: Short class inside requested slot (10:15 - 10:45 blocks 10:00 - 11:00)")
        void testInternalOverlap() {
            LocalTime classStart = LocalTime.of(10, 15);
            LocalTime classEnd = LocalTime.of(10, 45);

            boolean overlaps = roomService.isOverlapping(classStart, classEnd, REQ_START, REQ_END);

            assertThat(overlaps).isTrue();
        }

        @Test
        @DisplayName("Boundary Adjacent Before: Class ends exactly when window starts (09:00 - 10:00 does NOT block 10:00 - 11:00)")
        void testBoundaryAdjacentBefore_DoesNotOverlap() {
            LocalTime classStart = LocalTime.of(9, 0);
            LocalTime classEnd = LocalTime.of(10, 0);

            // Logic: 09:00 < 11:00 (true), but classEnd (10:00) is NOT > reqStart (10:00) => FALSE!
            boolean overlaps = roomService.isOverlapping(classStart, classEnd, REQ_START, REQ_END);

            assertThat(overlaps)
                    .as("A class ending at 10:00:00 does not occupy the 10:00-11:00 window")
                    .isFalse();
        }

        @Test
        @DisplayName("Boundary Adjacent After: Class starts exactly when window ends (11:00 - 12:00 does NOT block 10:00 - 11:00)")
        void testBoundaryAdjacentAfter_DoesNotOverlap() {
            LocalTime classStart = LocalTime.of(11, 0);
            LocalTime classEnd = LocalTime.of(12, 0);

            // Logic: classStart (11:00) is NOT < reqEnd (11:00) => FALSE!
            boolean overlaps = roomService.isOverlapping(classStart, classEnd, REQ_START, REQ_END);

            assertThat(overlaps)
                    .as("A class starting at 11:00:00 does not block the 10:00-11:00 window")
                    .isFalse();
        }

        @Test
        @DisplayName("Completely Outside Before: Class 08:00 - 09:00 does not block 10:00 - 11:00")
        void testCompletelyOutsideBefore_DoesNotOverlap() {
            LocalTime classStart = LocalTime.of(8, 0);
            LocalTime classEnd = LocalTime.of(9, 0);

            boolean overlaps = roomService.isOverlapping(classStart, classEnd, REQ_START, REQ_END);

            assertThat(overlaps).isFalse();
        }

        @Test
        @DisplayName("Completely Outside After: Class 13:00 - 14:00 does not block 10:00 - 11:00")
        void testCompletelyOutsideAfter_DoesNotOverlap() {
            LocalTime classStart = LocalTime.of(13, 0);
            LocalTime classEnd = LocalTime.of(14, 0);

            boolean overlaps = roomService.isOverlapping(classStart, classEnd, REQ_START, REQ_END);

            assertThat(overlaps).isFalse();
        }
    }

    @Nested
    @DisplayName("Free Until Calculation Tests")
    class FreeUntilTests {

        private Room testRoom;

        @BeforeEach
        void setUp() {
            testRoom = new Room(1L, "A102", "Main Block", 1, 60, RoomType.CLASSROOM);
        }

        @Test
        @DisplayName("Next class starts after lunch at 13:00 -> Free until '13:00'")
        void testFreeUntil_WithLaterClass() {
            Timetable morning = new Timetable(testRoom, DayOfWeekEnum.MON, LocalTime.of(9, 0), LocalTime.of(10, 0), "OOP", "Dr. Gosling", "CSE-2B");
            Timetable afternoon = new Timetable(testRoom, DayOfWeekEnum.MON, LocalTime.of(13, 0), LocalTime.of(14, 0), "SE", "Dr. Sommerville", "IT-3A");
            List<Timetable> schedule = Arrays.asList(morning, afternoon);

            String freeUntil = roomService.computeFreeUntil(schedule, LocalTime.of(11, 0));

            assertThat(freeUntil).isEqualTo("13:00");
        }

        @Test
        @DisplayName("Next class starts immediately at window end (11:00) -> Free until '11:00'")
        void testFreeUntil_ImmediateNextClass() {
            Timetable nextClass = new Timetable(testRoom, DayOfWeekEnum.MON, LocalTime.of(11, 0), LocalTime.of(12, 0), "OS", "Prof. Tanenbaum", "CSE-2B");
            List<Timetable> schedule = Collections.singletonList(nextClass);

            String freeUntil = roomService.computeFreeUntil(schedule, LocalTime.of(11, 0));

            assertThat(freeUntil).isEqualTo("11:00");
        }

        @Test
        @DisplayName("No classes scheduled later in the day -> Free until 'Rest of the day'")
        void testFreeUntil_RestOfTheDay() {
            Timetable morning = new Timetable(testRoom, DayOfWeekEnum.MON, LocalTime.of(9, 0), LocalTime.of(10, 0), "OOP", "Dr. Gosling", "CSE-2B");
            List<Timetable> schedule = Collections.singletonList(morning);

            String freeUntil = roomService.computeFreeUntil(schedule, LocalTime.of(11, 0));

            assertThat(freeUntil).isEqualTo("Rest of the day");
        }

        @Test
        @DisplayName("Empty schedule -> Free until 'Rest of the day'")
        void testFreeUntil_EmptySchedule() {
            String freeUntil = roomService.computeFreeUntil(Collections.emptyList(), LocalTime.of(11, 0));

            assertThat(freeUntil).isEqualTo("Rest of the day");
        }
    }

    @Nested
    @DisplayName("Validation and Service Execution Tests")
    class ServiceExecutionTests {

        @Test
        @DisplayName("Validation fails when end time equals start time")
        void testValidation_EndEqualsStart_ThrowsBadRequest() {
            LocalTime time = LocalTime.of(10, 0);

            assertThatThrownBy(() -> roomService.validateTimeWindow(time, time))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageContaining("must be strictly after start time");
        }

        @Test
        @DisplayName("Validation fails when end time is before start time")
        void testValidation_EndBeforeStart_ThrowsBadRequest() {
            LocalTime start = LocalTime.of(11, 0);
            LocalTime end = LocalTime.of(10, 0);

            assertThatThrownBy(() -> roomService.validateTimeWindow(start, end))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageContaining("must be strictly after start time");
        }

        @Test
        @DisplayName("Validation fails when day is null")
        void testValidation_NullDay_ThrowsBadRequest() {
            assertThatThrownBy(() -> roomService.getFreeRooms(null, REQ_START, REQ_END, null, null, null, null))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageContaining("Day of week must be specified");
        }

        @Test
        @DisplayName("getFreeRooms correctly filters out busy rooms and includes free rooms")
        void testGetFreeRooms_IntegrationScenario() {
            Room freeRoom = new Room(1L, "A102", "Main Block", 1, 60, RoomType.CLASSROOM);
            Room busyRoom = new Room(2L, "A101", "Main Block", 1, 60, RoomType.CLASSROOM);

            when(roomRepository.findWithFilters(any(), any(), any(), any()))
                    .thenReturn(Arrays.asList(freeRoom, busyRoom));

            // Busy room has a class 10:00-11:00
            Timetable busyClass = new Timetable(busyRoom, DayOfWeekEnum.MON, LocalTime.of(10, 0), LocalTime.of(11, 0), "OS", "Prof. Tanenbaum", "CSE-2A");
            // Free room has next class at 11:00
            Timetable nextClassForFreeRoom = new Timetable(freeRoom, DayOfWeekEnum.MON, LocalTime.of(11, 0), LocalTime.of(12, 0), "OS", "Prof. Tanenbaum", "CSE-2B");

            when(timetableRepository.findByDayOfWeek(DayOfWeekEnum.MON))
                    .thenReturn(Arrays.asList(busyClass, nextClassForFreeRoom));

            List<FreeRoomResponse> result = roomService.getFreeRooms(
                    DayOfWeekEnum.MON, REQ_START, REQ_END, null, null, null, null
            );

            assertThat(result).hasSize(1);
            FreeRoomResponse response = result.get(0);
            assertThat(response.getRoomNumber()).isEqualTo("A102");
            assertThat(response.getFreeUntil()).isEqualTo("11:00");
        }
    }
}

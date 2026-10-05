package com.roomradar.repository;

import com.roomradar.entity.DayOfWeekEnum;
import com.roomradar.entity.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface TimetableRepository extends JpaRepository<Timetable, Long> {

    /**
     * Retrieve all timetable entries for a specific room on a given day ordered chronologically.
     */
    List<Timetable> findByRoomIdAndDayOfWeekOrderByStartTimeAsc(Long roomId, DayOfWeekEnum dayOfWeek);

    /**
     * Retrieve all timetable entries across all rooms for a given day.
     */
    List<Timetable> findByDayOfWeek(DayOfWeekEnum dayOfWeek);

    /**
     * Retrieve entries that overlap with requested time window on a given day.
     * Core overlap condition: entry.start_time < requested_end AND entry.end_time > requested_start
     */
    @Query("SELECT t FROM Timetable t WHERE t.dayOfWeek = :dayOfWeek AND " +
           "t.startTime < :requestedEnd AND t.endTime > :requestedStart")
    List<Timetable> findOverlappingEntries(
            @Param("dayOfWeek") DayOfWeekEnum dayOfWeek,
            @Param("requestedStart") LocalTime requestedStart,
            @Param("requestedEnd") LocalTime requestedEnd
    );
}

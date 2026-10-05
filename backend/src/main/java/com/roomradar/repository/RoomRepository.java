package com.roomradar.repository;

import com.roomradar.entity.Room;
import com.roomradar.entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    Optional<Room> findByRoomNumber(String roomNumber);

    /**
     * Query rooms with optional filter predicates.
     */
    @Query("SELECT r FROM Room r WHERE " +
           "(:building IS NULL OR LOWER(r.building) = LOWER(:building) OR LOWER(r.building) LIKE LOWER(CONCAT('%', :building, '%'))) AND " +
           "(:floor IS NULL OR r.floor = :floor) AND " +
           "(:roomType IS NULL OR r.roomType = :roomType) AND " +
           "(:minCapacity IS NULL OR r.capacity >= :minCapacity) " +
           "ORDER BY r.building ASC, r.floor ASC, r.roomNumber ASC")
    List<Room> findWithFilters(
            @Param("building") String building,
            @Param("floor") Integer floor,
            @Param("roomType") RoomType roomType,
            @Param("minCapacity") Integer minCapacity
    );
}

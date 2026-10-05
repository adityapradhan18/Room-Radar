-- ============================================================================
-- Room Radar - Database Schema (PostgreSQL)
-- ============================================================================

-- Drop tables if they already exist (in reverse order of dependencies)
DROP TABLE IF EXISTS timetable;
DROP TABLE IF EXISTS rooms;

-- ----------------------------------------------------------------------------
-- Table: rooms
-- Stores metadata about all college rooms, labs, and seminar halls.
-- ----------------------------------------------------------------------------
CREATE TABLE rooms (
    id BIGSERIAL PRIMARY KEY,
    room_number VARCHAR(20) NOT NULL UNIQUE,
    building VARCHAR(100) NOT NULL,
    floor INTEGER NOT NULL,
    capacity INTEGER NOT NULL CHECK (capacity > 0),
    room_type VARCHAR(50) NOT NULL CHECK (room_type IN ('CLASSROOM', 'LAB', 'SEMINAR_HALL'))
);

-- ----------------------------------------------------------------------------
-- Table: timetable
-- Stores class schedules across days of the week and time intervals.
-- ----------------------------------------------------------------------------
CREATE TABLE timetable (
    id BIGSERIAL PRIMARY KEY,
    room_id BIGINT NOT NULL,
    day_of_week VARCHAR(10) NOT NULL CHECK (day_of_week IN ('MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT')),
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    subject VARCHAR(150) NOT NULL,
    faculty VARCHAR(150) NOT NULL,
    batch VARCHAR(50) NOT NULL,
    CONSTRAINT fk_timetable_room FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE,
    CONSTRAINT chk_time_window CHECK (end_time > start_time)
);

-- ----------------------------------------------------------------------------
-- Indexes for optimized querying
-- ----------------------------------------------------------------------------
CREATE INDEX idx_timetable_room_day ON timetable(room_id, day_of_week);
CREATE INDEX idx_timetable_day_times ON timetable(day_of_week, start_time, end_time);
CREATE INDEX idx_rooms_building_floor ON rooms(building, floor);
CREATE INDEX idx_rooms_room_type ON rooms(room_type);

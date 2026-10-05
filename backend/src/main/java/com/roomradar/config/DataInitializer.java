package com.roomradar.config;

import com.roomradar.entity.DayOfWeekEnum;
import com.roomradar.entity.Room;
import com.roomradar.entity.RoomType;
import com.roomradar.entity.Timetable;
import com.roomradar.repository.RoomRepository;
import com.roomradar.repository.TimetableRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.*;

/**
 * Automatically seeds initial demo rooms and timetable data if the database is currently empty.
 * This guarantees the prototype is instantly runnable even before manual SQL execution.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final RoomRepository roomRepository;
    private final TimetableRepository timetableRepository;

    public DataInitializer(RoomRepository roomRepository, TimetableRepository timetableRepository) {
        this.roomRepository = roomRepository;
        this.timetableRepository = timetableRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (roomRepository.count() > 0) {
            log.info("Database already contains {} rooms. Skipping initial data seeding.", roomRepository.count());
            return;
        }

        log.info("Database is empty. Seeding initial rooms and timetable data for Room Radar...");

        // 1. Create the 10 specified campus rooms
        Room a101 = new Room("A101", "Main Block", 1, 60, RoomType.CLASSROOM);
        Room a102 = new Room("A102", "Main Block", 1, 60, RoomType.CLASSROOM);
        Room a201 = new Room("A201", "Main Block", 2, 80, RoomType.CLASSROOM);
        Room a202 = new Room("A202", "Main Block", 2, 50, RoomType.CLASSROOM);
        Room b101 = new Room("B101", "Science Block", 1, 40, RoomType.LAB);
        Room b102 = new Room("B102", "Science Block", 1, 40, RoomType.LAB);
        Room b201 = new Room("B201", "Science Block", 2, 70, RoomType.CLASSROOM);
        Room c101 = new Room("C101", "Tech Block", 1, 120, RoomType.SEMINAR_HALL);
        Room c201 = new Room("C201", "Tech Block", 2, 55, RoomType.CLASSROOM);
        Room c202 = new Room("C202", "Tech Block", 2, 45, RoomType.CLASSROOM);

        List<Room> rooms = roomRepository.saveAll(Arrays.asList(a101, a102, a201, a202, b101, b102, b201, c101, c201, c202));
        Map<String, Room> roomMap = new HashMap<>();
        for (Room r : rooms) {
            roomMap.put(r.getRoomNumber(), r);
        }

        List<Timetable> scheduleList = new ArrayList<>();

        // MONDAY SCHEDULE
        // A101 (Busy at 10-11)
        scheduleList.add(new Timetable(roomMap.get("A101"), DayOfWeekEnum.MON, LocalTime.of(9, 0), LocalTime.of(10, 0), "Data Structures & Algorithms", "Dr. Alan Turing", "CSE-2A"));
        scheduleList.add(new Timetable(roomMap.get("A101"), DayOfWeekEnum.MON, LocalTime.of(10, 0), LocalTime.of(11, 0), "Operating Systems Principles", "Prof. Andrew Tanenbaum", "CSE-2A"));
        scheduleList.add(new Timetable(roomMap.get("A101"), DayOfWeekEnum.MON, LocalTime.of(11, 0), LocalTime.of(12, 0), "Database Management Systems", "Dr. Edgar Codd", "CSE-2B"));
        scheduleList.add(new Timetable(roomMap.get("A101"), DayOfWeekEnum.MON, LocalTime.of(13, 0), LocalTime.of(14, 0), "Computer Networks", "Prof. Larry Peterson", "CSE-3A"));
        scheduleList.add(new Timetable(roomMap.get("A101"), DayOfWeekEnum.MON, LocalTime.of(14, 0), LocalTime.of(15, 0), "Discrete Mathematics", "Dr. Donald Knuth", "CSE-2A"));

        // A102 (FREE at 10-11! Next class 11:00)
        scheduleList.add(new Timetable(roomMap.get("A102"), DayOfWeekEnum.MON, LocalTime.of(9, 0), LocalTime.of(10, 0), "Object Oriented Programming in Java", "Dr. James Gosling", "CSE-2B"));
        scheduleList.add(new Timetable(roomMap.get("A102"), DayOfWeekEnum.MON, LocalTime.of(11, 0), LocalTime.of(12, 0), "Operating Systems Principles", "Prof. Andrew Tanenbaum", "CSE-2B"));
        scheduleList.add(new Timetable(roomMap.get("A102"), DayOfWeekEnum.MON, LocalTime.of(13, 0), LocalTime.of(14, 0), "Software Engineering Practices", "Dr. Ian Sommerville", "IT-3A"));
        scheduleList.add(new Timetable(roomMap.get("A102"), DayOfWeekEnum.MON, LocalTime.of(14, 0), LocalTime.of(15, 0), "Theory of Computation", "Dr. Michael Sipser", "CSE-3B"));
        scheduleList.add(new Timetable(roomMap.get("A102"), DayOfWeekEnum.MON, LocalTime.of(15, 0), LocalTime.of(16, 0), "Compiler Design", "Dr. Alfred Aho", "CSE-3A"));

        // A201 (Busy at 10-11)
        scheduleList.add(new Timetable(roomMap.get("A201"), DayOfWeekEnum.MON, LocalTime.of(10, 0), LocalTime.of(11, 0), "Computer Organization & Architecture", "Dr. David Patterson", "ECE-2A"));
        scheduleList.add(new Timetable(roomMap.get("A201"), DayOfWeekEnum.MON, LocalTime.of(11, 0), LocalTime.of(12, 0), "Digital Signal Processing", "Dr. Alan Oppenheim", "ECE-3A"));
        scheduleList.add(new Timetable(roomMap.get("A201"), DayOfWeekEnum.MON, LocalTime.of(13, 0), LocalTime.of(14, 0), "Microprocessors & Interfacing", "Prof. Ramesh Gaonkar", "ECE-2B"));
        scheduleList.add(new Timetable(roomMap.get("A201"), DayOfWeekEnum.MON, LocalTime.of(14, 0), LocalTime.of(15, 0), "Signals and Systems", "Dr. Simon Haykin", "ECE-2A"));
        scheduleList.add(new Timetable(roomMap.get("A201"), DayOfWeekEnum.MON, LocalTime.of(15, 0), LocalTime.of(16, 0), "Electromagnetic Fields", "Dr. Matthew Sadiku", "ECE-2B"));

        // A202 (FREE at 10-11! Next class 13:00)
        scheduleList.add(new Timetable(roomMap.get("A202"), DayOfWeekEnum.MON, LocalTime.of(13, 0), LocalTime.of(14, 0), "Linear Algebra & Applications", "Dr. Gilbert Strang", "MATH-1A"));
        scheduleList.add(new Timetable(roomMap.get("A202"), DayOfWeekEnum.MON, LocalTime.of(14, 0), LocalTime.of(15, 0), "Probability & Statistics", "Dr. Sheldon Ross", "MATH-2B"));
        scheduleList.add(new Timetable(roomMap.get("A202"), DayOfWeekEnum.MON, LocalTime.of(15, 0), LocalTime.of(16, 0), "Numerical Analysis", "Dr. Richard Burden", "MATH-2A"));
        scheduleList.add(new Timetable(roomMap.get("A202"), DayOfWeekEnum.MON, LocalTime.of(16, 0), LocalTime.of(17, 0), "Graph Theory & Combinatorics", "Dr. Frank Harary", "CSE-3B"));

        // B101 (Lab - Busy at 10-11 due to 2-hr lab 09:00-11:00)
        scheduleList.add(new Timetable(roomMap.get("B101"), DayOfWeekEnum.MON, LocalTime.of(9, 0), LocalTime.of(11, 0), "Advanced Java Programming Lab", "Prof. Joshua Bloch", "CSE-3A"));
        scheduleList.add(new Timetable(roomMap.get("B101"), DayOfWeekEnum.MON, LocalTime.of(13, 0), LocalTime.of(15, 0), "Database Management Systems Lab", "Dr. Edgar Codd", "CSE-2A"));
        scheduleList.add(new Timetable(roomMap.get("B101"), DayOfWeekEnum.MON, LocalTime.of(15, 0), LocalTime.of(17, 0), "Computer Networks Lab", "Prof. Larry Peterson", "CSE-3B"));

        // B102 (Lab - FREE at 10-11! Next class 13:00)
        scheduleList.add(new Timetable(roomMap.get("B102"), DayOfWeekEnum.MON, LocalTime.of(9, 0), LocalTime.of(10, 0), "Applied Physics Laboratory", "Dr. Richard Feynman", "PHY-1A"));
        scheduleList.add(new Timetable(roomMap.get("B102"), DayOfWeekEnum.MON, LocalTime.of(13, 0), LocalTime.of(15, 0), "Chemistry & Material Sciences Lab", "Dr. Marie Curie", "CHEM-1B"));
        scheduleList.add(new Timetable(roomMap.get("B102"), DayOfWeekEnum.MON, LocalTime.of(15, 0), LocalTime.of(16, 0), "Electronic Devices Lab", "Dr. John Bardeen", "ECE-1A"));

        // B201 (Busy at 10-11)
        scheduleList.add(new Timetable(roomMap.get("B201"), DayOfWeekEnum.MON, LocalTime.of(9, 0), LocalTime.of(10, 0), "Engineering Physics", "Dr. Richard Feynman", "PHY-1B"));
        scheduleList.add(new Timetable(roomMap.get("B201"), DayOfWeekEnum.MON, LocalTime.of(10, 0), LocalTime.of(11, 0), "Engineering Chemistry", "Dr. Marie Curie", "CHEM-1A"));
        scheduleList.add(new Timetable(roomMap.get("B201"), DayOfWeekEnum.MON, LocalTime.of(11, 0), LocalTime.of(12, 0), "Calculus & Analytical Geometry", "Dr. Gilbert Strang", "MATH-1B"));
        scheduleList.add(new Timetable(roomMap.get("B201"), DayOfWeekEnum.MON, LocalTime.of(13, 0), LocalTime.of(14, 0), "Environmental Studies", "Dr. Rachel Carson", "ENV-1A"));
        scheduleList.add(new Timetable(roomMap.get("B201"), DayOfWeekEnum.MON, LocalTime.of(14, 0), LocalTime.of(15, 0), "Basic Electrical Engineering", "Dr. Nikola Tesla", "EE-1A"));
        scheduleList.add(new Timetable(roomMap.get("B201"), DayOfWeekEnum.MON, LocalTime.of(15, 0), LocalTime.of(16, 0), "Basic Electrical Engineering", "Dr. Nikola Tesla", "EE-1B"));

        // C101 (Seminar Hall - Busy at 10-11)
        scheduleList.add(new Timetable(roomMap.get("C101"), DayOfWeekEnum.MON, LocalTime.of(10, 0), LocalTime.of(11, 0), "Technical Communication & Soft Skills", "Dr. Steven Pinker", "ALL-1"));
        scheduleList.add(new Timetable(roomMap.get("C101"), DayOfWeekEnum.MON, LocalTime.of(11, 0), LocalTime.of(12, 0), "Professional Ethics & Human Values", "Prof. Michael Sandel", "ALL-2"));
        scheduleList.add(new Timetable(roomMap.get("C101"), DayOfWeekEnum.MON, LocalTime.of(13, 0), LocalTime.of(14, 0), "Industry Keynote: Cloud Innovations", "Dr. Werner Vogels", "ALL-3"));
        scheduleList.add(new Timetable(roomMap.get("C101"), DayOfWeekEnum.MON, LocalTime.of(14, 0), LocalTime.of(16, 0), "Tech Entrepreneurship Seminar", "Prof. Clayton Christensen", "ALL-4"));

        // C201 (Busy at 10-11)
        scheduleList.add(new Timetable(roomMap.get("C201"), DayOfWeekEnum.MON, LocalTime.of(9, 0), LocalTime.of(10, 0), "Introduction to Artificial Intelligence", "Dr. Peter Norvig", "AIML-3A"));
        scheduleList.add(new Timetable(roomMap.get("C201"), DayOfWeekEnum.MON, LocalTime.of(10, 0), LocalTime.of(11, 0), "Machine Learning Foundations", "Dr. Andrew Ng", "AIML-3A"));
        scheduleList.add(new Timetable(roomMap.get("C201"), DayOfWeekEnum.MON, LocalTime.of(11, 0), LocalTime.of(12, 0), "Computer Vision Applications", "Dr. Fei-Fei Li", "AIML-3B"));
        scheduleList.add(new Timetable(roomMap.get("C201"), DayOfWeekEnum.MON, LocalTime.of(13, 0), LocalTime.of(14, 0), "Deep Learning Architectures", "Dr. Yoshua Bengio", "AIML-4A"));
        scheduleList.add(new Timetable(roomMap.get("C201"), DayOfWeekEnum.MON, LocalTime.of(14, 0), LocalTime.of(15, 0), "Natural Language Processing", "Dr. Christopher Manning", "AIML-4B"));
        scheduleList.add(new Timetable(roomMap.get("C201"), DayOfWeekEnum.MON, LocalTime.of(15, 0), LocalTime.of(16, 0), "Reinforcement Learning", "Dr. Richard Sutton", "AIML-4A"));

        // C202 (FREE at 10-11! Next class 14:00)
        scheduleList.add(new Timetable(roomMap.get("C202"), DayOfWeekEnum.MON, LocalTime.of(14, 0), LocalTime.of(15, 0), "Cloud Computing Architectures", "Prof. Werner Vogels", "IT-3B"));
        scheduleList.add(new Timetable(roomMap.get("C202"), DayOfWeekEnum.MON, LocalTime.of(15, 0), LocalTime.of(16, 0), "Cyber Security & Cryptography", "Dr. Bruce Schneier", "IT-3B"));
        scheduleList.add(new Timetable(roomMap.get("C202"), DayOfWeekEnum.MON, LocalTime.of(16, 0), LocalTime.of(17, 0), "Distributed Systems", "Dr. Leslie Lamport", "IT-4A"));

        // TUESDAY SCHEDULE
        scheduleList.add(new Timetable(roomMap.get("A101"), DayOfWeekEnum.TUE, LocalTime.of(9, 0), LocalTime.of(10, 0), "Operating Systems Principles", "Prof. Andrew Tanenbaum", "CSE-2A"));
        scheduleList.add(new Timetable(roomMap.get("A101"), DayOfWeekEnum.TUE, LocalTime.of(10, 0), LocalTime.of(11, 0), "Data Structures & Algorithms", "Dr. Alan Turing", "CSE-2A"));
        scheduleList.add(new Timetable(roomMap.get("A101"), DayOfWeekEnum.TUE, LocalTime.of(11, 0), LocalTime.of(12, 0), "Computer Networks", "Prof. Larry Peterson", "CSE-3A"));
        scheduleList.add(new Timetable(roomMap.get("A101"), DayOfWeekEnum.TUE, LocalTime.of(14, 0), LocalTime.of(15, 0), "Database Management Systems", "Dr. Edgar Codd", "CSE-2B"));
        scheduleList.add(new Timetable(roomMap.get("A101"), DayOfWeekEnum.TUE, LocalTime.of(15, 0), LocalTime.of(16, 0), "Design & Analysis of Algorithms", "Dr. Donald Knuth", "CSE-2B"));

        scheduleList.add(new Timetable(roomMap.get("A102"), DayOfWeekEnum.TUE, LocalTime.of(9, 0), LocalTime.of(10, 0), "Theory of Computation", "Dr. Michael Sipser", "CSE-3B"));
        scheduleList.add(new Timetable(roomMap.get("A102"), DayOfWeekEnum.TUE, LocalTime.of(10, 0), LocalTime.of(11, 0), "Software Engineering Practices", "Dr. Ian Sommerville", "IT-3A"));
        scheduleList.add(new Timetable(roomMap.get("A102"), DayOfWeekEnum.TUE, LocalTime.of(13, 0), LocalTime.of(14, 0), "Compiler Design", "Dr. Alfred Aho", "CSE-3A"));
        scheduleList.add(new Timetable(roomMap.get("A102"), DayOfWeekEnum.TUE, LocalTime.of(14, 0), LocalTime.of(15, 0), "Object Oriented Programming in Java", "Dr. James Gosling", "CSE-2B"));

        scheduleList.add(new Timetable(roomMap.get("B101"), DayOfWeekEnum.TUE, LocalTime.of(9, 0), LocalTime.of(11, 0), "Operating Systems Lab", "Prof. Andrew Tanenbaum", "CSE-2A"));
        scheduleList.add(new Timetable(roomMap.get("B101"), DayOfWeekEnum.TUE, LocalTime.of(11, 0), LocalTime.of(12, 0), "Linux Shell Scripting Lab", "Dr. Linus Torvalds", "CSE-2B"));
        scheduleList.add(new Timetable(roomMap.get("B101"), DayOfWeekEnum.TUE, LocalTime.of(14, 0), LocalTime.of(16, 0), "Data Structures Implementation Lab", "Dr. Alan Turing", "CSE-2A"));

        scheduleList.add(new Timetable(roomMap.get("B102"), DayOfWeekEnum.TUE, LocalTime.of(10, 0), LocalTime.of(12, 0), "Digital Electronics Lab", "Dr. John Bardeen", "ECE-2A"));
        scheduleList.add(new Timetable(roomMap.get("B102"), DayOfWeekEnum.TUE, LocalTime.of(13, 0), LocalTime.of(15, 0), "Microprocessors Programming Lab", "Prof. Ramesh Gaonkar", "ECE-2B"));

        timetableRepository.saveAll(scheduleList);
        log.info("Successfully seeded {} rooms and {} timetable slots.", rooms.size(), scheduleList.size());
    }
}

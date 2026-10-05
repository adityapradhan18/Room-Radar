-- ============================================================================
-- Room Radar - Seed Data (PostgreSQL)
-- ============================================================================

-- Clean existing data
TRUNCATE TABLE timetable RESTART IDENTITY CASCADE;
TRUNCATE TABLE rooms RESTART IDENTITY CASCADE;

-- ----------------------------------------------------------------------------
-- Insert 10 Campus Rooms
-- ----------------------------------------------------------------------------
INSERT INTO rooms (id, room_number, building, floor, capacity, room_type) VALUES
(1,  'A101', 'Main Block',    1, 60,  'CLASSROOM'),
(2,  'A102', 'Main Block',    1, 60,  'CLASSROOM'),
(3,  'A201', 'Main Block',    2, 80,  'CLASSROOM'),
(4,  'A202', 'Main Block',    2, 50,  'CLASSROOM'),
(5,  'B101', 'Science Block', 1, 40,  'LAB'),
(6,  'B102', 'Science Block', 1, 40,  'LAB'),
(7,  'B201', 'Science Block', 2, 70,  'CLASSROOM'),
(8,  'C101', 'Tech Block',    1, 120, 'SEMINAR_HALL'),
(9,  'C201', 'Tech Block',    2, 55,  'CLASSROOM'),
(10, 'C202', 'Tech Block',    2, 45,  'CLASSROOM');

-- Reset room sequence to match explicit IDs
SELECT setval('rooms_id_seq', (SELECT MAX(id) FROM rooms));

-- ----------------------------------------------------------------------------
-- Insert Realistic Timetable Schedule
-- Standard Periods: P1(09-10), P2(10-11), P3(11-12), P4(12-13 Lunch),
--                   P5(13-14), P6(14-15), P7(15-16), P8(16-17)
-- ----------------------------------------------------------------------------

-- ============================================================================
-- MONDAY
-- Note: Monday 10:00-11:00 has:
--   BUSY: A101, A201, B101 (in 09:00-11:00 lab), B201, C101, C201
--   FREE: A102 (free until 11:00), A202 (free until 13:00),
--         B102 (free until 13:00), C202 (free until 14:00)
-- ============================================================================

-- A101 (Main Block, Fl 1, Classroom)
INSERT INTO timetable (room_id, day_of_week, start_time, end_time, subject, faculty, batch) VALUES
(1, 'MON', '09:00', '10:00', 'Data Structures & Algorithms', 'Dr. Alan Turing', 'CSE-2A'),
(1, 'MON', '10:00', '11:00', 'Operating Systems Principles', 'Prof. Andrew Tanenbaum', 'CSE-2A'),
(1, 'MON', '11:00', '12:00', 'Database Management Systems', 'Dr. Edgar Codd', 'CSE-2B'),
(1, 'MON', '13:00', '14:00', 'Computer Networks', 'Prof. Larry Peterson', 'CSE-3A'),
(1, 'MON', '14:00', '15:00', 'Discrete Mathematics', 'Dr. Donald Knuth', 'CSE-2A');

-- A102 (Main Block, Fl 1, Classroom) - Free at 10-11!
INSERT INTO timetable (room_id, day_of_week, start_time, end_time, subject, faculty, batch) VALUES
(2, 'MON', '09:00', '10:00', 'Object Oriented Programming in Java', 'Dr. James Gosling', 'CSE-2B'),
(2, 'MON', '11:00', '12:00', 'Operating Systems Principles', 'Prof. Andrew Tanenbaum', 'CSE-2B'),
(2, 'MON', '13:00', '14:00', 'Software Engineering Practices', 'Dr. Ian Sommerville', 'IT-3A'),
(2, 'MON', '14:00', '15:00', 'Theory of Computation', 'Dr. Michael Sipser', 'CSE-3B'),
(2, 'MON', '15:00', '16:00', 'Compiler Design', 'Dr. Alfred Aho', 'CSE-3A');

-- A201 (Main Block, Fl 2, Classroom)
INSERT INTO timetable (room_id, day_of_week, start_time, end_time, subject, faculty, batch) VALUES
(3, 'MON', '10:00', '11:00', 'Computer Organization & Architecture', 'Dr. David Patterson', 'ECE-2A'),
(3, 'MON', '11:00', '12:00', 'Digital Signal Processing', 'Dr. Alan Oppenheim', 'ECE-3A'),
(3, 'MON', '13:00', '14:00', 'Microprocessors & Interfacing', 'Prof. Ramesh Gaonkar', 'ECE-2B'),
(3, 'MON', '14:00', '15:00', 'Signals and Systems', 'Dr. Simon Haykin', 'ECE-2A'),
(3, 'MON', '15:00', '16:00', 'Electromagnetic Fields', 'Dr. Matthew Sadiku', 'ECE-2B');

-- A202 (Main Block, Fl 2, Classroom) - Free at 10-11!
INSERT INTO timetable (room_id, day_of_week, start_time, end_time, subject, faculty, batch) VALUES
(4, 'MON', '13:00', '14:00', 'Linear Algebra & Applications', 'Dr. Gilbert Strang', 'MATH-1A'),
(4, 'MON', '14:00', '15:00', 'Probability & Statistics', 'Dr. Sheldon Ross', 'MATH-2B'),
(4, 'MON', '15:00', '16:00', 'Numerical Analysis', 'Dr. Richard Burden', 'MATH-2A'),
(4, 'MON', '16:00', '17:00', 'Graph Theory & Combinatorics', 'Dr. Frank Harary', 'CSE-3B');

-- B101 (Science Block, Fl 1, Lab) - 2-hr labs! Overlaps 10-11!
INSERT INTO timetable (room_id, day_of_week, start_time, end_time, subject, faculty, batch) VALUES
(5, 'MON', '09:00', '11:00', 'Advanced Java Programming Lab', 'Prof. Joshua Bloch', 'CSE-3A'),
(5, 'MON', '13:00', '15:00', 'Database Management Systems Lab', 'Dr. Edgar Codd', 'CSE-2A'),
(5, 'MON', '15:00', '17:00', 'Computer Networks Lab', 'Prof. Larry Peterson', 'CSE-3B');

-- B102 (Science Block, Fl 1, Lab) - Free at 10-11!
INSERT INTO timetable (room_id, day_of_week, start_time, end_time, subject, faculty, batch) VALUES
(6, 'MON', '09:00', '10:00', 'Applied Physics Laboratory', 'Dr. Richard Feynman', 'PHY-1A'),
(6, 'MON', '13:00', '15:00', 'Chemistry & Material Sciences Lab', 'Dr. Marie Curie', 'CHEM-1B'),
(6, 'MON', '15:00', '16:00', 'Electronic Devices Lab', 'Dr. John Bardeen', 'ECE-1A');

-- B201 (Science Block, Fl 2, Classroom)
INSERT INTO timetable (room_id, day_of_week, start_time, end_time, subject, faculty, batch) VALUES
(7, 'MON', '09:00', '10:00', 'Engineering Physics', 'Dr. Richard Feynman', 'PHY-1B'),
(7, 'MON', '10:00', '11:00', 'Engineering Chemistry', 'Dr. Marie Curie', 'CHEM-1A'),
(7, 'MON', '11:00', '12:00', 'Calculus & Analytical Geometry', 'Dr. Gilbert Strang', 'MATH-1B'),
(7, 'MON', '13:00', '14:00', 'Environmental Studies', 'Dr. Rachel Carson', 'ENV-1A'),
(7, 'MON', '14:00', '15:00', 'Basic Electrical Engineering', 'Dr. Nikola Tesla', 'EE-1A'),
(7, 'MON', '15:00', '16:00', 'Basic Electrical Engineering', 'Dr. Nikola Tesla', 'EE-1B');

-- C101 (Tech Block, Fl 1, Seminar Hall)
INSERT INTO timetable (room_id, day_of_week, start_time, end_time, subject, faculty, batch) VALUES
(8, 'MON', '10:00', '11:00', 'Technical Communication & Soft Skills', 'Dr. Steven Pinker', 'ALL-1'),
(8, 'MON', '11:00', '12:00', 'Professional Ethics & Human Values', 'Prof. Michael Sandel', 'ALL-2'),
(8, 'MON', '13:00', '14:00', 'Industry Keynote: Cloud Innovations', 'Dr. Werner Vogels', 'ALL-3'),
(8, 'MON', '14:00', '16:00', 'Tech Entrepreneurship Seminar', 'Prof. Clayton Christensen', 'ALL-4');

-- C201 (Tech Block, Fl 2, Classroom)
INSERT INTO timetable (room_id, day_of_week, start_time, end_time, subject, faculty, batch) VALUES
(9, 'MON', '09:00', '10:00', 'Introduction to Artificial Intelligence', 'Dr. Peter Norvig', 'AIML-3A'),
(9, 'MON', '10:00', '11:00', 'Machine Learning Foundations', 'Dr. Andrew Ng', 'AIML-3A'),
(9, 'MON', '11:00', '12:00', 'Computer Vision Applications', 'Dr. Fei-Fei Li', 'AIML-3B'),
(9, 'MON', '13:00', '14:00', 'Deep Learning Architectures', 'Dr. Yoshua Bengio', 'AIML-4A'),
(9, 'MON', '14:00', '15:00', 'Natural Language Processing', 'Dr. Christopher Manning', 'AIML-4B'),
(9, 'MON', '15:00', '16:00', 'Reinforcement Learning', 'Dr. Richard Sutton', 'AIML-4A');

-- C202 (Tech Block, Fl 2, Classroom) - Free at 10-11!
INSERT INTO timetable (room_id, day_of_week, start_time, end_time, subject, faculty, batch) VALUES
(10, 'MON', '14:00', '15:00', 'Cloud Computing Architectures', 'Prof. Werner Vogels', 'IT-3B'),
(10, 'MON', '15:00', '16:00', 'Cyber Security & Cryptography', 'Dr. Bruce Schneier', 'IT-3B'),
(10, 'MON', '16:00', '17:00', 'Distributed Systems', 'Dr. Leslie Lamport', 'IT-4A');

-- ============================================================================
-- TUESDAY
-- ============================================================================
INSERT INTO timetable (room_id, day_of_week, start_time, end_time, subject, faculty, batch) VALUES
-- A101
(1, 'TUE', '09:00', '10:00', 'Operating Systems Principles', 'Prof. Andrew Tanenbaum', 'CSE-2A'),
(1, 'TUE', '10:00', '11:00', 'Data Structures & Algorithms', 'Dr. Alan Turing', 'CSE-2A'),
(1, 'TUE', '11:00', '12:00', 'Computer Networks', 'Prof. Larry Peterson', 'CSE-3A'),
(1, 'TUE', '14:00', '15:00', 'Database Management Systems', 'Dr. Edgar Codd', 'CSE-2B'),
(1, 'TUE', '15:00', '16:00', 'Design & Analysis of Algorithms', 'Dr. Donald Knuth', 'CSE-2B'),

-- A102
(2, 'TUE', '09:00', '10:00', 'Theory of Computation', 'Dr. Michael Sipser', 'CSE-3B'),
(2, 'TUE', '10:00', '11:00', 'Software Engineering Practices', 'Dr. Ian Sommerville', 'IT-3A'),
(2, 'TUE', '13:00', '14:00', 'Compiler Design', 'Dr. Alfred Aho', 'CSE-3A'),
(2, 'TUE', '14:00', '15:00', 'Object Oriented Programming in Java', 'Dr. James Gosling', 'CSE-2B'),

-- A201
(3, 'TUE', '09:00', '10:00', 'Digital Signal Processing', 'Dr. Alan Oppenheim', 'ECE-3A'),
(3, 'TUE', '11:00', '12:00', 'Signals and Systems', 'Dr. Simon Haykin', 'ECE-2A'),
(3, 'TUE', '13:00', '14:00', 'Computer Organization & Architecture', 'Dr. David Patterson', 'ECE-2A'),
(3, 'TUE', '15:00', '16:00', 'Control Systems Engineering', 'Dr. Katsuhiko Ogata', 'ECE-3A'),

-- A202
(4, 'TUE', '10:00', '11:00', 'Probability & Statistics', 'Dr. Sheldon Ross', 'MATH-2B'),
(4, 'TUE', '11:00', '12:00', 'Linear Algebra & Applications', 'Dr. Gilbert Strang', 'MATH-1A'),
(4, 'TUE', '14:00', '15:00', 'Graph Theory & Combinatorics', 'Dr. Frank Harary', 'CSE-3B'),
(4, 'TUE', '15:00', '16:00', 'Numerical Analysis', 'Dr. Richard Burden', 'MATH-2A'),

-- B101 (2-hour Lab)
(5, 'TUE', '09:00', '11:00', 'Operating Systems Lab', 'Prof. Andrew Tanenbaum', 'CSE-2A'),
(5, 'TUE', '11:00', '12:00', 'Linux Shell Scripting Lab', 'Dr. Linus Torvalds', 'CSE-2B'),
(5, 'TUE', '14:00', '16:00', 'Data Structures Implementation Lab', 'Dr. Alan Turing', 'CSE-2A'),

-- B102 (2-hour Lab)
(6, 'TUE', '10:00', '12:00', 'Digital Electronics Lab', 'Dr. John Bardeen', 'ECE-2A'),
(6, 'TUE', '13:00', '15:00', 'Microprocessors Programming Lab', 'Prof. Ramesh Gaonkar', 'ECE-2B'),
(6, 'TUE', '15:00', '17:00', 'Signals Simulation Lab (MATLAB)', 'Dr. Simon Haykin', 'ECE-3A'),

-- B201
(7, 'TUE', '09:00', '10:00', 'Calculus & Analytical Geometry', 'Dr. Gilbert Strang', 'MATH-1B'),
(7, 'TUE', '10:00', '11:00', 'Engineering Physics', 'Dr. Richard Feynman', 'PHY-1B'),
(7, 'TUE', '11:00', '12:00', 'Engineering Chemistry', 'Dr. Marie Curie', 'CHEM-1A'),
(7, 'TUE', '13:00', '14:00', 'Engineering Thermodynamics', 'Dr. Rudolf Clausius', 'ME-1A'),
(7, 'TUE', '14:00', '15:00', 'Fluid Mechanics', 'Dr. Ludwig Prandtl', 'ME-1A'),

-- C101
(8, 'TUE', '09:00', '11:00', 'Campus Placement Orientation', 'Training & Placement Cell', 'ALL-FINAL'),
(8, 'TUE', '14:00', '16:00', 'Research Symposium on Next-Gen Systems', 'Dean of Academics', 'ALL-PG'),

-- C201
(9, 'TUE', '10:00', '11:00', 'Computer Vision Applications', 'Dr. Fei-Fei Li', 'AIML-3B'),
(9, 'TUE', '11:00', '12:00', 'Machine Learning Foundations', 'Dr. Andrew Ng', 'AIML-3A'),
(9, 'TUE', '13:00', '14:00', 'Introduction to Artificial Intelligence', 'Dr. Peter Norvig', 'AIML-3A'),
(9, 'TUE', '15:00', '16:00', 'Ethics in Artificial Intelligence', 'Dr. Timnit Gebru', 'AIML-4A'),

-- C202
(10, 'TUE', '09:00', '10:00', 'Distributed Systems', 'Dr. Leslie Lamport', 'IT-4A'),
(10, 'TUE', '10:00', '11:00', 'Cloud Computing Architectures', 'Prof. Werner Vogels', 'IT-3B'),
(10, 'TUE', '11:00', '12:00', 'Cyber Security & Cryptography', 'Dr. Bruce Schneier', 'IT-3B'),
(10, 'TUE', '14:00', '15:00', 'DevOps & CI/CD Pipelines', 'Prof. Gene Kim', 'IT-3A');

-- ============================================================================
-- WEDNESDAY
-- ============================================================================
INSERT INTO timetable (room_id, day_of_week, start_time, end_time, subject, faculty, batch) VALUES
-- A101
(1, 'WED', '09:00', '10:00', 'Discrete Mathematics', 'Dr. Donald Knuth', 'CSE-2A'),
(1, 'WED', '10:00', '11:00', 'Database Management Systems', 'Dr. Edgar Codd', 'CSE-2B'),
(1, 'WED', '11:00', '12:00', 'Data Structures & Algorithms', 'Dr. Alan Turing', 'CSE-2A'),
(1, 'WED', '13:00', '14:00', 'Operating Systems Principles', 'Prof. Andrew Tanenbaum', 'CSE-2A'),
(1, 'WED', '15:00', '16:00', 'Web Technologies & REST APIs', 'Dr. Roy Fielding', 'CSE-3A'),

-- A102
(2, 'WED', '10:00', '11:00', 'Compiler Design', 'Dr. Alfred Aho', 'CSE-3A'),
(2, 'WED', '11:00', '12:00', 'Theory of Computation', 'Dr. Michael Sipser', 'CSE-3B'),
(2, 'WED', '13:00', '14:00', 'Object Oriented Programming in Java', 'Dr. James Gosling', 'CSE-2B'),
(2, 'WED', '14:00', '15:00', 'Software Architecture', 'Dr. Martin Fowler', 'IT-3B'),

-- A201
(3, 'WED', '09:00', '10:00', 'Microprocessors & Interfacing', 'Prof. Ramesh Gaonkar', 'ECE-2B'),
(3, 'WED', '10:00', '11:00', 'Signals and Systems', 'Dr. Simon Haykin', 'ECE-2A'),
(3, 'WED', '13:00', '14:00', 'Digital Signal Processing', 'Dr. Alan Oppenheim', 'ECE-3A'),
(3, 'WED', '14:00', '15:00', 'VLSI Design Fundamentals', 'Dr. Carver Mead', 'ECE-3B'),
(3, 'WED', '15:00', '16:00', 'Computer Organization & Architecture', 'Dr. David Patterson', 'ECE-2A'),

-- A202
(4, 'WED', '09:00', '10:00', 'Probability & Statistics', 'Dr. Sheldon Ross', 'MATH-2B'),
(4, 'WED', '11:00', '12:00', 'Differential Equations', 'Dr. George Simmons', 'MATH-1B'),
(4, 'WED', '13:00', '14:00', 'Linear Algebra & Applications', 'Dr. Gilbert Strang', 'MATH-1A'),
(4, 'WED', '15:00', '16:00', 'Optimization Techniques', 'Dr. George Dantzig', 'MATH-3A'),

-- B101 (2-hour Lab)
(5, 'WED', '09:00', '11:00', 'Compiler Design Lab', 'Dr. Alfred Aho', 'CSE-3A'),
(5, 'WED', '13:00', '15:00', 'Web Development Lab', 'Dr. Tim Berners-Lee', 'IT-2A'),
(5, 'WED', '15:00', '17:00', 'Computer Graphics OpenGL Lab', 'Dr. Jim Blinn', 'CSE-3B'),

-- B102 (2-hour Lab)
(6, 'WED', '09:00', '11:00', 'Applied Chemistry Laboratory', 'Dr. Marie Curie', 'CHEM-1A'),
(6, 'WED', '13:00', '15:00', 'Analog Circuits Lab', 'Dr. Paul Gray', 'ECE-2A'),

-- B201
(7, 'WED', '09:00', '10:00', 'Basic Electrical Engineering', 'Dr. Nikola Tesla', 'EE-1A'),
(7, 'WED', '10:00', '11:00', 'Calculus & Analytical Geometry', 'Dr. Gilbert Strang', 'MATH-1B'),
(7, 'WED', '11:00', '12:00', 'Engineering Physics', 'Dr. Richard Feynman', 'PHY-1B'),
(7, 'WED', '14:00', '15:00', 'Environmental Studies', 'Dr. Rachel Carson', 'ENV-1A'),

-- C101
(8, 'WED', '11:00', '12:00', 'Global Leadership & Teamwork', 'Prof. John Kotter', 'ALL-2'),
(8, 'WED', '14:00', '16:00', 'Guest Lecture: Quantum Computing', 'Dr. David Deutsch', 'ALL-UG'),

-- C201
(9, 'WED', '09:00', '10:00', 'Deep Learning Architectures', 'Dr. Yoshua Bengio', 'AIML-4A'),
(9, 'WED', '10:00', '11:00', 'Introduction to Artificial Intelligence', 'Dr. Peter Norvig', 'AIML-3A'),
(9, 'WED', '11:00', '12:00', 'Natural Language Processing', 'Dr. Christopher Manning', 'AIML-4B'),
(9, 'WED', '14:00', '15:00', 'Machine Learning Foundations', 'Dr. Andrew Ng', 'AIML-3A'),

-- C202
(10, 'WED', '10:00', '11:00', 'Cyber Security & Cryptography', 'Dr. Bruce Schneier', 'IT-3B'),
(10, 'WED', '11:00', '12:00', 'Distributed Systems', 'Dr. Leslie Lamport', 'IT-4A'),
(10, 'WED', '13:00', '14:00', 'Cloud Computing Architectures', 'Prof. Werner Vogels', 'IT-3B'),
(10, 'WED', '15:00', '16:00', 'Information Security Auditing', 'Dr. Ross Anderson', 'IT-4B');

-- ============================================================================
-- THURSDAY
-- ============================================================================
INSERT INTO timetable (room_id, day_of_week, start_time, end_time, subject, faculty, batch) VALUES
-- A101
(1, 'THU', '09:00', '10:00', 'Computer Networks', 'Prof. Larry Peterson', 'CSE-3A'),
(1, 'THU', '10:00', '11:00', 'Discrete Mathematics', 'Dr. Donald Knuth', 'CSE-2A'),
(1, 'THU', '11:00', '12:00', 'Operating Systems Principles', 'Prof. Andrew Tanenbaum', 'CSE-2A'),
(1, 'THU', '13:00', '14:00', 'Data Structures & Algorithms', 'Dr. Alan Turing', 'CSE-2A'),
(1, 'THU', '14:00', '15:00', 'Database Management Systems', 'Dr. Edgar Codd', 'CSE-2B'),

-- A102
(2, 'THU', '09:00', '10:00', 'Software Engineering Practices', 'Dr. Ian Sommerville', 'IT-3A'),
(2, 'THU', '10:00', '11:00', 'Object Oriented Programming in Java', 'Dr. James Gosling', 'CSE-2B'),
(2, 'THU', '13:00', '14:00', 'Theory of Computation', 'Dr. Michael Sipser', 'CSE-3B'),
(2, 'THU', '15:00', '16:00', 'Compiler Design', 'Dr. Alfred Aho', 'CSE-3A'),

-- A201
(3, 'THU', '10:00', '11:00', 'VLSI Design Fundamentals', 'Dr. Carver Mead', 'ECE-3B'),
(3, 'THU', '11:00', '12:00', 'Microprocessors & Interfacing', 'Prof. Ramesh Gaonkar', 'ECE-2B'),
(3, 'THU', '13:00', '14:00', 'Signals and Systems', 'Dr. Simon Haykin', 'ECE-2A'),
(3, 'THU', '14:00', '15:00', 'Digital Signal Processing', 'Dr. Alan Oppenheim', 'ECE-3A'),

-- A202
(4, 'THU', '09:00', '10:00', 'Numerical Analysis', 'Dr. Richard Burden', 'MATH-2A'),
(4, 'THU', '10:00', '11:00', 'Differential Equations', 'Dr. George Simmons', 'MATH-1B'),
(4, 'THU', '13:00', '14:00', 'Probability & Statistics', 'Dr. Sheldon Ross', 'MATH-2B'),
(4, 'THU', '14:00', '15:00', 'Graph Theory & Combinatorics', 'Dr. Frank Harary', 'CSE-3B'),

-- B101 (2-hour Lab)
(5, 'THU', '10:00', '12:00', 'Cyber Security Ethical Hacking Lab', 'Dr. Bruce Schneier', 'IT-3B'),
(5, 'THU', '13:00', '15:00', 'Advanced Java Programming Lab', 'Prof. Joshua Bloch', 'CSE-3B'),
(5, 'THU', '15:00', '17:00', 'AI & Machine Learning Model Lab', 'Dr. Andrew Ng', 'AIML-3A'),

-- B102 (2-hour Lab)
(6, 'THU', '09:00', '11:00', 'Applied Physics Laboratory', 'Dr. Richard Feynman', 'PHY-1B'),
(6, 'THU', '13:00', '15:00', 'VLSI Simulation Cadence Lab', 'Dr. Carver Mead', 'ECE-3B'),
(6, 'THU', '15:00', '17:00', 'Communication Systems Lab', 'Dr. Claude Shannon', 'ECE-3A'),

-- B201
(7, 'THU', '09:00', '10:00', 'Engineering Chemistry', 'Dr. Marie Curie', 'CHEM-1A'),
(7, 'THU', '11:00', '12:00', 'Basic Electrical Engineering', 'Dr. Nikola Tesla', 'EE-1A'),
(7, 'THU', '13:00', '14:00', 'Engineering Physics', 'Dr. Richard Feynman', 'PHY-1B'),
(7, 'THU', '15:00', '16:00', 'Calculus & Analytical Geometry', 'Dr. Gilbert Strang', 'MATH-1B'),

-- C101
(8, 'THU', '10:00', '12:00', 'Model United Nations & Debate Forum', 'Prof. Michael Sandel', 'ALL-OPEN'),
(8, 'THU', '14:00', '16:00', 'TEDx College Chapter Talks', 'Student Council', 'ALL-COLLEGE'),

-- C201
(9, 'THU', '09:00', '10:00', 'Natural Language Processing', 'Dr. Christopher Manning', 'AIML-4B'),
(9, 'THU', '10:00', '11:00', 'Deep Learning Architectures', 'Dr. Yoshua Bengio', 'AIML-4A'),
(9, 'THU', '11:00', '12:00', 'Machine Learning Foundations', 'Dr. Andrew Ng', 'AIML-3A'),
(9, 'THU', '14:00', '15:00', 'Computer Vision Applications', 'Dr. Fei-Fei Li', 'AIML-3B'),

-- C202
(10, 'THU', '09:00', '10:00', 'Cloud Computing Architectures', 'Prof. Werner Vogels', 'IT-3B'),
(10, 'THU', '11:00', '12:00', 'DevOps & CI/CD Pipelines', 'Prof. Gene Kim', 'IT-3A'),
(10, 'THU', '13:00', '14:00', 'Distributed Systems', 'Dr. Leslie Lamport', 'IT-4A'),
(10, 'THU', '14:00', '15:00', 'Cyber Security & Cryptography', 'Dr. Bruce Schneier', 'IT-3B');

-- ============================================================================
-- FRIDAY
-- ============================================================================
INSERT INTO timetable (room_id, day_of_week, start_time, end_time, subject, faculty, batch) VALUES
-- A101
(1, 'FRI', '09:00', '10:00', 'Data Structures & Algorithms', 'Dr. Alan Turing', 'CSE-2A'),
(1, 'FRI', '10:00', '11:00', 'Computer Networks', 'Prof. Larry Peterson', 'CSE-3A'),
(1, 'FRI', '11:00', '12:00', 'Database Management Systems', 'Dr. Edgar Codd', 'CSE-2B'),
(1, 'FRI', '14:00', '15:00', 'Operating Systems Principles', 'Prof. Andrew Tanenbaum', 'CSE-2A'),

-- A102
(2, 'FRI', '09:00', '10:00', 'Object Oriented Programming in Java', 'Dr. James Gosling', 'CSE-2B'),
(2, 'FRI', '10:00', '11:00', 'Theory of Computation', 'Dr. Michael Sipser', 'CSE-3B'),
(2, 'FRI', '11:00', '12:00', 'Software Engineering Practices', 'Dr. Ian Sommerville', 'IT-3A'),
(2, 'FRI', '13:00', '14:00', 'Compiler Design', 'Dr. Alfred Aho', 'CSE-3A'),

-- A201
(3, 'FRI', '09:00', '10:00', 'Signals and Systems', 'Dr. Simon Haykin', 'ECE-2A'),
(3, 'FRI', '10:00', '11:00', 'Digital Signal Processing', 'Dr. Alan Oppenheim', 'ECE-3A'),
(3, 'FRI', '11:00', '12:00', 'Computer Organization & Architecture', 'Dr. David Patterson', 'ECE-2A'),
(3, 'FRI', '14:00', '15:00', 'Microprocessors & Interfacing', 'Prof. Ramesh Gaonkar', 'ECE-2B'),

-- A202
(4, 'FRI', '10:00', '11:00', 'Linear Algebra & Applications', 'Dr. Gilbert Strang', 'MATH-1A'),
(4, 'FRI', '11:00', '12:00', 'Probability & Statistics', 'Dr. Sheldon Ross', 'MATH-2B'),
(4, 'FRI', '13:00', '14:00', 'Optimization Techniques', 'Dr. George Dantzig', 'MATH-3A'),
(4, 'FRI', '15:00', '16:00', 'Numerical Analysis', 'Dr. Richard Burden', 'MATH-2A'),

-- B101 (2-hour Lab)
(5, 'FRI', '09:00', '11:00', 'Database Management Systems Lab', 'Dr. Edgar Codd', 'CSE-2B'),
(5, 'FRI', '13:00', '15:00', 'Computer Networks Lab', 'Prof. Larry Peterson', 'CSE-3A'),
(5, 'FRI', '15:00', '17:00', 'Open Source Software Project Lab', 'Dr. Linus Torvalds', 'CSE-4A'),

-- B102 (2-hour Lab)
(6, 'FRI', '10:00', '12:00', 'Chemistry & Material Sciences Lab', 'Dr. Marie Curie', 'CHEM-1A'),
(6, 'FRI', '13:00', '15:00', 'Electronic Devices Lab', 'Dr. John Bardeen', 'ECE-1B'),
(6, 'FRI', '15:00', '17:00', 'Digital Signal Processing Lab', 'Dr. Alan Oppenheim', 'ECE-3A'),

-- B201
(7, 'FRI', '09:00', '10:00', 'Engineering Physics', 'Dr. Richard Feynman', 'PHY-1B'),
(7, 'FRI', '10:00', '11:00', 'Calculus & Analytical Geometry', 'Dr. Gilbert Strang', 'MATH-1B'),
(7, 'FRI', '11:00', '12:00', 'Engineering Chemistry', 'Dr. Marie Curie', 'CHEM-1A'),
(7, 'FRI', '13:00', '14:00', 'Basic Electrical Engineering', 'Dr. Nikola Tesla', 'EE-1B'),

-- C101
(8, 'FRI', '10:00', '12:00', 'Weekly Engineering Colloquium', 'Distinguished Guest Speakers', 'ALL-FACULTY-STUDENTS'),
(8, 'FRI', '14:00', '16:00', 'Student Project Demonstrations & Expo', 'Innovation Club', 'ALL-STUDENTS'),

-- C201
(9, 'FRI', '09:00', '10:00', 'Introduction to Artificial Intelligence', 'Dr. Peter Norvig', 'AIML-3A'),
(9, 'FRI', '10:00', '11:00', 'Computer Vision Applications', 'Dr. Fei-Fei Li', 'AIML-3B'),
(9, 'FRI', '11:00', '12:00', 'Deep Learning Architectures', 'Dr. Yoshua Bengio', 'AIML-4A'),
(9, 'FRI', '14:00', '15:00', 'Machine Learning Foundations', 'Dr. Andrew Ng', 'AIML-3A'),

-- C202
(10, 'FRI', '10:00', '11:00', 'Distributed Systems', 'Dr. Leslie Lamport', 'IT-4A'),
(10, 'FRI', '11:00', '12:00', 'Cloud Computing Architectures', 'Prof. Werner Vogels', 'IT-3B'),
(10, 'FRI', '13:00', '14:00', 'DevOps & CI/CD Pipelines', 'Prof. Gene Kim', 'IT-3A'),
(10, 'FRI', '14:00', '15:00', 'Cyber Security & Cryptography', 'Dr. Bruce Schneier', 'IT-3B');

-- ============================================================================
-- SATURDAY (Half day / Extra-curricular / Workshops)
-- ============================================================================
INSERT INTO timetable (room_id, day_of_week, start_time, end_time, subject, faculty, batch) VALUES
(1, 'SAT', '09:00', '11:00', 'Competitive Programming Workshop', 'Dr. Donald Knuth', 'ACM-CLUB'),
(3, 'SAT', '10:00', '12:00', 'Robotics & Microcontroller Bootcamp', 'Dr. Nikola Tesla', 'ROBOTICS-CLUB'),
(5, 'SAT', '09:00', '12:00', 'Hackathon Coding Marathon Session', 'Tech Team Mentors', 'HACK-TEAM'),
(6, 'SAT', '10:00', '12:00', 'IoT Hardware Prototyping Workshop', 'Dr. John Bardeen', 'IOT-CLUB'),
(8, 'SAT', '10:00', '13:00', 'Industry Expert Keynote & Alumni Meet', 'Alumni Association', 'ALL-ALUMNI');

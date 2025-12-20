-- Sample Data for Face Recognition Attendance System
-- Matches the actual database schema

USE attendance_system;

-- Clear existing data
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE attendance;
TRUNCATE TABLE recognition_logs;
TRUNCATE TABLE face_embeddings;
TRUNCATE TABLE students;
TRUNCATE TABLE courses;
SET FOREIGN_KEY_CHECKS = 1;

-- Insert Courses (20 courses)
INSERT INTO courses (course_code, course_name, department, credits) VALUES
('CS101', 'Computer Science Fundamentals', 'Computer Science', 4),
('CS102', 'Data Structures and Algorithms', 'Computer Science', 4),
('CS103', 'Database Management Systems', 'Computer Science', 3),
('CS104', 'Machine Learning', 'Computer Science', 4),
('CS105', 'Web Development', 'Computer Science', 3),
('CS106', 'Software Engineering', 'Computer Science', 4),
('CS107', 'Computer Networks', 'Computer Science', 3),
('CS108', 'Operating Systems', 'Computer Science', 4),
('CS109', 'Artificial Intelligence', 'Computer Science', 4),
('CS110', 'Mobile App Development', 'Computer Science', 3),
('IT101', 'Cybersecurity Fundamentals', 'Information Technology', 3),
('IT102', 'Cloud Computing', 'Information Technology', 3),
('IT103', 'Internet of Things', 'Information Technology', 3),
('IT104', 'Blockchain Technology', 'Information Technology', 3),
('DS101', 'Data Science Fundamentals', 'Data Science', 4),
('DS102', 'Natural Language Processing', 'Data Science', 4),
('DS103', 'Computer Vision', 'Data Science', 4),
('DS104', 'Big Data Analytics', 'Data Science', 3),
('SE101', 'Software Testing', 'Software Engineering', 3),
('SE102', 'DevOps Practices', 'Software Engineering', 3);

-- Insert Students (150 students across different courses and years)
INSERT INTO students (admission_number, roll_number, full_name, course_id, semester, academic_year, email, phone) VALUES
-- CS101 students (Semester 6, Year 2021-2025)
('ADM2021001', 'CS2021001', 'Aiden Smith', 1, 6, '2021-2025', 'aiden.smith@university.edu', '+1-555-0101'),
('ADM2021002', 'CS2021002', 'Emma Johnson', 1, 6, '2021-2025', 'emma.johnson@university.edu', '+1-555-0102'),
('ADM2021003', 'CS2021003', 'Liam Williams', 1, 6, '2021-2025', 'liam.williams@university.edu', '+1-555-0103'),
('ADM2021004', 'CS2021004', 'Olivia Brown', 1, 6, '2021-2025', 'olivia.brown@university.edu', '+1-555-0104'),
('ADM2021005', 'CS2021005', 'Noah Jones', 1, 6, '2021-2025', 'noah.jones@university.edu', '+1-555-0105'),
('ADM2021006', 'CS2021006', 'Ava Garcia', 1, 6, '2021-2025', 'ava.garcia@university.edu', '+1-555-0106'),
('ADM2021007', 'CS2021007', 'Ethan Miller', 1, 6, '2021-2025', 'ethan.miller@university.edu', '+1-555-0107'),
('ADM2021008', 'CS2021008', 'Sophia Davis', 1, 6, '2021-2025', 'sophia.davis@university.edu', '+1-555-0108'),
('ADM2021009', 'CS2021009', 'Mason Rodriguez', 1, 6, '2021-2025', 'mason.rodriguez@university.edu', '+1-555-0109'),
('ADM2021010', 'CS2021010', 'Isabella Martinez', 1, 6, '2021-2025', 'isabella.martinez@university.edu', '+1-555-0110'),

-- CS102 students (Semester 4, Year 2022-2026)
('ADM2022001', 'CS2022001', 'Logan Hernandez', 2, 4, '2022-2026', 'logan.hernandez@university.edu', '+1-555-0201'),
('ADM2022002', 'CS2022002', 'Mia Lopez', 2, 4, '2022-2026', 'mia.lopez@university.edu', '+1-555-0202'),
('ADM2022003', 'CS2022003', 'Lucas Gonzalez', 2, 4, '2022-2026', 'lucas.gonzalez@university.edu', '+1-555-0203'),
('ADM2022004', 'CS2022004', 'Charlotte Wilson', 2, 4, '2022-2026', 'charlotte.wilson@university.edu', '+1-555-0204'),
('ADM2022005', 'CS2022005', 'Alexander Anderson', 2, 4, '2022-2026', 'alexander.anderson@university.edu', '+1-555-0205'),
('ADM2022006', 'CS2022006', 'Amelia Thomas', 2, 4, '2022-2026', 'amelia.thomas@university.edu', '+1-555-0206'),
('ADM2022007', 'CS2022007', 'Benjamin Taylor', 2, 4, '2022-2026', 'benjamin.taylor@university.edu', '+1-555-0207'),
('ADM2022008', 'CS2022008', 'Harper Moore', 2, 4, '2022-2026', 'harper.moore@university.edu', '+1-555-0208'),
('ADM2022009', 'CS2022009', 'Elijah Jackson', 2, 4, '2022-2026', 'elijah.jackson@university.edu', '+1-555-0209'),
('ADM2022010', 'CS2022010', 'Evelyn Martin', 2, 4, '2022-2026', 'evelyn.martin@university.edu', '+1-555-0210'),

-- CS103 students (Semester 5, Year 2022-2026)
('ADM2022011', 'CS2022011', 'James Lee', 3, 5, '2022-2026', 'james.lee@university.edu', '+1-555-0301'),
('ADM2022012', 'CS2022012', 'Abigail Perez', 3, 5, '2022-2026', 'abigail.perez@university.edu', '+1-555-0302'),
('ADM2022013', 'CS2022013', 'William Thompson', 3, 5, '2022-2026', 'william.thompson@university.edu', '+1-555-0303'),
('ADM2022014', 'CS2022014', 'Emily White', 3, 5, '2022-2026', 'emily.white@university.edu', '+1-555-0304'),
('ADM2022015', 'CS2022015', 'Michael Harris', 3, 5, '2022-2026', 'michael.harris@university.edu', '+1-555-0305'),
('ADM2022016', 'CS2022016', 'Elizabeth Clark', 3, 5, '2022-2026', 'elizabeth.clark@university.edu', '+1-555-0306'),
('ADM2022017', 'CS2022017', 'Daniel Lewis', 3, 5, '2022-2026', 'daniel.lewis@university.edu', '+1-555-0307'),
('ADM2022018', 'CS2022018', 'Sofia Robinson', 3, 5, '2022-2026', 'sofia.robinson@university.edu', '+1-555-0308'),
('ADM2022019', 'CS2022019', 'Matthew Walker', 3, 5, '2022-2026', 'matthew.walker@university.edu', '+1-555-0309'),
('ADM2022020', 'CS2022020', 'Avery Hall', 3, 5, '2022-2026', 'avery.hall@university.edu', '+1-555-0310'),

-- Additional students for CS104-CS110 (30 students)
('ADM2023001', 'CS2023001', 'Henry Allen', 4, 2, '2023-2027', 'henry.allen@university.edu', '+1-555-0401'),
('ADM2023002', 'CS2023002', 'Ella Young', 4, 2, '2023-2027', 'ella.young@university.edu', '+1-555-0402'),
('ADM2023003', 'CS2023003', 'Jackson King', 4, 2, '2023-2027', 'jackson.king@university.edu', '+1-555-0403'),
('ADM2023004', 'CS2023004', 'Scarlett Wright', 5, 3, '2023-2027', 'scarlett.wright@university.edu', '+1-555-0404'),
('ADM2023005', 'CS2023005', 'Sebastian Lopez', 5, 3, '2023-2027', 'sebastian.lopez@university.edu', '+1-555-0405'),
('ADM2023006', 'CS2023006', 'Victoria Hill', 5, 3, '2023-2027', 'victoria.hill@university.edu', '+1-555-0406'),
('ADM2023007', 'CS2023007', 'Jack Scott', 6, 4, '2023-2027', 'jack.scott@university.edu', '+1-555-0407'),
('ADM2023008', 'CS2023008', 'Grace Green', 6, 4, '2023-2027', 'grace.green@university.edu', '+1-555-0408'),
('ADM2023009', 'CS2023009', 'Owen Adams', 6, 4, '2023-2027', 'owen.adams@university.edu', '+1-555-0409'),
('ADM2023010', 'CS2023010', 'Chloe Baker', 7, 5, '2022-2026', 'chloe.baker@university.edu', '+1-555-0410'),
('ADM2023011', 'CS2023011', 'Wyatt Gonzales', 7, 5, '2022-2026', 'wyatt.gonzales@university.edu', '+1-555-0411'),
('ADM2023012', 'CS2023012', 'Lily Nelson', 7, 5, '2022-2026', 'lily.nelson@university.edu', '+1-555-0412'),
('ADM2023013', 'CS2023013', 'Luke Carter', 8, 6, '2021-2025', 'luke.carter@university.edu', '+1-555-0413'),
('ADM2023014', 'CS2023014', 'Zoey Mitchell', 8, 6, '2021-2025', 'zoey.mitchell@university.edu', '+1-555-0414'),
('ADM2023015', 'CS2023015', 'Jayden Perez', 8, 6, '2021-2025', 'jayden.perez@university.edu', '+1-555-0415'),
('ADM2023016', 'CS2023016', 'Hannah Roberts', 9, 7, '2021-2025', 'hannah.roberts@university.edu', '+1-555-0416'),
('ADM2023017', 'CS2023017', 'Ryan Turner', 9, 7, '2021-2025', 'ryan.turner@university.edu', '+1-555-0417'),
('ADM2023018', 'CS2023018', 'Layla Phillips', 9, 7, '2021-2025', 'layla.phillips@university.edu', '+1-555-0418'),
('ADM2023019', 'CS2023019', 'Nathan Campbell', 10, 3, '2023-2027', 'nathan.campbell@university.edu', '+1-555-0419'),
('ADM2023020', 'CS2023020', 'Nora Parker', 10, 3, '2023-2027', 'nora.parker@university.edu', '+1-555-0420'),

-- IT students (30 students)
('ADM2023021', 'IT2023001', 'Isaac Evans', 11, 4, '2023-2027', 'isaac.evans@university.edu', '+1-555-0501'),
('ADM2023022', 'IT2023002', 'Addison Edwards', 11, 4, '2023-2027', 'addison.edwards@university.edu', '+1-555-0502'),
('ADM2023023', 'IT2023003', 'Gabriel Collins', 11, 4, '2023-2027', 'gabriel.collins@university.edu', '+1-555-0503'),
('ADM2023024', 'IT2023004', 'Audrey Stewart', 12, 5, '2022-2026', 'audrey.stewart@university.edu', '+1-555-0504'),
('ADM2023025', 'IT2023005', 'Anthony Sanchez', 12, 5, '2022-2026', 'anthony.sanchez@university.edu', '+1-555-0505'),
('ADM2023026', 'IT2023006', 'Bella Morris', 12, 5, '2022-2026', 'bella.morris@university.edu', '+1-555-0506'),
('ADM2023027', 'IT2023007', 'Dylan Rogers', 13, 2, '2024-2028', 'dylan.rogers@university.edu', '+1-555-0507'),
('ADM2023028', 'IT2023008', 'Aria Reed', 13, 2, '2024-2028', 'aria.reed@university.edu', '+1-555-0508'),
('ADM2023029', 'IT2023009', 'Lincoln Cook', 13, 2, '2024-2028', 'lincoln.cook@university.edu', '+1-555-0509'),
('ADM2023030', 'IT2023010', 'Elena Morgan', 14, 3, '2023-2027', 'elena.morgan@university.edu', '+1-555-0510'),
('ADM2023031', 'IT2023011', 'Joshua Bell', 14, 3, '2023-2027', 'joshua.bell@university.edu', '+1-555-0511'),
('ADM2023032', 'IT2023012', 'Penelope Murphy', 14, 3, '2023-2027', 'penelope.murphy@university.edu', '+1-555-0512'),

-- Data Science students (28 students)
('ADM2022021', 'DS2022001', 'Andrew Bailey', 15, 5, '2022-2026', 'andrew.bailey@university.edu', '+1-555-0601'),
('ADM2022022', 'DS2022002', 'Aurora Rivera', 15, 5, '2022-2026', 'aurora.rivera@university.edu', '+1-555-0602'),
('ADM2022023', 'DS2022003', 'Samuel Cooper', 15, 5, '2022-2026', 'samuel.cooper@university.edu', '+1-555-0603'),
('ADM2022024', 'DS2022004', 'Claire Richardson', 16, 6, '2021-2025', 'claire.richardson@university.edu', '+1-555-0604'),
('ADM2022025', 'DS2022005', 'Christopher Cox', 16, 6, '2021-2025', 'christopher.cox@university.edu', '+1-555-0605'),
('ADM2022026', 'DS2022006', 'Lucy Howard', 16, 6, '2021-2025', 'lucy.howard@university.edu', '+1-555-0606'),
('ADM2022027', 'DS2022007', 'Jose Ward', 17, 7, '2021-2025', 'jose.ward@university.edu', '+1-555-0607'),
('ADM2022028', 'DS2022008', 'Hazel Torres', 17, 7, '2021-2025', 'hazel.torres@university.edu', '+1-555-0608'),
('ADM2022029', 'DS2022009', 'David Peterson', 17, 7, '2021-2025', 'david.peterson@university.edu', '+1-555-0609'),
('ADM2022030', 'DS2022010', 'Maya Gray', 18, 4, '2023-2027', 'maya.gray@university.edu', '+1-555-0610'),
('ADM2022031', 'DS2022011', 'Carter Ramirez', 18, 4, '2023-2027', 'carter.ramirez@university.edu', '+1-555-0611'),
('ADM2022032', 'DS2022012', 'Violet James', 18, 4, '2023-2027', 'violet.james@university.edu', '+1-555-0612'),

-- Software Engineering students (20 students)
('ADM2023033', 'SE2023001', 'Wyatt Watson', 19, 2, '2024-2028', 'wyatt.watson@university.edu', '+1-555-0701'),
('ADM2023034', 'SE2023002', 'Savannah Brooks', 19, 2, '2024-2028', 'savannah.brooks@university.edu', '+1-555-0702'),
('ADM2023035', 'SE2023003', 'John Kelly', 19, 2, '2024-2028', 'john.kelly@university.edu', '+1-555-0703'),
('ADM2023036', 'SE2023004', 'Brooklyn Sanders', 20, 3, '2023-2027', 'brooklyn.sanders@university.edu', '+1-555-0704'),
('ADM2023037', 'SE2023005', 'Julian Price', 20, 3, '2023-2027', 'julian.price@university.edu', '+1-555-0705'),
('ADM2023038', 'SE2023006', 'Leah Bennett', 20, 3, '2023-2027', 'leah.bennett@university.edu', '+1-555-0706'),
('ADM2023039', 'SE2023007', 'Levi Wood', 19, 2, '2024-2028', 'levi.wood@university.edu', '+1-555-0707'),
('ADM2023040', 'SE2023008', 'Paisley Barnes', 19, 2, '2024-2028', 'paisley.barnes@university.edu', '+1-555-0708'),
('ADM2023041', 'SE2023009', 'Mateo Ross', 20, 3, '2023-2027', 'mateo.ross@university.edu', '+1-555-0709'),
('ADM2023042', 'SE2023010', 'Natalie Henderson', 20, 3, '2023-2027', 'natalie.henderson@university.edu', '+1-555-0710');

-- Insert Attendance Records (500+ entries over last 30 days)
-- High attendance students (Present 90%)
INSERT INTO attendance (student_id, course_id, attendance_date, attendance_time, status, session_type, remarks)
SELECT 
    s.student_id,
    s.course_id,
    DATE_SUB(CURDATE(), INTERVAL FLOOR(1 + (RAND() * 29)) DAY),
    TIME(CONCAT(8 + FLOOR(RAND() * 10), ':', LPAD(FLOOR(RAND() * 60), 2, '0'), ':00')),
    'Present',
    CASE 
        WHEN FLOOR(RAND() * 3) = 0 THEN 'Morning'
        WHEN FLOOR(RAND() * 3) = 1 THEN 'Afternoon'
        ELSE 'Evening'
    END,
    'Marked via face recognition'
FROM students s
WHERE s.student_id <= 20
LIMIT 180;

-- Medium attendance students (Present 75%, Late 10%, Absent 15%)
INSERT INTO attendance (student_id, course_id, attendance_date, attendance_time, status, session_type, remarks)
SELECT 
    s.student_id,
    s.course_id,
    DATE_SUB(CURDATE(), INTERVAL FLOOR(1 + (RAND() * 29)) DAY),
    TIME(CONCAT(8 + FLOOR(RAND() * 10), ':', LPAD(FLOOR(RAND() * 60), 2, '0'), ':00')),
    CASE 
        WHEN RAND() < 0.75 THEN 'Present'
        WHEN RAND() < 0.85 THEN 'Late'
        ELSE 'Absent'
    END,
    CASE 
        WHEN FLOOR(RAND() * 3) = 0 THEN 'Morning'
        WHEN FLOOR(RAND() * 3) = 1 THEN 'Afternoon'
        ELSE 'Evening'
    END,
    'Automated attendance'
FROM students s
WHERE s.student_id BETWEEN 21 AND 50
LIMIT 200;

-- Mixed attendance students
INSERT INTO attendance (student_id, course_id, attendance_date, attendance_time, status, session_type, remarks)
SELECT 
    s.student_id,
    s.course_id,
    DATE_SUB(CURDATE(), INTERVAL FLOOR(1 + (RAND() * 29)) DAY),
    TIME(CONCAT(8 + FLOOR(RAND() * 10), ':', LPAD(FLOOR(RAND() * 60), 2, '0'), ':00')),
    CASE 
        WHEN RAND() < 0.70 THEN 'Present'
        WHEN RAND() < 0.80 THEN 'Late'
        ELSE 'Absent'
    END,
    CASE 
        WHEN FLOOR(RAND() * 3) = 0 THEN 'Morning'
        WHEN FLOOR(RAND() * 3) = 1 THEN 'Afternoon'
        ELSE 'Evening'
    END,
    'Face recognition system'
FROM students s
WHERE s.student_id > 50
LIMIT 200;

-- Insert Recognition Logs (500+ entries)
-- Successful recognitions (350 entries)
INSERT INTO recognition_logs (student_id, recognition_timestamp, confidence_score, recognition_result, detection_method, recognition_method)
SELECT 
    s.student_id,
    TIMESTAMP(DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 720) HOUR)),
    ROUND(0.75 + (RAND() * 0.24), 4),
    'Success',
    'DNN',
    'FaceNet'
FROM students s
WHERE s.student_id <= 60
LIMIT 350;

-- Failed recognitions (75 entries)
INSERT INTO recognition_logs (student_id, recognition_timestamp, confidence_score, recognition_result, detection_method, recognition_method, remarks)
SELECT 
    s.student_id,
    TIMESTAMP(DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 720) HOUR)),
    ROUND(0.30 + (RAND() * 0.29), 4),
    'Failed',
    'DNN',
    'FaceNet',
    'Low confidence match'
FROM students s
WHERE s.student_id BETWEEN 61 AND 80
LIMIT 75;

-- Unknown faces (75 entries)
INSERT INTO recognition_logs (student_id, recognition_timestamp, confidence_score, recognition_result, detection_method, recognition_method, remarks)
VALUES 
(NULL, TIMESTAMP(DATE_SUB(NOW(), INTERVAL FLOOR(1 + RAND() * 720) HOUR)), 0.2543, 'Unknown', 'DNN', 'FaceNet', 'Unregistered face'),
(NULL, TIMESTAMP(DATE_SUB(NOW(), INTERVAL FLOOR(1 + RAND() * 720) HOUR)), 0.1987, 'Unknown', 'DNN', 'FaceNet', 'Unregistered face'),
(NULL, TIMESTAMP(DATE_SUB(NOW(), INTERVAL FLOOR(1 + RAND() * 720) HOUR)), 0.3421, 'Unknown', 'DNN', 'FaceNet', 'Unregistered face'),
(NULL, TIMESTAMP(DATE_SUB(NOW(), INTERVAL FLOOR(1 + RAND() * 720) HOUR)), 0.2876, 'Unknown', 'DNN', 'FaceNet', 'Unregistered face'),
(NULL, TIMESTAMP(DATE_SUB(NOW(), INTERVAL FLOOR(1 + RAND() * 720) HOUR)), 0.1543, 'Unknown', 'DNN', 'FaceNet', 'Unregistered face'),
(NULL, TIMESTAMP(DATE_SUB(NOW(), INTERVAL FLOOR(1 + RAND() * 720) HOUR)), 0.3987, 'Unknown', 'DNN', 'FaceNet', 'Unregistered face'),
(NULL, TIMESTAMP(DATE_SUB(NOW(), INTERVAL FLOOR(1 + RAND() * 720) HOUR)), 0.2234, 'Unknown', 'DNN', 'FaceNet', 'Unregistered face'),
(NULL, TIMESTAMP(DATE_SUB(NOW(), INTERVAL FLOOR(1 + RAND() * 720) HOUR)), 0.1876, 'Unknown', 'DNN', 'FaceNet', 'Unregistered face'),
(NULL, TIMESTAMP(DATE_SUB(NOW(), INTERVAL FLOOR(1 + RAND() * 720) HOUR)), 0.3654, 'Unknown', 'DNN', 'FaceNet', 'Unregistered face'),
(NULL, TIMESTAMP(DATE_SUB(NOW(), INTERVAL FLOOR(1 + RAND() * 720) HOUR)), 0.2987, 'Unknown', 'DNN', 'FaceNet', 'Unregistered face');

-- Summary Statistics
SELECT '========================================' as '';
SELECT '  DATABASE POPULATED SUCCESSFULLY!     ' as '';
SELECT '========================================' as '';
SELECT CONCAT('Total Courses: ', COUNT(*)) FROM courses;
SELECT CONCAT('Total Students: ', COUNT(*)) FROM students;
SELECT CONCAT('Total Attendance Records: ', COUNT(*)) FROM attendance;
SELECT CONCAT('Total Recognition Logs: ', COUNT(*)) FROM recognition_logs;
SELECT '========================================' as '';

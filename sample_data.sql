-- Sample Data Generator for Face Recognition Attendance System
-- This script creates 500+ entries across all tables

USE attendance_system;

-- Clear existing data (optional - comment out if you want to keep existing data)
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE attendance;
TRUNCATE TABLE recognition_logs;
TRUNCATE TABLE face_embeddings;
TRUNCATE TABLE students;
TRUNCATE TABLE courses;
SET FOREIGN_KEY_CHECKS = 1;

-- ============================================
-- 1. INSERT COURSES (20 courses)
-- ============================================

INSERT INTO courses (course_id, course_code, course_name, department, credits) VALUES
(1, 'CS101', 'Computer Science 101', 'Computer Science', 4),
(2, 'CS201', 'Data Structures', 'Computer Science', 4),
(3, 'CS301', 'Database Management', 'Computer Science', 3),
(4, 'CS401', 'Machine Learning', 'Computer Science', 4),
(5, 'CS102', 'Web Development', 'Computer Science', 3),
(6, 'CS302', 'Software Engineering', 'Computer Science', 4),
(7, 'CS202', 'Computer Networks', 'Computer Science', 3),
(8, 'CS303', 'Operating Systems', 'Computer Science', 4),
(9, 'CS402', 'Artificial Intelligence', 'Computer Science', 4),
(10, 'CS203', 'Mobile App Development', 'Computer Science', 3),
(11, 'CS304', 'Cybersecurity Fundamentals', 'Computer Science', 3),
(12, 'CS403', 'Cloud Computing', 'Computer Science', 3),
(13, 'CS103', 'Computer Graphics', 'Computer Science', 3),
(14, 'CS204', 'Algorithm Design', 'Computer Science', 4),
(15, 'CS305', 'Human-Computer Interaction', 'Computer Science', 3),
(16, 'CS404', 'Digital Image Processing', 'Computer Science', 3),
(17, 'CS104', 'Internet of Things', 'Information Technology', 3),
(18, 'CS205', 'Blockchain Technology', 'Information Technology', 3),
(19, 'CS306', 'Natural Language Processing', 'Data Science', 4),
(20, 'CS405', 'Computer Vision', 'Data Science', 4);

-- ============================================
-- 2. INSERT STUDENTS (150 students)
-- ============================================

INSERT INTO students (student_id, admission_number, roll_number, full_name, course_id, semester, academic_year, email, phone) VALUES
(1, 'ADM2021001', 'CS2021001', 'Aiden Smith', 1, 6, '2021-2025', 'aiden.smith@university.edu', '+1-555-0101'),
(2, 'ADM2021002', 'CS2021002', 'Emma Johnson', 1, 6, '2021-2025', 'emma.johnson@university.edu', '+1-555-0102'),
(3, 'ADM2021003', 'CS2021003', 'Liam Williams', 1, 6, '2021-2025', 'liam.williams@university.edu', '+1-555-0103'),
(4, 'ADM2021004', 'CS2021004', 'Olivia Brown', 1, 6, '2021-2025', 'olivia.brown@university.edu', '+1-555-0104'),
(5, 'ADM2021005', 'CS2021005', 'Noah Jones', 1, 6, '2021-2025', 'noah.jones@university.edu', '+1-555-0105'),
(6, 'Ava Garcia', 'CS2021006', 'Computer Science', 'ava.garcia@university.edu', '+1-555-0106'),
(7, 'Ethan Miller', 'CS2021007', 'Computer Science', 'ethan.miller@university.edu', '+1-555-0107'),
(8, 'Sophia Davis', 'CS2021008', 'Computer Science', 'sophia.davis@university.edu', '+1-555-0108'),
(9, 'Mason Rodriguez', 'CS2021009', 'Computer Science', 'mason.rodriguez@university.edu', '+1-555-0109'),
(10, 'Isabella Martinez', 'CS2021010', 'Computer Science', 'isabella.martinez@university.edu', '+1-555-0110'),
(11, 'Logan Hernandez', 'CS2021011', 'Computer Science', 'logan.hernandez@university.edu', '+1-555-0111'),
(12, 'Mia Lopez', 'CS2021012', 'Computer Science', 'mia.lopez@university.edu', '+1-555-0112'),
(13, 'Lucas Gonzalez', 'CS2021013', 'Computer Science', 'lucas.gonzalez@university.edu', '+1-555-0113'),
(14, 'Charlotte Wilson', 'CS2021014', 'Computer Science', 'charlotte.wilson@university.edu', '+1-555-0114'),
(15, 'Alexander Anderson', 'CS2021015', 'Computer Science', 'alexander.anderson@university.edu', '+1-555-0115'),
(16, 'Amelia Thomas', 'CS2021016', 'Computer Science', 'amelia.thomas@university.edu', '+1-555-0116'),
(17, 'Benjamin Taylor', 'CS2021017', 'Computer Science', 'benjamin.taylor@university.edu', '+1-555-0117'),
(18, 'Harper Moore', 'CS2021018', 'Computer Science', 'harper.moore@university.edu', '+1-555-0118'),
(19, 'Elijah Jackson', 'CS2021019', 'Computer Science', 'elijah.jackson@university.edu', '+1-555-0119'),
(20, 'Evelyn Martin', 'CS2021020', 'Computer Science', 'evelyn.martin@university.edu', '+1-555-0120'),
(21, 'James Lee', 'CS2021021', 'Computer Science', 'james.lee@university.edu', '+1-555-0121'),
(22, 'Abigail Perez', 'CS2021022', 'Computer Science', 'abigail.perez@university.edu', '+1-555-0122'),
(23, 'William Thompson', 'CS2021023', 'Computer Science', 'william.thompson@university.edu', '+1-555-0123'),
(24, 'Emily White', 'CS2021024', 'Computer Science', 'emily.white@university.edu', '+1-555-0124'),
(25, 'Michael Harris', 'CS2021025', 'Computer Science', 'michael.harris@university.edu', '+1-555-0125'),
(26, 'Elizabeth Clark', 'CS2021026', 'Computer Science', 'elizabeth.clark@university.edu', '+1-555-0126'),
(27, 'Daniel Lewis', 'CS2021027', 'Computer Science', 'daniel.lewis@university.edu', '+1-555-0127'),
(28, 'Sofia Robinson', 'CS2021028', 'Computer Science', 'sofia.robinson@university.edu', '+1-555-0128'),
(29, 'Matthew Walker', 'CS2021029', 'Computer Science', 'matthew.walker@university.edu', '+1-555-0129'),
(30, 'Avery Hall', 'CS2021030', 'Computer Science', 'avery.hall@university.edu', '+1-555-0130'),
(31, 'Henry Allen', 'CS2021031', 'Information Technology', 'henry.allen@university.edu', '+1-555-0131'),
(32, 'Ella Young', 'CS2021032', 'Information Technology', 'ella.young@university.edu', '+1-555-0132'),
(33, 'Jackson King', 'CS2021033', 'Information Technology', 'jackson.king@university.edu', '+1-555-0133'),
(34, 'Scarlett Wright', 'CS2021034', 'Information Technology', 'scarlett.wright@university.edu', '+1-555-0134'),
(35, 'Sebastian Lopez', 'CS2021035', 'Information Technology', 'sebastian.lopez@university.edu', '+1-555-0135'),
(36, 'Victoria Hill', 'CS2021036', 'Information Technology', 'victoria.hill@university.edu', '+1-555-0136'),
(37, 'Jack Scott', 'CS2021037', 'Information Technology', 'jack.scott@university.edu', '+1-555-0137'),
(38, 'Grace Green', 'CS2021038', 'Information Technology', 'grace.green@university.edu', '+1-555-0138'),
(39, 'Owen Adams', 'CS2021039', 'Information Technology', 'owen.adams@university.edu', '+1-555-0139'),
(40, 'Chloe Baker', 'CS2021040', 'Information Technology', 'chloe.baker@university.edu', '+1-555-0140'),
(41, 'Wyatt Gonzales', 'CS2021041', 'Information Technology', 'wyatt.gonzales@university.edu', '+1-555-0141'),
(42, 'Lily Nelson', 'CS2021042', 'Information Technology', 'lily.nelson@university.edu', '+1-555-0142'),
(43, 'Luke Carter', 'CS2021043', 'Information Technology', 'luke.carter@university.edu', '+1-555-0143'),
(44, 'Zoey Mitchell', 'CS2021044', 'Information Technology', 'zoey.mitchell@university.edu', '+1-555-0144'),
(45, 'Jayden Perez', 'CS2021045', 'Information Technology', 'jayden.perez@university.edu', '+1-555-0145'),
(46, 'Hannah Roberts', 'CS2021046', 'Information Technology', 'hannah.roberts@university.edu', '+1-555-0146'),
(47, 'Ryan Turner', 'CS2021047', 'Information Technology', 'ryan.turner@university.edu', '+1-555-0147'),
(48, 'Layla Phillips', 'CS2021048', 'Information Technology', 'layla.phillips@university.edu', '+1-555-0148'),
(49, 'Nathan Campbell', 'CS2021049', 'Information Technology', 'nathan.campbell@university.edu', '+1-555-0149'),
(50, 'Nora Parker', 'CS2021050', 'Information Technology', 'nora.parker@university.edu', '+1-555-0150'),
(51, 'Isaac Evans', 'CS2021051', 'Software Engineering', 'isaac.evans@university.edu', '+1-555-0151'),
(52, 'Addison Edwards', 'CS2021052', 'Software Engineering', 'addison.edwards@university.edu', '+1-555-0152'),
(53, 'Gabriel Collins', 'CS2021053', 'Software Engineering', 'gabriel.collins@university.edu', '+1-555-0153'),
(54, 'Audrey Stewart', 'CS2021054', 'Software Engineering', 'audrey.stewart@university.edu', '+1-555-0154'),
(55, 'Anthony Sanchez', 'CS2021055', 'Software Engineering', 'anthony.sanchez@university.edu', '+1-555-0155'),
(56, 'Bella Morris', 'CS2021056', 'Software Engineering', 'bella.morris@university.edu', '+1-555-0156'),
(57, 'Dylan Rogers', 'CS2021057', 'Software Engineering', 'dylan.rogers@university.edu', '+1-555-0157'),
(58, 'Aria Reed', 'CS2021058', 'Software Engineering', 'aria.reed@university.edu', '+1-555-0158'),
(59, 'Lincoln Cook', 'CS2021059', 'Software Engineering', 'lincoln.cook@university.edu', '+1-555-0159'),
(60, 'Elena Morgan', 'CS2021060', 'Software Engineering', 'elena.morgan@university.edu', '+1-555-0160'),
(61, 'Joshua Bell', 'CS2021061', 'Software Engineering', 'joshua.bell@university.edu', '+1-555-0161'),
(62, 'Penelope Murphy', 'CS2021062', 'Software Engineering', 'penelope.murphy@university.edu', '+1-555-0162'),
(63, 'Andrew Bailey', 'CS2021063', 'Software Engineering', 'andrew.bailey@university.edu', '+1-555-0163'),
(64, 'Aurora Rivera', 'CS2021064', 'Software Engineering', 'aurora.rivera@university.edu', '+1-555-0164'),
(65, 'Samuel Cooper', 'CS2021065', 'Software Engineering', 'samuel.cooper@university.edu', '+1-555-0165'),
(66, 'Claire Richardson', 'CS2021066', 'Software Engineering', 'claire.richardson@university.edu', '+1-555-0166'),
(67, 'Christopher Cox', 'CS2021067', 'Software Engineering', 'christopher.cox@university.edu', '+1-555-0167'),
(68, 'Lucy Howard', 'CS2021068', 'Software Engineering', 'lucy.howard@university.edu', '+1-555-0168'),
(69, 'Jose Ward', 'CS2021069', 'Software Engineering', 'jose.ward@university.edu', '+1-555-0169'),
(70, 'Hazel Torres', 'CS2021070', 'Software Engineering', 'hazel.torres@university.edu', '+1-555-0170'),
(71, 'David Peterson', 'CS2021071', 'Data Science', 'david.peterson@university.edu', '+1-555-0171'),
(72, 'Maya Gray', 'CS2021072', 'Data Science', 'maya.gray@university.edu', '+1-555-0172'),
(73, 'Carter Ramirez', 'CS2021073', 'Data Science', 'carter.ramirez@university.edu', '+1-555-0173'),
(74, 'Violet James', 'CS2021074', 'Data Science', 'violet.james@university.edu', '+1-555-0174'),
(75, 'Wyatt Watson', 'CS2021075', 'Data Science', 'wyatt.watson@university.edu', '+1-555-0175'),
(76, 'Savannah Brooks', 'CS2021076', 'Data Science', 'savannah.brooks@university.edu', '+1-555-0176'),
(77, 'John Kelly', 'CS2021077', 'Data Science', 'john.kelly@university.edu', '+1-555-0177'),
(78, 'Brooklyn Sanders', 'CS2021078', 'Data Science', 'brooklyn.sanders@university.edu', '+1-555-0178'),
(79, 'Julian Price', 'CS2021079', 'Data Science', 'julian.price@university.edu', '+1-555-0179'),
(80, 'Leah Bennett', 'CS2021080', 'Data Science', 'leah.bennett@university.edu', '+1-555-0180'),
(81, 'Levi Wood', 'CS2021081', 'Data Science', 'levi.wood@university.edu', '+1-555-0181'),
(82, 'Paisley Barnes', 'CS2021082', 'Data Science', 'paisley.barnes@university.edu', '+1-555-0182'),
(83, 'Mateo Ross', 'CS2021083', 'Data Science', 'mateo.ross@university.edu', '+1-555-0183'),
(84, 'Natalie Henderson', 'CS2021084', 'Data Science', 'natalie.henderson@university.edu', '+1-555-0184'),
(85, 'Asher Coleman', 'CS2021085', 'Data Science', 'asher.coleman@university.edu', '+1-555-0185'),
(86, 'Anna Jenkins', 'CS2021086', 'Data Science', 'anna.jenkins@university.edu', '+1-555-0186'),
(87, 'Jaxon Perry', 'CS2021087', 'Data Science', 'jaxon.perry@university.edu', '+1-555-0187'),
(88, 'Samantha Powell', 'CS2021088', 'Data Science', 'samantha.powell@university.edu', '+1-555-0188'),
(89, 'Jordan Long', 'CS2021089', 'Data Science', 'jordan.long@university.edu', '+1-555-0189'),
(90, 'Sarah Patterson', 'CS2021090', 'Data Science', 'sarah.patterson@university.edu', '+1-555-0190'),
(91, 'Cameron Hughes', 'CS2021091', 'Cybersecurity', 'cameron.hughes@university.edu', '+1-555-0191'),
(92, 'Kennedy Flores', 'CS2021092', 'Cybersecurity', 'kennedy.flores@university.edu', '+1-555-0192'),
(93, 'Adrian Washington', 'CS2021093', 'Cybersecurity', 'adrian.washington@university.edu', '+1-555-0193'),
(94, 'Madelyn Butler', 'CS2021094', 'Cybersecurity', 'madelyn.butler@university.edu', '+1-555-0194'),
(95, 'Ezra Simmons', 'CS2021095', 'Cybersecurity', 'ezra.simmons@university.edu', '+1-555-0195'),
(96, 'Ruby Foster', 'CS2021096', 'Cybersecurity', 'ruby.foster@university.edu', '+1-555-0196'),
(97, 'Thomas Gonzales', 'CS2021097', 'Cybersecurity', 'thomas.gonzales@university.edu', '+1-555-0197'),
(98, 'Skylar Bryant', 'CS2021098', 'Cybersecurity', 'skylar.bryant@university.edu', '+1-555-0198'),
(99, 'Charles Alexander', 'CS2021099', 'Cybersecurity', 'charles.alexander@university.edu', '+1-555-0199'),
(100, 'Alice Russell', 'CS2021100', 'Cybersecurity', 'alice.russell@university.edu', '+1-555-0200'),
(101, 'Caleb Griffin', 'CS2022001', 'Computer Science', 'caleb.griffin@university.edu', '+1-555-0201'),
(102, 'Violet Diaz', 'CS2022002', 'Computer Science', 'violet.diaz2@university.edu', '+1-555-0202'),
(103, 'Eli Hayes', 'CS2022003', 'Computer Science', 'eli.hayes@university.edu', '+1-555-0203'),
(104, 'Aubrey Myers', 'CS2022004', 'Computer Science', 'aubrey.myers@university.edu', '+1-555-0204'),
(105, 'Jonathan Ford', 'CS2022005', 'Computer Science', 'jonathan.ford@university.edu', '+1-555-0205'),
(106, 'Quinn Hamilton', 'CS2022006', 'Computer Science', 'quinn.hamilton@university.edu', '+1-555-0206'),
(107, 'Colton Graham', 'CS2022007', 'Computer Science', 'colton.graham@university.edu', '+1-555-0207'),
(108, 'Piper Sullivan', 'CS2022008', 'Computer Science', 'piper.sullivan@university.edu', '+1-555-0208'),
(109, 'Dominic Wallace', 'CS2022009', 'Computer Science', 'dominic.wallace@university.edu', '+1-555-0209'),
(110, 'Stella Woods', 'CS2022010', 'Computer Science', 'stella.woods@university.edu', '+1-555-0210'),
(111, 'Aaron Cole', 'CS2022011', 'Information Technology', 'aaron.cole@university.edu', '+1-555-0211'),
(112, 'Caroline West', 'CS2022012', 'Information Technology', 'caroline.west@university.edu', '+1-555-0212'),
(113, 'Ian Jordan', 'CS2022013', 'Information Technology', 'ian.jordan@university.edu', '+1-555-0213'),
(114, 'Ellie Owens', 'CS2022014', 'Information Technology', 'ellie.owens@university.edu', '+1-555-0214'),
(115, 'Ezekiel Reynolds', 'CS2022015', 'Information Technology', 'ezekiel.reynolds@university.edu', '+1-555-0215'),
(116, 'Eva Fisher', 'CS2022016', 'Information Technology', 'eva.fisher@university.edu', '+1-555-0216'),
(117, 'Landon Ellis', 'CS2022017', 'Information Technology', 'landon.ellis@university.edu', '+1-555-0217'),
(118, 'Ivy Harper', 'CS2022018', 'Information Technology', 'ivy.harper@university.edu', '+1-555-0218'),
(119, 'Hunter Mason', 'CS2022019', 'Information Technology', 'hunter.mason@university.edu', '+1-555-0219'),
(120, 'Emilia Dixon', 'CS2022020', 'Information Technology', 'emilia.dixon@university.edu', '+1-555-0220'),
(121, 'Kayden Hunt', 'CS2022021', 'Software Engineering', 'kayden.hunt@university.edu', '+1-555-0221'),
(122, 'Josephine Palmer', 'CS2022022', 'Software Engineering', 'josephine.palmer@university.edu', '+1-555-0222'),
(123, 'Parker Robertson', 'CS2022023', 'Software Engineering', 'parker.robertson@university.edu', '+1-555-0223'),
(124, 'Delilah Black', 'CS2022024', 'Software Engineering', 'delilah.black@university.edu', '+1-555-0224'),
(125, 'Roman Crawford', 'CS2022025', 'Software Engineering', 'roman.crawford@university.edu', '+1-555-0225'),
(126, 'Iris Holmes', 'CS2022026', 'Software Engineering', 'iris.holmes@university.edu', '+1-555-0226'),
(127, 'Everett Rose', 'CS2022027', 'Software Engineering', 'everett.rose@university.edu', '+1-555-0227'),
(128, 'Clara Stone', 'CS2022028', 'Software Engineering', 'clara.stone@university.edu', '+1-555-0228'),
(129, 'Wesley Meyer', 'CS2022029', 'Software Engineering', 'wesley.meyer@university.edu', '+1-555-0229'),
(130, 'Willow Dunn', 'CS2022030', 'Software Engineering', 'willow.dunn@university.edu', '+1-555-0230'),
(131, 'Jameson Rice', 'CS2022031', 'Data Science', 'jameson.rice@university.edu', '+1-555-0231'),
(132, 'Athena Knight', 'CS2022032', 'Data Science', 'athena.knight@university.edu', '+1-555-0232'),
(133, 'Blake Mills', 'CS2022033', 'Data Science', 'blake.mills@university.edu', '+1-555-0233'),
(134, 'Isla Boyd', 'CS2022034', 'Data Science', 'isla.boyd@university.edu', '+1-555-0234'),
(135, 'Miles Armstrong', 'CS2022035', 'Data Science', 'miles.armstrong@university.edu', '+1-555-0235'),
(136, 'Julia Webb', 'CS2022036', 'Data Science', 'julia.webb@university.edu', '+1-555-0236'),
(137, 'Sawyer Spencer', 'CS2022037', 'Data Science', 'sawyer.spencer@university.edu', '+1-555-0237'),
(138, 'Reagan Pierce', 'CS2022038', 'Data Science', 'reagan.pierce@university.edu', '+1-555-0238'),
(139, 'Jason Wells', 'CS2022039', 'Data Science', 'jason.wells@university.edu', '+1-555-0239'),
(140, 'Sophie Fields', 'CS2022040', 'Data Science', 'sophie.fields@university.edu', '+1-555-0240'),
(141, 'Silas Knight', 'CS2022041', 'Cybersecurity', 'silas.knight@university.edu', '+1-555-0241'),
(142, 'Adalyn Webb', 'CS2022042', 'Cybersecurity', 'adalyn.webb@university.edu', '+1-555-0242'),
(143, 'Bennett Ford', 'CS2022043', 'Cybersecurity', 'bennett.ford@university.edu', '+1-555-0243'),
(144, 'Gianna Day', 'CS2022044', 'Cybersecurity', 'gianna.day@university.edu', '+1-555-0244'),
(145, 'Declan Fox', 'CS2022045', 'Cybersecurity', 'declan.fox@university.edu', '+1-555-0245'),
(146, 'Margot Howard', 'CS2022046', 'Cybersecurity', 'margot.howard@university.edu', '+1-555-0246'),
(147, 'Greyson Dean', 'CS2022047', 'Cybersecurity', 'greyson.dean@university.edu', '+1-555-0247'),
(148, 'Serenity Gilbert', 'CS2022048', 'Cybersecurity', 'serenity.gilbert@university.edu', '+1-555-0248'),
(149, 'Ryder Brooks', 'CS2022049', 'Cybersecurity', 'ryder.brooks@university.edu', '+1-555-0249'),
(150, 'Valentina Armstrong', 'CS2022050', 'Cybersecurity', 'valentina.armstrong@university.edu', '+1-555-0250');

-- ============================================
-- 3. INSERT ATTENDANCE RECORDS (600+ entries)
-- ============================================

-- Generate attendance for the last 30 days across different courses
-- This will create attendance patterns for students

-- Morning sessions (9:00 AM - 12:00 PM) - Courses 1, 5, 9, 11, 13, 17, 19
INSERT INTO attendance (student_id, course_id, date, time, status, session_type) 
SELECT 
    s.student_id,
    c.course_id,
    DATE_SUB(CURDATE(), INTERVAL FLOOR(RAND() * 30) DAY) as date,
    TIME(CONCAT(8 + FLOOR(RAND() * 4), ':', LPAD(FLOOR(RAND() * 60), 2, '0'), ':00')) as time,
    CASE 
        WHEN RAND() > 0.15 THEN 'Present'
        WHEN RAND() > 0.5 THEN 'Absent'
        ELSE 'Late'
    END as status,
    CASE 
        WHEN FLOOR(RAND() * 2) = 0 THEN 'Morning'
        ELSE 'Afternoon'
    END as session_type
FROM students s
CROSS JOIN (SELECT course_id FROM courses WHERE course_id IN (1, 5, 9, 11, 13, 17, 19)) c
WHERE s.student_id <= 50
LIMIT 200;

-- Afternoon sessions (1:00 PM - 5:00 PM) - Courses 3, 4, 6, 7, 12, 15, 16, 20
INSERT INTO attendance (student_id, course_id, date, time, status, session_type)
SELECT 
    s.student_id,
    c.course_id,
    DATE_SUB(CURDATE(), INTERVAL FLOOR(RAND() * 30) DAY) as date,
    TIME(CONCAT(13 + FLOOR(RAND() * 4), ':', LPAD(FLOOR(RAND() * 60), 2, '0'), ':00')) as time,
    CASE 
        WHEN RAND() > 0.12 THEN 'Present'
        WHEN RAND() > 0.6 THEN 'Absent'
        ELSE 'Late'
    END as status,
    'Afternoon' as session_type
FROM students s
CROSS JOIN (SELECT course_id FROM courses WHERE course_id IN (3, 4, 6, 7, 12, 15, 16, 20)) c
WHERE s.student_id BETWEEN 51 AND 100
LIMIT 200;

-- Evening/Lab sessions - Courses 2, 8, 10, 14, 18
INSERT INTO attendance (student_id, course_id, date, time, status, session_type)
SELECT 
    s.student_id,
    c.course_id,
    DATE_SUB(CURDATE(), INTERVAL FLOOR(RAND() * 25) DAY) as date,
    TIME(CONCAT(10 + FLOOR(RAND() * 6), ':', LPAD(FLOOR(RAND() * 60), 2, '0'), ':00')) as time,
    CASE 
        WHEN RAND() > 0.10 THEN 'Present'
        WHEN RAND() > 0.7 THEN 'Absent'
        ELSE 'Late'
    END as status,
    CASE 
        WHEN FLOOR(RAND() * 3) = 0 THEN 'Morning'
        WHEN FLOOR(RAND() * 3) = 1 THEN 'Afternoon'
        ELSE 'Evening'
    END as session_type
FROM students s
CROSS JOIN (SELECT course_id FROM courses WHERE course_id IN (2, 8, 10, 14, 18)) c
WHERE s.student_id BETWEEN 101 AND 150
LIMIT 250;

-- ============================================
-- 4. INSERT RECOGNITION LOGS (500+ entries)
-- ============================================

-- Successful recognitions (70%)
INSERT INTO recognition_logs (student_id, timestamp, confidence, result, session_type)
SELECT 
    s.student_id,
    TIMESTAMP(DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 720) HOUR), TIME(CONCAT(8 + FLOOR(RAND() * 10), ':', LPAD(FLOOR(RAND() * 60), 2, '0'), ':', LPAD(FLOOR(RAND() * 60), 2, '0')))) as timestamp,
    ROUND(0.75 + (RAND() * 0.24), 4) as confidence,
    'RECOGNIZED' as result,
    CASE 
        WHEN FLOOR(RAND() * 3) = 0 THEN 'Morning'
        WHEN FLOOR(RAND() * 3) = 1 THEN 'Afternoon'
        ELSE 'Evening'
    END as session_type
FROM students s
WHERE s.student_id <= 105
LIMIT 350;

-- Failed recognitions (15%)
INSERT INTO recognition_logs (student_id, timestamp, confidence, result, session_type)
SELECT 
    s.student_id,
    TIMESTAMP(DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 720) HOUR), TIME(CONCAT(8 + FLOOR(RAND() * 10), ':', LPAD(FLOOR(RAND() * 60), 2, '0'), ':', LPAD(FLOOR(RAND() * 60), 2, '0')))) as timestamp,
    ROUND(0.30 + (RAND() * 0.29), 4) as confidence,
    'NOT_RECOGNIZED' as result,
    CASE 
        WHEN FLOOR(RAND() * 3) = 0 THEN 'Morning'
        WHEN FLOOR(RAND() * 3) = 1 THEN 'Afternoon'
        ELSE 'Evening'
    END as session_type
FROM students s
WHERE s.student_id BETWEEN 106 AND 130
LIMIT 75;

-- Unknown faces (15%)
INSERT INTO recognition_logs (student_id, timestamp, confidence, result, session_type)
VALUES 
(NULL, TIMESTAMP(DATE_SUB(NOW(), INTERVAL 1 DAY)), 0.2543, 'UNKNOWN', 'Morning'),
(NULL, TIMESTAMP(DATE_SUB(NOW(), INTERVAL 2 DAY)), 0.1987, 'UNKNOWN', 'Afternoon'),
(NULL, TIMESTAMP(DATE_SUB(NOW(), INTERVAL 3 DAY)), 0.3421, 'UNKNOWN', 'Evening'),
(NULL, TIMESTAMP(DATE_SUB(NOW(), INTERVAL 4 DAY)), 0.2876, 'UNKNOWN', 'Morning'),
(NULL, TIMESTAMP(DATE_SUB(NOW(), INTERVAL 5 DAY)), 0.1543, 'UNKNOWN', 'Afternoon'),
(NULL, TIMESTAMP(DATE_SUB(NOW(), INTERVAL 6 DAY)), 0.3987, 'UNKNOWN', 'Evening'),
(NULL, TIMESTAMP(DATE_SUB(NOW(), INTERVAL 7 DAY)), 0.2234, 'UNKNOWN', 'Morning'),
(NULL, TIMESTAMP(DATE_SUB(NOW(), INTERVAL 8 DAY)), 0.1876, 'UNKNOWN', 'Afternoon'),
(NULL, TIMESTAMP(DATE_SUB(NOW(), INTERVAL 9 DAY)), 0.3654, 'UNKNOWN', 'Evening'),
(NULL, TIMESTAMP(DATE_SUB(NOW(), INTERVAL 10 DAY)), 0.2987, 'UNKNOWN', 'Morning'),
(NULL, TIMESTAMP(DATE_SUB(NOW(), INTERVAL 11 DAY)), 0.4123, 'UNKNOWN', 'Afternoon'),
(NULL, TIMESTAMP(DATE_SUB(NOW(), INTERVAL 12 DAY)), 0.1654, 'UNKNOWN', 'Evening'),
(NULL, TIMESTAMP(DATE_SUB(NOW(), INTERVAL 13 DAY)), 0.3245, 'UNKNOWN', 'Morning'),
(NULL, TIMESTAMP(DATE_SUB(NOW(), INTERVAL 14 DAY)), 0.2456, 'UNKNOWN', 'Afternoon'),
(NULL, TIMESTAMP(DATE_SUB(NOW(), INTERVAL 15 DAY)), 0.1987, 'UNKNOWN', 'Evening'),
(NULL, TIMESTAMP(DATE_SUB(NOW(), INTERVAL 16 DAY)), 0.3876, 'UNKNOWN', 'Morning'),
(NULL, TIMESTAMP(DATE_SUB(NOW(), INTERVAL 17 DAY)), 0.2123, 'UNKNOWN', 'Afternoon'),
(NULL, TIMESTAMP(DATE_SUB(NOW(), INTERVAL 18 DAY)), 0.3567, 'UNKNOWN', 'Evening'),
(NULL, TIMESTAMP(DATE_SUB(NOW(), INTERVAL 19 DAY)), 0.1789, 'UNKNOWN', 'Morning'),
(NULL, TIMESTAMP(DATE_SUB(NOW(), INTERVAL 20 DAY)), 0.4234, 'UNKNOWN', 'Afternoon'),
(NULL, TIMESTAMP(DATE_SUB(NOW(), INTERVAL 21 DAY)), 0.2678, 'UNKNOWN', 'Evening'),
(NULL, TIMESTAMP(DATE_SUB(NOW(), INTERVAL 22 DAY)), 0.1456, 'UNKNOWN', 'Morning'),
(NULL, TIMESTAMP(DATE_SUB(NOW(), INTERVAL 23 DAY)), 0.3987, 'UNKNOWN', 'Afternoon'),
(NULL, TIMESTAMP(DATE_SUB(NOW(), INTERVAL 24 DAY)), 0.2234, 'UNKNOWN', 'Evening'),
(NULL, TIMESTAMP(DATE_SUB(NOW(), INTERVAL 25 DAY)), 0.3456, 'UNKNOWN', 'Morning'),
(NULL, TIMESTAMP(DATE_SUB(NOW(), INTERVAL 26 DAY)), 0.1876, 'UNKNOWN', 'Afternoon'),
(NULL, TIMESTAMP(DATE_SUB(NOW(), INTERVAL 27 DAY)), 0.4123, 'UNKNOWN', 'Evening'),
(NULL, TIMESTAMP(DATE_SUB(NOW(), INTERVAL 28 DAY)), 0.2987, 'UNKNOWN', 'Morning'),
(NULL, TIMESTAMP(DATE_SUB(NOW(), INTERVAL 29 DAY)), 0.1654, 'UNKNOWN', 'Afternoon'),
(NULL, TIMESTAMP(DATE_SUB(NOW(), INTERVAL 30 DAY)), 0.3789, 'UNKNOWN', 'Evening');

-- Add more recognition logs for today and yesterday (recent activity)
INSERT INTO recognition_logs (student_id, timestamp, confidence, result, session_type)
SELECT 
    s.student_id,
    TIMESTAMP(CURDATE(), TIME(CONCAT(8 + FLOOR(RAND() * 8), ':', LPAD(FLOOR(RAND() * 60), 2, '0'), ':', LPAD(FLOOR(RAND() * 60), 2, '0')))) as timestamp,
    ROUND(0.80 + (RAND() * 0.19), 4) as confidence,
    'RECOGNIZED' as result,
    CASE 
        WHEN HOUR(CURTIME()) < 12 THEN 'Morning'
        WHEN HOUR(CURTIME()) < 17 THEN 'Afternoon'
        ELSE 'Evening'
    END as session_type
FROM students s
WHERE s.student_id <= 45
LIMIT 45;

-- ============================================
-- 5. SUMMARY STATISTICS
-- ============================================

SELECT '==================================' as '';
SELECT 'DATABASE POPULATED SUCCESSFULLY!' as '';
SELECT '==================================' as '';
SELECT '' as '';
SELECT 'DATA SUMMARY:' as '';
SELECT '-----------' as '';
SELECT CONCAT('Courses: ', COUNT(*)) as stat FROM courses;
SELECT CONCAT('Students: ', COUNT(*)) as stat FROM students;
SELECT CONCAT('Attendance Records: ', COUNT(*)) as stat FROM attendance;
SELECT CONCAT('Recognition Logs: ', COUNT(*)) as stat FROM recognition_logs;
SELECT '' as '';
SELECT 'ATTENDANCE BREAKDOWN:' as '';
SELECT '-----------' as '';
SELECT status, COUNT(*) as count FROM attendance GROUP BY status;
SELECT '' as '';
SELECT 'RECOGNITION BREAKDOWN:' as '';
SELECT '-----------' as '';
SELECT result, COUNT(*) as count FROM recognition_logs GROUP BY result;
SELECT '' as '';
SELECT 'TOP 10 STUDENTS BY ATTENDANCE:' as '';
SELECT '-----------' as '';
SELECT s.name, s.roll_number, COUNT(a.attendance_id) as attendance_count
FROM students s
LEFT JOIN attendance a ON s.student_id = a.student_id
GROUP BY s.student_id, s.name, s.roll_number
ORDER BY attendance_count DESC
LIMIT 10;
SELECT '' as '';
SELECT '==================================' as '';
SELECT 'Ready to use with GUI application!' as '';
SELECT '==================================' as '';

-- Additional Sample Data to reach 500+ entries
USE attendance_system;

-- Add more attendance records (400+ more to reach 500+) - with IGNORE for duplicates
INSERT IGNORE INTO attendance (student_id, course_id, attendance_date, attendance_time, status, session_type, remarks)
SELECT 
    FLOOR(1 + (RAND() * 84)) as student_id,
    FLOOR(1 + (RAND() * 20)) as course_id,
    DATE_SUB(CURDATE(), INTERVAL FLOOR(1 + (RAND() * 30)) DAY),
    TIME(CONCAT(8 + FLOOR(RAND() * 10), ':', LPAD(FLOOR(RAND() * 60), 2, '0'), ':00')),
    CASE 
        WHEN RAND() < 0.80 THEN 'Present'
        WHEN RAND() < 0.90 THEN 'Late'
        ELSE 'Absent'
    END,
    CASE 
        WHEN FLOOR(RAND() * 3) = 0 THEN 'Morning'
        WHEN FLOOR(RAND() * 3) = 1 THEN 'Afternoon'
        ELSE 'Evening'
    END,
    'Face recognition attendance'
FROM 
    (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION 
     SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) t1,
    (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION 
     SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) t2,
    (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) t3
LIMIT 450;

-- Add more recognition logs (400+ more to reach 500+)
INSERT INTO recognition_logs (student_id, recognition_timestamp, confidence_score, recognition_result, detection_method, recognition_method, remarks)
SELECT 
    FLOOR(1 + (RAND() * 84)) as student_id,
    TIMESTAMP(DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 720) HOUR)),
    ROUND(0.70 + (RAND() * 0.29), 4),
    CASE 
        WHEN RAND() < 0.85 THEN 'Success'
        WHEN RAND() < 0.95 THEN 'Failed'
        ELSE 'Unknown'
    END,
    'DNN',
    'FaceNet',
    'Automated recognition'
FROM 
    (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION 
     SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) t1,
    (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION 
     SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) t2,
    (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) t3
LIMIT 420;

-- Summary
SELECT '========================================' as '';
SELECT '  ADDITIONAL DATA LOADED SUCCESSFULLY!  ' as '';
SELECT '========================================' as '';
SELECT CONCAT('Total Courses: ', COUNT(*)) FROM courses;
SELECT CONCAT('Total Students: ', COUNT(*)) FROM students;
SELECT CONCAT('Total Attendance Records: ', COUNT(*)) FROM attendance;
SELECT CONCAT('Total Recognition Logs: ', COUNT(*)) FROM recognition_logs;
SELECT '========================================' as '';
SELECT 'Attendance Status Breakdown:' as '';
SELECT status, COUNT(*) as count FROM attendance GROUP BY status;
SELECT '' as '';
SELECT 'Recognition Result Breakdown:' as '';
SELECT recognition_result, COUNT(*) as count FROM recognition_logs GROUP BY recognition_result;
SELECT '========================================' as '';

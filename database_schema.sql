-- =====================================================
-- Facial Recognition Based Smart Attendance System
-- Database Schema
-- =====================================================

-- Create database
CREATE DATABASE IF NOT EXISTS attendance_system;
USE attendance_system;

-- =====================================================
-- Table: courses
-- Description: Stores academic program information
-- =====================================================
CREATE TABLE IF NOT EXISTS courses (
    course_id INT AUTO_INCREMENT PRIMARY KEY,
    course_code VARCHAR(20) NOT NULL UNIQUE,
    course_name VARCHAR(100) NOT NULL,
    department VARCHAR(100),
    credits INT DEFAULT 3,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_course_code (course_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- Table: students
-- Description: Stores student profile information
-- =====================================================
CREATE TABLE IF NOT EXISTS students (
    student_id INT AUTO_INCREMENT PRIMARY KEY,
    admission_number VARCHAR(50) NOT NULL UNIQUE,
    roll_number VARCHAR(50) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    course_id INT NOT NULL,
    semester INT NOT NULL,
    academic_year VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    facial_data_path VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE RESTRICT,
    INDEX idx_admission_number (admission_number),
    INDEX idx_roll_number (roll_number),
    INDEX idx_full_name (full_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- Table: attendance
-- Description: Stores daily attendance records
-- =====================================================
CREATE TABLE IF NOT EXISTS attendance (
    attendance_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    course_id INT NOT NULL,
    attendance_date DATE NOT NULL,
    attendance_time TIME NOT NULL,
    session_type ENUM('Morning', 'Afternoon', 'Evening', 'Full Day') DEFAULT 'Full Day',
    status ENUM('Present', 'Absent', 'Late', 'Excused') DEFAULT 'Present',
    marked_by VARCHAR(50) DEFAULT 'Face Recognition System',
    remarks TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE RESTRICT,
    UNIQUE KEY unique_attendance (student_id, course_id, attendance_date, session_type),
    INDEX idx_student_date (student_id, attendance_date),
    INDEX idx_course_date (course_id, attendance_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- Table: facial_training_data
-- Description: Stores facial recognition training metadata
-- =====================================================
CREATE TABLE IF NOT EXISTS facial_training_data (
    training_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    training_file_path VARCHAR(255) NOT NULL,
    image_count INT DEFAULT 0,
    training_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_trained BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    INDEX idx_student_training (student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- Table: face_embeddings
-- Description: Stores deep learning face embeddings for recognition
-- =====================================================
CREATE TABLE IF NOT EXISTS face_embeddings (
    embedding_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    embedding_vector BLOB NOT NULL,
    embedding_model VARCHAR(50) DEFAULT 'FaceNet',
    embedding_dimension INT DEFAULT 128,
    image_source VARCHAR(255),
    quality_score FLOAT DEFAULT 0.0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    INDEX idx_student_embedding (student_id),
    INDEX idx_model (embedding_model)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- Table: recognition_logs
-- Description: Stores logs of all face recognition attempts
-- =====================================================
CREATE TABLE IF NOT EXISTS recognition_logs (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT,
    recognition_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    confidence_score FLOAT NOT NULL,
    recognition_result ENUM('Success', 'Failed', 'Unknown') DEFAULT 'Unknown',
    detection_method VARCHAR(50) DEFAULT 'DNN',
    recognition_method VARCHAR(50) DEFAULT 'FaceNet',
    camera_id VARCHAR(50),
    location VARCHAR(100),
    remarks TEXT,
    INDEX idx_timestamp (recognition_timestamp),
    INDEX idx_student (student_id),
    INDEX idx_result (recognition_result),
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- Insert Sample Data
-- =====================================================

-- Insert sample courses
INSERT INTO courses (course_code, course_name, department, credits) VALUES
('CSE101', 'Computer Science Engineering', 'Engineering', 4),
('IT102', 'Information Technology', 'Engineering', 4),
('ECE103', 'Electronics and Communication', 'Engineering', 4),
('MBA104', 'Master of Business Administration', 'Management', 3),
('BCA105', 'Bachelor of Computer Applications', 'Computer Science', 3)
ON DUPLICATE KEY UPDATE course_name=VALUES(course_name);

-- Insert sample students (at least 10 students)
INSERT INTO students (admission_number, roll_number, full_name, course_id, semester, academic_year, email, phone, is_active) VALUES
('ADM2024001', 'CSE2024001', 'Rahul Sharma', 1, 5, '2024-25', 'rahul.sharma@university.edu', '+91-9876543210', TRUE),
('ADM2024002', 'CSE2024002', 'Priya Singh', 1, 5, '2024-25', 'priya.singh@university.edu', '+91-9876543211', TRUE),
('ADM2024003', 'IT2024001', 'Amit Kumar', 2, 3, '2024-25', 'amit.kumar@university.edu', '+91-9876543212', TRUE),
('ADM2024004', 'IT2024002', 'Sneha Patel', 2, 3, '2024-25', 'sneha.patel@university.edu', '+91-9876543213', TRUE),
('ADM2024005', 'ECE2024001', 'Vikram Reddy', 3, 7, '2024-25', 'vikram.reddy@university.edu', '+91-9876543214', TRUE),
('ADM2024006', 'ECE2024002', 'Ananya Iyer', 3, 7, '2024-25', 'ananya.iyer@university.edu', '+91-9876543215', TRUE),
('ADM2024007', 'MBA2024001', 'Karthik Menon', 4, 2, '2024-25', 'karthik.menon@university.edu', '+91-9876543216', TRUE),
('ADM2024008', 'MBA2024002', 'Divya Nair', 4, 2, '2024-25', 'divya.nair@university.edu', '+91-9876543217', TRUE),
('ADM2024009', 'BCA2024001', 'Rohan Gupta', 5, 4, '2024-25', 'rohan.gupta@university.edu', '+91-9876543218', TRUE),
('ADM2024010', 'BCA2024002', 'Kavya Desai', 5, 4, '2024-25', 'kavya.desai@university.edu', '+91-9876543219', TRUE),
('ADM2024011', 'CSE2024003', 'Arjun Rao', 1, 5, '2024-25', 'arjun.rao@university.edu', '+91-9876543220', TRUE),
('ADM2024012', 'IT2024003', 'Meera Verma', 2, 3, '2024-25', 'meera.verma@university.edu', '+91-9876543221', TRUE),
('ADM2024013', 'ECE2024003', 'Siddharth Joshi', 3, 7, '2024-25', 'siddharth.joshi@university.edu', '+91-9876543222', TRUE),
('ADM2024014', 'BCA2024003', 'Pooja Mehta', 5, 4, '2024-25', 'pooja.mehta@university.edu', '+91-9876543223', TRUE),
('ADM2024015', 'CSE2024004', 'Aditya Kapoor', 1, 5, '2024-25', 'aditya.kapoor@university.edu', '+91-9876543224', TRUE)
ON DUPLICATE KEY UPDATE full_name=VALUES(full_name);

-- Insert sample attendance records (recent dates)
INSERT INTO attendance (student_id, course_id, attendance_date, attendance_time, session_type, status, marked_by) VALUES
-- December 16, 2025 (Monday)
(1, 1, '2025-12-16', '09:15:00', 'Morning', 'Present', 'Face Recognition System'),
(2, 1, '2025-12-16', '09:16:00', 'Morning', 'Present', 'Face Recognition System'),
(11, 1, '2025-12-16', '09:17:00', 'Morning', 'Present', 'Face Recognition System'),
(15, 1, '2025-12-16', '09:18:00', 'Morning', 'Late', 'Face Recognition System'),
(3, 2, '2025-12-16', '10:05:00', 'Morning', 'Present', 'Face Recognition System'),
(4, 2, '2025-12-16', '10:06:00', 'Morning', 'Present', 'Face Recognition System'),
(12, 2, '2025-12-16', '10:07:00', 'Morning', 'Present', 'Face Recognition System'),
(5, 3, '2025-12-16', '11:10:00', 'Morning', 'Present', 'Face Recognition System'),
(6, 3, '2025-12-16', '11:11:00', 'Morning', 'Present', 'Face Recognition System'),
(13, 3, '2025-12-16', '11:12:00', 'Morning', 'Present', 'Face Recognition System'),

-- December 17, 2025 (Tuesday)
(1, 1, '2025-12-17', '09:10:00', 'Morning', 'Present', 'Face Recognition System'),
(2, 1, '2025-12-17', '09:11:00', 'Morning', 'Present', 'Face Recognition System'),
(11, 1, '2025-12-17', '09:25:00', 'Morning', 'Late', 'Face Recognition System'),
(15, 1, '2025-12-17', '09:12:00', 'Morning', 'Present', 'Face Recognition System'),
(7, 4, '2025-12-17', '14:00:00', 'Afternoon', 'Present', 'Face Recognition System'),
(8, 4, '2025-12-17', '14:01:00', 'Afternoon', 'Present', 'Face Recognition System'),
(9, 5, '2025-12-17', '15:30:00', 'Afternoon', 'Present', 'Face Recognition System'),
(10, 5, '2025-12-17', '15:31:00', 'Afternoon', 'Present', 'Face Recognition System'),
(14, 5, '2025-12-17', '15:32:00', 'Afternoon', 'Present', 'Face Recognition System'),

-- December 18, 2025 (Wednesday)
(1, 1, '2025-12-18', '09:08:00', 'Morning', 'Present', 'Face Recognition System'),
(2, 1, '2025-12-18', '09:09:00', 'Morning', 'Present', 'Face Recognition System'),
(11, 1, '2025-12-18', '09:10:00', 'Morning', 'Present', 'Face Recognition System'),
(15, 1, '2025-12-18', '09:11:00', 'Morning', 'Present', 'Face Recognition System'),
(3, 2, '2025-12-18', '10:00:00', 'Morning', 'Present', 'Face Recognition System'),
(4, 2, '2025-12-18', '10:01:00', 'Morning', 'Present', 'Face Recognition System'),
(12, 2, '2025-12-18', '10:02:00', 'Morning', 'Present', 'Face Recognition System'),
(5, 3, '2025-12-18', '11:05:00', 'Morning', 'Present', 'Face Recognition System'),
(6, 3, '2025-12-18', '11:06:00', 'Morning', 'Present', 'Face Recognition System'),
(13, 3, '2025-12-18', '11:07:00', 'Morning', 'Present', 'Face Recognition System'),
(7, 4, '2025-12-18', '14:05:00', 'Afternoon', 'Present', 'Face Recognition System'),
(8, 4, '2025-12-18', '14:06:00', 'Afternoon', 'Present', 'Face Recognition System'),
(9, 5, '2025-12-18', '15:25:00', 'Afternoon', 'Present', 'Face Recognition System'),
(10, 5, '2025-12-18', '15:26:00', 'Afternoon', 'Present', 'Face Recognition System'),
(14, 5, '2025-12-18', '15:27:00', 'Afternoon', 'Present', 'Face Recognition System'),

-- December 19, 2025 (Today)
(1, 1, '2025-12-19', '09:05:00', 'Morning', 'Present', 'Face Recognition System'),
(2, 1, '2025-12-19', '09:06:00', 'Morning', 'Present', 'Face Recognition System'),
(11, 1, '2025-12-19', '09:07:00', 'Morning', 'Present', 'Face Recognition System'),
(15, 1, '2025-12-19', '09:08:00', 'Morning', 'Present', 'Face Recognition System'),
(3, 2, '2025-12-19', '10:10:00', 'Morning', 'Present', 'Face Recognition System'),
(4, 2, '2025-12-19', '10:11:00', 'Morning', 'Present', 'Face Recognition System'),
(12, 2, '2025-12-19', '10:12:00', 'Morning', 'Present', 'Face Recognition System'),
(5, 3, '2025-12-19', '11:15:00', 'Morning', 'Present', 'Face Recognition System'),
(6, 3, '2025-12-19', '11:16:00', 'Morning', 'Present', 'Face Recognition System'),
(13, 3, '2025-12-19', '11:17:00', 'Morning', 'Present', 'Face Recognition System'),
(7, 4, '2025-12-19', '14:10:00', 'Afternoon', 'Present', 'Face Recognition System'),
(8, 4, '2025-12-19', '14:11:00', 'Afternoon', 'Present', 'Face Recognition System')
ON DUPLICATE KEY UPDATE status=VALUES(status);

-- =====================================================
-- Views for Reporting
-- =====================================================

-- View: Daily Attendance Summary
CREATE OR REPLACE VIEW daily_attendance_summary AS
SELECT 
    a.attendance_date,
    c.course_name,
    a.session_type,
    COUNT(DISTINCT a.student_id) as total_present,
    (SELECT COUNT(*) FROM students s WHERE s.course_id = c.course_id AND s.is_active = TRUE) as total_students,
    ROUND((COUNT(DISTINCT a.student_id) * 100.0 / 
        (SELECT COUNT(*) FROM students s WHERE s.course_id = c.course_id AND s.is_active = TRUE)), 2) as attendance_percentage
FROM attendance a
JOIN courses c ON a.course_id = c.course_id
WHERE a.status = 'Present'
GROUP BY a.attendance_date, c.course_name, a.session_type;

-- View: Student Attendance Report
CREATE OR REPLACE VIEW student_attendance_report AS
SELECT 
    s.student_id,
    s.admission_number,
    s.full_name,
    s.roll_number,
    c.course_name,
    s.semester,
    COUNT(a.attendance_id) as total_classes_attended,
    s.created_at as enrollment_date
FROM students s
JOIN courses c ON s.course_id = c.course_id
LEFT JOIN attendance a ON s.student_id = a.student_id AND a.status = 'Present'
WHERE s.is_active = TRUE
GROUP BY s.student_id, s.admission_number, s.full_name, s.roll_number, c.course_name, s.semester, s.created_at;

-- =====================================================
-- Stored Procedures
-- =====================================================

DELIMITER //

-- Procedure: Mark Attendance
CREATE PROCEDURE IF NOT EXISTS mark_attendance(
    IN p_student_id INT,
    IN p_course_id INT,
    IN p_session_type VARCHAR(20)
)
BEGIN
    DECLARE attendance_exists INT;
    
    -- Check if attendance already marked for today
    SELECT COUNT(*) INTO attendance_exists
    FROM attendance
    WHERE student_id = p_student_id
    AND course_id = p_course_id
    AND attendance_date = CURDATE()
    AND session_type = p_session_type;
    
    IF attendance_exists = 0 THEN
        INSERT INTO attendance (student_id, course_id, attendance_date, attendance_time, session_type, status)
        VALUES (p_student_id, p_course_id, CURDATE(), CURTIME(), p_session_type, 'Present');
        SELECT 'Attendance marked successfully' as message, 1 as success;
    ELSE
        SELECT 'Attendance already marked for this session' as message, 0 as success;
    END IF;
END //

DELIMITER ;

-- =====================================================
-- End of Schema
-- =====================================================

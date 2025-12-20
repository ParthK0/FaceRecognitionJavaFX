-- =====================================================
-- Deep Learning Upgrade Migration Script
-- Run this script to add new tables for DL features
-- =====================================================

USE attendance_system;

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
-- Verification Queries
-- =====================================================

-- Check if tables were created successfully
SELECT 'face_embeddings table created' AS status 
FROM information_schema.tables 
WHERE table_schema = 'attendance_system' 
AND table_name = 'face_embeddings';

SELECT 'recognition_logs table created' AS status 
FROM information_schema.tables 
WHERE table_schema = 'attendance_system' 
AND table_name = 'recognition_logs';

-- Show table structures
DESCRIBE face_embeddings;
DESCRIBE recognition_logs;

-- =====================================================
-- Success Message
-- =====================================================
SELECT '✅ Migration completed successfully!' AS message;
SELECT '📊 You can now use the Deep Learning Face Recognition System' AS info;
SELECT '🔄 Next step: Run training with DeepLearningTrainer' AS next_step;

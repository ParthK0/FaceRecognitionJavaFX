package com.myapp.util;

import com.myapp.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database Connection Manager
 * Handles database connection pooling and initialization
 */
public class DatabaseConnection {
    private static Connection connection = null;
    private static boolean initialized = false;

    /**
     * Get database connection (creates if doesn't exist)
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Load MySQL JDBC Driver
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                // Create connection
                connection = DriverManager.getConnection(
                    DatabaseConfig.getUrl(),
                    DatabaseConfig.getUsername(),
                    DatabaseConfig.getPassword()
                );
                
                System.out.println("✓ Database connection established successfully!");
                
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver not found: " + e.getMessage());
            }
        }
        return connection;
    }

    /**
     * Initialize database (create database and tables if they don't exist)
     */
    public static void initializeDatabase() throws SQLException {
        if (initialized) {
            return;
        }

        try {
            // First, connect without specifying database to create it if needed
            String baseUrl = String.format("jdbc:mysql://%s:%s/?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
                    DatabaseConfig.getProperty("db.host", "localhost"),
                    DatabaseConfig.getProperty("db.port", "3306"));
            
            Connection initConn = DriverManager.getConnection(
                    baseUrl,
                    DatabaseConfig.getUsername(),
                    DatabaseConfig.getPassword()
            );

            Statement stmt = initConn.createStatement();
            
            // Create database if it doesn't exist
            String createDbQuery = "CREATE DATABASE IF NOT EXISTS " + DatabaseConfig.getDatabase();
            stmt.executeUpdate(createDbQuery);
            System.out.println("✓ Database '" + DatabaseConfig.getDatabase() + "' verified/created");
            
            // Use the database
            stmt.executeUpdate("USE " + DatabaseConfig.getDatabase());
            
            // Create tables
            createTables(stmt);
            
            stmt.close();
            initConn.close();
            
            initialized = true;
            System.out.println("✓ Database initialization completed successfully!");
            
        } catch (SQLException e) {
            System.err.println("✗ Error initializing database: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Create all necessary tables
     */
    private static void createTables(Statement stmt) throws SQLException {
        // Create courses table
        String createCoursesTable = 
            "CREATE TABLE IF NOT EXISTS courses (" +
            "course_id INT AUTO_INCREMENT PRIMARY KEY," +
            "course_code VARCHAR(20) NOT NULL UNIQUE," +
            "course_name VARCHAR(100) NOT NULL," +
            "department VARCHAR(100)," +
            "credits INT DEFAULT 3," +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
            "INDEX idx_course_code (course_code)" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
        stmt.executeUpdate(createCoursesTable);
        System.out.println("  ✓ Table 'courses' created/verified");

        // Create students table
        String createStudentsTable = 
            "CREATE TABLE IF NOT EXISTS students (" +
            "student_id INT AUTO_INCREMENT PRIMARY KEY," +
            "admission_number VARCHAR(50) NOT NULL UNIQUE," +
            "roll_number VARCHAR(50) NOT NULL," +
            "full_name VARCHAR(100) NOT NULL," +
            "course_id INT NOT NULL," +
            "semester INT NOT NULL," +
            "academic_year VARCHAR(20) NOT NULL," +
            "email VARCHAR(100)," +
            "phone VARCHAR(20)," +
            "facial_data_path VARCHAR(255)," +
            "is_active BOOLEAN DEFAULT TRUE," +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
            "FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE RESTRICT," +
            "INDEX idx_admission_number (admission_number)," +
            "INDEX idx_roll_number (roll_number)," +
            "INDEX idx_full_name (full_name)" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
        stmt.executeUpdate(createStudentsTable);
        System.out.println("  ✓ Table 'students' created/verified");

        // Create attendance table
        String createAttendanceTable = 
            "CREATE TABLE IF NOT EXISTS attendance (" +
            "attendance_id INT AUTO_INCREMENT PRIMARY KEY," +
            "student_id INT NOT NULL," +
            "course_id INT NOT NULL," +
            "attendance_date DATE NOT NULL," +
            "attendance_time TIME NOT NULL," +
            "session_type ENUM('Morning', 'Afternoon', 'Evening', 'Full Day') DEFAULT 'Full Day'," +
            "status ENUM('Present', 'Absent', 'Late', 'Excused') DEFAULT 'Present'," +
            "marked_by VARCHAR(50) DEFAULT 'Face Recognition System'," +
            "remarks TEXT," +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE," +
            "FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE RESTRICT," +
            "UNIQUE KEY unique_attendance (student_id, course_id, attendance_date, session_type)," +
            "INDEX idx_student_date (student_id, attendance_date)," +
            "INDEX idx_course_date (course_id, attendance_date)" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
        stmt.executeUpdate(createAttendanceTable);
        System.out.println("  ✓ Table 'attendance' created/verified");

        // Insert sample courses if table is empty
        insertSampleData(stmt);
    }

    /**
     * Insert sample course data
     */
    private static void insertSampleData(Statement stmt) throws SQLException {
        String insertCourses = 
            "INSERT IGNORE INTO courses (course_code, course_name, department, credits) VALUES " +
            "('CSE101', 'Computer Science Engineering', 'Engineering', 4)," +
            "('IT102', 'Information Technology', 'Engineering', 4)," +
            "('ECE103', 'Electronics and Communication', 'Engineering', 4)," +
            "('MBA104', 'Master of Business Administration', 'Management', 3)," +
            "('BCA105', 'Bachelor of Computer Applications', 'Computer Science', 3)";
        int rows = stmt.executeUpdate(insertCourses);
        if (rows > 0) {
            System.out.println("  ✓ Sample course data inserted");
        }
    }

    /**
     * Test database connection
     */
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("✗ Connection test failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Close database connection
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✓ Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("✗ Error closing connection: " + e.getMessage());
        }
    }

    /**
     * Execute SQL file (for initial setup)
     */
    public static void executeSqlFile(String filePath) throws SQLException {
        // This method can be enhanced to read and execute SQL files
        // For now, we use the createTables method
        System.out.println("Database tables created using built-in schema");
    }
}

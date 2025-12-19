package com.myapp.dao;

import com.myapp.model.Student;
import com.myapp.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Student entity
 * Handles all database operations related to students
 */
public class StudentDAO {

    /**
     * Get all students from database
     */
    public List<Student> getAllStudents() throws SQLException {
        List<Student> students = new ArrayList<>();
        String query = "SELECT s.*, c.course_name FROM students s " +
                      "LEFT JOIN courses c ON s.course_id = c.course_id " +
                      "WHERE s.is_active = TRUE ORDER BY s.full_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
        }
        return students;
    }

    /**
     * Get student by ID
     */
    public Student getStudentById(int studentId) throws SQLException {
        String query = "SELECT s.*, c.course_name FROM students s " +
                      "LEFT JOIN courses c ON s.course_id = c.course_id " +
                      "WHERE s.student_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToStudent(rs);
            }
        }
        return null;
    }

    /**
     * Get student by admission number
     */
    public Student getStudentByAdmissionNumber(String admissionNumber) throws SQLException {
        String query = "SELECT s.*, c.course_name FROM students s " +
                      "LEFT JOIN courses c ON s.course_id = c.course_id " +
                      "WHERE s.admission_number = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, admissionNumber);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToStudent(rs);
            }
        }
        return null;
    }

    /**
     * Get students by course ID
     */
    public List<Student> getStudentsByCourse(int courseId) throws SQLException {
        List<Student> students = new ArrayList<>();
        String query = "SELECT s.*, c.course_name FROM students s " +
                      "LEFT JOIN courses c ON s.course_id = c.course_id " +
                      "WHERE s.course_id = ? AND s.is_active = TRUE ORDER BY s.roll_number";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, courseId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
        }
        return students;
    }

    /**
     * Insert new student
     */
    public int insertStudent(Student student) throws SQLException {
        String query = "INSERT INTO students (admission_number, roll_number, full_name, course_id, " +
                      "semester, academic_year, email, phone, facial_data_path) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, student.getAdmissionNumber());
            pstmt.setString(2, student.getRollNumber());
            pstmt.setString(3, student.getFullName());
            pstmt.setInt(4, student.getCourseId());
            pstmt.setInt(5, student.getSemester());
            pstmt.setString(6, student.getAcademicYear());
            pstmt.setString(7, student.getEmail());
            pstmt.setString(8, student.getPhone());
            pstmt.setString(9, student.getFacialDataPath());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    /**
     * Update student
     */
    public boolean updateStudent(Student student) throws SQLException {
        String query = "UPDATE students SET admission_number = ?, roll_number = ?, full_name = ?, " +
                      "course_id = ?, semester = ?, academic_year = ?, email = ?, phone = ?, " +
                      "facial_data_path = ?, is_active = ? WHERE student_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, student.getAdmissionNumber());
            pstmt.setString(2, student.getRollNumber());
            pstmt.setString(3, student.getFullName());
            pstmt.setInt(4, student.getCourseId());
            pstmt.setInt(5, student.getSemester());
            pstmt.setString(6, student.getAcademicYear());
            pstmt.setString(7, student.getEmail());
            pstmt.setString(8, student.getPhone());
            pstmt.setString(9, student.getFacialDataPath());
            pstmt.setBoolean(10, student.isActive());
            pstmt.setInt(11, student.getStudentId());
            
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Update student facial data path
     */
    public boolean updateFacialDataPath(int studentId, String facialDataPath) throws SQLException {
        String query = "UPDATE students SET facial_data_path = ? WHERE student_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, facialDataPath);
            pstmt.setInt(2, studentId);
            
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Delete student (soft delete - mark as inactive)
     */
    public boolean deleteStudent(int studentId) throws SQLException {
        String query = "UPDATE students SET is_active = FALSE WHERE student_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, studentId);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Permanently delete student
     */
    public boolean permanentlyDeleteStudent(int studentId) throws SQLException {
        String query = "DELETE FROM students WHERE student_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, studentId);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Search students
     */
    public List<Student> searchStudents(String searchTerm) throws SQLException {
        List<Student> students = new ArrayList<>();
        String query = "SELECT s.*, c.course_name FROM students s " +
                      "LEFT JOIN courses c ON s.course_id = c.course_id " +
                      "WHERE (s.full_name LIKE ? OR s.admission_number LIKE ? OR s.roll_number LIKE ?) " +
                      "AND s.is_active = TRUE ORDER BY s.full_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
        }
        return students;
    }

    /**
     * Map ResultSet to Student object
     */
    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setStudentId(rs.getInt("student_id"));
        student.setAdmissionNumber(rs.getString("admission_number"));
        student.setRollNumber(rs.getString("roll_number"));
        student.setFullName(rs.getString("full_name"));
        student.setCourseId(rs.getInt("course_id"));
        student.setSemester(rs.getInt("semester"));
        student.setAcademicYear(rs.getString("academic_year"));
        student.setEmail(rs.getString("email"));
        student.setPhone(rs.getString("phone"));
        student.setFacialDataPath(rs.getString("facial_data_path"));
        student.setActive(rs.getBoolean("is_active"));
        
        // Join field
        student.setCourseName(rs.getString("course_name"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            student.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            student.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return student;
    }

    /**
     * Check if student exists
     */
    public boolean studentExists(String admissionNumber) throws SQLException {
        return getStudentByAdmissionNumber(admissionNumber) != null;
    }

    /**
     * Get total number of active students
     */
    public int getTotalStudents() throws SQLException {
        String query = "SELECT COUNT(*) as count FROM students WHERE is_active = TRUE";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            if (rs.next()) {
                return rs.getInt("count");
            }
        }
        return 0;
    }

    /**
     * Get students with facial data trained
     */
    public List<Student> getStudentsWithFacialData() throws SQLException {
        List<Student> students = new ArrayList<>();
        String query = "SELECT s.*, c.course_name FROM students s " +
                      "LEFT JOIN courses c ON s.course_id = c.course_id " +
                      "WHERE s.facial_data_path IS NOT NULL AND s.is_active = TRUE " +
                      "ORDER BY s.student_id";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
        }
        return students;
    }
}

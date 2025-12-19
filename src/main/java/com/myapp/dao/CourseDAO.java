package com.myapp.dao;

import com.myapp.model.Course;
import com.myapp.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Course entity
 * Handles all database operations related to courses
 */
public class CourseDAO {

    /**
     * Get all courses from database
     */
    public List<Course> getAllCourses() throws SQLException {
        List<Course> courses = new ArrayList<>();
        String query = "SELECT * FROM courses ORDER BY course_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                courses.add(mapResultSetToCourse(rs));
            }
        }
        return courses;
    }

    /**
     * Get course by ID
     */
    public Course getCourseById(int courseId) throws SQLException {
        String query = "SELECT * FROM courses WHERE course_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, courseId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToCourse(rs);
            }
        }
        return null;
    }

    /**
     * Get course by code
     */
    public Course getCourseByCode(String courseCode) throws SQLException {
        String query = "SELECT * FROM courses WHERE course_code = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, courseCode);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToCourse(rs);
            }
        }
        return null;
    }

    /**
     * Insert new course
     */
    public int insertCourse(Course course) throws SQLException {
        String query = "INSERT INTO courses (course_code, course_name, department, credits) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, course.getCourseCode());
            pstmt.setString(2, course.getCourseName());
            pstmt.setString(3, course.getDepartment());
            pstmt.setInt(4, course.getCredits());
            
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
     * Update course
     */
    public boolean updateCourse(Course course) throws SQLException {
        String query = "UPDATE courses SET course_code = ?, course_name = ?, department = ?, credits = ? WHERE course_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, course.getCourseCode());
            pstmt.setString(2, course.getCourseName());
            pstmt.setString(3, course.getDepartment());
            pstmt.setInt(4, course.getCredits());
            pstmt.setInt(5, course.getCourseId());
            
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Delete course
     */
    public boolean deleteCourse(int courseId) throws SQLException {
        String query = "DELETE FROM courses WHERE course_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, courseId);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Search courses by name or code
     */
    public List<Course> searchCourses(String searchTerm) throws SQLException {
        List<Course> courses = new ArrayList<>();
        String query = "SELECT * FROM courses WHERE course_name LIKE ? OR course_code LIKE ? OR department LIKE ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                courses.add(mapResultSetToCourse(rs));
            }
        }
        return courses;
    }

    /**
     * Map ResultSet to Course object
     */
    private Course mapResultSetToCourse(ResultSet rs) throws SQLException {
        Course course = new Course();
        course.setCourseId(rs.getInt("course_id"));
        course.setCourseCode(rs.getString("course_code"));
        course.setCourseName(rs.getString("course_name"));
        course.setDepartment(rs.getString("department"));
        course.setCredits(rs.getInt("credits"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            course.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            course.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return course;
    }

    /**
     * Check if course exists
     */
    public boolean courseExists(String courseCode) throws SQLException {
        return getCourseByCode(courseCode) != null;
    }

    /**
     * Get total number of courses
     */
    public int getTotalCourses() throws SQLException {
        String query = "SELECT COUNT(*) as count FROM courses";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            if (rs.next()) {
                return rs.getInt("count");
            }
        }
        return 0;
    }
}

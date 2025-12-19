package com.myapp.dao;

import com.myapp.model.Attendance;
import com.myapp.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Attendance entity
 * Handles all database operations related to attendance
 */
public class AttendanceDAO {

    /**
     * Mark attendance for a student
     */
    public boolean markAttendance(Attendance attendance) throws SQLException {
        // Check if attendance already exists
        if (attendanceExists(attendance.getStudentId(), attendance.getCourseId(), 
                            attendance.getAttendanceDate(), attendance.getSessionType())) {
            return false; // Attendance already marked
        }

        String query = "INSERT INTO attendance (student_id, course_id, attendance_date, " +
                      "attendance_time, session_type, status, marked_by, remarks) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, attendance.getStudentId());
            pstmt.setInt(2, attendance.getCourseId());
            pstmt.setDate(3, Date.valueOf(attendance.getAttendanceDate()));
            pstmt.setTime(4, Time.valueOf(attendance.getAttendanceTime()));
            pstmt.setString(5, attendance.getSessionType().getDisplayName());
            pstmt.setString(6, attendance.getStatus().getDisplayName());
            pstmt.setString(7, attendance.getMarkedBy());
            pstmt.setString(8, attendance.getRemarks());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    attendance.setAttendanceId(rs.getInt(1));
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check if attendance already exists
     */
    public boolean attendanceExists(int studentId, int courseId, LocalDate date, 
                                   Attendance.SessionType sessionType) throws SQLException {
        String query = "SELECT COUNT(*) as count FROM attendance WHERE student_id = ? " +
                      "AND course_id = ? AND attendance_date = ? AND session_type = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);
            pstmt.setDate(3, Date.valueOf(date));
            pstmt.setString(4, sessionType.getDisplayName());
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        }
        return false;
    }

    /**
     * Get attendance by ID
     */
    public Attendance getAttendanceById(int attendanceId) throws SQLException {
        String query = "SELECT a.*, s.full_name as student_name, s.admission_number, c.course_name " +
                      "FROM attendance a " +
                      "JOIN students s ON a.student_id = s.student_id " +
                      "JOIN courses c ON a.course_id = c.course_id " +
                      "WHERE a.attendance_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, attendanceId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToAttendance(rs);
            }
        }
        return null;
    }

    /**
     * Get attendance records for a student
     */
    public List<Attendance> getAttendanceByStudent(int studentId) throws SQLException {
        List<Attendance> records = new ArrayList<>();
        String query = "SELECT a.*, s.full_name as student_name, s.admission_number, c.course_name " +
                      "FROM attendance a " +
                      "JOIN students s ON a.student_id = s.student_id " +
                      "JOIN courses c ON a.course_id = c.course_id " +
                      "WHERE a.student_id = ? ORDER BY a.attendance_date DESC, a.attendance_time DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                records.add(mapResultSetToAttendance(rs));
            }
        }
        return records;
    }

    /**
     * Get attendance records for a course
     */
    public List<Attendance> getAttendanceByCourse(int courseId) throws SQLException {
        List<Attendance> records = new ArrayList<>();
        String query = "SELECT a.*, s.full_name as student_name, s.admission_number, c.course_name " +
                      "FROM attendance a " +
                      "JOIN students s ON a.student_id = s.student_id " +
                      "JOIN courses c ON a.course_id = c.course_id " +
                      "WHERE a.course_id = ? ORDER BY a.attendance_date DESC, a.attendance_time DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, courseId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                records.add(mapResultSetToAttendance(rs));
            }
        }
        return records;
    }

    /**
     * Get attendance records for a specific date
     */
    public List<Attendance> getAttendanceByDate(LocalDate date) throws SQLException {
        List<Attendance> records = new ArrayList<>();
        String query = "SELECT a.*, s.full_name as student_name, s.admission_number, c.course_name " +
                      "FROM attendance a " +
                      "JOIN students s ON a.student_id = s.student_id " +
                      "JOIN courses c ON a.course_id = c.course_id " +
                      "WHERE a.attendance_date = ? ORDER BY a.attendance_time DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setDate(1, Date.valueOf(date));
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                records.add(mapResultSetToAttendance(rs));
            }
        }
        return records;
    }

    /**
     * Get attendance records for date range
     */
    public List<Attendance> getAttendanceByDateRange(LocalDate startDate, LocalDate endDate) 
            throws SQLException {
        List<Attendance> records = new ArrayList<>();
        String query = "SELECT a.*, s.full_name as student_name, s.admission_number, c.course_name " +
                      "FROM attendance a " +
                      "JOIN students s ON a.student_id = s.student_id " +
                      "JOIN courses c ON a.course_id = c.course_id " +
                      "WHERE a.attendance_date BETWEEN ? AND ? " +
                      "ORDER BY a.attendance_date DESC, a.attendance_time DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setDate(1, Date.valueOf(startDate));
            pstmt.setDate(2, Date.valueOf(endDate));
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                records.add(mapResultSetToAttendance(rs));
            }
        }
        return records;
    }

    /**
     * Get today's attendance
     */
    public List<Attendance> getTodayAttendance() throws SQLException {
        return getAttendanceByDate(LocalDate.now());
    }

    /**
     * Update attendance status
     */
    public boolean updateAttendanceStatus(int attendanceId, Attendance.AttendanceStatus status, 
                                         String remarks) throws SQLException {
        String query = "UPDATE attendance SET status = ?, remarks = ? WHERE attendance_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, status.getDisplayName());
            pstmt.setString(2, remarks);
            pstmt.setInt(3, attendanceId);
            
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Delete attendance record
     */
    public boolean deleteAttendance(int attendanceId) throws SQLException {
        String query = "DELETE FROM attendance WHERE attendance_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, attendanceId);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Get attendance statistics for a student
     */
    public int getAttendanceCount(int studentId, int courseId) throws SQLException {
        String query = "SELECT COUNT(*) as count FROM attendance " +
                      "WHERE student_id = ? AND course_id = ? AND status = 'Present'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count");
            }
        }
        return 0;
    }

    /**
     * Get attendance percentage for a student in a course
     */
    public double getAttendancePercentage(int studentId, int courseId) throws SQLException {
        String query = "SELECT " +
                      "(SELECT COUNT(*) FROM attendance WHERE student_id = ? AND course_id = ? AND status = 'Present') as present, " +
                      "(SELECT COUNT(*) FROM attendance WHERE student_id = ? AND course_id = ?) as total";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);
            pstmt.setInt(3, studentId);
            pstmt.setInt(4, courseId);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int present = rs.getInt("present");
                int total = rs.getInt("total");
                if (total > 0) {
                    return (present * 100.0) / total;
                }
            }
        }
        return 0.0;
    }

    /**
     * Map ResultSet to Attendance object
     */
    private Attendance mapResultSetToAttendance(ResultSet rs) throws SQLException {
        Attendance attendance = new Attendance();
        attendance.setAttendanceId(rs.getInt("attendance_id"));
        attendance.setStudentId(rs.getInt("student_id"));
        attendance.setCourseId(rs.getInt("course_id"));
        attendance.setAttendanceDate(rs.getDate("attendance_date").toLocalDate());
        attendance.setAttendanceTime(rs.getTime("attendance_time").toLocalTime());
        attendance.setSessionType(Attendance.SessionType.fromString(rs.getString("session_type")));
        attendance.setStatus(Attendance.AttendanceStatus.fromString(rs.getString("status")));
        attendance.setMarkedBy(rs.getString("marked_by"));
        attendance.setRemarks(rs.getString("remarks"));
        
        // Join fields
        attendance.setStudentName(rs.getString("student_name"));
        attendance.setCourseName(rs.getString("course_name"));
        attendance.setAdmissionNumber(rs.getString("admission_number"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            attendance.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return attendance;
    }

    /**
     * Get total attendance records
     */
    public int getTotalAttendanceRecords() throws SQLException {
        String query = "SELECT COUNT(*) as count FROM attendance";
        
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
     * Get all attendance records
     */
    public List<Attendance> getAllAttendance() throws SQLException {
        String query = "SELECT * FROM attendance ORDER BY attendance_date DESC, attendance_time DESC";
        List<Attendance> attendanceList = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                attendanceList.add(mapResultSetToAttendance(rs));
            }
        }
        return attendanceList;
    }}

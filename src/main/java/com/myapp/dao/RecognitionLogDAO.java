package com.myapp.dao;

import com.myapp.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object for Recognition Logs
 * Tracks all face recognition attempts for analytics and debugging
 */
public class RecognitionLogDAO {

    public enum RecognitionResult {
        SUCCESS("Success"),
        FAILED("Failed"),
        UNKNOWN("Unknown");

        private final String displayName;

        RecognitionResult(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Log a recognition attempt
     */
    public boolean logRecognition(Integer studentId, float confidenceScore, 
                                  RecognitionResult result, String detectionMethod,
                                  String recognitionMethod, String cameraId, 
                                  String location, String remarks) throws SQLException {
        String query = "INSERT INTO recognition_logs (student_id, confidence_score, " +
                      "recognition_result, detection_method, recognition_method, " +
                      "camera_id, location, remarks) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            if (studentId != null) {
                pstmt.setInt(1, studentId);
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }
            pstmt.setFloat(2, confidenceScore);
            pstmt.setString(3, result.getDisplayName());
            pstmt.setString(4, detectionMethod);
            pstmt.setString(5, recognitionMethod);
            pstmt.setString(6, cameraId);
            pstmt.setString(7, location);
            pstmt.setString(8, remarks);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Get recognition logs for a specific student
     */
    public List<Map<String, Object>> getStudentLogs(int studentId, int limit) throws SQLException {
        List<Map<String, Object>> logs = new ArrayList<>();
        String query = "SELECT * FROM recognition_logs WHERE student_id = ? " +
                      "ORDER BY recognition_timestamp DESC LIMIT ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, limit);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                logs.add(mapResultSetToLog(rs));
            }
        }
        return logs;
    }

    /**
     * Get recent recognition logs
     */
    public List<Map<String, Object>> getRecentLogs(int limit) throws SQLException {
        List<Map<String, Object>> logs = new ArrayList<>();
        String query = "SELECT rl.*, s.full_name, s.admission_number " +
                      "FROM recognition_logs rl " +
                      "LEFT JOIN students s ON rl.student_id = s.student_id " +
                      "ORDER BY rl.recognition_timestamp DESC LIMIT ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> log = mapResultSetToLog(rs);
                log.put("student_name", rs.getString("full_name"));
                log.put("admission_number", rs.getString("admission_number"));
                logs.add(log);
            }
        }
        return logs;
    }

    /**
     * Get recognition statistics for a date range
     */
    public Map<String, Object> getRecognitionStatistics(LocalDateTime startDate, 
                                                       LocalDateTime endDate) throws SQLException {
        Map<String, Object> stats = new HashMap<>();
        
        String query = "SELECT " +
                      "COUNT(*) as total_attempts, " +
                      "SUM(CASE WHEN recognition_result = 'Success' THEN 1 ELSE 0 END) as successful, " +
                      "SUM(CASE WHEN recognition_result = 'Failed' THEN 1 ELSE 0 END) as failed, " +
                      "SUM(CASE WHEN recognition_result = 'Unknown' THEN 1 ELSE 0 END) as unknown, " +
                      "AVG(confidence_score) as avg_confidence, " +
                      "MAX(confidence_score) as max_confidence, " +
                      "MIN(confidence_score) as min_confidence " +
                      "FROM recognition_logs " +
                      "WHERE recognition_timestamp BETWEEN ? AND ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setTimestamp(1, Timestamp.valueOf(startDate));
            pstmt.setTimestamp(2, Timestamp.valueOf(endDate));
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                stats.put("total_attempts", rs.getInt("total_attempts"));
                stats.put("successful", rs.getInt("successful"));
                stats.put("failed", rs.getInt("failed"));
                stats.put("unknown", rs.getInt("unknown"));
                stats.put("avg_confidence", rs.getFloat("avg_confidence"));
                stats.put("max_confidence", rs.getFloat("max_confidence"));
                stats.put("min_confidence", rs.getFloat("min_confidence"));
            }
        }
        return stats;
    }

    /**
     * Get recognition count by student
     */
    public Map<Integer, Integer> getRecognitionCountByStudent(LocalDateTime startDate, 
                                                              LocalDateTime endDate) throws SQLException {
        Map<Integer, Integer> counts = new HashMap<>();
        String query = "SELECT student_id, COUNT(*) as count " +
                      "FROM recognition_logs " +
                      "WHERE student_id IS NOT NULL " +
                      "AND recognition_timestamp BETWEEN ? AND ? " +
                      "GROUP BY student_id";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setTimestamp(1, Timestamp.valueOf(startDate));
            pstmt.setTimestamp(2, Timestamp.valueOf(endDate));
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                counts.put(rs.getInt("student_id"), rs.getInt("count"));
            }
        }
        return counts;
    }

    /**
     * Delete old logs (for maintenance)
     */
    public int deleteOldLogs(LocalDateTime beforeDate) throws SQLException {
        String query = "DELETE FROM recognition_logs WHERE recognition_timestamp < ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setTimestamp(1, Timestamp.valueOf(beforeDate));
            return pstmt.executeUpdate();
        }
    }

    /**
     * Map ResultSet to log map
     */
    private Map<String, Object> mapResultSetToLog(ResultSet rs) throws SQLException {
        Map<String, Object> log = new HashMap<>();
        log.put("log_id", rs.getInt("log_id"));
        log.put("student_id", rs.getObject("student_id"));
        log.put("recognition_timestamp", rs.getTimestamp("recognition_timestamp"));
        log.put("confidence_score", rs.getFloat("confidence_score"));
        log.put("recognition_result", rs.getString("recognition_result"));
        log.put("detection_method", rs.getString("detection_method"));
        log.put("recognition_method", rs.getString("recognition_method"));
        log.put("camera_id", rs.getString("camera_id"));
        log.put("location", rs.getString("location"));
        log.put("remarks", rs.getString("remarks"));
        return log;
    }
}

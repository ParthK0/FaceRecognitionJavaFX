package com.myapp.dao;

import com.myapp.ml.FaceNetEmbeddingGenerator;
import com.myapp.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object for Face Embeddings
 * Handles storage and retrieval of face embeddings in the database
 */
public class FaceEmbeddingDAO {

    /**
     * Store a face embedding for a student
     */
    public boolean storeEmbedding(int studentId, float[] embedding, String imageSource, 
                                 float qualityScore, String model) throws SQLException {
        String query = "INSERT INTO face_embeddings (student_id, embedding_vector, " +
                      "embedding_model, embedding_dimension, image_source, quality_score) " +
                      "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            byte[] embeddingBytes = FaceNetEmbeddingGenerator.embeddingToBytes(embedding);
            
            pstmt.setInt(1, studentId);
            pstmt.setBytes(2, embeddingBytes);
            pstmt.setString(3, model);
            pstmt.setInt(4, embedding.length);
            pstmt.setString(5, imageSource);
            pstmt.setFloat(6, qualityScore);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Get all embeddings for a specific student
     */
    public List<float[]> getStudentEmbeddings(int studentId) throws SQLException {
        List<float[]> embeddings = new ArrayList<>();
        String query = "SELECT embedding_vector FROM face_embeddings WHERE student_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                byte[] embeddingBytes = rs.getBytes("embedding_vector");
                float[] embedding = FaceNetEmbeddingGenerator.bytesToEmbedding(embeddingBytes);
                embeddings.add(embedding);
            }
        }
        return embeddings;
    }

    /**
     * Get all embeddings from the database
     * Returns a map of student_id -> list of embeddings
     */
    public Map<Integer, List<float[]>> getAllEmbeddings() throws SQLException {
        Map<Integer, List<float[]>> embeddingsMap = new HashMap<>();
        String query = "SELECT student_id, embedding_vector FROM face_embeddings";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                int studentId = rs.getInt("student_id");
                byte[] embeddingBytes = rs.getBytes("embedding_vector");
                float[] embedding = FaceNetEmbeddingGenerator.bytesToEmbedding(embeddingBytes);
                
                embeddingsMap.computeIfAbsent(studentId, k -> new ArrayList<>()).add(embedding);
            }
        }
        return embeddingsMap;
    }

    /**
     * Delete all embeddings for a student
     */
    public boolean deleteStudentEmbeddings(int studentId) throws SQLException {
        String query = "DELETE FROM face_embeddings WHERE student_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, studentId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Get count of embeddings for a student
     */
    public int getEmbeddingCount(int studentId) throws SQLException {
        String query = "SELECT COUNT(*) as count FROM face_embeddings WHERE student_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count");
            }
        }
        return 0;
    }

    /**
     * Update quality score for an embedding
     */
    public boolean updateQualityScore(int embeddingId, float qualityScore) throws SQLException {
        String query = "UPDATE face_embeddings SET quality_score = ? WHERE embedding_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setFloat(1, qualityScore);
            pstmt.setInt(2, embeddingId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Get average embedding quality score for a student
     */
    public float getAverageQualityScore(int studentId) throws SQLException {
        String query = "SELECT AVG(quality_score) as avg_score FROM face_embeddings WHERE student_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getFloat("avg_score");
            }
        }
        return 0.0f;
    }

    /**
     * Get total number of embeddings in the database
     */
    public int getTotalEmbeddingCount() throws SQLException {
        String query = "SELECT COUNT(*) as count FROM face_embeddings";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            ResultSet rs = stmt.executeQuery(query);
            
            if (rs.next()) {
                return rs.getInt("count");
            }
        }
        return 0;
    }

    /**
     * Get number of unique students with embeddings
     */
    public int getUniqueStudentCount() throws SQLException {
        String query = "SELECT COUNT(DISTINCT student_id) as count FROM face_embeddings";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            ResultSet rs = stmt.executeQuery(query);
            
            if (rs.next()) {
                return rs.getInt("count");
            }
        }
        return 0;
    }
}

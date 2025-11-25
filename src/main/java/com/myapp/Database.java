package com.myapp;

import java.sql.*;

public class Database {
    private static final String URL = "jdbc:mysql://localhost:3306/facelogin";
    private static final String USER = "root"; // change as needed
    private static final String PASS = "password"; // change

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    // create users table if not exists
    public static void init() {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "username VARCHAR(100) UNIQUE NOT NULL, " +
                "password_hash VARCHAR(256) NOT NULL, " +
                "label INT NOT NULL" +
                ")";
        try (Connection c = getConnection(); Statement s = c.createStatement()) {
            s.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean addUser(String username, String passwordHash, int label) {
        String sql = "INSERT INTO users (username, password_hash, label) VALUES (?,?,?)";
        try (Connection c = getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, username);
            p.setString(2, passwordHash);
            p.setInt(3, label);
            p.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Integer getLabelForUser(String username) {
        String sql = "SELECT label FROM users WHERE username = ?";
        try (Connection c = getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, username);
            ResultSet rs = p.executeQuery();
            if (rs.next()) return rs.getInt("label");
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public static String getPasswordHash(String username) {
        String sql = "SELECT password_hash FROM users WHERE username = ?";
        try (Connection c = getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, username);
            ResultSet rs = p.executeQuery();
            if (rs.next()) return rs.getString("password_hash");
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
}

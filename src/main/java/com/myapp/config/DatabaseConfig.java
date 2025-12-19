package com.myapp.config;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Database configuration manager
 * Handles loading and saving database connection properties
 */
public class DatabaseConfig {
    private static final String CONFIG_FILE = "db.properties";
    private static Properties properties;

    // Default configuration
    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_PORT = "3306";
    private static final String DEFAULT_DATABASE = "attendance_system";
    private static final String DEFAULT_USERNAME = "root";
    private static final String DEFAULT_PASSWORD = "";
    private static final String DEFAULT_USE_SSL = "false";
    private static final String DEFAULT_AUTO_RECONNECT = "true";

    static {
        loadProperties();
    }

    /**
     * Load database properties from file or create default configuration
     */
    private static void loadProperties() {
        properties = new Properties();
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            properties.load(fis);
        } catch (IOException e) {
            // File doesn't exist, create with defaults
            setDefaultProperties();
            saveProperties();
        }
    }

    /**
     * Set default database properties
     */
    private static void setDefaultProperties() {
        properties.setProperty("db.host", DEFAULT_HOST);
        properties.setProperty("db.port", DEFAULT_PORT);
        properties.setProperty("db.database", DEFAULT_DATABASE);
        properties.setProperty("db.username", DEFAULT_USERNAME);
        properties.setProperty("db.password", DEFAULT_PASSWORD);
        properties.setProperty("db.useSSL", DEFAULT_USE_SSL);
        properties.setProperty("db.autoReconnect", DEFAULT_AUTO_RECONNECT);
    }

    /**
     * Save properties to file
     */
    private static void saveProperties() {
        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            properties.store(fos, "Database Configuration for Attendance System");
        } catch (IOException e) {
            System.err.println("Error saving database configuration: " + e.getMessage());
        }
    }

    /**
     * Get database connection URL
     */
    public static String getUrl() {
        String host = properties.getProperty("db.host", DEFAULT_HOST);
        String port = properties.getProperty("db.port", DEFAULT_PORT);
        String database = properties.getProperty("db.database", DEFAULT_DATABASE);
        String useSSL = properties.getProperty("db.useSSL", DEFAULT_USE_SSL);
        String autoReconnect = properties.getProperty("db.autoReconnect", DEFAULT_AUTO_RECONNECT);
        String allowPublicKeyRetrieval = properties.getProperty("db.allowPublicKeyRetrieval", "true");
        
        return String.format("jdbc:mysql://%s:%s/%s?useSSL=%s&autoReconnect=%s&allowPublicKeyRetrieval=%s&serverTimezone=UTC",
                host, port, database, useSSL, autoReconnect, allowPublicKeyRetrieval);
    }

    /**
     * Get database username
     */
    public static String getUsername() {
        return properties.getProperty("db.username", DEFAULT_USERNAME);
    }

    /**
     * Get database password
     */
    public static String getPassword() {
        return properties.getProperty("db.password", DEFAULT_PASSWORD);
    }

    /**
     * Get database name
     */
    public static String getDatabase() {
        return properties.getProperty("db.database", DEFAULT_DATABASE);
    }

    /**
     * Update database configuration
     */
    public static void updateConfig(String host, String port, String database, 
                                    String username, String password) {
        properties.setProperty("db.host", host);
        properties.setProperty("db.port", port);
        properties.setProperty("db.database", database);
        properties.setProperty("db.username", username);
        properties.setProperty("db.password", password);
        saveProperties();
    }

    /**
     * Get property value
     */
    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * Reload configuration from file
     */
    public static void reload() {
        loadProperties();
    }
}

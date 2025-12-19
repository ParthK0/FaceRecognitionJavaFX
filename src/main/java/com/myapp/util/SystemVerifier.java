package com.myapp.util;

import com.myapp.config.DatabaseConfig;
import com.myapp.util.DatabaseConnection;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;

import java.io.File;
import java.sql.Connection;

/**
 * System Environment Verification Utility
 * Checks if all prerequisites are properly configured
 */
public class SystemVerifier {

    public static void main(String[] args) {
        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║      SYSTEM ENVIRONMENT VERIFICATION                           ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝\n");

        boolean allPassed = true;

        // Check 1: Java Version
        System.out.println("1. Checking Java Version...");
        String javaVersion = System.getProperty("java.version");
        System.out.println("   Java Version: " + javaVersion);
        if (javaVersion.startsWith("21")) {
            System.out.println("   ✓ Java 21 detected");
        } else {
            System.out.println("   ⚠ Warning: Java 21 recommended, found " + javaVersion);
            allPassed = false;
        }

        // Check 2: OpenCV Libraries
        System.out.println("\n2. Checking OpenCV Libraries...");
        try {
            Mat testMat = new Mat();
            System.out.println("   ✓ OpenCV libraries loaded successfully");
            testMat.release();
        } catch (Exception e) {
            System.out.println("   ✗ OpenCV libraries not loaded: " + e.getMessage());
            allPassed = false;
        }

        // Check 3: Haar Cascade File
        System.out.println("\n3. Checking Haar Cascade Classifier...");
        String haarCascadePath = "src/main/resources/haarcascade_frontalface_default.xml";
        File haarFile = new File(haarCascadePath);
        if (haarFile.exists()) {
            System.out.println("   ✓ Haar Cascade file found: " + haarCascadePath);
        } else {
            System.out.println("   ✗ Haar Cascade file not found: " + haarCascadePath);
            allPassed = false;
        }

        // Check 4: Camera Access
        System.out.println("\n4. Checking Camera Access...");
        try {
            VideoCapture camera = new VideoCapture(0);
            if (camera.isOpened()) {
                System.out.println("   ✓ Camera accessible (index 0)");
                camera.release();
            } else {
                System.out.println("   ⚠ Warning: Camera not accessible");
                System.out.println("   Note: Camera may be in use by another application");
                allPassed = false;
            }
        } catch (Exception e) {
            System.out.println("   ✗ Error accessing camera: " + e.getMessage());
            allPassed = false;
        }

        // Check 5: Database Configuration
        System.out.println("\n5. Checking Database Configuration...");
        try {
            String dbUrl = DatabaseConfig.getUrl();
            String dbUser = DatabaseConfig.getUsername();
            System.out.println("   Database URL: " + dbUrl);
            System.out.println("   Database User: " + dbUser);
            System.out.println("   ✓ Configuration file loaded");
        } catch (Exception e) {
            System.out.println("   ✗ Error loading configuration: " + e.getMessage());
            allPassed = false;
        }

        // Check 6: Database Connection
        System.out.println("\n6. Checking Database Connection...");
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("   ✓ Database connection successful");
                System.out.println("   Database: " + DatabaseConfig.getDatabase());
            } else {
                System.out.println("   ✗ Database connection failed");
                allPassed = false;
            }
        } catch (Exception e) {
            System.out.println("   ✗ Database connection error: " + e.getMessage());
            System.out.println("   Please check:");
            System.out.println("   - MySQL server is running");
            System.out.println("   - Credentials in db.properties are correct");
            System.out.println("   - Database exists or can be created");
            allPassed = false;
        }

        // Check 7: Required Directories
        System.out.println("\n7. Checking Required Directories...");
        String[] requiredDirs = {"dataset", "trainer"};
        for (String dirName : requiredDirs) {
            File dir = new File(dirName);
            if (!dir.exists()) {
                if (dir.mkdirs()) {
                    System.out.println("   ✓ Created directory: " + dirName);
                } else {
                    System.out.println("   ⚠ Warning: Could not create directory: " + dirName);
                }
            } else {
                System.out.println("   ✓ Directory exists: " + dirName);
            }
        }

        // Check 8: Database Tables
        System.out.println("\n8. Checking Database Tables...");
        try {
            DatabaseConnection.initializeDatabase();
            System.out.println("   ✓ Database tables verified/created");
        } catch (Exception e) {
            System.out.println("   ✗ Error initializing database: " + e.getMessage());
            allPassed = false;
        }

        // Final Summary
        System.out.println("\n" + "═".repeat(70));
        if (allPassed) {
            System.out.println("                ✓ ALL CHECKS PASSED");
            System.out.println("    System is ready! You can now run the application.");
        } else {
            System.out.println("                ⚠ SOME CHECKS FAILED");
            System.out.println("    Please fix the issues above before running the application.");
        }
        System.out.println("═".repeat(70));

        // System Info
        System.out.println("\n📊 System Information:");
        System.out.println("   OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version"));
        System.out.println("   Java Home: " + System.getProperty("java.home"));
        System.out.println("   User Directory: " + System.getProperty("user.dir"));
        System.out.println("   Available Processors: " + Runtime.getRuntime().availableProcessors());

        // Cleanup
        DatabaseConnection.closeConnection();

        System.out.println("\n✓ Verification complete!\n");
    }
}

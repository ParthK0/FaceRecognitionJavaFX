package com.myapp.service;

import com.myapp.dao.StudentDAO;
import com.myapp.model.Student;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.global.opencv_highgui;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;

import java.io.File;
import java.sql.SQLException;

/**
 * Service for capturing and creating facial dataset
 * Integrates with database to associate facial data with students
 */
public class FacialDataCaptureService {
    private final StudentDAO studentDAO;
    private static final String HAAR_CASCADE_PATH = "src/main/resources/haarcascade_frontalface_default.xml";
    private static final int DEFAULT_IMAGE_COUNT = 50;

    public FacialDataCaptureService() {
        this.studentDAO = new StudentDAO();
    }

    /**
     * Capture facial images for a student
     */
    public boolean captureFacialData(int studentId, int imageCount) throws SQLException {
        // Get student from database
        Student student = studentDAO.getStudentById(studentId);
        if (student == null) {
            System.err.println("✗ Student with ID " + studentId + " not found");
            return false;
        }

        if (!student.isActive()) {
            System.err.println("✗ Student is not active");
            return false;
        }

        System.out.println("\n╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║            FACIAL DATA CAPTURE SYSTEM                         ║");
        System.out.println("╠═══════════════════════════════════════════════════════════════╣");
        System.out.println("║ Student: " + String.format("%-50s", student.getFullName()) + "║");
        System.out.println("║ Admission No: " + String.format("%-46s", student.getAdmissionNumber()) + "║");
        System.out.println("║ Images to capture: " + String.format("%-41d", imageCount) + "║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝\n");

        // Create dataset directory
        String datasetPath = "dataset/" + student.getFullName().toLowerCase().replaceAll("\\s+", "_");
        File datasetDir = new File(datasetPath);
        if (!datasetDir.exists()) {
            datasetDir.mkdirs();
            System.out.println("✓ Created dataset directory: " + datasetPath);
        }

        // Initialize face detector
        CascadeClassifier faceDetector = new CascadeClassifier(HAAR_CASCADE_PATH);
        if (faceDetector.empty()) {
            System.err.println("✗ Failed to load Haar Cascade classifier");
            return false;
        }

        // Open camera
        VideoCapture camera = new VideoCapture(0);
        if (!camera.isOpened()) {
            System.err.println("✗ Failed to open camera");
            return false;
        }

        System.out.println("✓ Camera initialized successfully");
        System.out.println("✓ Face detector loaded");
        System.out.println("\n📸 Instructions:");
        System.out.println("   - Look directly at the camera");
        System.out.println("   - Move your face slightly for different angles");
        System.out.println("   - Press 'q' to quit early\n");
        System.out.println("Starting capture in 3 seconds...\n");

        // Wait for 3 seconds
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Mat frame = new Mat();
        Mat grayFrame = new Mat();
        int capturedCount = 0;
        int frameCount = 0;

        while (capturedCount < imageCount) {
            camera.read(frame);
            if (frame.empty()) {
                continue;
            }

            frameCount++;

            // Convert to grayscale
            opencv_imgproc.cvtColor(frame, grayFrame, opencv_imgproc.COLOR_BGR2GRAY);

            // Detect faces
            RectVector faces = new RectVector();
            faceDetector.detectMultiScale(grayFrame, faces, 1.1, 5, 0, new Size(30, 30), new Size());

            // Process detected faces
            if (faces.size() > 0) {
                Rect faceRect = faces.get(0); // Take first face
                
                // Extract face region
                Mat faceImage = new Mat(grayFrame, faceRect);
                
                // Save image every 5 frames to avoid too similar images
                if (frameCount % 5 == 0) {
                    String filename = datasetPath + "/" + String.format("%03d", capturedCount + 1) + ".jpg";
                    opencv_imgcodecs.imwrite(filename, faceImage);
                    capturedCount++;
                    
                    // Progress indicator
                    int progress = (capturedCount * 100) / imageCount;
                    System.out.print("\r✓ Progress: " + progress + "% [" + capturedCount + "/" + imageCount + "] ");
                    System.out.print("█".repeat(progress / 5) + " ".repeat(20 - progress / 5));
                }

                // Draw rectangle around face
                opencv_imgproc.rectangle(frame, 
                    new Point(faceRect.x(), faceRect.y()),
                    new Point(faceRect.x() + faceRect.width(), faceRect.y() + faceRect.height()),
                    new Scalar(0, 255, 0, 0), 2, 0, 0);
                
                // Display count on frame
                opencv_imgproc.putText(frame, 
                    "Captured: " + capturedCount + "/" + imageCount,
                    new Point(10, 30),
                    opencv_imgproc.FONT_HERSHEY_SIMPLEX,
                    1.0,
                    new Scalar(0, 255, 0, 0),
                    2,
                    opencv_imgproc.LINE_AA,
                    false);
            } else {
                // No face detected
                opencv_imgproc.putText(frame, 
                    "No face detected - Please look at camera",
                    new Point(10, 30),
                    opencv_imgproc.FONT_HERSHEY_SIMPLEX,
                    0.7,
                    new Scalar(0, 0, 255, 0),
                    2,
                    opencv_imgproc.LINE_AA,
                    false);
            }

            // Show frame
            opencv_highgui.imshow("Facial Data Capture - " + student.getFullName(), frame);

            // Check for quit key
            int key = opencv_highgui.waitKey(30);
            if (key == 'q' || key == 'Q') {
                System.out.println("\n\n⚠ Capture interrupted by user");
                break;
            }
        }

        // Cleanup
        camera.release();
        opencv_highgui.destroyAllWindows();
        frame.release();
        grayFrame.release();

        System.out.println("\n\n✓ Facial data capture completed!");
        System.out.println("✓ Total images captured: " + capturedCount);
        System.out.println("✓ Images saved to: " + datasetPath);

        // Update database with facial data path
        if (capturedCount > 0) {
            studentDAO.updateFacialDataPath(studentId, datasetPath);
            System.out.println("✓ Database updated with facial data path");
            return true;
        }

        return false;
    }

    /**
     * Capture facial data with default image count
     */
    public boolean captureFacialData(int studentId) throws SQLException {
        return captureFacialData(studentId, DEFAULT_IMAGE_COUNT);
    }

    /**
     * Capture facial data by admission number
     */
    public boolean captureFacialDataByAdmission(String admissionNumber, int imageCount) throws SQLException {
        Student student = studentDAO.getStudentByAdmissionNumber(admissionNumber);
        if (student == null) {
            System.err.println("✗ Student with admission number " + admissionNumber + " not found");
            return false;
        }
        return captureFacialData(student.getStudentId(), imageCount);
    }

    /**
     * Check if student has facial data
     */
    public boolean hasFacialData(int studentId) throws SQLException {
        Student student = studentDAO.getStudentById(studentId);
        if (student == null) {
            return false;
        }
        
        String facialDataPath = student.getFacialDataPath();
        if (facialDataPath == null || facialDataPath.isEmpty()) {
            return false;
        }
        
        File datasetDir = new File(facialDataPath);
        return datasetDir.exists() && datasetDir.isDirectory() && datasetDir.listFiles() != null 
               && datasetDir.listFiles().length > 0;
    }

    /**
     * Get image count for a student
     */
    public int getImageCount(int studentId) throws SQLException {
        Student student = studentDAO.getStudentById(studentId);
        if (student == null || student.getFacialDataPath() == null) {
            return 0;
        }
        
        File datasetDir = new File(student.getFacialDataPath());
        if (!datasetDir.exists() || !datasetDir.isDirectory()) {
            return 0;
        }
        
        File[] files = datasetDir.listFiles((dir, name) -> 
            name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".jpeg") || 
            name.toLowerCase().endsWith(".png"));
        
        return files != null ? files.length : 0;
    }
}

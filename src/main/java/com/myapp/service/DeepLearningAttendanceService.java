package com.myapp.service;

import com.myapp.dao.RecognitionLogDAO;
import com.myapp.dao.StudentDAO;
import com.myapp.ml.DeepLearningRecognizer;
import com.myapp.ml.DNNFaceDetector;
import com.myapp.model.Attendance;
import com.myapp.model.Student;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;
import org.bytedeco.opencv.global.opencv_highgui;
import org.bytedeco.opencv.global.opencv_imgproc;

import java.sql.SQLException;
import java.util.List;

/**
 * Deep Learning based Face Recognition Attendance Service
 * Uses advanced DNN face detection and FaceNet embeddings
 */
public class DeepLearningAttendanceService {
    private final StudentDAO studentDAO;
    private final AttendanceService attendanceService;
    private final RecognitionLogDAO recognitionLogDAO;
    private DeepLearningRecognizer recognizer;
    private DNNFaceDetector faceDetector;

    public DeepLearningAttendanceService() {
        this.studentDAO = new StudentDAO();
        this.attendanceService = new AttendanceService();
        this.recognitionLogDAO = new RecognitionLogDAO();
    }

    /**
     * Start real-time face recognition with automatic attendance marking
     */
    public void startAttendanceRecognition(int courseId, Attendance.SessionType sessionType) {
        System.out.println("\n╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║     DEEP LEARNING FACE RECOGNITION ATTENDANCE SYSTEM          ║");
        System.out.println("╠═══════════════════════════════════════════════════════════════╣");
        System.out.println("║ Course ID: " + String.format("%-50d", courseId) + "║");
        System.out.println("║ Session: " + String.format("%-52s", sessionType.getDisplayName()) + "║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝\n");

        try {
            // Initialize recognizer and detector
            recognizer = new DeepLearningRecognizer();
            faceDetector = new DNNFaceDetector();
            System.out.println("✓ Deep learning models loaded");

            // Verify we have students with embeddings
            List<Student> students = studentDAO.getStudentsWithFacialData();
            if (students.isEmpty()) {
                System.err.println("✗ No students found with facial data");
                System.err.println("  Please train the model first");
                return;
            }
            System.out.println("✓ Loaded " + students.size() + " student records");

        } catch (Exception e) {
            System.err.println("✗ Failed to initialize: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        // Open camera
        VideoCapture camera = new VideoCapture(0);
        if (!camera.isOpened()) {
            System.err.println("✗ Failed to open camera");
            cleanup();
            return;
        }
        System.out.println("✓ Camera initialized\n");

        System.out.println("📹 Face recognition started...");
        System.out.println("   Press 'q' to quit\n");

        Mat frame = new Mat();
        long lastRecognitionTime = 0;
        int lastRecognizedStudentId = -1;
        final long RECOGNITION_COOLDOWN = 5000; // 5 seconds cooldown

        while (true) {
            camera.read(frame);
            if (frame.empty()) {
                continue;
            }

            long currentTime = System.currentTimeMillis();

            // Detect faces
            List<Rect> faces = faceDetector.detectFaces(frame);

            // Process each detected face
            for (Rect faceRect : faces) {
                // Draw rectangle around face
                Scalar color = new Scalar(0, 255, 0, 0); // Green
                opencv_imgproc.rectangle(frame, faceRect, color, 2, 0, 0);

                // Perform recognition periodically
                if ((currentTime - lastRecognitionTime) > RECOGNITION_COOLDOWN) {
                    DeepLearningRecognizer.RecognitionResult result = recognizer.recognize(frame);

                    if (result.isRecognized()) {
                        int studentId = result.getStudentId();
                        
                        // Only mark if it's a different student or cooldown has passed
                        if (studentId != lastRecognizedStudentId) {
                            try {
                                // Try to mark attendance
                                boolean marked = attendanceService.markAttendance(
                                    studentId, courseId, sessionType);

                                if (marked) {
                                    System.out.println("✓ ATTENDANCE MARKED: " + result.getStudentName() +
                                                     " (" + result.getAdmissionNumber() + ")");
                                    System.out.println("  Confidence: " + 
                                                     String.format("%.1f%%", result.getConfidence() * 100));
                                    color = new Scalar(0, 255, 0, 0); // Green for success
                                } else {
                                    System.out.println("⚠ Already marked: " + result.getStudentName());
                                    color = new Scalar(255, 165, 0, 0); // Orange for duplicate
                                }

                                lastRecognizedStudentId = studentId;
                                lastRecognitionTime = currentTime;

                            } catch (SQLException e) {
                                System.err.println("✗ Database error: " + e.getMessage());
                            }
                        }

                        // Draw name on frame
                        String label = result.getStudentName() + " - " + 
                                     String.format("%.0f%%", result.getConfidence() * 100);
                        opencv_imgproc.putText(frame, label,
                                             new Point(faceRect.x(), faceRect.y() - 10),
                                             opencv_imgproc.FONT_HERSHEY_SIMPLEX,
                                             0.6, new Scalar(255, 255, 255, 0), 2, 0, false);
                    } else {
                        // Unknown face
                        color = new Scalar(0, 0, 255, 0); // Red for unknown
                        opencv_imgproc.putText(frame, "Unknown",
                                             new Point(faceRect.x(), faceRect.y() - 10),
                                             opencv_imgproc.FONT_HERSHEY_SIMPLEX,
                                             0.6, new Scalar(0, 0, 255, 0), 2, 0, false);

                        // Reset if unknown face
                        if ((currentTime - lastRecognitionTime) > RECOGNITION_COOLDOWN) {
                            lastRecognizedStudentId = -1;
                            lastRecognitionTime = currentTime;
                        }
                    }
                }
            }

            // Display status text
            String statusText = faces.size() + " face(s) detected";
            opencv_imgproc.putText(frame, statusText,
                                 new Point(10, 30),
                                 opencv_imgproc.FONT_HERSHEY_SIMPLEX,
                                 0.7, new Scalar(255, 255, 255, 0), 2, 0, false);

            // Display frame
            opencv_highgui.imshow("Deep Learning Attendance - Press 'q' to quit", frame);

            // Check for quit key
            if (opencv_highgui.waitKey(30) == 'q') {
                break;
            }
        }

        camera.release();
        opencv_highgui.destroyAllWindows();
        cleanup();
        System.out.println("\n✓ Attendance session ended");
    }

    /**
     * Recognize a single face from camera (one-shot recognition)
     */
    public DeepLearningRecognizer.RecognitionResult recognizeOnce() {
        try {
            if (recognizer == null) {
                recognizer = new DeepLearningRecognizer();
            }
            if (faceDetector == null) {
                faceDetector = new DNNFaceDetector();
            }

            VideoCapture camera = new VideoCapture(0);
            if (!camera.isOpened()) {
                System.err.println("✗ Failed to open camera");
                return null;
            }

            Mat frame = new Mat();
            DeepLearningRecognizer.RecognitionResult result = null;

            System.out.println("Looking for face... Press 'q' to cancel");

            while (true) {
                camera.read(frame);
                if (frame.empty()) {
                    continue;
                }

                // Detect faces
                List<Rect> faces = faceDetector.detectFaces(frame);

                if (!faces.isEmpty()) {
                    // Draw rectangles
                    for (Rect face : faces) {
                        opencv_imgproc.rectangle(frame, face, 
                                               new Scalar(0, 255, 0, 0), 2, 0, 0);
                    }

                    // Recognize
                    result = recognizer.recognize(frame);
                    
                    if (result.isRecognized()) {
                        System.out.println("✓ Recognized: " + result);
                        break;
                    }
                }

                opencv_highgui.imshow("Recognition - Press 'q' to cancel", frame);
                if (opencv_highgui.waitKey(30) == 'q') {
                    break;
                }
            }

            camera.release();
            opencv_highgui.destroyAllWindows();
            return result;

        } catch (Exception e) {
            System.err.println("✗ Error during recognition: " + e.getMessage());
            return null;
        }
    }

    /**
     * Get recognition statistics
     */
    public void printStatistics() {
        try {
            java.time.LocalDateTime startOfDay = java.time.LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
            java.time.LocalDateTime endOfDay = java.time.LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);

            java.util.Map<String, Object> stats = recognitionLogDAO.getRecognitionStatistics(startOfDay, endOfDay);

            System.out.println("\n╔═══════════════════════════════════════════════════════════════╗");
            System.out.println("║     RECOGNITION STATISTICS (TODAY)                            ║");
            System.out.println("╠═══════════════════════════════════════════════════════════════╣");
            System.out.println("║ Total Attempts:    " + String.format("%-44d", stats.get("total_attempts")) + "║");
            System.out.println("║ Successful:        " + String.format("%-44d", stats.get("successful")) + "║");
            System.out.println("║ Failed:            " + String.format("%-44d", stats.get("failed")) + "║");
            System.out.println("║ Unknown:           " + String.format("%-44d", stats.get("unknown")) + "║");
            System.out.println("║ Avg Confidence:    " + String.format("%-44.2f", stats.get("avg_confidence")) + "║");
            System.out.println("╚═══════════════════════════════════════════════════════════════╝\n");

        } catch (SQLException e) {
            System.err.println("✗ Failed to retrieve statistics: " + e.getMessage());
        }
    }

    /**
     * Clean up resources
     */
    private void cleanup() {
        if (recognizer != null) {
            recognizer.close();
        }
        if (faceDetector != null) {
            faceDetector.close();
        }
    }
}

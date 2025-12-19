package com.myapp.service;

import com.myapp.dao.StudentDAO;
import com.myapp.model.Attendance;
import com.myapp.model.Student;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;
import org.bytedeco.opencv.global.opencv_face;
import org.bytedeco.opencv.global.opencv_highgui;
import org.bytedeco.opencv.global.opencv_imgproc;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

/**
 * Service for real-time face recognition with attendance marking
 */
public class FaceRecognitionAttendanceService {
    private final StudentDAO studentDAO;
    private final AttendanceService attendanceService;
    private static final String HAAR_CASCADE_PATH = "src/main/resources/haarcascade_frontalface_default.xml";
    private static final String TRAINER_FILE = "trainer/multi.yml";
    private static final double CONFIDENCE_THRESHOLD = 80.0;

    public FaceRecognitionAttendanceService() {
        this.studentDAO = new StudentDAO();
        this.attendanceService = new AttendanceService();
    }

    /**
     * Start real-time face recognition and attendance marking
     */
    public void startAttendanceRecognition(int courseId, Attendance.SessionType sessionType) {
        System.out.println("\n╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║     FACIAL RECOGNITION BASED ATTENDANCE SYSTEM                ║");
        System.out.println("╠═══════════════════════════════════════════════════════════════╣");
        System.out.println("║ Course ID: " + String.format("%-50d", courseId) + "║");
        System.out.println("║ Session: " + String.format("%-52s", sessionType.getDisplayName()) + "║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝\n");

        // Load face recognizer
        File trainerFile = new File(TRAINER_FILE);
        if (!trainerFile.exists()) {
            System.err.println("✗ Trainer file not found: " + TRAINER_FILE);
            System.err.println("✗ Please train the model first using the training option");
            return;
        }

        LBPHFaceRecognizer recognizer = LBPHFaceRecognizer.create();
        recognizer.read(TRAINER_FILE);
        System.out.println("✓ Face recognizer model loaded");

        // Load face detector
        CascadeClassifier faceDetector = new CascadeClassifier(HAAR_CASCADE_PATH);
        if (faceDetector.empty()) {
            System.err.println("✗ Failed to load Haar Cascade classifier");
            return;
        }
        System.out.println("✓ Face detector loaded");

        // Load student labels mapping
        try {
            List<Student> students = studentDAO.getStudentsWithFacialData();
            if (students.isEmpty()) {
                System.err.println("✗ No students found with facial data");
                return;
            }
            System.out.println("✓ Loaded " + students.size() + " student records");
        } catch (SQLException e) {
            System.err.println("✗ Error loading student data: " + e.getMessage());
            return;
        }

        // Open camera
        VideoCapture camera = new VideoCapture(0);
        if (!camera.isOpened()) {
            System.err.println("✗ Failed to open camera");
            return;
        }
        System.out.println("✓ Camera initialized\n");

        System.out.println("📹 Face recognition started...");
        System.out.println("   Press 'q' to quit\n");

        Mat frame = new Mat();
        Mat grayFrame = new Mat();
        IntPointer label = new IntPointer(1);
        DoublePointer confidence = new DoublePointer(1);

        long lastRecognitionTime = 0;
        int lastRecognizedId = -1;
        final long RECOGNITION_COOLDOWN = 5000; // 5 seconds cooldown

        while (true) {
            camera.read(frame);
            if (frame.empty()) {
                continue;
            }

            // Convert to grayscale
            opencv_imgproc.cvtColor(frame, grayFrame, opencv_imgproc.COLOR_BGR2GRAY);

            // Detect faces
            RectVector faces = new RectVector();
            faceDetector.detectMultiScale(grayFrame, faces, 1.1, 5, 0, new Size(30, 30), new Size());

            // Process each detected face
            for (int i = 0; i < faces.size(); i++) {
                Rect faceRect = faces.get(i);
                Mat faceROI = new Mat(grayFrame, faceRect);

                // Recognize face
                recognizer.predict(faceROI, label, confidence);
                int predictedLabel = label.get(0);
                double predictionConfidence = confidence.get(0);

                // Draw rectangle around face
                Scalar color = new Scalar(0, 255, 0, 0); // Green by default
                String displayText = "Unknown";

                // If confidence is good enough
                if (predictionConfidence < CONFIDENCE_THRESHOLD) {
                    try {
                        // Get student by ID (label is student_id)
                        Student student = studentDAO.getStudentById(predictedLabel);
                        
                        if (student != null && student.isActive()) {
                            displayText = student.getFullName();
                            
                            // Check if enough time has passed since last recognition
                            long currentTime = System.currentTimeMillis();
                            if (predictedLabel != lastRecognizedId || 
                                (currentTime - lastRecognitionTime) > RECOGNITION_COOLDOWN) {
                                
                                // Try to mark attendance
                                try {
                                    boolean marked = attendanceService.markAttendance(
                                        student.getStudentId(), courseId, sessionType);
                                    
                                    if (marked) {
                                        color = new Scalar(0, 255, 0, 0); // Green for success
                                        System.out.println("✓ ATTENDANCE MARKED: " + student.getFullName() + 
                                                         " (" + student.getAdmissionNumber() + ")");
                                    } else {
                                        color = new Scalar(255, 165, 0, 0); // Orange for already marked
                                    }
                                    
                                    lastRecognizedId = predictedLabel;
                                    lastRecognitionTime = currentTime;
                                    
                                } catch (SQLException e) {
                                    System.err.println("✗ Error marking attendance: " + e.getMessage());
                                    color = new Scalar(0, 0, 255, 0); // Red for error
                                }
                            }
                            
                            displayText += " (" + String.format("%.1f", predictionConfidence) + "%)";
                        } else {
                            displayText = "Unknown Student";
                            color = new Scalar(0, 0, 255, 0); // Red
                        }
                    } catch (SQLException e) {
                        System.err.println("✗ Database error: " + e.getMessage());
                        color = new Scalar(0, 0, 255, 0); // Red
                    }
                } else {
                    color = new Scalar(0, 0, 255, 0); // Red for low confidence
                    displayText = "Unknown (Low confidence)";
                }

                // Draw rectangle and text
                opencv_imgproc.rectangle(frame,
                    new Point(faceRect.x(), faceRect.y()),
                    new Point(faceRect.x() + faceRect.width(), faceRect.y() + faceRect.height()),
                    color, 2, 0, 0);
                opencv_imgproc.putText(frame, displayText,
                    new Point(faceRect.x(), faceRect.y() - 10),
                    opencv_imgproc.FONT_HERSHEY_SIMPLEX,
                    0.7,
                    color,
                    2,
                    opencv_imgproc.LINE_AA,
                    false);

                faceROI.release();
            }

            // Display info text
            opencv_imgproc.putText(frame,
                "Press 'q' to quit | Green = Recognized | Red = Unknown",
                new Point(10, 30),
                opencv_imgproc.FONT_HERSHEY_SIMPLEX,
                0.6,
                new Scalar(255, 255, 255, 0),
                1,
                opencv_imgproc.LINE_AA,
                false);

            // Show frame
            opencv_highgui.imshow("Face Recognition Attendance System", frame);

            // Check for quit
            int key = opencv_highgui.waitKey(30);
            if (key == 'q' || key == 'Q' || key == 27) { // 'q' or ESC
                System.out.println("\n✓ Face recognition stopped");
                break;
            }
        }

        // Cleanup
        camera.release();
        opencv_highgui.destroyAllWindows();
        frame.release();
        grayFrame.release();
        
        // Display today's attendance summary
        try {
            System.out.println("\n");
            attendanceService.displayTodayAttendanceSummary();
        } catch (SQLException e) {
            System.err.println("✗ Error displaying summary: " + e.getMessage());
        }
    }

    /**
     * Test face recognition without marking attendance
     */
    public void testFaceRecognition() {
        System.out.println("\n╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║         FACE RECOGNITION TEST MODE (No Attendance)            ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝\n");

        // Similar to startAttendanceRecognition but without marking attendance
        // Implementation can be added if needed
        System.out.println("Test mode - Recognition without attendance marking");
        System.out.println("(Feature can be implemented as needed)");
    }
}

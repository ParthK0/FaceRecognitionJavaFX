package com.myapp.service;

import com.myapp.dao.StudentDAO;
import com.myapp.ml.DNNFaceDetector;
import com.myapp.model.Attendance;
import com.myapp.model.Student;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;
import org.bytedeco.opencv.global.opencv_imgproc;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Advanced Face Recognition Service with DNN detection and real-time feedback
 * Provides production-ready face detection and recognition capabilities
 */
public class AdvancedFaceRecognitionService {
    private final StudentDAO studentDAO;
    private final AttendanceService attendanceService;
    private DNNFaceDetector dnnDetector;
    private LBPHFaceRecognizer recognizer;
    
    private static final String TRAINER_FILE = "trainer/multi.yml";
    private static final double CONFIDENCE_THRESHOLD = 75.0; // Lower is better for LBPH
    private static final long RECOGNITION_COOLDOWN = 5000; // 5 seconds
    
    private VideoCapture camera;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private Map<Integer, Long> lastRecognitionTime = new HashMap<>();
    
    // Callback interface for real-time updates
    public interface RecognitionCallback {
        void onFrameProcessed(BufferedImage frame, List<FaceDetection> detections);
        void onAttendanceMarked(Student student, boolean success, String message);
        void onError(String error);
    }
    
    /**
     * Represents a detected face with recognition results
     */
    public static class FaceDetection {
        public final Rect rect;
        public final Student student;
        public final double confidence;
        public final RecognitionStatus status;
        
        public enum RecognitionStatus {
            RECOGNIZED, UNKNOWN, LOW_CONFIDENCE, ALREADY_MARKED
        }
        
        public FaceDetection(Rect rect, Student student, double confidence, RecognitionStatus status) {
            this.rect = rect;
            this.student = student;
            this.confidence = confidence;
            this.status = status;
        }
    }
    
    public AdvancedFaceRecognitionService() {
        this.studentDAO = new StudentDAO();
        this.attendanceService = new AttendanceService();
    }
    
    /**
     * Initialize the recognition system
     */
    public boolean initialize() {
        try {
            // Initialize DNN face detector
            System.out.println("Initializing DNN face detector...");
            dnnDetector = new DNNFaceDetector();
            System.out.println("✓ DNN face detector ready");
            
            // Load face recognizer
            File trainerFile = new File(TRAINER_FILE);
            if (!trainerFile.exists()) {
                System.err.println("✗ Trainer file not found: " + TRAINER_FILE);
                return false;
            }
            
            recognizer = LBPHFaceRecognizer.create();
            recognizer.read(TRAINER_FILE);
            System.out.println("✓ Face recognizer model loaded");
            
            return true;
        } catch (Exception e) {
            System.err.println("✗ Initialization failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Start camera capture
     */
    public boolean startCamera() {
        try {
            camera = new VideoCapture(0);
            if (!camera.isOpened()) {
                System.err.println("✗ Failed to open camera");
                return false;
            }
            
            // Set camera properties for better quality
            camera.set(org.bytedeco.opencv.global.opencv_videoio.CAP_PROP_FRAME_WIDTH, 1280);
            camera.set(org.bytedeco.opencv.global.opencv_videoio.CAP_PROP_FRAME_HEIGHT, 720);
            camera.set(org.bytedeco.opencv.global.opencv_videoio.CAP_PROP_FPS, 30);
            
            System.out.println("✓ Camera started (1280x720 @ 30fps)");
            return true;
        } catch (Exception e) {
            System.err.println("✗ Camera initialization failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Start recognition with callback for UI updates
     */
    public void startRecognition(int courseId, Attendance.SessionType sessionType, RecognitionCallback callback) {
        if (isRunning.get()) {
            callback.onError("Recognition is already running");
            return;
        }
        
        isRunning.set(true);
        lastRecognitionTime.clear();
        
        // Run in background thread
        new Thread(() -> {
            Mat frame = new Mat();
            Mat grayFrame = new Mat();
            
            try {
                while (isRunning.get()) {
                    // Capture frame
                    if (!camera.read(frame) || frame.empty()) {
                        Thread.sleep(10);
                        continue;
                    }
                    
                    // Convert to grayscale for recognition
                    opencv_imgproc.cvtColor(frame, grayFrame, opencv_imgproc.COLOR_BGR2GRAY);
                    
                    // Detect faces using DNN
                    List<Rect> faceRects = dnnDetector.detectFaces(frame);
                    List<FaceDetection> detections = new ArrayList<>();
                    
                    // Process each detected face
                    for (Rect faceRect : faceRects) {
                        FaceDetection detection = processFace(grayFrame, faceRect, courseId, sessionType, callback);
                        if (detection != null) {
                            detections.add(detection);
                            
                            // Draw on frame
                            drawDetection(frame, detection);
                        }
                    }
                    
                    // Convert to BufferedImage and send to callback
                    BufferedImage bufferedImage = matToBufferedImage(frame);
                    if (bufferedImage != null) {
                        callback.onFrameProcessed(bufferedImage, detections);
                    }
                    
                    // Control frame rate
                    Thread.sleep(33); // ~30 FPS
                }
            } catch (Exception e) {
                callback.onError("Recognition error: " + e.getMessage());
                e.printStackTrace();
            } finally {
                frame.release();
                grayFrame.release();
            }
        }, "FaceRecognitionThread").start();
    }
    
    /**
     * Process a single detected face
     */
    private FaceDetection processFace(Mat grayFrame, Rect faceRect, int courseId, 
                                      Attendance.SessionType sessionType, RecognitionCallback callback) {
        try {
            // Extract face ROI
            Mat faceROI = new Mat(grayFrame, faceRect);
            
            // Resize for recognition (LBPH works better with consistent size)
            Mat resizedFace = new Mat();
            opencv_imgproc.resize(faceROI, resizedFace, new Size(200, 200));
            
            // Predict
            IntPointer label = new IntPointer(1);
            DoublePointer confidence = new DoublePointer(1);
            recognizer.predict(resizedFace, label, confidence);
            
            int predictedLabel = label.get(0);
            double predictionConfidence = confidence.get(0);
            
            faceROI.release();
            resizedFace.release();
            
            // Determine status
            if (predictionConfidence < CONFIDENCE_THRESHOLD) {
                // Good confidence - try to get student
                Student student = studentDAO.getStudentById(predictedLabel);
                
                if (student != null && student.isActive()) {
                    // Check cooldown
                    long currentTime = System.currentTimeMillis();
                    Long lastTime = lastRecognitionTime.get(predictedLabel);
                    
                    if (lastTime == null || (currentTime - lastTime) > RECOGNITION_COOLDOWN) {
                        // Try to mark attendance
                        try {
                            boolean marked = attendanceService.markAttendance(
                                student.getStudentId(), courseId, sessionType);
                            
                            lastRecognitionTime.put(predictedLabel, currentTime);
                            
                            if (marked) {
                                callback.onAttendanceMarked(student, true, 
                                    "Attendance marked successfully");
                                return new FaceDetection(faceRect, student, predictionConfidence,
                                    FaceDetection.RecognitionStatus.RECOGNIZED);
                            } else {
                                callback.onAttendanceMarked(student, false, 
                                    "Attendance already marked");
                                return new FaceDetection(faceRect, student, predictionConfidence,
                                    FaceDetection.RecognitionStatus.ALREADY_MARKED);
                            }
                        } catch (SQLException e) {
                            callback.onError("Database error: " + e.getMessage());
                            return new FaceDetection(faceRect, student, predictionConfidence,
                                FaceDetection.RecognitionStatus.RECOGNIZED);
                        }
                    } else {
                        // Within cooldown
                        return new FaceDetection(faceRect, student, predictionConfidence,
                            FaceDetection.RecognitionStatus.ALREADY_MARKED);
                    }
                }
            }
            
            // Unknown or low confidence
            return new FaceDetection(faceRect, null, predictionConfidence,
                predictionConfidence >= CONFIDENCE_THRESHOLD ? 
                    FaceDetection.RecognitionStatus.LOW_CONFIDENCE :
                    FaceDetection.RecognitionStatus.UNKNOWN);
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Draw detection results on frame
     */
    private void drawDetection(Mat frame, FaceDetection detection) {
        Scalar color;
        String text;
        
        switch (detection.status) {
            case RECOGNIZED:
                color = new Scalar(0, 255, 0, 0); // Green
                text = detection.student.getFullName() + 
                       String.format(" (%.1f%%)", 100 - detection.confidence);
                break;
            case ALREADY_MARKED:
                color = new Scalar(0, 165, 255, 0); // Orange
                text = detection.student.getFullName() + " - Present";
                break;
            case LOW_CONFIDENCE:
                color = new Scalar(0, 0, 255, 0); // Red
                text = String.format("Unknown (%.1f%%)", 100 - detection.confidence);
                break;
            default:
                color = new Scalar(0, 0, 255, 0); // Red
                text = "Unknown";
        }
        
        // Draw rectangle
        opencv_imgproc.rectangle(frame,
            new Point(detection.rect.x(), detection.rect.y()),
            new Point(detection.rect.x() + detection.rect.width(), 
                     detection.rect.y() + detection.rect.height()),
            color, 3, opencv_imgproc.LINE_AA, 0);
        
        // Draw text background
        int baseline[] = new int[1];
        Size textSize = opencv_imgproc.getTextSize(text, 
            opencv_imgproc.FONT_HERSHEY_DUPLEX, 0.7, 2, baseline);
        
        opencv_imgproc.rectangle(frame,
            new Point(detection.rect.x(), detection.rect.y() - textSize.height() - 10),
            new Point(detection.rect.x() + (int)textSize.width(), detection.rect.y()),
            color, -1, opencv_imgproc.LINE_AA, 0);
        
        // Draw text
        opencv_imgproc.putText(frame, text,
            new Point(detection.rect.x(), detection.rect.y() - 5),
            opencv_imgproc.FONT_HERSHEY_DUPLEX,
            0.7,
            new Scalar(255, 255, 255, 0),
            2,
            opencv_imgproc.LINE_AA,
            false);
    }
    
    /**
     * Stop recognition
     */
    public void stopRecognition() {
        isRunning.set(false);
    }
    
    /**
     * Stop camera and cleanup
     */
    public void cleanup() {
        stopRecognition();
        
        if (camera != null && camera.isOpened()) {
            camera.release();
        }
        
        System.out.println("✓ Cleanup complete");
    }
    
    /**
     * Check if recognition is running
     */
    public boolean isRunning() {
        return isRunning.get();
    }
    
    /**
     * Convert OpenCV Mat to BufferedImage
     */
    private BufferedImage matToBufferedImage(Mat mat) {
        try {
            int width = mat.cols();
            int height = mat.rows();
            int channels = mat.channels();
            
            byte[] sourcePixels = new byte[width * height * channels];
            mat.data().get(sourcePixels);
            
            BufferedImage image;
            if (channels == 3) {
                image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
                final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
                System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);
            } else {
                image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
                final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
                System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);
            }
            
            return image;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

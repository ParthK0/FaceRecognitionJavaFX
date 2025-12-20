package com.myapp.ml;

import com.myapp.dao.FaceEmbeddingDAO;
import com.myapp.dao.RecognitionLogDAO;
import com.myapp.dao.StudentDAO;
import com.myapp.model.Student;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;
import org.bytedeco.opencv.global.opencv_highgui;
import org.bytedeco.opencv.global.opencv_imgproc;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Deep Learning based face recognizer
 * Uses DNN face detector and FaceNet embeddings for recognition
 */
public class DeepLearningRecognizer {
    private DNNFaceDetector faceDetector;
    private FaceNetEmbeddingGenerator embeddingGenerator;
    private FaceEmbeddingDAO embeddingDAO;
    private StudentDAO studentDAO;
    private RecognitionLogDAO recognitionLogDAO;

    // Threshold for face recognition (cosine similarity)
    // Higher threshold = more strict matching
    private static final double RECOGNITION_THRESHOLD = 0.6;
    
    // Minimum number of embeddings to match against
    private static final int MIN_EMBEDDINGS_FOR_MATCH = 2;

    /**
     * Recognition result class
     */
    public static class RecognitionResult {
        private Integer studentId;
        private String studentName;
        private String admissionNumber;
        private double confidence;
        private boolean recognized;

        public RecognitionResult(Integer studentId, String studentName, String admissionNumber, 
                                double confidence, boolean recognized) {
            this.studentId = studentId;
            this.studentName = studentName;
            this.admissionNumber = admissionNumber;
            this.confidence = confidence;
            this.recognized = recognized;
        }

        public Integer getStudentId() { return studentId; }
        public String getStudentName() { return studentName; }
        public String getAdmissionNumber() { return admissionNumber; }
        public double getConfidence() { return confidence; }
        public boolean isRecognized() { return recognized; }

        @Override
        public String toString() {
            if (recognized) {
                return String.format("%s (%s) - Confidence: %.2f%%", 
                                   studentName, admissionNumber, confidence * 100);
            } else {
                return "Unknown - No match found";
            }
        }
    }

    public DeepLearningRecognizer() throws Exception {
        this.faceDetector = new DNNFaceDetector();
        this.embeddingGenerator = new FaceNetEmbeddingGenerator();
        this.embeddingDAO = new FaceEmbeddingDAO();
        this.studentDAO = new StudentDAO();
        this.recognitionLogDAO = new RecognitionLogDAO();
    }

    /**
     * Recognize a face from an image
     */
    public RecognitionResult recognize(Mat image) {
        try {
            // Detect face
            Rect faceRect = faceDetector.detectLargestFace(image);
            if (faceRect == null) {
                logRecognition(null, 0.0f, RecognitionLogDAO.RecognitionResult.FAILED, 
                             "No face detected");
                return new RecognitionResult(null, null, null, 0.0, false);
            }

            // Extract face region
            Mat faceROI = new Mat(image, faceRect);

            // Generate embedding
            float[] queryEmbedding = embeddingGenerator.generateEmbedding(faceROI);
            faceROI.close();

            if (queryEmbedding == null) {
                logRecognition(null, 0.0f, RecognitionLogDAO.RecognitionResult.FAILED, 
                             "Failed to generate embedding");
                return new RecognitionResult(null, null, null, 0.0, false);
            }

            // Match against stored embeddings
            return matchEmbedding(queryEmbedding);

        } catch (Exception e) {
            System.err.println("Error during recognition: " + e.getMessage());
            return new RecognitionResult(null, null, null, 0.0, false);
        }
    }

    /**
     * Match an embedding against all stored embeddings
     */
    private RecognitionResult matchEmbedding(float[] queryEmbedding) throws SQLException {
        // Get all embeddings from database
        Map<Integer, List<float[]>> allEmbeddings = embeddingDAO.getAllEmbeddings();

        if (allEmbeddings.isEmpty()) {
            logRecognition(null, 0.0f, RecognitionLogDAO.RecognitionResult.UNKNOWN, 
                         "No embeddings in database");
            return new RecognitionResult(null, null, null, 0.0, false);
        }

        int bestMatchStudentId = -1;
        double bestSimilarity = -1.0;

        // Compare with each student's embeddings
        for (Map.Entry<Integer, List<float[]>> entry : allEmbeddings.entrySet()) {
            int studentId = entry.getKey();
            List<float[]> studentEmbeddings = entry.getValue();

            // Calculate average similarity with this student's embeddings
            double avgSimilarity = 0.0;
            int validComparisons = 0;

            for (float[] storedEmbedding : studentEmbeddings) {
                double similarity = FaceNetEmbeddingGenerator.calculateSimilarity(
                    queryEmbedding, storedEmbedding);
                
                if (similarity >= 0) {
                    avgSimilarity += similarity;
                    validComparisons++;
                }
            }

            if (validComparisons > 0) {
                avgSimilarity /= validComparisons;

                if (avgSimilarity > bestSimilarity) {
                    bestSimilarity = avgSimilarity;
                    bestMatchStudentId = studentId;
                }
            }
        }

        // Check if best match meets threshold
        if (bestSimilarity >= RECOGNITION_THRESHOLD && bestMatchStudentId != -1) {
            // Get student details
            Student student = studentDAO.getStudentById(bestMatchStudentId);
            
            if (student != null && student.isActive()) {
                logRecognition(bestMatchStudentId, (float) bestSimilarity, 
                             RecognitionLogDAO.RecognitionResult.SUCCESS, 
                             "Successfully recognized");
                
                return new RecognitionResult(
                    student.getStudentId(),
                    student.getFullName(),
                    student.getAdmissionNumber(),
                    bestSimilarity,
                    true
                );
            }
        }

        // No match found
        logRecognition(null, (float) bestSimilarity, 
                     RecognitionLogDAO.RecognitionResult.UNKNOWN, 
                     "Confidence below threshold");
        return new RecognitionResult(null, null, null, bestSimilarity, false);
    }

    /**
     * Start real-time face recognition from camera
     */
    public void startRealtimeRecognition() {
        System.out.println("\n╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║     DEEP LEARNING FACE RECOGNITION SYSTEM                     ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝\n");
        System.out.println("Starting camera... Press 'q' to quit\n");

        VideoCapture camera = new VideoCapture(0);
        if (!camera.isOpened()) {
            System.err.println("✗ Failed to open camera");
            return;
        }

        Mat frame = new Mat();
        long lastRecognitionTime = 0;
        final long RECOGNITION_COOLDOWN = 3000; // 3 seconds between recognitions

        while (true) {
            camera.read(frame);
            if (frame.empty()) {
                continue;
            }

            long currentTime = System.currentTimeMillis();

            // Detect faces
            List<Rect> faces = faceDetector.detectFaces(frame);

            // Draw rectangles around detected faces
            for (Rect faceRect : faces) {
                // Draw rectangle
                Scalar color = new Scalar(0, 255, 0, 0); // Green
                opencv_imgproc.rectangle(frame, faceRect, color, 2, 0, 0);

                // Perform recognition periodically
                if ((currentTime - lastRecognitionTime) > RECOGNITION_COOLDOWN) {
                    RecognitionResult result = recognize(frame);
                    
                    if (result.isRecognized()) {
                        System.out.println("✓ RECOGNIZED: " + result);
                        color = new Scalar(0, 255, 0, 0); // Green for recognized
                    } else {
                        System.out.println("✗ UNKNOWN PERSON");
                        color = new Scalar(0, 0, 255, 0); // Red for unknown
                    }
                    
                    lastRecognitionTime = currentTime;
                }

                // Add label
                String label = faces.size() + " face(s) detected";
                opencv_imgproc.putText(frame, label, 
                                      new Point(10, 30),
                                      opencv_imgproc.FONT_HERSHEY_SIMPLEX,
                                      0.7, new Scalar(255, 255, 255, 0), 2, 0, false);
            }

            // Display frame
            opencv_highgui.imshow("Face Recognition - Press 'q' to quit", frame);

            // Check for quit key
            if (opencv_highgui.waitKey(30) == 'q') {
                break;
            }
        }

        camera.release();
        opencv_highgui.destroyAllWindows();
        System.out.println("\nCamera released. Goodbye!");
    }

    /**
     * Log recognition attempt to database
     */
    private void logRecognition(Integer studentId, float confidence, 
                                RecognitionLogDAO.RecognitionResult result, String remarks) {
        try {
            recognitionLogDAO.logRecognition(
                studentId, confidence, result, "DNN", "FaceNet", 
                "default_camera", "main_entrance", remarks);
        } catch (SQLException e) {
            System.err.println("Failed to log recognition: " + e.getMessage());
        }
    }

    /**
     * Clean up resources
     */
    public void close() {
        if (faceDetector != null) {
            faceDetector.close();
        }
        if (embeddingGenerator != null) {
            embeddingGenerator.close();
        }
    }

    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        DeepLearningRecognizer recognizer = null;
        try {
            recognizer = new DeepLearningRecognizer();
            recognizer.startRealtimeRecognition();
        } catch (Exception e) {
            System.err.println("Error initializing recognizer: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (recognizer != null) {
                recognizer.close();
            }
        }
    }
}

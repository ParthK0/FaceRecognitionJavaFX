package com.myapp.ml;

import com.myapp.dao.FaceEmbeddingDAO;
import com.myapp.dao.StudentDAO;
import com.myapp.model.Student;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.global.opencv_imgcodecs;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Deep Learning based face trainer
 * Uses DNN face detector and FaceNet embeddings instead of LBPH
 */
public class DeepLearningTrainer {
    private DNNFaceDetector faceDetector;
    private FaceNetEmbeddingGenerator embeddingGenerator;
    private FaceEmbeddingDAO embeddingDAO;
    private StudentDAO studentDAO;

    private static final float MIN_QUALITY_SCORE = 0.5f;
    private static final String DATASET_BASE_PATH = "dataset";

    public DeepLearningTrainer() throws Exception {
        this.faceDetector = new DNNFaceDetector();
        this.embeddingGenerator = new FaceNetEmbeddingGenerator();
        this.embeddingDAO = new FaceEmbeddingDAO();
        this.studentDAO = new StudentDAO();
    }

    /**
     * Train a single student by processing their dataset images
     */
    public boolean trainStudent(int studentId, String datasetPath) {
        System.out.println("\n╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║     DEEP LEARNING FACE TRAINING SYSTEM                        ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝\n");

        try {
            // Get student details
            Student student = studentDAO.getStudentById(studentId);
            if (student == null) {
                System.err.println("✗ Student not found with ID: " + studentId);
                return false;
            }

            // Auto-determine dataset path if not provided or invalid
            if (datasetPath == null || datasetPath.trim().isEmpty()) {
                datasetPath = getStudentDatasetPath(student);
            }

            System.out.println("Training for: " + student.getFullName());
            System.out.println("Dataset path: " + datasetPath + "\n");

            // Create dataset directory if it doesn't exist
            File datasetDir = new File(datasetPath);
            if (!datasetDir.exists()) {
                System.out.println("Creating dataset directory: " + datasetPath);
                if (!datasetDir.mkdirs()) {
                    System.err.println("✗ Failed to create dataset directory");
                    return false;
                }
            }

            if (!datasetDir.isDirectory()) {
                System.err.println("✗ Dataset path is not a directory: " + datasetPath);
                return false;
            }

            // Update student's facial_data_path in database
            try {
                studentDAO.updateFacialDataPath(studentId, datasetPath);
                System.out.println("✓ Updated student's dataset path in database\n");
            } catch (SQLException e) {
                System.err.println("⚠ Warning: Failed to update dataset path in database: " + e.getMessage());
            }

            File[] imageFiles = datasetDir.listFiles((dir, name) -> 
                name.toLowerCase().endsWith(".jpg") || 
                name.toLowerCase().endsWith(".jpeg") || 
                name.toLowerCase().endsWith(".png"));

            if (imageFiles == null || imageFiles.length == 0) {
                System.err.println("✗ No images found in dataset directory");
                return false;
            }

            System.out.println("Found " + imageFiles.length + " images");
            System.out.println("Processing images...\n");

            // Delete existing embeddings for this student
            embeddingDAO.deleteStudentEmbeddings(studentId);

            int processedCount = 0;
            int successCount = 0;
            List<String> errors = new ArrayList<>();

            for (File imageFile : imageFiles) {
                processedCount++;
                System.out.print("[" + processedCount + "/" + imageFiles.length + "] " + 
                               imageFile.getName() + "... ");

                try {
                    // Read image
                    Mat image = opencv_imgcodecs.imread(imageFile.getAbsolutePath());
                    if (image.empty()) {
                        System.out.println("✗ Failed to read");
                        errors.add(imageFile.getName() + ": Failed to read image");
                        continue;
                    }

                    // Detect face
                    Rect faceRect = faceDetector.detectLargestFace(image);
                    if (faceRect == null) {
                        System.out.println("✗ No face detected");
                        errors.add(imageFile.getName() + ": No face detected");
                        image.close();
                        continue;
                    }

                    // Extract face region
                    Mat faceROI = new Mat(image, faceRect);

                    // Generate embedding
                    float[] embedding = embeddingGenerator.generateEmbedding(faceROI);
                    if (embedding == null) {
                        System.out.println("✗ Failed to generate embedding");
                        errors.add(imageFile.getName() + ": Failed to generate embedding");
                        faceROI.close();
                        image.close();
                        continue;
                    }

                    // Calculate quality score (based on face size and clarity)
                    float qualityScore = calculateQualityScore(faceRect, image.cols(), image.rows());

                    // Store embedding in database
                    boolean stored = embeddingDAO.storeEmbedding(
                        studentId, embedding, imageFile.getName(), 
                        qualityScore, "FaceNet");

                    if (stored) {
                        System.out.println("✓ Success (quality: " + 
                                         String.format("%.2f", qualityScore) + ")");
                        successCount++;
                    } else {
                        System.out.println("✗ Failed to store");
                        errors.add(imageFile.getName() + ": Failed to store embedding");
                    }

                    // Clean up
                    faceROI.close();
                    image.close();

                } catch (Exception e) {
                    System.out.println("✗ Error: " + e.getMessage());
                    errors.add(imageFile.getName() + ": " + e.getMessage());
                }
            }

            // Print summary
            System.out.println("\n" + "═".repeat(65));
            System.out.println("TRAINING SUMMARY");
            System.out.println("═".repeat(65));
            System.out.println("Total images processed: " + processedCount);
            System.out.println("Successfully trained:   " + successCount);
            System.out.println("Failed:                 " + (processedCount - successCount));
            System.out.println("Success rate:           " + 
                             String.format("%.1f%%", (successCount * 100.0 / processedCount)));

            if (!errors.isEmpty() && errors.size() <= 5) {
                System.out.println("\nErrors:");
                for (String error : errors) {
                    System.out.println("  • " + error);
                }
            }

            System.out.println("═".repeat(65) + "\n");

            return successCount > 0;

        } catch (SQLException e) {
            System.err.println("✗ Database error: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("✗ Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Train all students with facial data
     */
    public void trainAllStudents() {
        System.out.println("\n╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║     TRAIN ALL STUDENTS - DEEP LEARNING                        ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝\n");

        try {
            // Get all active students (not just those with facial data already set)
            List<Student> students = studentDAO.getAllStudents();
            
            if (students.isEmpty()) {
                System.out.println("✗ No students found in database");
                return;
            }

            // Filter students that have datasets
            List<Student> studentsWithDatasets = new ArrayList<>();
            System.out.println("Checking for existing datasets...\n");
            
            for (Student student : students) {
                String datasetPath = getStudentDatasetPath(student);
                File datasetDir = new File(datasetPath);
                
                if (datasetDir.exists() && datasetDir.isDirectory()) {
                    File[] images = datasetDir.listFiles((dir, name) -> 
                        name.toLowerCase().endsWith(".jpg") ||
                        name.toLowerCase().endsWith(".jpeg") ||
                        name.toLowerCase().endsWith(".png"));
                    
                    if (images != null && images.length > 0) {
                        studentsWithDatasets.add(student);
                        System.out.println("✓ Found dataset for " + student.getFullName() + 
                                         " (" + images.length + " images)");
                    }
                }
            }
            
            if (studentsWithDatasets.isEmpty()) {
                System.out.println("\n✗ No students found with dataset images");
                System.out.println("Please capture facial images first using the camera feature.");
                return;
            }

            System.out.println("\nFound " + studentsWithDatasets.size() + " student(s) to train\n");

            int successCount = 0;
            for (int i = 0; i < studentsWithDatasets.size(); i++) {
                Student student = studentsWithDatasets.get(i);
                System.out.println("─".repeat(65));
                System.out.println("Training " + (i + 1) + "/" + studentsWithDatasets.size() + ": " + 
                                 student.getFullName());
                System.out.println("─".repeat(65));

                String datasetPath = getStudentDatasetPath(student);
                if (trainStudent(student.getStudentId(), datasetPath)) {
                    successCount++;
                }
            }

            // Final summary
            System.out.println("\n" + "═".repeat(65));
            System.out.println("OVERALL TRAINING SUMMARY");
            System.out.println("═".repeat(65));
            System.out.println("Total students:       " + studentsWithDatasets.size());
            System.out.println("Successfully trained: " + successCount);
            System.out.println("Failed:               " + (studentsWithDatasets.size() - successCount));
            System.out.println("═".repeat(65) + "\n");

        } catch (Exception e) {
            System.err.println("✗ Error during training: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Get the dataset path for a student (creates directory structure)
     */
    private String getStudentDatasetPath(Student student) {
        // Sanitize student name for use as directory name
        String sanitizedName = student.getFullName()
            .toLowerCase()
            .replaceAll("[^a-z0-9]", "_")
            .replaceAll("_+", "_")
            .replaceAll("^_|_$", "");
        
        // Use admission number as fallback if name sanitization fails
        if (sanitizedName.isEmpty()) {
            sanitizedName = "student_" + student.getAdmissionNumber()
                .replaceAll("[^a-zA-Z0-9]", "_");
        }
        
        return DATASET_BASE_PATH + File.separator + sanitizedName;
    }

    /**
     * Calculate quality score for a face detection
     */
    private float calculateQualityScore(Rect faceRect, int imageWidth, int imageHeight) {
        // Calculate face size relative to image
        float faceArea = faceRect.width() * faceRect.height();
        float imageArea = imageWidth * imageHeight;
        float areaRatio = faceArea / imageArea;

        // Optimal face should occupy 10-40% of image
        float sizeScore;
        if (areaRatio < 0.05f) {
            sizeScore = areaRatio / 0.05f; // Too small
        } else if (areaRatio > 0.5f) {
            sizeScore = 1.0f - (areaRatio - 0.5f); // Too large
        } else {
            sizeScore = 1.0f; // Good size
        }

        // Check if face is centered (faces near center are usually better quality)
        float faceCenterX = faceRect.x() + faceRect.width() / 2.0f;
        float faceCenterY = faceRect.y() + faceRect.height() / 2.0f;
        float imageCenterX = imageWidth / 2.0f;
        float imageCenterY = imageHeight / 2.0f;

        float distanceFromCenter = (float) Math.sqrt(
            Math.pow(faceCenterX - imageCenterX, 2) + 
            Math.pow(faceCenterY - imageCenterY, 2));
        float maxDistance = (float) Math.sqrt(
            Math.pow(imageWidth / 2.0, 2) + 
            Math.pow(imageHeight / 2.0, 2));
        float centerScore = 1.0f - (distanceFromCenter / maxDistance);

        // Combined score
        return (sizeScore * 0.7f + centerScore * 0.3f);
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
     * Main method for command-line training
     */
    public static void main(String[] args) {
        DeepLearningTrainer trainer = null;
        try {
            trainer = new DeepLearningTrainer();
            
            if (args.length > 0 && args[0].equals("--all")) {
                // Train all students
                trainer.trainAllStudents();
            } else if (args.length >= 2) {
                // Train specific student
                int studentId = Integer.parseInt(args[0]);
                String datasetPath = args[1];
                trainer.trainStudent(studentId, datasetPath);
            } else {
                System.out.println("Usage:");
                System.out.println("  java DeepLearningTrainer --all              (train all students)");
                System.out.println("  java DeepLearningTrainer <studentId> <path> (train specific student)");
            }
        } catch (Exception e) {
            System.err.println("Error initializing trainer: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (trainer != null) {
                trainer.close();
            }
        }
    }
}

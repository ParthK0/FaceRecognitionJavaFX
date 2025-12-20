package com.myapp;

import com.myapp.service.DeepLearningAttendanceService;
import com.myapp.ml.DeepLearningRecognizer;
import com.myapp.ml.DeepLearningTrainer;
import com.myapp.model.Attendance;
import com.myapp.dao.RecognitionLogDAO;

import java.util.Scanner;

/**
 * Main application for Deep Learning Face Recognition System
 * Provides a menu-driven interface for all features
 */
public class DeepLearningMain {
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        printBanner();

        while (running) {
            printMenu();
            System.out.print("Enter your choice: ");
            
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        trainAllStudents();
                        break;
                    case 2:
                        trainSpecificStudent(scanner);
                        break;
                    case 3:
                        startRealtimeRecognition();
                        break;
                    case 4:
                        startAttendanceMarking(scanner);
                        break;
                    case 5:
                        recognizeOnce();
                        break;
                    case 6:
                        viewStatistics();
                        break;
                    case 7:
                        viewRecentLogs(scanner);
                        break;
                    case 0:
                        running = false;
                        System.out.println("\nThank you for using Deep Learning Face Recognition System!");
                        System.out.println("Goodbye! 👋\n");
                        break;
                    default:
                        System.out.println("\n❌ Invalid choice. Please try again.\n");
                }
            } catch (Exception e) {
                System.out.println("\n❌ Error: " + e.getMessage() + "\n");
                scanner.nextLine(); // Clear invalid input
            }
        }

        scanner.close();
    }

    private static void printBanner() {
        System.out.println("\n" + "═".repeat(70));
        System.out.println("║" + " ".repeat(68) + "║");
        System.out.println("║     🎓 DEEP LEARNING FACE RECOGNITION ATTENDANCE SYSTEM 🎓" + " ".repeat(8) + "║");
        System.out.println("║" + " ".repeat(68) + "║");
        System.out.println("║     Powered by: DNN Face Detection + FaceNet Embeddings" + " ".repeat(12) + "║");
        System.out.println("║" + " ".repeat(68) + "║");
        System.out.println("═".repeat(70) + "\n");
    }

    private static void printMenu() {
        System.out.println("╔═══════════════════════════════════════════════════════════════════╗");
        System.out.println("║                           MAIN MENU                               ║");
        System.out.println("╠═══════════════════════════════════════════════════════════════════╣");
        System.out.println("║  TRAINING                                                         ║");
        System.out.println("║    1. Train All Students (Batch Training)                         ║");
        System.out.println("║    2. Train Specific Student                                      ║");
        System.out.println("║                                                                   ║");
        System.out.println("║  RECOGNITION                                                      ║");
        System.out.println("║    3. Start Real-time Face Recognition                            ║");
        System.out.println("║    4. Start Attendance Marking (with Recognition)                 ║");
        System.out.println("║    5. Recognize Face Once (Single Shot)                           ║");
        System.out.println("║                                                                   ║");
        System.out.println("║  ANALYTICS                                                        ║");
        System.out.println("║    6. View Recognition Statistics                                 ║");
        System.out.println("║    7. View Recent Recognition Logs                                ║");
        System.out.println("║                                                                   ║");
        System.out.println("║    0. Exit                                                        ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════════╝");
        System.out.println();
    }

    private static void trainAllStudents() {
        System.out.println("\n🔄 Starting batch training for all students...\n");
        try {
            DeepLearningTrainer trainer = new DeepLearningTrainer();
            trainer.trainAllStudents();
            trainer.close();
            System.out.println("\n✅ Training completed successfully!\n");
            pauseForUser();
        } catch (Exception e) {
            System.out.println("\n❌ Training failed: " + e.getMessage() + "\n");
            pauseForUser();
        }
    }

    private static void trainSpecificStudent(Scanner scanner) {
        System.out.println("\n📚 Train Specific Student");
        System.out.println("─".repeat(50));
        
        try {
            System.out.print("Enter Student ID: ");
            int studentId = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            
            System.out.print("Enter dataset path (e.g., dataset/student_name): ");
            String datasetPath = scanner.nextLine();
            
            System.out.println("\n🔄 Starting training...\n");
            
            DeepLearningTrainer trainer = new DeepLearningTrainer();
            boolean success = trainer.trainStudent(studentId, datasetPath);
            trainer.close();
            
            if (success) {
                System.out.println("\n✅ Training completed successfully!\n");
            } else {
                System.out.println("\n❌ Training failed. Check logs above for details.\n");
            }
            pauseForUser();
        } catch (Exception e) {
            System.out.println("\n❌ Error: " + e.getMessage() + "\n");
            pauseForUser();
        }
    }

    private static void startRealtimeRecognition() {
        System.out.println("\n📹 Starting real-time face recognition...");
        System.out.println("Press 'q' in the camera window to quit\n");
        pauseForUser();
        
        try {
            DeepLearningRecognizer recognizer = new DeepLearningRecognizer();
            recognizer.startRealtimeRecognition();
            recognizer.close();
            System.out.println("\n✅ Recognition session ended\n");
            pauseForUser();
        } catch (Exception e) {
            System.out.println("\n❌ Error: " + e.getMessage() + "\n");
            pauseForUser();
        }
    }

    private static void startAttendanceMarking(Scanner scanner) {
        System.out.println("\n✅ Attendance Marking with Face Recognition");
        System.out.println("─".repeat(50));
        
        try {
            System.out.print("Enter Course ID: ");
            int courseId = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            
            System.out.println("\nSelect Session Type:");
            System.out.println("1. Morning");
            System.out.println("2. Afternoon");
            System.out.println("3. Evening");
            System.out.println("4. Full Day");
            System.out.print("Choice: ");
            int sessionChoice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            
            Attendance.SessionType sessionType;
            switch (sessionChoice) {
                case 1: sessionType = Attendance.SessionType.MORNING; break;
                case 2: sessionType = Attendance.SessionType.AFTERNOON; break;
                case 3: sessionType = Attendance.SessionType.EVENING; break;
                case 4: sessionType = Attendance.SessionType.FULL_DAY; break;
                default:
                    System.out.println("\n❌ Invalid session type\n");
                    return;
            }
            
            System.out.println("\n📹 Starting attendance marking...");
            System.out.println("Press 'q' in the camera window to quit\n");
            pauseForUser();
            
            DeepLearningAttendanceService service = new DeepLearningAttendanceService();
            service.startAttendanceRecognition(courseId, sessionType);
            
            System.out.println("\n✅ Attendance session completed\n");
            pauseForUser();
        } catch (Exception e) {
            System.out.println("\n❌ Error: " + e.getMessage() + "\n");
            pauseForUser();
        }
    }

    private static void recognizeOnce() {
        System.out.println("\n📸 Single Face Recognition");
        System.out.println("─".repeat(50));
        System.out.println("Position your face in front of the camera...");
        System.out.println("Press 'q' to cancel\n");
        pauseForUser();
        
        try {
            DeepLearningAttendanceService service = new DeepLearningAttendanceService();
            DeepLearningRecognizer.RecognitionResult result = service.recognizeOnce();
            
            if (result != null && result.isRecognized()) {
                System.out.println("\n✅ RECOGNIZED!");
                System.out.println("─".repeat(50));
                System.out.println("Student Name:      " + result.getStudentName());
                System.out.println("Admission Number:  " + result.getAdmissionNumber());
                System.out.println("Confidence:        " + String.format("%.2f%%", result.getConfidence() * 100));
                System.out.println("─".repeat(50) + "\n");
            } else {
                System.out.println("\n❌ Face not recognized or not found\n");
            }
            pauseForUser();
        } catch (Exception e) {
            System.out.println("\n❌ Error: " + e.getMessage() + "\n");
            pauseForUser();
        }
    }

    private static void viewStatistics() {
        System.out.println("\n📊 Recognition Statistics");
        System.out.println("─".repeat(50));
        
        try {
            DeepLearningAttendanceService service = new DeepLearningAttendanceService();
            service.printStatistics();
            pauseForUser();
        } catch (Exception e) {
            System.out.println("\n❌ Error: " + e.getMessage() + "\n");
            pauseForUser();
        }
    }

    private static void viewRecentLogs(Scanner scanner) {
        System.out.println("\n📝 Recent Recognition Logs");
        System.out.println("─".repeat(50));
        
        try {
            System.out.print("How many recent logs to display? (default 20): ");
            String input = scanner.nextLine();
            int limit = input.isEmpty() ? 20 : Integer.parseInt(input);
            
            RecognitionLogDAO logDAO = new RecognitionLogDAO();
            java.util.List<java.util.Map<String, Object>> logs = logDAO.getRecentLogs(limit);
            
            if (logs.isEmpty()) {
                System.out.println("\n📭 No logs found\n");
            } else {
                System.out.println("\n" + logs.size() + " most recent logs:\n");
                System.out.println(String.format("%-20s %-25s %-12s %-15s", 
                                                "Timestamp", "Student", "Result", "Confidence"));
                System.out.println("─".repeat(75));
                
                for (java.util.Map<String, Object> log : logs) {
                    String timestamp = log.get("recognition_timestamp").toString();
                    String student = log.get("student_name") != null ? 
                                   log.get("student_name").toString() : "Unknown";
                    String result = log.get("recognition_result").toString();
                    String confidence = String.format("%.2f%%", 
                                      ((Number) log.get("confidence_score")).floatValue() * 100);
                    
                    System.out.println(String.format("%-20s %-25s %-12s %-15s", 
                                                    timestamp, student, result, confidence));
                }
                System.out.println();
            }
            pauseForUser();
        } catch (Exception e) {
            System.out.println("\n❌ Error: " + e.getMessage() + "\n");
            pauseForUser();
        }
    }

    private static void pauseForUser() {
        System.out.println("Press Enter to continue...");
        try {
            System.in.read();
            // Clear remaining input
            while (System.in.available() > 0) {
                System.in.read();
            }
        } catch (Exception e) {
            // Ignore
        }
    }
}

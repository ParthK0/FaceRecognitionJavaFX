package com.myapp;

import com.myapp.dao.CourseDAO;
import com.myapp.dao.StudentDAO;
import com.myapp.model.Attendance;
import com.myapp.model.Course;
import com.myapp.model.Student;
import com.myapp.service.*;
import com.myapp.util.DatabaseConnection;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

/**
 * Main Attendance System Application
 * Facial Recognition Based Smart Attendance System
 */
public class AttendanceSystem {
    private static final Scanner scanner = new Scanner(System.in);
    private static final StudentService studentService = new StudentService();
    private static final AttendanceService attendanceService = new AttendanceService();
    private static final FacialDataCaptureService facialCaptureService = new FacialDataCaptureService();
    private static final FaceRecognitionAttendanceService recognitionService = new FaceRecognitionAttendanceService();
    private static final CourseDAO courseDAO = new CourseDAO();
    private static final StudentDAO studentDAO = new StudentDAO();
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public static void main(String[] args) {
        // Initialize database
        try {
            System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
            System.out.println("║  FACIAL RECOGNITION BASED SMART ATTENDANCE SYSTEM             ║");
            System.out.println("║  Version 1.0 - Java, OpenCV, MySQL, Maven                    ║");
            System.out.println("╚════════════════════════════════════════════════════════════════╝\n");
            
            System.out.println("Initializing system...\n");
            DatabaseConnection.initializeDatabase();
            
            System.out.println("\n✓ System initialized successfully!\n");
            
            // Main menu loop
            boolean running = true;
            while (running) {
                running = showMainMenu();
            }
            
        } catch (SQLException e) {
            System.err.println("\n✗ Fatal Error: " + e.getMessage());
            System.err.println("✗ Please check your database configuration and try again.");
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection();
            scanner.close();
            System.out.println("\n✓ System shutdown complete. Goodbye!");
        }
    }

    /**
     * Display main menu and handle selection
     */
    private static boolean showMainMenu() {
        System.out.println("\n" + "═".repeat(70));
        System.out.println("                        MAIN MENU");
        System.out.println("═".repeat(70));
        System.out.println("  1. Student Management");
        System.out.println("  2. Facial Data Capture");
        System.out.println("  3. Train Face Recognition Model");
        System.out.println("  4. Mark Attendance (Face Recognition)");
        System.out.println("  5. View Attendance Reports");
        System.out.println("  6. Course Management");
        System.out.println("  7. System Settings");
        System.out.println("  0. Exit");
        System.out.println("═".repeat(70));
        System.out.print("Enter your choice: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            System.out.println();

            switch (choice) {
                case 1 -> studentManagementMenu();
                case 2 -> facialDataCaptureMenu();
                case 3 -> trainFaceRecognitionModel();
                case 4 -> markAttendanceMenu();
                case 5 -> viewAttendanceReportsMenu();
                case 6 -> courseManagementMenu();
                case 7 -> systemSettingsMenu();
                case 0 -> {
                    return false;
                }
                default -> System.out.println("⚠ Invalid choice. Please try again.");
            }
        } catch (NumberFormatException e) {
            System.out.println("⚠ Invalid input. Please enter a number.");
        } catch (Exception e) {
            System.err.println("✗ Error: " + e.getMessage());
            e.printStackTrace();
        }

        return true;
    }

    /**
     * Student Management Menu
     */
    private static void studentManagementMenu() {
        System.out.println("═".repeat(70));
        System.out.println("                   STUDENT MANAGEMENT");
        System.out.println("═".repeat(70));
        System.out.println("  1. Register New Student");
        System.out.println("  2. View All Students");
        System.out.println("  3. Search Student");
        System.out.println("  4. View Student Details");
        System.out.println("  5. Update Student Information");
        System.out.println("  0. Back to Main Menu");
        System.out.println("═".repeat(70));
        System.out.print("Enter your choice: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            System.out.println();

            switch (choice) {
                case 1 -> registerNewStudent();
                case 2 -> viewAllStudents();
                case 3 -> searchStudent();
                case 4 -> viewStudentDetails();
                case 5 -> updateStudent();
                case 0 -> {
                    return;
                }
                default -> System.out.println("⚠ Invalid choice.");
            }
        } catch (Exception e) {
            System.err.println("✗ Error: " + e.getMessage());
        }

        pause();
    }

    /**
     * Register new student
     */
    private static void registerNewStudent() throws SQLException {
        System.out.println("═".repeat(70));
        System.out.println("                  REGISTER NEW STUDENT");
        System.out.println("═".repeat(70));

        // Display available courses
        List<Course> courses = courseDAO.getAllCourses();
        if (courses.isEmpty()) {
            System.out.println("✗ No courses available. Please add courses first.");
            return;
        }

        System.out.println("\nAvailable Courses:");
        for (Course course : courses) {
            System.out.printf("  %d. %s (%s)%n", course.getCourseId(), course.getCourseName(), course.getCourseCode());
        }

        System.out.print("\nEnter Admission Number: ");
        String admissionNumber = scanner.nextLine().trim();

        System.out.print("Enter Roll Number: ");
        String rollNumber = scanner.nextLine().trim();

        System.out.print("Enter Full Name: ");
        String fullName = scanner.nextLine().trim();

        System.out.print("Enter Course ID: ");
        int courseId = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Enter Semester (1-12): ");
        int semester = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Enter Academic Year (e.g., 2024-2025): ");
        String academicYear = scanner.nextLine().trim();

        System.out.print("Enter Email (optional): ");
        String email = scanner.nextLine().trim();

        System.out.print("Enter Phone (optional): ");
        String phone = scanner.nextLine().trim();

        try {
            Student student = studentService.registerStudent(
                admissionNumber, rollNumber, fullName, courseId, semester, 
                academicYear, email, phone
            );

            System.out.println("\n✓ Student registered successfully!");
            System.out.println(student.toDetailedString());
            System.out.println("\n💡 Next step: Capture facial data for this student (Menu 2)");

        } catch (Exception e) {
            System.err.println("\n✗ Registration failed: " + e.getMessage());
        }
    }

    /**
     * View all students
     */
    private static void viewAllStudents() throws SQLException {
        List<Student> students = studentService.getAllStudents();

        System.out.println("═".repeat(100));
        System.out.println("                                ALL STUDENTS");
        System.out.println("═".repeat(100));

        if (students.isEmpty()) {
            System.out.println("No students found.");
        } else {
            System.out.printf("%-5s %-15s %-10s %-25s %-30s %-5s %-12s%n",
                            "ID", "Adm. No", "Roll No", "Name", "Course", "Sem", "Year");
            System.out.println("-".repeat(100));

            for (Student student : students) {
                System.out.printf("%-5d %-15s %-10s %-25s %-30s %-5d %-12s%n",
                                student.getStudentId(),
                                student.getAdmissionNumber(),
                                student.getRollNumber(),
                                truncate(student.getFullName(), 25),
                                truncate(student.getCourseName(), 30),
                                student.getSemester(),
                                student.getAcademicYear());
            }

            System.out.println("-".repeat(100));
            System.out.println("Total Students: " + students.size());
        }
        System.out.println("═".repeat(100));
    }

    /**
     * Search student
     */
    private static void searchStudent() throws SQLException {
        System.out.print("Enter search term (name, admission no, or roll no): ");
        String searchTerm = scanner.nextLine().trim();

        List<Student> students = studentService.searchStudents(searchTerm);

        System.out.println("\n═".repeat(100));
        System.out.println("                           SEARCH RESULTS");
        System.out.println("═".repeat(100));

        if (students.isEmpty()) {
            System.out.println("No students found matching: " + searchTerm);
        } else {
            System.out.printf("%-5s %-15s %-10s %-25s %-30s%n",
                            "ID", "Adm. No", "Roll No", "Name", "Course");
            System.out.println("-".repeat(100));

            for (Student student : students) {
                System.out.printf("%-5d %-15s %-10s %-25s %-30s%n",
                                student.getStudentId(),
                                student.getAdmissionNumber(),
                                student.getRollNumber(),
                                truncate(student.getFullName(), 25),
                                truncate(student.getCourseName(), 30));
            }

            System.out.println("-".repeat(100));
            System.out.println("Found " + students.size() + " student(s)");
        }
        System.out.println("═".repeat(100));
    }

    /**
     * View student details
     */
    private static void viewStudentDetails() throws SQLException {
        System.out.print("Enter Student ID or Admission Number: ");
        String input = scanner.nextLine().trim();

        Student student;
        try {
            int studentId = Integer.parseInt(input);
            student = studentService.getStudentById(studentId);
        } catch (NumberFormatException e) {
            student = studentService.getStudentByAdmissionNumber(input);
        }

        if (student == null) {
            System.out.println("\n✗ Student not found.");
            return;
        }

        System.out.println("\n" + student.toDetailedString());

        // Check facial data
        boolean hasFacialData = facialCaptureService.hasFacialData(student.getStudentId());
        int imageCount = facialCaptureService.getImageCount(student.getStudentId());

        System.out.println("\n╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║                   FACIAL DATA STATUS                          ║");
        System.out.println("╠═══════════════════════════════════════════════════════════════╣");
        System.out.printf("║ Has Facial Data   : %-42s ║%n", hasFacialData ? "Yes" : "No");
        System.out.printf("║ Image Count       : %-42d ║%n", imageCount);
        System.out.printf("║ Dataset Path      : %-42s ║%n", 
                         student.getFacialDataPath() != null ? student.getFacialDataPath() : "N/A");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
    }

    /**
     * Update student
     */
    private static void updateStudent() throws SQLException {
        System.out.print("Enter Student ID to update: ");
        int studentId = Integer.parseInt(scanner.nextLine().trim());

        Student student = studentService.getStudentById(studentId);
        if (student == null) {
            System.out.println("\n✗ Student not found.");
            return;
        }

        System.out.println("\nCurrent Details:");
        System.out.println(student.toDetailedString());

        System.out.println("\n(Press Enter to keep current value)");

        System.out.print("Email [" + student.getEmail() + "]: ");
        String email = scanner.nextLine().trim();
        if (!email.isEmpty()) student.setEmail(email);

        System.out.print("Phone [" + student.getPhone() + "]: ");
        String phone = scanner.nextLine().trim();
        if (!phone.isEmpty()) student.setPhone(phone);

        System.out.print("Semester [" + student.getSemester() + "]: ");
        String semStr = scanner.nextLine().trim();
        if (!semStr.isEmpty()) student.setSemester(Integer.parseInt(semStr));

        if (studentService.updateStudent(student)) {
            System.out.println("\n✓ Student information updated successfully!");
        } else {
            System.out.println("\n✗ Failed to update student information.");
        }
    }

    /**
     * Facial Data Capture Menu
     */
    private static void facialDataCaptureMenu() throws SQLException {
        System.out.println("═".repeat(70));
        System.out.println("                 FACIAL DATA CAPTURE");
        System.out.println("═".repeat(70));

        System.out.print("Enter Student ID or Admission Number: ");
        String input = scanner.nextLine().trim();

        Student student;
        try {
            int studentId = Integer.parseInt(input);
            student = studentService.getStudentById(studentId);
        } catch (NumberFormatException e) {
            student = studentService.getStudentByAdmissionNumber(input);
        }

        if (student == null) {
            System.out.println("\n✗ Student not found.");
            return;
        }

        System.out.println("\nStudent: " + student.getFullName() + " (" + student.getAdmissionNumber() + ")");

        System.out.print("Enter number of images to capture (default 50): ");
        String countStr = scanner.nextLine().trim();
        int imageCount = countStr.isEmpty() ? 50 : Integer.parseInt(countStr);

        System.out.println("\n⚠ Make sure your camera is connected and working.");
        System.out.println("⚠ Good lighting is essential for quality facial data.");
        System.out.print("\nPress Enter to start capture...");
        scanner.nextLine();

        boolean success = facialCaptureService.captureFacialData(student.getStudentId(), imageCount);

        if (success) {
            System.out.println("\n💡 Next step: Train the face recognition model (Menu 3)");
        }

        pause();
    }

    /**
     * Train Face Recognition Model
     */
    private static void trainFaceRecognitionModel() {
        System.out.println("═".repeat(70));
        System.out.println("            TRAIN FACE RECOGNITION MODEL");
        System.out.println("═".repeat(70));

        System.out.println("\nThis will train the face recognition model using all available");
        System.out.println("student facial data. This may take a few minutes...");
        System.out.print("\nPress Enter to start training...");
        scanner.nextLine();

        try {
            TrainerMulti.main(new String[]{});
            System.out.println("\n✓ Model training completed!");
            System.out.println("💡 You can now use face recognition for attendance (Menu 4)");
        } catch (Exception e) {
            System.err.println("\n✗ Training failed: " + e.getMessage());
            e.printStackTrace();
        }

        pause();
    }

    /**
     * Mark Attendance Menu
     */
    private static void markAttendanceMenu() throws SQLException {
        System.out.println("═".repeat(70));
        System.out.println("           MARK ATTENDANCE (FACE RECOGNITION)");
        System.out.println("═".repeat(70));

        // Display available courses
        List<Course> courses = courseDAO.getAllCourses();
        if (courses.isEmpty()) {
            System.out.println("✗ No courses available.");
            return;
        }

        System.out.println("\nAvailable Courses:");
        for (Course course : courses) {
            System.out.printf("  %d. %s (%s)%n", course.getCourseId(), course.getCourseName(), course.getCourseCode());
        }

        System.out.print("\nEnter Course ID: ");
        int courseId = Integer.parseInt(scanner.nextLine().trim());

        System.out.println("\nSession Types:");
        System.out.println("  1. Morning");
        System.out.println("  2. Afternoon");
        System.out.println("  3. Evening");
        System.out.println("  4. Full Day");
        System.out.print("Select Session Type: ");
        int sessionChoice = Integer.parseInt(scanner.nextLine().trim());

        Attendance.SessionType sessionType = switch (sessionChoice) {
            case 1 -> Attendance.SessionType.MORNING;
            case 2 -> Attendance.SessionType.AFTERNOON;
            case 3 -> Attendance.SessionType.EVENING;
            default -> Attendance.SessionType.FULL_DAY;
        };

        System.out.println("\n⚠ Make sure your camera is connected.");
        System.out.println("⚠ Students should look at the camera to mark attendance.");
        System.out.print("\nPress Enter to start face recognition...");
        scanner.nextLine();

        recognitionService.startAttendanceRecognition(courseId, sessionType);

        pause();
    }

    /**
     * View Attendance Reports Menu
     */
    private static void viewAttendanceReportsMenu() throws SQLException {
        System.out.println("═".repeat(70));
        System.out.println("                 ATTENDANCE REPORTS");
        System.out.println("═".repeat(70));
        System.out.println("  1. Today's Attendance");
        System.out.println("  2. Student Attendance History");
        System.out.println("  3. Course Attendance Report");
        System.out.println("  4. Date-wise Attendance");
        System.out.println("  0. Back");
        System.out.println("═".repeat(70));
        System.out.print("Enter your choice: ");

        int choice = Integer.parseInt(scanner.nextLine().trim());
        System.out.println();

        switch (choice) {
            case 1 -> viewTodayAttendance();
            case 2 -> viewStudentAttendanceHistory();
            case 3 -> viewCourseAttendanceReport();
            case 4 -> viewDateWiseAttendance();
        }

        pause();
    }

    /**
     * View today's attendance
     */
    private static void viewTodayAttendance() throws SQLException {
        attendanceService.displayTodayAttendanceSummary();
    }

    /**
     * View student attendance history
     */
    private static void viewStudentAttendanceHistory() throws SQLException {
        System.out.print("Enter Student ID or Admission Number: ");
        String input = scanner.nextLine().trim();

        Student student;
        try {
            int studentId = Integer.parseInt(input);
            student = studentService.getStudentById(studentId);
        } catch (NumberFormatException e) {
            student = studentService.getStudentByAdmissionNumber(input);
        }

        if (student == null) {
            System.out.println("\n✗ Student not found.");
            return;
        }

        List<Attendance> records = attendanceService.getStudentAttendance(student.getStudentId());

        System.out.println("\n═".repeat(90));
        System.out.println("              ATTENDANCE HISTORY - " + student.getFullName());
        System.out.println("═".repeat(90));

        if (records.isEmpty()) {
            System.out.println("No attendance records found.");
        } else {
            System.out.printf("%-12s %-10s %-30s %-15s %-10s%n",
                            "Date", "Time", "Course", "Session", "Status");
            System.out.println("-".repeat(90));

            for (Attendance record : records) {
                System.out.printf("%-12s %-10s %-30s %-15s %-10s%n",
                                record.getAttendanceDate().format(dateFormatter),
                                record.getAttendanceTime(),
                                truncate(record.getCourseName(), 30),
                                record.getSessionType().getDisplayName(),
                                record.getStatus().getDisplayName());
            }

            System.out.println("-".repeat(90));
            System.out.println("Total Records: " + records.size());
        }
        System.out.println("═".repeat(90));
    }

    /**
     * View course attendance report
     */
    private static void viewCourseAttendanceReport() throws SQLException {
        System.out.print("Enter Course ID: ");
        int courseId = Integer.parseInt(scanner.nextLine().trim());

        List<Attendance> records = attendanceService.getCourseAttendance(courseId);

        System.out.println("\n═".repeat(90));
        System.out.println("                     COURSE ATTENDANCE REPORT");
        System.out.println("═".repeat(90));

        if (records.isEmpty()) {
            System.out.println("No attendance records found for this course.");
        } else {
            System.out.printf("%-12s %-10s %-25s %-15s %-15s%n",
                            "Date", "Time", "Student", "Admission No", "Session");
            System.out.println("-".repeat(90));

            for (Attendance record : records) {
                System.out.printf("%-12s %-10s %-25s %-15s %-15s%n",
                                record.getAttendanceDate().format(dateFormatter),
                                record.getAttendanceTime(),
                                truncate(record.getStudentName(), 25),
                                record.getAdmissionNumber(),
                                record.getSessionType().getDisplayName());
            }

            System.out.println("-".repeat(90));
            System.out.println("Total Records: " + records.size());
        }
        System.out.println("═".repeat(90));
    }

    /**
     * View date-wise attendance
     */
    private static void viewDateWiseAttendance() throws SQLException {
        System.out.print("Enter date (DD-MM-YYYY) or press Enter for today: ");
        String dateStr = scanner.nextLine().trim();

        LocalDate date = dateStr.isEmpty() ? LocalDate.now() : LocalDate.parse(dateStr, dateFormatter);

        List<Attendance> records = attendanceService.getAttendanceByDate(date);

        System.out.println("\n═".repeat(90));
        System.out.println("              ATTENDANCE REPORT - " + date.format(dateFormatter));
        System.out.println("═".repeat(90));

        if (records.isEmpty()) {
            System.out.println("No attendance records found for this date.");
        } else {
            System.out.printf("%-10s %-25s %-15s %-30s %-15s%n",
                            "Time", "Student", "Admission No", "Course", "Session");
            System.out.println("-".repeat(90));

            for (Attendance record : records) {
                System.out.printf("%-10s %-25s %-15s %-30s %-15s%n",
                                record.getAttendanceTime(),
                                truncate(record.getStudentName(), 25),
                                record.getAdmissionNumber(),
                                truncate(record.getCourseName(), 30),
                                record.getSessionType().getDisplayName());
            }

            System.out.println("-".repeat(90));
            System.out.println("Total Present: " + records.size());
        }
        System.out.println("═".repeat(90));
    }

    /**
     * Course Management Menu
     */
    private static void courseManagementMenu() throws SQLException {
        System.out.println("═".repeat(70));
        System.out.println("                  COURSE MANAGEMENT");
        System.out.println("═".repeat(70));

        List<Course> courses = courseDAO.getAllCourses();

        if (courses.isEmpty()) {
            System.out.println("No courses found.");
        } else {
            System.out.printf("%-5s %-15s %-35s %-20s %-8s%n",
                            "ID", "Code", "Name", "Department", "Credits");
            System.out.println("-".repeat(70));

            for (Course course : courses) {
                System.out.printf("%-5d %-15s %-35s %-20s %-8d%n",
                                course.getCourseId(),
                                course.getCourseCode(),
                                truncate(course.getCourseName(), 35),
                                truncate(course.getDepartment(), 20),
                                course.getCredits());
            }

            System.out.println("-".repeat(70));
            System.out.println("Total Courses: " + courses.size());
        }
        System.out.println("═".repeat(70));

        pause();
    }

    /**
     * System Settings Menu
     */
    private static void systemSettingsMenu() {
        System.out.println("═".repeat(70));
        System.out.println("                  SYSTEM SETTINGS");
        System.out.println("═".repeat(70));
        System.out.println("\n  Database Configuration:");
        System.out.println("  Location: db.properties");
        System.out.println("\n  Default settings:");
        System.out.println("  - Database: attendance_system");
        System.out.println("  - Host: localhost");
        System.out.println("  - Port: 3306");
        System.out.println("\n  Edit db.properties file to change database settings.");
        System.out.println("═".repeat(70));

        pause();
    }

    /**
     * Utility method to truncate strings
     */
    private static String truncate(String str, int length) {
        if (str == null) return "";
        return str.length() > length ? str.substring(0, length - 3) + "..." : str;
    }

    /**
     * Pause and wait for user input
     */
    private static void pause() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }
}

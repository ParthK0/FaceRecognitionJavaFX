package com.myapp.service;

import com.myapp.dao.StudentDAO;
import com.myapp.dao.CourseDAO;
import com.myapp.model.Student;
import com.myapp.model.Course;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

/**
 * Service class for Student Management
 * Handles business logic for student operations
 */
public class StudentService {
    private final StudentDAO studentDAO;
    private final CourseDAO courseDAO;

    public StudentService() {
        this.studentDAO = new StudentDAO();
        this.courseDAO = new CourseDAO();
    }

    /**
     * Register a new student
     */
    public Student registerStudent(String admissionNumber, String rollNumber, String fullName,
                                   int courseId, int semester, String academicYear,
                                   String email, String phone) throws SQLException {
        
        // Validate inputs
        if (admissionNumber == null || admissionNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Admission number cannot be empty");
        }
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be empty");
        }
        if (rollNumber == null || rollNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Roll number cannot be empty");
        }
        if (semester < 1 || semester > 12) {
            throw new IllegalArgumentException("Semester must be between 1 and 12");
        }

        // Check if student already exists
        if (studentDAO.studentExists(admissionNumber)) {
            throw new IllegalArgumentException("Student with admission number " + admissionNumber + " already exists");
        }

        // Verify course exists
        Course course = courseDAO.getCourseById(courseId);
        if (course == null) {
            throw new IllegalArgumentException("Course with ID " + courseId + " does not exist");
        }

        // Create student object
        Student student = new Student(admissionNumber, rollNumber, fullName, courseId, semester, academicYear);
        student.setEmail(email);
        student.setPhone(phone);

        // Insert into database
        int studentId = studentDAO.insertStudent(student);
        if (studentId > 0) {
            student.setStudentId(studentId);
            student.setCourseName(course.getCourseName());
            
            // Create dataset directory for facial data
            String datasetPath = "dataset/" + fullName.toLowerCase().replaceAll("\\s+", "_");
            File datasetDir = new File(datasetPath);
            if (!datasetDir.exists()) {
                datasetDir.mkdirs();
            }
            student.setFacialDataPath(datasetPath);
            studentDAO.updateFacialDataPath(studentId, datasetPath);
            
            return student;
        } else {
            throw new SQLException("Failed to register student");
        }
    }

    /**
     * Get all students
     */
    public List<Student> getAllStudents() throws SQLException {
        return studentDAO.getAllStudents();
    }

    /**
     * Get student by ID
     */
    public Student getStudentById(int studentId) throws SQLException {
        return studentDAO.getStudentById(studentId);
    }

    /**
     * Get student by admission number
     */
    public Student getStudentByAdmissionNumber(String admissionNumber) throws SQLException {
        return studentDAO.getStudentByAdmissionNumber(admissionNumber);
    }

    /**
     * Search students
     */
    public List<Student> searchStudents(String searchTerm) throws SQLException {
        return studentDAO.searchStudents(searchTerm);
    }

    /**
     * Get students by course
     */
    public List<Student> getStudentsByCourse(int courseId) throws SQLException {
        return studentDAO.getStudentsByCourse(courseId);
    }

    /**
     * Update student information
     */
    public boolean updateStudent(Student student) throws SQLException {
        // Validate
        if (student.getStudentId() <= 0) {
            throw new IllegalArgumentException("Invalid student ID");
        }
        return studentDAO.updateStudent(student);
    }

    /**
     * Update student facial data path
     */
    public boolean updateFacialDataPath(int studentId, String facialDataPath) throws SQLException {
        return studentDAO.updateFacialDataPath(studentId, facialDataPath);
    }

    /**
     * Deactivate student
     */
    public boolean deactivateStudent(int studentId) throws SQLException {
        return studentDAO.deleteStudent(studentId);
    }

    /**
     * Get total students count
     */
    public int getTotalStudents() throws SQLException {
        return studentDAO.getTotalStudents();
    }

    /**
     * Get students with facial data
     */
    public List<Student> getStudentsWithFacialData() throws SQLException {
        return studentDAO.getStudentsWithFacialData();
    }

    /**
     * Validate student data before registration
     */
    public void validateStudentData(String admissionNumber, String rollNumber, String fullName,
                                   int semester, String academicYear) throws IllegalArgumentException {
        if (admissionNumber == null || admissionNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Admission number is required");
        }
        if (rollNumber == null || rollNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Roll number is required");
        }
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name is required");
        }
        if (semester < 1 || semester > 12) {
            throw new IllegalArgumentException("Semester must be between 1 and 12");
        }
        if (academicYear == null || academicYear.trim().isEmpty()) {
            throw new IllegalArgumentException("Academic year is required");
        }
    }

    /**
     * Check if student exists
     */
    public boolean studentExists(String admissionNumber) throws SQLException {
        return studentDAO.studentExists(admissionNumber);
    }
}

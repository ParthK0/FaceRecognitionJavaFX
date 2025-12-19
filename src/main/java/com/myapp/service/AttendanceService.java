package com.myapp.service;

import com.myapp.dao.AttendanceDAO;
import com.myapp.dao.StudentDAO;
import com.myapp.model.Attendance;
import com.myapp.model.Student;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Service class for Attendance Management
 * Handles business logic for attendance operations
 */
public class AttendanceService {
    private final AttendanceDAO attendanceDAO;
    private final StudentDAO studentDAO;

    public AttendanceService() {
        this.attendanceDAO = new AttendanceDAO();
        this.studentDAO = new StudentDAO();
    }

    /**
     * Mark attendance for a student
     */
    public boolean markAttendance(int studentId, int courseId, Attendance.SessionType sessionType) 
            throws SQLException {
        
        // Verify student exists and is active
        Student student = studentDAO.getStudentById(studentId);
        if (student == null) {
            throw new IllegalArgumentException("Student with ID " + studentId + " does not exist");
        }
        if (!student.isActive()) {
            throw new IllegalArgumentException("Student is not active");
        }

        // Check if attendance already marked
        if (attendanceDAO.attendanceExists(studentId, courseId, LocalDate.now(), sessionType)) {
            System.out.println("⚠ Attendance already marked for " + student.getFullName() + 
                             " (" + student.getAdmissionNumber() + ") for " + sessionType.getDisplayName() + " session today");
            return false;
        }

        // Create attendance record
        Attendance attendance = new Attendance(studentId, courseId, sessionType);
        attendance.setAttendanceDate(LocalDate.now());
        attendance.setAttendanceTime(LocalTime.now());
        attendance.setStatus(Attendance.AttendanceStatus.PRESENT);

        boolean marked = attendanceDAO.markAttendance(attendance);
        
        if (marked) {
            System.out.println("✓ Attendance marked successfully for " + student.getFullName() + 
                             " (" + student.getAdmissionNumber() + ")");
        }
        
        return marked;
    }

    /**
     * Mark attendance with specific date and time
     */
    public boolean markAttendance(int studentId, int courseId, LocalDate date, 
                                 LocalTime time, Attendance.SessionType sessionType) throws SQLException {
        
        // Verify student exists and is active
        Student student = studentDAO.getStudentById(studentId);
        if (student == null) {
            throw new IllegalArgumentException("Student with ID " + studentId + " does not exist");
        }

        // Check if attendance already marked
        if (attendanceDAO.attendanceExists(studentId, courseId, date, sessionType)) {
            return false;
        }

        // Create attendance record
        Attendance attendance = new Attendance(studentId, courseId, sessionType);
        attendance.setAttendanceDate(date);
        attendance.setAttendanceTime(time);

        return attendanceDAO.markAttendance(attendance);
    }

    /**
     * Check if attendance is already marked
     */
    public boolean isAttendanceMarked(int studentId, int courseId, Attendance.SessionType sessionType) 
            throws SQLException {
        return attendanceDAO.attendanceExists(studentId, courseId, LocalDate.now(), sessionType);
    }

    /**
     * Get attendance records for a student
     */
    public List<Attendance> getStudentAttendance(int studentId) throws SQLException {
        return attendanceDAO.getAttendanceByStudent(studentId);
    }

    /**
     * Get attendance records for a course
     */
    public List<Attendance> getCourseAttendance(int courseId) throws SQLException {
        return attendanceDAO.getAttendanceByCourse(courseId);
    }

    /**
     * Get today's attendance
     */
    public List<Attendance> getTodayAttendance() throws SQLException {
        return attendanceDAO.getTodayAttendance();
    }

    /**
     * Get attendance for a specific date
     */
    public List<Attendance> getAttendanceByDate(LocalDate date) throws SQLException {
        return attendanceDAO.getAttendanceByDate(date);
    }

    /**
     * Get attendance for date range
     */
    public List<Attendance> getAttendanceByDateRange(LocalDate startDate, LocalDate endDate) 
            throws SQLException {
        return attendanceDAO.getAttendanceByDateRange(startDate, endDate);
    }

    /**
     * Get attendance count for a student in a course
     */
    public int getAttendanceCount(int studentId, int courseId) throws SQLException {
        return attendanceDAO.getAttendanceCount(studentId, courseId);
    }

    /**
     * Get attendance percentage for a student in a course
     */
    public double getAttendancePercentage(int studentId, int courseId) throws SQLException {
        return attendanceDAO.getAttendancePercentage(studentId, courseId);
    }

    /**
     * Update attendance status
     */
    public boolean updateAttendanceStatus(int attendanceId, Attendance.AttendanceStatus status, 
                                         String remarks) throws SQLException {
        return attendanceDAO.updateAttendanceStatus(attendanceId, status, remarks);
    }

    /**
     * Delete attendance record
     */
    public boolean deleteAttendance(int attendanceId) throws SQLException {
        return attendanceDAO.deleteAttendance(attendanceId);
    }

    /**
     * Get total attendance records
     */
    public int getTotalAttendanceRecords() throws SQLException {
        return attendanceDAO.getTotalAttendanceRecords();
    }

    /**
     * Generate attendance summary for today
     */
    public void displayTodayAttendanceSummary() throws SQLException {
        List<Attendance> todayRecords = getTodayAttendance();
        
        System.out.println("\n" + "═".repeat(80));
        System.out.println("                    TODAY'S ATTENDANCE SUMMARY");
        System.out.println("                    Date: " + LocalDate.now());
        System.out.println("═".repeat(80));
        
        if (todayRecords.isEmpty()) {
            System.out.println("No attendance records found for today.");
        } else {
            System.out.printf("%-5s %-20s %-15s %-25s %-10s%n", 
                            "ID", "Student Name", "Admission No", "Course", "Session");
            System.out.println("-".repeat(80));
            
            for (Attendance record : todayRecords) {
                System.out.printf("%-5d %-20s %-15s %-25s %-10s%n",
                                record.getAttendanceId(),
                                truncate(record.getStudentName(), 20),
                                record.getAdmissionNumber(),
                                truncate(record.getCourseName(), 25),
                                record.getSessionType().getDisplayName());
            }
            
            System.out.println("-".repeat(80));
            System.out.println("Total Present: " + todayRecords.size());
        }
        System.out.println("═".repeat(80));
    }

    /**
     * Helper method to truncate string
     */
    private String truncate(String str, int length) {
        if (str == null) return "";
        return str.length() > length ? str.substring(0, length - 3) + "..." : str;
    }

    /**
     * Validate attendance marking eligibility
     */
    public void validateAttendanceEligibility(int studentId, int courseId) throws SQLException {
        Student student = studentDAO.getStudentById(studentId);
        
        if (student == null) {
            throw new IllegalArgumentException("Student not found");
        }
        
        if (!student.isActive()) {
            throw new IllegalArgumentException("Student is not active");
        }
        
        if (student.getCourseId() != courseId) {
            throw new IllegalArgumentException("Student is not enrolled in this course");
        }
    }
}

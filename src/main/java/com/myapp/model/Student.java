package com.myapp.model;

import java.time.LocalDateTime;

/**
 * Entity class representing a Student in the attendance system
 */
public class Student {
    private int studentId;
    private String admissionNumber;
    private String rollNumber;
    private String fullName;
    private int courseId;
    private int semester;
    private String academicYear;
    private String email;
    private String phone;
    private String facialDataPath;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // For joining with Course
    private String courseName;

    // Constructors
    public Student() {
        this.isActive = true;
    }

    public Student(String admissionNumber, String rollNumber, String fullName, 
                   int courseId, int semester, String academicYear) {
        this.admissionNumber = admissionNumber;
        this.rollNumber = rollNumber;
        this.fullName = fullName;
        this.courseId = courseId;
        this.semester = semester;
        this.academicYear = academicYear;
        this.isActive = true;
    }

    // Getters and Setters
    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getAdmissionNumber() {
        return admissionNumber;
    }

    public void setAdmissionNumber(String admissionNumber) {
        this.admissionNumber = admissionNumber;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFacialDataPath() {
        return facialDataPath;
    }

    public void setFacialDataPath(String facialDataPath) {
        this.facialDataPath = facialDataPath;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    @Override
    public String toString() {
        return String.format("Student[ID=%d, AdmNo=%s, Roll=%s, Name=%s, Course=%s, Sem=%d, Year=%s]",
                studentId, admissionNumber, rollNumber, fullName, courseName != null ? courseName : "N/A", 
                semester, academicYear);
    }

    public String toDetailedString() {
        return String.format(
            "╔═══════════════════════════════════════════════════════════════╗\n" +
            "║                    STUDENT PROFILE                            ║\n" +
            "╠═══════════════════════════════════════════════════════════════╣\n" +
            "║ Student ID        : %-42d ║\n" +
            "║ Admission Number  : %-42s ║\n" +
            "║ Roll Number       : %-42s ║\n" +
            "║ Full Name         : %-42s ║\n" +
            "║ Course            : %-42s ║\n" +
            "║ Semester          : %-42d ║\n" +
            "║ Academic Year     : %-42s ║\n" +
            "║ Email             : %-42s ║\n" +
            "║ Phone             : %-42s ║\n" +
            "║ Status            : %-42s ║\n" +
            "╚═══════════════════════════════════════════════════════════════╝",
            studentId, admissionNumber, rollNumber, fullName, 
            courseName != null ? courseName : "N/A",
            semester, academicYear,
            email != null ? email : "N/A",
            phone != null ? phone : "N/A",
            isActive ? "Active" : "Inactive"
        );
    }
}

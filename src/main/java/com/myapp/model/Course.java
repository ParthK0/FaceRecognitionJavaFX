package com.myapp.model;

import java.time.LocalDateTime;

/**
 * Entity class representing a Course in the attendance system
 */
public class Course {
    private int courseId;
    private String courseCode;
    private String courseName;
    private String department;
    private int credits;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public Course() {
    }

    public Course(String courseCode, String courseName, String department, int credits) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.department = department;
        this.credits = credits;
    }

    public Course(int courseId, String courseCode, String courseName, String department, int credits) {
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.department = department;
        this.credits = credits;
    }

    // Getters and Setters
    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
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

    @Override
    public String toString() {
        return String.format("Course[ID=%d, Code=%s, Name=%s, Department=%s, Credits=%d]",
                courseId, courseCode, courseName, department, credits);
    }
}

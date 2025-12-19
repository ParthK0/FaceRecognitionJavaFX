package com.myapp.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Entity class representing an Attendance record in the system
 */
public class Attendance {
    private int attendanceId;
    private int studentId;
    private int courseId;
    private LocalDate attendanceDate;
    private LocalTime attendanceTime;
    private SessionType sessionType;
    private AttendanceStatus status;
    private String markedBy;
    private String remarks;
    private LocalDateTime createdAt;

    // For joining with Student and Course
    private String studentName;
    private String courseName;
    private String admissionNumber;

    // Enums
    public enum SessionType {
        MORNING("Morning"),
        AFTERNOON("Afternoon"),
        EVENING("Evening"),
        FULL_DAY("Full Day");

        private final String displayName;

        SessionType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public static SessionType fromString(String text) {
            for (SessionType type : SessionType.values()) {
                if (type.displayName.equalsIgnoreCase(text)) {
                    return type;
                }
            }
            return FULL_DAY;
        }
    }

    public enum AttendanceStatus {
        PRESENT("Present"),
        ABSENT("Absent"),
        LATE("Late"),
        EXCUSED("Excused");

        private final String displayName;

        AttendanceStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public static AttendanceStatus fromString(String text) {
            for (AttendanceStatus status : AttendanceStatus.values()) {
                if (status.displayName.equalsIgnoreCase(text)) {
                    return status;
                }
            }
            return PRESENT;
        }
    }

    // Constructors
    public Attendance() {
        this.attendanceDate = LocalDate.now();
        this.attendanceTime = LocalTime.now();
        this.sessionType = SessionType.FULL_DAY;
        this.status = AttendanceStatus.PRESENT;
        this.markedBy = "Face Recognition System";
    }

    public Attendance(int studentId, int courseId) {
        this();
        this.studentId = studentId;
        this.courseId = courseId;
    }

    public Attendance(int studentId, int courseId, SessionType sessionType) {
        this(studentId, courseId);
        this.sessionType = sessionType;
    }

    // Getters and Setters
    public int getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(int attendanceId) {
        this.attendanceId = attendanceId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(LocalDate attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public LocalTime getAttendanceTime() {
        return attendanceTime;
    }

    public void setAttendanceTime(LocalTime attendanceTime) {
        this.attendanceTime = attendanceTime;
    }

    public SessionType getSessionType() {
        return sessionType;
    }

    public void setSessionType(SessionType sessionType) {
        this.sessionType = sessionType;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public void setStatus(AttendanceStatus status) {
        this.status = status;
    }

    public String getMarkedBy() {
        return markedBy;
    }

    public void setMarkedBy(String markedBy) {
        this.markedBy = markedBy;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getAdmissionNumber() {
        return admissionNumber;
    }

    public void setAdmissionNumber(String admissionNumber) {
        this.admissionNumber = admissionNumber;
    }

    @Override
    public String toString() {
        return String.format("Attendance[ID=%d, Student=%s, Course=%s, Date=%s, Time=%s, Session=%s, Status=%s]",
                attendanceId, studentName != null ? studentName : "N/A", 
                courseName != null ? courseName : "N/A",
                attendanceDate, attendanceTime, sessionType.getDisplayName(), status.getDisplayName());
    }
}

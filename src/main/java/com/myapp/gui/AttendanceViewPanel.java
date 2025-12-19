package com.myapp.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import com.myapp.dao.AttendanceDAO;
import com.myapp.dao.CourseDAO;
import com.myapp.model.Attendance;
import com.myapp.model.Course;
import java.util.List;

/**
 * Attendance Viewing Screen
 * Displays attendance records in tabular format with filtering
 */
public class AttendanceViewPanel extends JPanel {
    private MainDashboard dashboard;
    private AttendanceDAO attendanceDAO;
    private CourseDAO courseDAO;
    
    private JTable tblAttendance;
    private DefaultTableModel tableModel;
    private JComboBox<String> cmbFilterCourse;
    private JTextField txtFilterDate;
    private JButton btnFilter;
    private JButton btnShowAll;
    private JButton btnRefresh;
    private JButton btnBack;
    private JLabel lblRecordCount;
    
    public AttendanceViewPanel(MainDashboard dashboard) {
        this.dashboard = dashboard;
        this.attendanceDAO = new AttendanceDAO();
        this.courseDAO = new CourseDAO();
        
        initializeUI();
        loadAllAttendance();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        JPanel headerPanel = createHeaderPanel("View Attendance Records", 
            "Browse and filter attendance data");
        add(headerPanel, BorderLayout.NORTH);
        
        // Filter panel
        JPanel filterPanel = createFilterPanel();
        add(filterPanel, BorderLayout.WEST);
        
        // Table panel
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel(String title, String subtitle) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(155, 89, 182));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(236, 240, 241));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(subtitleLabel);
        
        return panel;
    }
    
    private JPanel createFilterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Filters"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setPreferredSize(new Dimension(220, 0));
        
        // Course filter
        panel.add(new JLabel("Filter by Course:"));
        panel.add(Box.createVerticalStrut(5));
        cmbFilterCourse = new JComboBox<>();
        cmbFilterCourse.addItem("All Courses");
        loadCoursesForFilter();
        panel.add(cmbFilterCourse);
        
        panel.add(Box.createVerticalStrut(15));
        
        // Date filter
        panel.add(new JLabel("Filter by Date:"));
        panel.add(Box.createVerticalStrut(5));
        txtFilterDate = new JTextField(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        panel.add(txtFilterDate);
        
        panel.add(Box.createVerticalStrut(10));
        
        JLabel dateHintLabel = new JLabel("<html><small>Format: YYYY-MM-DD<br>Leave empty for all dates</small></html>");
        dateHintLabel.setForeground(Color.GRAY);
        panel.add(dateHintLabel);
        
        panel.add(Box.createVerticalStrut(20));
        
        // Filter buttons
        btnFilter = new JButton("Apply Filter");
        btnFilter.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnFilter.addActionListener(e -> applyFilter());
        panel.add(btnFilter);
        
        panel.add(Box.createVerticalStrut(10));
        
        btnShowAll = new JButton("Show All");
        btnShowAll.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnShowAll.addActionListener(e -> loadAllAttendance());
        panel.add(btnShowAll);
        
        panel.add(Box.createVerticalStrut(10));
        
        btnRefresh = new JButton("Refresh");
        btnRefresh.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRefresh.addActionListener(e -> refreshCurrentView());
        panel.add(btnRefresh);
        
        panel.add(Box.createVerticalGlue());
        
        // Record count
        lblRecordCount = new JLabel("Records: 0");
        lblRecordCount.setFont(new Font("Arial", Font.BOLD, 12));
        lblRecordCount.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblRecordCount);
        
        return panel;
    }
    
    private void loadCoursesForFilter() {
        try {
            List<Course> courses = courseDAO.getAllCourses();
            for (Course course : courses) {
                cmbFilterCourse.addItem(course.getCourseId() + " - " + course.getCourseName());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading courses: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Attendance Records"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Create table
        String[] columns = {"ID", "Student ID", "Student Name", "Course", "Date", "Time", "Session", "Status", "Marked By"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tblAttendance = new JTable(tableModel);
        tblAttendance.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblAttendance.setRowHeight(25);
        tblAttendance.getTableHeader().setReorderingAllowed(false);
        tblAttendance.setAutoCreateRowSorter(true);
        
        // Set column widths
        tblAttendance.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblAttendance.getColumnModel().getColumn(1).setPreferredWidth(80);
        tblAttendance.getColumnModel().getColumn(2).setPreferredWidth(150);
        tblAttendance.getColumnModel().getColumn(3).setPreferredWidth(200);
        tblAttendance.getColumnModel().getColumn(4).setPreferredWidth(100);
        tblAttendance.getColumnModel().getColumn(5).setPreferredWidth(80);
        tblAttendance.getColumnModel().getColumn(6).setPreferredWidth(100);
        tblAttendance.getColumnModel().getColumn(7).setPreferredWidth(80);
        tblAttendance.getColumnModel().getColumn(8).setPreferredWidth(180);
        
        JScrollPane scrollPane = new JScrollPane(tblAttendance);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        
        btnBack = new JButton("Back to Dashboard");
        btnBack.setFont(new Font("Arial", Font.PLAIN, 14));
        btnBack.setPreferredSize(new Dimension(180, 40));
        btnBack.addActionListener(e -> dashboard.showHome());
        
        panel.add(btnBack);
        
        return panel;
    }
    
    private void loadAllAttendance() {
        try {
            List<Attendance> attendanceList = attendanceDAO.getAllAttendance();
            displayAttendance(attendanceList);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading attendance: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void applyFilter() {
        try {
            String courseFilter = (String) cmbFilterCourse.getSelectedItem();
            String dateFilter = txtFilterDate.getText().trim();
            
            List<Attendance> attendanceList;
            
            if (courseFilter != null && !courseFilter.equals("All Courses")) {
                // Filter by course
                int courseId = Integer.parseInt(courseFilter.split(" - ")[0]);
                attendanceList = attendanceDAO.getAttendanceByCourse(courseId);
            } else if (!dateFilter.isEmpty()) {
                // Filter by date
                LocalDate date = LocalDate.parse(dateFilter);
                attendanceList = attendanceDAO.getAttendanceByDate(date);
            } else {
                // No filter, show all
                attendanceList = attendanceDAO.getAllAttendance();
            }
            
            displayAttendance(attendanceList);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error applying filter: " + e.getMessage(),
                "Filter Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void refreshCurrentView() {
        // Reapply current filter or load all if no filter
        String courseFilter = (String) cmbFilterCourse.getSelectedItem();
        if (courseFilter != null && !courseFilter.equals("All Courses")) {
            applyFilter();
        } else {
            loadAllAttendance();
        }
    }
    
    private void displayAttendance(List<Attendance> attendanceList) {
        // Clear existing rows
        tableModel.setRowCount(0);
        
        // Add attendance records
        for (Attendance attendance : attendanceList) {
            Object[] row = {
                attendance.getAttendanceId(),
                attendance.getStudentId(),
                getStudentName(attendance.getStudentId()),
                getCourseName(attendance.getCourseId()),
                attendance.getAttendanceDate(),
                attendance.getAttendanceTime(),
                attendance.getSessionType(),
                attendance.getStatus(),
                attendance.getMarkedBy()
            };
            tableModel.addRow(row);
        }
        
        // Update record count
        lblRecordCount.setText("Records: " + attendanceList.size());
    }
    
    private String getStudentName(int studentId) {
        try {
            var student = new com.myapp.dao.StudentDAO().getStudentById(studentId);
            return student != null ? student.getFullName() : "Unknown";
        } catch (Exception e) {
            return "Unknown";
        }
    }
    
    private String getCourseName(int courseId) {
        try {
            Course course = courseDAO.getCourseById(courseId);
            return course != null ? course.getCourseName() : "Unknown";
        } catch (Exception e) {
            return "Unknown";
        }
    }
}

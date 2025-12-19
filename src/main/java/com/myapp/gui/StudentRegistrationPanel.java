package com.myapp.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import com.myapp.model.Student;
import com.myapp.service.StudentService;
import com.myapp.service.FacialDataCaptureService;
import com.myapp.dao.CourseDAO;
import com.myapp.model.Course;
import java.util.List;

/**
 * Student Registration Screen
 * Provides form-based student registration with facial data capture
 */
public class StudentRegistrationPanel extends JPanel {
    private MainDashboard dashboard;
    private StudentService studentService;
    private FacialDataCaptureService facialCaptureService;
    private CourseDAO courseDAO;
    
    // Form fields
    private JTextField txtFullName;
    private JTextField txtAdmissionNumber;
    private JTextField txtRollNumber;
    private JComboBox<String> cmbCourse;
    private JSpinner spnSemester;
    private JTextField txtAcademicYear;
    private JTextField txtEmail;
    private JTextField txtPhone;
    private JButton btnRegister;
    private JButton btnCaptureFace;
    private JButton btnBack;
    
    private Student registeredStudent;
    
    public StudentRegistrationPanel(MainDashboard dashboard) {
        this.dashboard = dashboard;
        this.studentService = new StudentService();
        this.facialCaptureService = new FacialDataCaptureService();
        this.courseDAO = new CourseDAO();
        
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        JPanel headerPanel = createHeaderPanel("Student Registration", 
            "Register new students and capture facial data");
        add(headerPanel, BorderLayout.NORTH);
        
        // Form panel
        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel(String title, String subtitle) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(52, 152, 219));
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
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Student Information"),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        
        // Full Name
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtFullName = new JTextField(20);
        panel.add(txtFullName, gbc);
        
        // Admission Number
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(new JLabel("Admission Number:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtAdmissionNumber = new JTextField(20);
        panel.add(txtAdmissionNumber, gbc);
        
        // Roll Number
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        panel.add(new JLabel("Roll Number:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtRollNumber = new JTextField(20);
        panel.add(txtRollNumber, gbc);
        
        // Course
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        panel.add(new JLabel("Course:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        cmbCourse = new JComboBox<>();
        loadCourses();
        panel.add(cmbCourse, gbc);
        
        // Semester
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        panel.add(new JLabel("Semester:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        spnSemester = new JSpinner(new SpinnerNumberModel(1, 1, 8, 1));
        panel.add(spnSemester, gbc);
        
        // Academic Year
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0;
        panel.add(new JLabel("Academic Year:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtAcademicYear = new JTextField("2024-25");
        panel.add(txtAcademicYear, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 0;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtEmail = new JTextField(20);
        panel.add(txtEmail, gbc);
        
        // Phone
        gbc.gridx = 0; gbc.gridy = 7; gbc.weightx = 0;
        panel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtPhone = new JTextField(20);
        panel.add(txtPhone, gbc);
        
        return panel;
    }
    
    private void loadCourses() {
        try {
            List<Course> courses = courseDAO.getAllCourses();
            cmbCourse.removeAllItems();
            for (Course course : courses) {
                cmbCourse.addItem(course.getCourseId() + " - " + course.getCourseName());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Failed to load courses: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        
        btnRegister = new JButton("Register Student");
        btnRegister.setFont(new Font("Arial", Font.BOLD, 14));
        btnRegister.setBackground(new Color(46, 204, 113));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFocusPainted(false);
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegister.setPreferredSize(new Dimension(180, 40));
        btnRegister.addActionListener(e -> registerStudent());
        
        btnCaptureFace = new JButton("Capture Facial Data");
        btnCaptureFace.setFont(new Font("Arial", Font.BOLD, 14));
        btnCaptureFace.setBackground(new Color(52, 152, 219));
        btnCaptureFace.setForeground(Color.WHITE);
        btnCaptureFace.setFocusPainted(false);
        btnCaptureFace.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCaptureFace.setPreferredSize(new Dimension(180, 40));
        btnCaptureFace.setEnabled(false);
        btnCaptureFace.addActionListener(e -> captureFacialData());
        
        btnBack = new JButton("Back to Dashboard");
        btnBack.setFont(new Font("Arial", Font.PLAIN, 14));
        btnBack.setPreferredSize(new Dimension(180, 40));
        btnBack.addActionListener(e -> dashboard.showHome());
        
        panel.add(btnRegister);
        panel.add(btnCaptureFace);
        panel.add(btnBack);
        
        return panel;
    }
    
    private void registerStudent() {
        // Validate inputs
        if (txtFullName.getText().trim().isEmpty() ||
            txtAdmissionNumber.getText().trim().isEmpty() ||
            txtRollNumber.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please fill in all required fields (Name, Admission Number, Roll Number)",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            // Extract course ID from combo box
            String courseSelection = (String) cmbCourse.getSelectedItem();
            if (courseSelection == null) {
                JOptionPane.showMessageDialog(this,
                    "Please select a course",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            int courseId = Integer.parseInt(courseSelection.split(" - ")[0]);
            
            // Register student using service method
            Student student = studentService.registerStudent(
                txtAdmissionNumber.getText().trim(),
                txtRollNumber.getText().trim(),
                txtFullName.getText().trim(),
                courseId,
                (Integer) spnSemester.getValue(),
                txtAcademicYear.getText().trim(),
                txtEmail.getText().trim(),
                txtPhone.getText().trim()
            );
            
            boolean success = (student != null);
            
            if (success) {
                registeredStudent = student;
                JOptionPane.showMessageDialog(this,
                    "Student registered successfully!\nStudent ID: " + student.getStudentId() +
                    "\n\nPlease click 'Capture Facial Data' to complete registration.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Enable capture button and disable register button
                btnRegister.setEnabled(false);
                btnCaptureFace.setEnabled(true);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to register student. Please check if admission number already exists.",
                    "Registration Failed",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error during registration: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void captureFacialData() {
        if (registeredStudent == null) {
            JOptionPane.showMessageDialog(this,
                "Please register a student first",
                "Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int choice = JOptionPane.showConfirmDialog(this,
            "The system will now activate your webcam to capture facial data.\n" +
            "Please ensure good lighting and look at the camera.\n\n" +
            "50 images will be captured. Continue?",
            "Facial Data Capture",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.INFORMATION_MESSAGE);
        
        if (choice == JOptionPane.YES_OPTION) {
            // Run capture in background thread
            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    facialCaptureService.captureFacialData(
                        registeredStudent.getStudentId(),
                        50  // Capture 50 images
                    );
                    return true;
                }
                
                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            JOptionPane.showMessageDialog(StudentRegistrationPanel.this,
                                "Facial data captured successfully!\n" +
                                "Student registration is complete.",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                            clearForm();
                        } else {
                            JOptionPane.showMessageDialog(StudentRegistrationPanel.this,
                                "Failed to capture facial data. Please try again.",
                                "Capture Failed",
                                JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(StudentRegistrationPanel.this,
                            "Error during facial capture: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                        e.printStackTrace();
                    }
                }
            };
            worker.execute();
        }
    }
    
    private void clearForm() {
        txtFullName.setText("");
        txtAdmissionNumber.setText("");
        txtRollNumber.setText("");
        txtEmail.setText("");
        txtPhone.setText("");
        spnSemester.setValue(1);
        txtAcademicYear.setText("2024-25");
        registeredStudent = null;
        btnRegister.setEnabled(true);
        btnCaptureFace.setEnabled(false);
    }
}

package com.myapp.swing;

import com.myapp.dao.StudentDAO;
import com.myapp.model.Student;
import com.myapp.service.StudentService;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.SQLException;

/**
 * Student Registration Panel - Professional enrollment form with face capture
 */
public class StudentRegistrationPanel extends JPanel {
    
    private MainDashboard parent;
    private StudentService studentService;
    
    // Form fields
    private JTextField admissionNumberField;
    private JTextField rollNumberField;
    private JTextField fullNameField;
    private JTextField courseField;
    private JTextField departmentField;
    private JSpinner semesterSpinner;
    private JTextField academicYearField;
    private JComboBox<String> genderCombo;
    private JTextField phoneField;
    private JTextField emailField;
    
    // Face capture
    private JLabel facePreviewLabel;
    private BufferedImage capturedFace;
    private String facialDataPath;
    
    public StudentRegistrationPanel(MainDashboard parent) {
        this.parent = parent;
        this.studentService = new StudentService();
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        initializeUI();
    }
    
    private void initializeUI() {
        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(52, 73, 94));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 60));
        
        JLabel titleLabel = new JLabel("Student Registration");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerPanel.add(titleLabel);
        
        // Main form panel with scroll
        JPanel formContainerPanel = new JPanel(new GridBagLayout());
        formContainerPanel.setBackground(Color.WHITE);
        formContainerPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // Identity Information Section
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        formContainerPanel.add(createSectionHeader("Identity Information"), gbc);
        gbc.gridwidth = 1;
        
        // Admission Number
        gbc.gridy = row;
        gbc.gridx = 0;
        formContainerPanel.add(createLabel("Admission Number:*"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        admissionNumberField = new JTextField(20);
        formContainerPanel.add(admissionNumberField, gbc);
        gbc.weightx = 0;
        row++;
        
        // Roll Number
        gbc.gridy = row;
        gbc.gridx = 0;
        formContainerPanel.add(createLabel("Roll Number:*"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        rollNumberField = new JTextField(20);
        formContainerPanel.add(rollNumberField, gbc);
        gbc.weightx = 0;
        row++;
        
        // Full Name
        gbc.gridy = row;
        gbc.gridx = 0;
        formContainerPanel.add(createLabel("Full Name:*"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        fullNameField = new JTextField(20);
        formContainerPanel.add(fullNameField, gbc);
        gbc.weightx = 0;
        row++;
        
        // Academic Information Section
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        formContainerPanel.add(createSectionHeader("Academic Information"), gbc);
        gbc.gridwidth = 1;
        
        // Course
        gbc.gridy = row;
        gbc.gridx = 0;
        formContainerPanel.add(createLabel("Course/Program:*"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        courseField = new JTextField(20);
        formContainerPanel.add(courseField, gbc);
        gbc.weightx = 0;
        row++;
        
        // Department
        gbc.gridy = row;
        gbc.gridx = 0;
        formContainerPanel.add(createLabel("Department:*"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        departmentField = new JTextField(20);
        formContainerPanel.add(departmentField, gbc);
        gbc.weightx = 0;
        row++;
        
        // Semester
        gbc.gridy = row;
        gbc.gridx = 0;
        formContainerPanel.add(createLabel("Semester:*"), gbc);
        gbc.gridx = 1;
        semesterSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 12, 1));
        formContainerPanel.add(semesterSpinner, gbc);
        row++;
        
        // Academic Year
        gbc.gridy = row;
        gbc.gridx = 0;
        formContainerPanel.add(createLabel("Academic Year:*"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        academicYearField = new JTextField("2024-2025", 20);
        formContainerPanel.add(academicYearField, gbc);
        gbc.weightx = 0;
        row++;
        
        // Personal Information Section
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        formContainerPanel.add(createSectionHeader("Personal Information"), gbc);
        gbc.gridwidth = 1;
        
        // Gender
        gbc.gridy = row;
        gbc.gridx = 0;
        formContainerPanel.add(createLabel("Gender:"), gbc);
        gbc.gridx = 1;
        genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        formContainerPanel.add(genderCombo, gbc);
        row++;
        
        // Phone
        gbc.gridy = row;
        gbc.gridx = 0;
        formContainerPanel.add(createLabel("Phone Number:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        phoneField = new JTextField(20);
        formContainerPanel.add(phoneField, gbc);
        gbc.weightx = 0;
        row++;
        
        // Email
        gbc.gridy = row;
        gbc.gridx = 0;
        formContainerPanel.add(createLabel("Email Address:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        emailField = new JTextField(20);
        formContainerPanel.add(emailField, gbc);
        gbc.weightx = 0;
        row++;
        
        // Face Capture Section
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        formContainerPanel.add(createSectionHeader("Biometric Enrollment"), gbc);
        gbc.gridwidth = 1;
        
        // Face preview
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JPanel facePanel = createFacePreviewPanel();
        formContainerPanel.add(facePanel, gbc);
        gbc.gridwidth = 1;
        row++;
        
        // Buttons
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 8, 8, 8);
        JPanel buttonPanel = createButtonPanel();
        formContainerPanel.add(buttonPanel, gbc);
        
        JScrollPane scrollPane = new JScrollPane(formContainerPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 13));
        return label;
    }
    
    private JPanel createSectionHeader(String text) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(Color.WHITE);
        
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 15));
        label.setForeground(new Color(52, 73, 94));
        
        JSeparator separator = new JSeparator();
        separator.setPreferredSize(new Dimension(400, 1));
        
        panel.add(label);
        
        return panel;
    }
    
    private JPanel createFacePreviewPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Facial Image"));
        
        facePreviewLabel = new JLabel("No image captured", SwingConstants.CENTER);
        facePreviewLabel.setPreferredSize(new Dimension(200, 200));
        facePreviewLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        facePreviewLabel.setOpaque(true);
        facePreviewLabel.setBackground(new Color(240, 240, 240));
        
        JButton captureButton = new JButton("📷 Capture Face");
        captureButton.setFont(new Font("Arial", Font.BOLD, 13));
        captureButton.setBackground(new Color(52, 152, 219));
        captureButton.setForeground(Color.WHITE);
        captureButton.setFocusPainted(false);
        captureButton.addActionListener(e -> captureFace());
        
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(facePreviewLabel);
        
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(captureButton, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(Color.WHITE);
        
        JButton saveButton = new JButton("💾 Save Student");
        saveButton.setFont(new Font("Arial", Font.BOLD, 14));
        saveButton.setBackground(new Color(46, 204, 113));
        saveButton.setForeground(Color.WHITE);
        saveButton.setPreferredSize(new Dimension(150, 40));
        saveButton.setFocusPainted(false);
        saveButton.addActionListener(e -> saveStudent());
        
        JButton clearButton = new JButton("🔄 Clear Form");
        clearButton.setFont(new Font("Arial", Font.PLAIN, 14));
        clearButton.setBackground(new Color(149, 165, 166));
        clearButton.setForeground(Color.WHITE);
        clearButton.setPreferredSize(new Dimension(150, 40));
        clearButton.setFocusPainted(false);
        clearButton.addActionListener(e -> clearForm());
        
        panel.add(saveButton);
        panel.add(clearButton);
        
        return panel;
    }
    
    private void captureFace() {
        // TODO: Implement camera capture
        JOptionPane.showMessageDialog(this,
            "Face capture feature will open camera and capture image.\n" +
            "Integration with OpenCV camera module pending.",
            "Capture Face",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void saveStudent() {
        // Validate form
        if (!validateForm()) {
            return;
        }
        
        try {
            // Call service with individual parameters
            Student student = studentService.registerStudent(
                admissionNumberField.getText().trim(),
                rollNumberField.getText().trim(),
                fullNameField.getText().trim(),
                1, // TODO: Get actual course ID from course selection
                (Integer) semesterSpinner.getValue(),
                academicYearField.getText().trim(),
                emailField.getText().trim(),
                phoneField.getText().trim()
            );
            
            if (student != null && student.getStudentId() > 0) {
                JOptionPane.showMessageDialog(this,
                    "Student registered successfully!\nStudent ID: " + student.getStudentId(),
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to register student. Please try again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error: " + ex.getMessage(),
                "Registration Error",
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private boolean validateForm() {
        // Check required fields
        if (admissionNumberField.getText().trim().isEmpty()) {
            showValidationError("Admission Number is required");
            admissionNumberField.requestFocus();
            return false;
        }
        
        if (rollNumberField.getText().trim().isEmpty()) {
            showValidationError("Roll Number is required");
            rollNumberField.requestFocus();
            return false;
        }
        
        if (fullNameField.getText().trim().isEmpty()) {
            showValidationError("Full Name is required");
            fullNameField.requestFocus();
            return false;
        }
        
        if (courseField.getText().trim().isEmpty()) {
            showValidationError("Course is required");
            courseField.requestFocus();
            return false;
        }
        
        if (departmentField.getText().trim().isEmpty()) {
            showValidationError("Department is required");
            departmentField.requestFocus();
            return false;
        }
        
        if (academicYearField.getText().trim().isEmpty()) {
            showValidationError("Academic Year is required");
            academicYearField.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void showValidationError(String message) {
        JOptionPane.showMessageDialog(this,
            message,
            "Validation Error",
            JOptionPane.WARNING_MESSAGE);
    }
    
    private void clearForm() {
        admissionNumberField.setText("");
        rollNumberField.setText("");
        fullNameField.setText("");
        courseField.setText("");
        departmentField.setText("");
        semesterSpinner.setValue(1);
        academicYearField.setText("2024-2025");
        genderCombo.setSelectedIndex(0);
        phoneField.setText("");
        emailField.setText("");
        facePreviewLabel.setText("No image captured");
        facePreviewLabel.setIcon(null);
        capturedFace = null;
        facialDataPath = null;
    }
}

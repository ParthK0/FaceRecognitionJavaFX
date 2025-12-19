package com.myapp.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import com.myapp.service.FaceRecognitionAttendanceService;
import com.myapp.dao.CourseDAO;
import com.myapp.model.Course;
import java.util.List;

/**
 * Attendance Marking Screen
 * Allows marking attendance through live face recognition
 */
public class AttendanceMarkingPanel extends JPanel {
    private MainDashboard dashboard;
    private FaceRecognitionAttendanceService recognitionService;
    private CourseDAO courseDAO;
    
    private JComboBox<String> cmbCourse;
    private JComboBox<String> cmbSession;
    private JButton btnStartRecognition;
    private JButton btnStopRecognition;
    private JButton btnBack;
    private JTextArea txtLog;
    private JLabel lblStatus;
    
    private boolean isRecognizing = false;
    
    public AttendanceMarkingPanel(MainDashboard dashboard) {
        this.dashboard = dashboard;
        this.recognitionService = new FaceRecognitionAttendanceService();
        this.courseDAO = new CourseDAO();
        
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        JPanel headerPanel = createHeaderPanel("Mark Attendance", 
            "Automated attendance marking using face recognition");
        add(headerPanel, BorderLayout.NORTH);
        
        // Configuration panel
        JPanel configPanel = createConfigPanel();
        add(configPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel(String title, String subtitle) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(231, 76, 60));
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
    
    private JPanel createConfigPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        
        // Settings panel
        JPanel settingsPanel = new JPanel(new GridBagLayout());
        settingsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Attendance Settings"),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Course selection
        gbc.gridx = 0; gbc.gridy = 0;
        settingsPanel.add(new JLabel("Course:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        cmbCourse = new JComboBox<>();
        loadCourses();
        settingsPanel.add(cmbCourse, gbc);
        
        // Session type
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        settingsPanel.add(new JLabel("Session Type:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        cmbSession = new JComboBox<>(new String[]{"Morning", "Afternoon", "Evening", "Full Day"});
        settingsPanel.add(cmbSession, gbc);
        
        // Status label
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        lblStatus = new JLabel("Status: Ready");
        lblStatus.setFont(new Font("Arial", Font.BOLD, 14));
        lblStatus.setForeground(new Color(39, 174, 96));
        settingsPanel.add(lblStatus, gbc);
        
        // Log panel
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Recognition Log"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        txtLog = new JTextArea(15, 50);
        txtLog.setEditable(false);
        txtLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(txtLog);
        logPanel.add(scrollPane, BorderLayout.CENTER);
        
        mainPanel.add(settingsPanel, BorderLayout.NORTH);
        mainPanel.add(logPanel, BorderLayout.CENTER);
        
        return mainPanel;
    }
    
    private void loadCourses() {
        try {
            List<Course> courses = courseDAO.getAllCourses();
            cmbCourse.removeAllItems();
            for (Course course : courses) {
                cmbCourse.addItem(course.getCourseId() + " - " + course.getCourseName());
            }
        } catch (Exception e) {
            appendLog("Error loading courses: " + e.getMessage());
        }
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        
        btnStartRecognition = new JButton("Start Recognition");
        btnStartRecognition.setFont(new Font("Arial", Font.BOLD, 14));
        btnStartRecognition.setBackground(new Color(46, 204, 113));
        btnStartRecognition.setForeground(Color.WHITE);
        btnStartRecognition.setFocusPainted(false);
        btnStartRecognition.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnStartRecognition.setPreferredSize(new Dimension(180, 40));
        btnStartRecognition.addActionListener(e -> startRecognition());
        
        btnStopRecognition = new JButton("Stop Recognition");
        btnStopRecognition.setFont(new Font("Arial", Font.BOLD, 14));
        btnStopRecognition.setBackground(new Color(231, 76, 60));
        btnStopRecognition.setForeground(Color.WHITE);
        btnStopRecognition.setFocusPainted(false);
        btnStopRecognition.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnStopRecognition.setPreferredSize(new Dimension(180, 40));
        btnStopRecognition.setEnabled(false);
        btnStopRecognition.addActionListener(e -> stopRecognition());
        
        btnBack = new JButton("Back to Dashboard");
        btnBack.setFont(new Font("Arial", Font.PLAIN, 14));
        btnBack.setPreferredSize(new Dimension(180, 40));
        btnBack.addActionListener(e -> dashboard.showHome());
        
        panel.add(btnStartRecognition);
        panel.add(btnStopRecognition);
        panel.add(btnBack);
        
        return panel;
    }
    
    private void startRecognition() {
        // Validate course selection
        if (cmbCourse.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a course",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            String courseSelection = (String) cmbCourse.getSelectedItem();
            int courseId = Integer.parseInt(courseSelection.split(" - ")[0]);
            String sessionType = (String) cmbSession.getSelectedItem();
            
            int choice = JOptionPane.showConfirmDialog(this,
                "Face recognition will now start.\n" +
                "Course: " + courseSelection + "\n" +
                "Session: " + sessionType + "\n\n" +
                "Make sure the webcam is connected and positioned properly.\n" +
                "Press 'q' in the camera window to stop recognition.\n\n" +
                "Continue?",
                "Start Face Recognition",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE);
            
            if (choice == JOptionPane.YES_OPTION) {
                isRecognizing = true;
                btnStartRecognition.setEnabled(false);
                btnStopRecognition.setEnabled(true);
                cmbCourse.setEnabled(false);
                cmbSession.setEnabled(false);
                lblStatus.setText("Status: Recognition Active");
                lblStatus.setForeground(new Color(231, 76, 60));
                
                txtLog.setText("");
                appendLog("=== Face Recognition Started ===");
                appendLog("Course ID: " + courseId);
                appendLog("Session: " + sessionType);
                appendLog("Date: " + java.time.LocalDate.now());
                appendLog("================================\n");
                
                // Run recognition in background thread
                SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        com.myapp.model.Attendance.SessionType session = com.myapp.model.Attendance.SessionType.valueOf(sessionType.replace(" ", ""));
                        recognitionService.startAttendanceRecognition(courseId, session);
                        return null;
                    }
                    
                    @Override
                    protected void process(List<String> chunks) {
                        for (String message : chunks) {
                            appendLog(message);
                        }
                    }
                    
                    @Override
                    protected void done() {
                        stopRecognition();
                    }
                };
                worker.execute();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error starting recognition: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            stopRecognition();
        }
    }
    
    private void stopRecognition() {
        isRecognizing = false;
        btnStartRecognition.setEnabled(true);
        btnStopRecognition.setEnabled(false);
        cmbCourse.setEnabled(true);
        cmbSession.setEnabled(true);
        lblStatus.setText("Status: Ready");
        lblStatus.setForeground(new Color(39, 174, 96));
        
        appendLog("\n=== Recognition Stopped ===");
        
        JOptionPane.showMessageDialog(this,
            "Face recognition stopped.\nAttendance records have been saved.",
            "Recognition Complete",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void appendLog(String message) {
        txtLog.append(message + "\n");
        txtLog.setCaretPosition(txtLog.getDocument().getLength());
    }
}

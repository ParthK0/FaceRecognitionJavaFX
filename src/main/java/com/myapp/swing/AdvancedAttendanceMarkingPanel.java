package com.myapp.swing;

import com.myapp.model.Attendance;
import com.myapp.model.Student;
import com.myapp.service.AdvancedFaceRecognitionService;
import com.myapp.service.AdvancedFaceRecognitionService.FaceDetection;
import com.myapp.dao.CourseDAO;
import com.myapp.model.Course;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.Timer;

/**
 * Advanced Attendance Marking Panel with Real-Time Camera Feed
 * Production-ready UI with live face detection visualization
 */
public class AdvancedAttendanceMarkingPanel extends JPanel {
    private MainDashboard dashboard;
    private AdvancedFaceRecognitionService recognitionService;
    private CourseDAO courseDAO;
    
    // UI Components
    private JPanel cameraPanel;
    private JLabel cameraLabel;
    private JComboBox<String> cmbCourse;
    private JComboBox<String> cmbSession;
    private JButton btnStartRecognition;
    private JButton btnStopRecognition;
    private JButton btnBack;
    private JTextArea txtLog;
    private JLabel lblStatus;
    private JLabel lblFacesDetected;
    private JLabel lblAttendanceCount;
    private JProgressBar progressBar;
    
    private int facesDetectedCount = 0;
    private int attendanceMarkedCount = 0;
    private boolean isRecognizing = false;
    
    // Animation timer
    private Timer statusAnimationTimer;
    private int animationFrame = 0;
    
    public AdvancedAttendanceMarkingPanel(MainDashboard dashboard) {
        this.dashboard = dashboard;
        this.recognitionService = new AdvancedFaceRecognitionService();
        this.courseDAO = new CourseDAO();
        
        initializeUI();
        initializeAnimations();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(236, 240, 241));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Main content - split view
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(800);
        splitPane.setResizeWeight(0.6);
        
        // Left: Camera feed
        splitPane.setLeftComponent(createCameraPanel());
        
        // Right: Controls and log
        splitPane.setRightComponent(createControlPanel());
        
        add(splitPane, BorderLayout.CENTER);
        
        // Bottom: Action buttons
        add(createActionPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(41, 128, 185));
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(52, 152, 219), 2, true),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel titleLabel = new JLabel("🎥 Live Face Recognition Attendance System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel("Powered by Deep Learning & OpenCV DNN");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(236, 240, 241));
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(subtitleLabel);
        
        panel.add(textPanel, BorderLayout.WEST);
        
        // Status indicators
        JPanel statusPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        statusPanel.setOpaque(false);
        
        lblStatus = new JLabel("● System Ready");
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblStatus.setForeground(new Color(46, 204, 113));
        
        lblFacesDetected = new JLabel("👤 Faces: 0");
        lblFacesDetected.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblFacesDetected.setForeground(Color.WHITE);
        
        lblAttendanceCount = new JLabel("✓ Marked: 0");
        lblAttendanceCount.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblAttendanceCount.setForeground(Color.WHITE);
        
        statusPanel.add(lblStatus);
        statusPanel.add(lblFacesDetected);
        statusPanel.add(lblAttendanceCount);
        
        panel.add(statusPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createCameraPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(189, 195, 199), 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Camera display area
        cameraPanel = new JPanel(new BorderLayout());
        cameraPanel.setBackground(new Color(44, 62, 80));
        cameraPanel.setBorder(new LineBorder(new Color(52, 73, 94), 3));
        cameraPanel.setPreferredSize(new Dimension(800, 600));
        
        cameraLabel = new JLabel();
        cameraLabel.setHorizontalAlignment(JLabel.CENTER);
        cameraLabel.setVerticalAlignment(JLabel.CENTER);
        
        // Initial placeholder
        cameraLabel.setText("<html><center>" +
            "<div style='font-size:48px; color:#95a5a6;'>📹</div>" +
            "<div style='font-size:18px; color:#7f8c8d; margin-top:20px;'>" +
            "Camera Preview</div>" +
            "<div style='font-size:14px; color:#95a5a6; margin-top:10px;'>" +
            "Click 'Start Recognition' to begin</div>" +
            "</center></html>");
        cameraLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        cameraPanel.add(cameraLabel, BorderLayout.CENTER);
        
        // Camera info panel
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        infoPanel.setBackground(new Color(52, 73, 94));
        
        JLabel infoLabel = new JLabel("Resolution: 1280x720 | FPS: 30 | Detector: DNN ResNet-SSD");
        infoLabel.setFont(new Font("Consolas", Font.PLAIN, 11));
        infoLabel.setForeground(new Color(149, 165, 166));
        infoPanel.add(infoLabel);
        
        cameraPanel.add(infoPanel, BorderLayout.SOUTH);
        
        panel.add(cameraPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        
        // Settings panel
        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        settingsPanel.setBackground(Color.WHITE);
        settingsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                new LineBorder(new Color(52, 152, 219), 2),
                " Attendance Settings ",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                new Color(41, 128, 185)
            ),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Course selection
        settingsPanel.add(createLabeledComponent("Course:", 
            cmbCourse = createStyledComboBox()));
        settingsPanel.add(Box.createVerticalStrut(15));
        
        // Session selection
        settingsPanel.add(createLabeledComponent("Session Type:", 
            cmbSession = createStyledComboBox()));
        
        cmbSession.setModel(new DefaultComboBoxModel<>(
            new String[]{"Morning", "Afternoon", "Evening", "FullDay"}));
        
        loadCourses();
        
        // Progress indicator
        settingsPanel.add(Box.createVerticalStrut(20));
        JLabel progressLabel = new JLabel("System Status");
        progressLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        progressLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        settingsPanel.add(progressLabel);
        
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(false);
        progressBar.setStringPainted(true);
        progressBar.setString("Ready");
        progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        progressBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        settingsPanel.add(progressBar);
        
        // Log panel
        JPanel logPanel = new JPanel(new BorderLayout(5, 5));
        logPanel.setBackground(Color.WHITE);
        logPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                new LineBorder(new Color(46, 204, 113), 2),
                " Recognition Log ",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                new Color(39, 174, 96)
            ),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        txtLog = new JTextArea();
        txtLog.setEditable(false);
        txtLog.setFont(new Font("Consolas", Font.PLAIN, 11));
        txtLog.setBackground(new Color(44, 62, 80));
        txtLog.setForeground(new Color(236, 240, 241));
        txtLog.setCaretColor(Color.WHITE);
        txtLog.setLineWrap(true);
        txtLog.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(txtLog);
        scrollPane.setPreferredSize(new Dimension(300, 350));
        logPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Combine
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.add(settingsPanel, BorderLayout.NORTH);
        mainPanel.add(logPanel, BorderLayout.CENTER);
        
        panel.add(mainPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        panel.setBackground(new Color(236, 240, 241));
        
        btnStartRecognition = createStyledButton("🚀 Start Recognition", 
            new Color(46, 204, 113), new Color(39, 174, 96));
        btnStartRecognition.addActionListener(e -> startRecognition());
        
        btnStopRecognition = createStyledButton("⏹ Stop Recognition", 
            new Color(231, 76, 60), new Color(192, 57, 43));
        btnStopRecognition.setEnabled(false);
        btnStopRecognition.addActionListener(e -> stopRecognition());
        
        btnBack = createStyledButton("← Back to Dashboard", 
            new Color(149, 165, 166), new Color(127, 140, 141));
        btnBack.addActionListener(e -> {
            if (isRecognizing) {
                int choice = JOptionPane.showConfirmDialog(this,
                    "Recognition is still running. Stop and go back?",
                    "Confirm",
                    JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    stopRecognition();
                    dashboard.showPanel("home");
                }
            } else {
                dashboard.showPanel("home");
            }
        });
        
        panel.add(btnStartRecognition);
        panel.add(btnStopRecognition);
        panel.add(btnBack);
        
        return panel;
    }
    
    private JButton createStyledButton(String text, Color bgColor, Color hoverColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(200, 45));
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(hoverColor);
                }
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    private JComboBox<String> createStyledComboBox() {
        JComboBox<String> combo = new JComboBox<>();
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        combo.setAlignmentX(Component.LEFT_ALIGNMENT);
        return combo;
    }
    
    private JPanel createLabeledComponent(String labelText, JComponent component) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(label);
        panel.add(Box.createVerticalStrut(5));
        panel.add(component);
        
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
            appendLog("❌ Error loading courses: " + e.getMessage());
        }
    }
    
    private void initializeAnimations() {
        statusAnimationTimer = new Timer(500, e -> {
            if (isRecognizing) {
                animationFrame = (animationFrame + 1) % 4;
                String dots = "...".substring(0, animationFrame);
                lblStatus.setText("● Recognizing" + dots);
            }
        });
    }
    
    private void startRecognition() {
        // Validate
        if (cmbCourse.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a course", "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String courseSelection = (String) cmbCourse.getSelectedItem();
        int courseId = Integer.parseInt(courseSelection.split(" - ")[0]);
        String sessionStr = (String) cmbSession.getSelectedItem();
        Attendance.SessionType sessionType = Attendance.SessionType.valueOf(sessionStr.replace(" ", ""));
        
        // Confirm
        int choice = JOptionPane.showConfirmDialog(this,
            "<html><body style='width: 300px;'>" +
            "<h3>Start Face Recognition</h3>" +
            "<p><b>Course:</b> " + courseSelection + "</p>" +
            "<p><b>Session:</b> " + sessionStr + "</p>" +
            "<br><p>The system will use DNN-based face detection for accurate recognition.</p>" +
            "<p>Make sure the camera has a clear view of the classroom.</p>" +
            "</body></html>",
            "Confirm Start",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.INFORMATION_MESSAGE);
        
        if (choice != JOptionPane.OK_OPTION) {
            return;
        }
        
        // Initialize
        progressBar.setIndeterminate(true);
        progressBar.setString("Initializing...");
        
        SwingWorker<Boolean, String> initWorker = new SwingWorker<Boolean, String>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                publish("🔄 Initializing recognition system...");
                if (!recognitionService.initialize()) {
                    publish("❌ Failed to initialize system");
                    return false;
                }
                
                publish("🎥 Starting camera...");
                if (!recognitionService.startCamera()) {
                    publish("❌ Failed to start camera");
                    return false;
                }
                
                publish("✅ System ready");
                return true;
            }
            
            @Override
            protected void process(List<String> chunks) {
                for (String msg : chunks) {
                    appendLog(msg);
                }
            }
            
            @Override
            protected void done() {
                try {
                    if (get()) {
                        // Start recognition
                        isRecognizing = true;
                        btnStartRecognition.setEnabled(false);
                        btnStopRecognition.setEnabled(true);
                        cmbCourse.setEnabled(false);
                        cmbSession.setEnabled(false);
                        progressBar.setIndeterminate(false);
                        progressBar.setValue(100);
                        progressBar.setString("Active");
                        
                        lblStatus.setText("● Recognizing...");
                        lblStatus.setForeground(new Color(231, 76, 60));
                        statusAnimationTimer.start();
                        
                        facesDetectedCount = 0;
                        attendanceMarkedCount = 0;
                        
                        appendLog("═══════════════════════════════");
                        appendLog("🎯 Recognition Started");
                        appendLog("Course: " + courseSelection);
                        appendLog("Session: " + sessionStr);
                        appendLog("═══════════════════════════════\n");
                        
                        // Start recognition with callback
                        recognitionService.startRecognition(courseId, sessionType,
                            new AdvancedFaceRecognitionService.RecognitionCallback() {
                                @Override
                                public void onFrameProcessed(BufferedImage frame, 
                                                            List<FaceDetection> detections) {
                                    SwingUtilities.invokeLater(() -> {
                                        // Update camera display
                                        ImageIcon icon = new ImageIcon(frame);
                                        cameraLabel.setIcon(icon);
                                        cameraLabel.setText(null);
                                        
                                        // Update face count
                                        facesDetectedCount = detections.size();
                                        lblFacesDetected.setText("👤 Faces: " + facesDetectedCount);
                                    });
                                }
                                
                                @Override
                                public void onAttendanceMarked(Student student, 
                                                               boolean success, String message) {
                                    SwingUtilities.invokeLater(() -> {
                                        if (success) {
                                            attendanceMarkedCount++;
                                            lblAttendanceCount.setText("✓ Marked: " + attendanceMarkedCount);
                                            appendLog("✅ " + student.getFullName() + 
                                                     " (" + student.getAdmissionNumber() + ")");
                                        } else {
                                            appendLog("ℹ️  " + student.getFullName() + " - " + message);
                                        }
                                    });
                                }
                                
                                @Override
                                public void onError(String error) {
                                    SwingUtilities.invokeLater(() -> {
                                        appendLog("❌ " + error);
                                    });
                                }
                            });
                    } else {
                        JOptionPane.showMessageDialog(AdvancedAttendanceMarkingPanel.this,
                            "Failed to initialize recognition system",
                            "Error", JOptionPane.ERROR_MESSAGE);
                        progressBar.setIndeterminate(false);
                        progressBar.setValue(0);
                        progressBar.setString("Failed");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    appendLog("❌ Error: " + e.getMessage());
                }
            }
        };
        
        initWorker.execute();
    }
    
    private void stopRecognition() {
        if (!isRecognizing) return;
        
        statusAnimationTimer.stop();
        isRecognizing = false;
        
        recognitionService.stopRecognition();
        recognitionService.cleanup();
        
        btnStartRecognition.setEnabled(true);
        btnStopRecognition.setEnabled(false);
        cmbCourse.setEnabled(true);
        cmbSession.setEnabled(true);
        
        lblStatus.setText("● System Ready");
        lblStatus.setForeground(new Color(46, 204, 113));
        
        progressBar.setValue(0);
        progressBar.setString("Stopped");
        
        appendLog("\n═══════════════════════════════");
        appendLog("⏹ Recognition Stopped");
        appendLog("Total Faces Detected: " + facesDetectedCount);
        appendLog("Attendance Marked: " + attendanceMarkedCount);
        appendLog("═══════════════════════════════");
        
        // Reset camera display
        cameraLabel.setIcon(null);
        cameraLabel.setText("<html><center>" +
            "<div style='font-size:48px; color:#95a5a6;'>✓</div>" +
            "<div style='font-size:18px; color:#7f8c8d; margin-top:20px;'>" +
            "Recognition Stopped</div>" +
            "<div style='font-size:14px; color:#95a5a6; margin-top:10px;'>" +
            attendanceMarkedCount + " attendance records saved</div>" +
            "</center></html>");
        
        JOptionPane.showMessageDialog(this,
            String.format("<html><body style='width: 250px;'>" +
                "<h3>Recognition Complete</h3>" +
                "<p><b>Faces Detected:</b> %d</p>" +
                "<p><b>Attendance Marked:</b> %d</p>" +
                "<p>All records have been saved.</p>" +
                "</body></html>", facesDetectedCount, attendanceMarkedCount),
            "Complete",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void resetPanel() {
        if (isRecognizing) {
            stopRecognition();
        }
        txtLog.setText("");
        facesDetectedCount = 0;
        attendanceMarkedCount = 0;
        lblFacesDetected.setText("👤 Faces: 0");
        lblAttendanceCount.setText("✓ Marked: 0");
        loadCourses();
    }
    
    private void appendLog(String message) {
        String timestamp = new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date());
        txtLog.append("[" + timestamp + "] " + message + "\n");
        txtLog.setCaretPosition(txtLog.getDocument().getLength());
    }
}

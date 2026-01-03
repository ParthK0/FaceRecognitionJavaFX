package com.myapp.swing;

import javax.swing.*;
import java.awt.*;

/**
 * Attendance Marking Panel - Live face recognition for attendance
 */
public class AttendanceMarkingPanel extends JPanel {
    
    private MainDashboard parent;
    private JLabel cameraPreviewLabel;
    private JTextArea statusArea;
    private JButton startButton;
    private JButton stopButton;
    private boolean isRecognitionActive = false;
    
    public AttendanceMarkingPanel(MainDashboard parent) {
        this.parent = parent;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        initializeUI();
    }
    
    private void initializeUI() {
        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(52, 73, 94));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 60));
        
        JLabel titleLabel = new JLabel("Mark Attendance - Live Recognition");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerPanel.add(titleLabel);
        
        // Main content
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Camera preview
        JPanel cameraPanel = new JPanel(new BorderLayout());
        cameraPanel.setBorder(BorderFactory.createTitledBorder("Camera Feed"));
        
        cameraPreviewLabel = new JLabel("Camera not active", SwingConstants.CENTER);
        cameraPreviewLabel.setPreferredSize(new Dimension(640, 480));
        cameraPreviewLabel.setBackground(new Color(44, 62, 80));
        cameraPreviewLabel.setForeground(Color.WHITE);
        cameraPreviewLabel.setOpaque(true);
        cameraPreviewLabel.setFont(new Font("Arial", Font.BOLD, 16));
        cameraPreviewLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        
        cameraPanel.add(cameraPreviewLabel, BorderLayout.CENTER);
        
        // Status and controls
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setPreferredSize(new Dimension(300, 0));
        
        // Control buttons
        JPanel controlPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        controlPanel.setBackground(Color.WHITE);
        controlPanel.setBorder(BorderFactory.createTitledBorder("Controls"));
        
        startButton = new JButton("▶ Start Recognition");
        startButton.setFont(new Font("Arial", Font.BOLD, 14));
        startButton.setBackground(new Color(46, 204, 113));
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.addActionListener(e -> startRecognition());
        
        stopButton = new JButton("⏹ Stop Recognition");
        stopButton.setFont(new Font("Arial", Font.BOLD, 14));
        stopButton.setBackground(new Color(231, 76, 60));
        stopButton.setForeground(Color.WHITE);
        stopButton.setFocusPainted(false);
        stopButton.setEnabled(false);
        stopButton.addActionListener(e -> stopRecognition());
        
        JButton refreshButton = new JButton("🔄 Refresh");
        refreshButton.setFont(new Font("Arial", Font.PLAIN, 13));
        refreshButton.setBackground(new Color(52, 152, 219));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(e -> resetPanel());
        
        controlPanel.add(startButton);
        controlPanel.add(stopButton);
        controlPanel.add(refreshButton);
        
        // Status area
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createTitledBorder("Recognition Status"));
        statusPanel.setBackground(Color.WHITE);
        
        statusArea = new JTextArea();
        statusArea.setEditable(false);
        statusArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        statusArea.setLineWrap(true);
        statusArea.setWrapStyleWord(true);
        statusArea.setText("System ready. Click 'Start Recognition' to begin.\n");
        
        JScrollPane statusScroll = new JScrollPane(statusArea);
        statusScroll.setPreferredSize(new Dimension(280, 300));
        statusPanel.add(statusScroll, BorderLayout.CENTER);
        
        rightPanel.add(controlPanel, BorderLayout.NORTH);
        rightPanel.add(statusPanel, BorderLayout.CENTER);
        
        // Instructions panel
        JPanel instructionsPanel = createInstructionsPanel();
        rightPanel.add(instructionsPanel, BorderLayout.SOUTH);
        
        mainPanel.add(cameraPanel, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createInstructionsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Instructions"));
        panel.setBackground(new Color(236, 240, 241));
        panel.setPreferredSize(new Dimension(280, 120));
        
        String[] instructions = {
            "1. Click Start Recognition",
            "2. Face the camera directly",
            "3. Attendance marked automatically",
            "4. Click Stop when done"
        };
        
        for (String instruction : instructions) {
            JLabel label = new JLabel(instruction);
            label.setFont(new Font("Arial", Font.PLAIN, 11));
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(label);
            panel.add(Box.createVerticalStrut(3));
        }
        
        return panel;
    }
    
    private void startRecognition() {
        isRecognitionActive = true;
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
        
        cameraPreviewLabel.setText("Camera starting...");
        cameraPreviewLabel.setBackground(new Color(39, 174, 96));
        
        appendStatus("[" + getCurrentTime() + "] Recognition started\n");
        appendStatus("Waiting for faces to detect...\n");
        
        // TODO: Start OpenCV camera thread
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this,
                "Live recognition system will integrate with OpenCV.\n" +
                "Camera feed will appear here with real-time face detection.",
                "Recognition Started",
                JOptionPane.INFORMATION_MESSAGE);
        });
    }
    
    private void stopRecognition() {
        isRecognitionActive = false;
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        
        cameraPreviewLabel.setText("Camera stopped");
        cameraPreviewLabel.setBackground(new Color(44, 62, 80));
        
        appendStatus("[" + getCurrentTime() + "] Recognition stopped\n");
        appendStatus("System ready for next session.\n\n");
        
        // TODO: Stop OpenCV camera thread
    }
    
    public void resetPanel() {
        if (isRecognitionActive) {
            stopRecognition();
        }
        statusArea.setText("System ready. Click 'Start Recognition' to begin.\n");
    }
    
    private void appendStatus(String message) {
        statusArea.append(message);
        statusArea.setCaretPosition(statusArea.getDocument().getLength());
    }
    
    private String getCurrentTime() {
        return new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date());
    }
    
    public void onFaceDetected(String studentName, int studentId) {
        appendStatus("[" + getCurrentTime() + "] Face detected: " + studentName + "\n");
        appendStatus("Marking attendance...\n");
    }
    
    public void onAttendanceMarked(String studentName, boolean success, String message) {
        if (success) {
            appendStatus("✓ " + message + "\n");
        } else {
            appendStatus("✗ " + message + "\n");
        }
        appendStatus("─".repeat(40) + "\n");
    }
}

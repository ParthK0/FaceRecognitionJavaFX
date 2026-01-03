package com.myapp.swing;

import com.myapp.TrainerMulti;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.PrintStream;
import java.io.OutputStream;

/**
 * Model Training Panel - GUI for training face recognition models
 * Provides a complete interface for training with progress tracking
 */
public class ModelTrainingPanel extends JPanel {
    
    private MainDashboard parentDashboard;
    private JTextArea logArea;
    private JButton trainButton;
    private JButton clearLogsButton;
    private JProgressBar progressBar;
    private JLabel statusLabel;
    private JComboBox<String> studentComboBox;
    private JCheckBox trainAllCheckBox;
    
    public ModelTrainingPanel(MainDashboard parent) {
        this.parentDashboard = parent;
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        initializeComponents();
    }
    
    private void initializeComponents() {
        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Center Panel with Options and Logs
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(Color.WHITE);
        
        // Options Panel
        JPanel optionsPanel = createOptionsPanel();
        centerPanel.add(optionsPanel, BorderLayout.NORTH);
        
        // Log Panel
        JPanel logPanel = createLogPanel();
        centerPanel.add(logPanel, BorderLayout.CENTER);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Bottom Panel with Progress
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Redirect System.out to log area
        redirectSystemOutToLogArea();
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JLabel titleLabel = new JLabel("🎓 Model Training");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(41, 128, 185));
        
        JLabel subtitleLabel = new JLabel("Train face recognition models for attendance system");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.GRAY);
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(Color.WHITE);
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(subtitleLabel);
        
        panel.add(textPanel, BorderLayout.WEST);
        
        return panel;
    }
    
    private JPanel createOptionsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(41, 128, 185), 2),
            "Training Options",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14),
            new Color(41, 128, 185)
        ));
        
        // Train All Checkbox
        JPanel checkboxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        checkboxPanel.setBackground(Color.WHITE);
        trainAllCheckBox = new JCheckBox("Train All Students");
        trainAllCheckBox.setFont(new Font("Arial", Font.PLAIN, 14));
        trainAllCheckBox.setBackground(Color.WHITE);
        trainAllCheckBox.setSelected(true);
        trainAllCheckBox.addActionListener(e -> {
            studentComboBox.setEnabled(!trainAllCheckBox.isSelected());
        });
        checkboxPanel.add(trainAllCheckBox);
        panel.add(checkboxPanel);
        
        // Student Selection
        JPanel studentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        studentPanel.setBackground(Color.WHITE);
        JLabel studentLabel = new JLabel("Select Student:");
        studentLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        studentPanel.add(studentLabel);
        
        studentComboBox = new JComboBox<>();
        studentComboBox.setPreferredSize(new Dimension(200, 30));
        studentComboBox.setFont(new Font("Arial", Font.PLAIN, 13));
        studentComboBox.setEnabled(false);
        loadStudents();
        studentPanel.add(studentComboBox);
        
        JButton refreshButton = new JButton("🔄 Refresh");
        refreshButton.setFont(new Font("Arial", Font.PLAIN, 12));
        refreshButton.addActionListener(e -> loadStudents());
        studentPanel.add(refreshButton);
        
        panel.add(studentPanel);
        
        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonsPanel.setBackground(Color.WHITE);
        
        trainButton = new JButton("🚀 Start Training");
        trainButton.setFont(new Font("Arial", Font.BOLD, 14));
        trainButton.setPreferredSize(new Dimension(160, 40));
        trainButton.setBackground(new Color(46, 204, 113));
        trainButton.setForeground(Color.WHITE);
        trainButton.setFocusPainted(false);
        trainButton.setBorderPainted(false);
        trainButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        trainButton.addActionListener(e -> startTraining());
        buttonsPanel.add(trainButton);
        
        clearLogsButton = new JButton("🗑️ Clear Logs");
        clearLogsButton.setFont(new Font("Arial", Font.PLAIN, 13));
        clearLogsButton.setPreferredSize(new Dimension(130, 40));
        clearLogsButton.setBackground(new Color(231, 76, 60));
        clearLogsButton.setForeground(Color.WHITE);
        clearLogsButton.setFocusPainted(false);
        clearLogsButton.setBorderPainted(false);
        clearLogsButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearLogsButton.addActionListener(e -> clearLogs());
        buttonsPanel.add(clearLogsButton);
        
        panel.add(buttonsPanel);
        
        return panel;
    }
    
    private JPanel createLogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(52, 73, 94), 2),
            "Training Logs",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14),
            new Color(52, 73, 94)
        ));
        
        logArea = new JTextArea();
        logArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setBackground(new Color(245, 245, 245));
        logArea.setText("Ready to train models. Select options and click 'Start Training'.\n");
        
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setPreferredSize(new Dimension(0, 300));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        statusLabel = new JLabel("Status: Ready");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 13));
        statusLabel.setForeground(new Color(41, 128, 185));
        panel.add(statusLabel, BorderLayout.NORTH);
        
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(0, 25));
        progressBar.setForeground(new Color(46, 204, 113));
        panel.add(progressBar, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void loadStudents() {
        studentComboBox.removeAllItems();
        File datasetDir = new File("dataset");
        if (datasetDir.exists() && datasetDir.isDirectory()) {
            File[] students = datasetDir.listFiles(File::isDirectory);
            if (students != null && students.length > 0) {
                for (File student : students) {
                    studentComboBox.addItem(student.getName());
                }
            } else {
                studentComboBox.addItem("No students found");
            }
        } else {
            studentComboBox.addItem("Dataset folder not found");
        }
    }
    
    private void startTraining() {
        trainButton.setEnabled(false);
        progressBar.setValue(0);
        progressBar.setIndeterminate(true);
        statusLabel.setText("Status: Training in progress...");
        statusLabel.setForeground(new Color(230, 126, 34));
        
        logArea.append("\n" + "=".repeat(80) + "\n");
        logArea.append("Training Started at " + new java.util.Date() + "\n");
        logArea.append("=".repeat(80) + "\n\n");
        
        // Run training in background thread
        SwingWorker<Void, String> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    if (trainAllCheckBox.isSelected()) {
                        publish("Training all students...\n");
                        TrainerMulti.trainAll();
                    } else {
                        String selectedStudent = (String) studentComboBox.getSelectedItem();
                        if (selectedStudent != null && !selectedStudent.contains("not found")) {
                            publish("Training student: " + selectedStudent + "\n");
                            // Train single student (you may need to implement this method)
                            TrainerMulti.trainAll(); // For now, train all
                        }
                    }
                } catch (Exception ex) {
                    publish("\n❌ ERROR: " + ex.getMessage() + "\n");
                    ex.printStackTrace();
                }
                return null;
            }
            
            @Override
            protected void process(java.util.List<String> chunks) {
                for (String message : chunks) {
                    logArea.append(message);
                }
                logArea.setCaretPosition(logArea.getDocument().getLength());
            }
            
            @Override
            protected void done() {
                progressBar.setIndeterminate(false);
                progressBar.setValue(100);
                trainButton.setEnabled(true);
                statusLabel.setText("Status: Training Complete ✓");
                statusLabel.setForeground(new Color(46, 204, 113));
                
                logArea.append("\n" + "=".repeat(80) + "\n");
                logArea.append("Training Completed at " + new java.util.Date() + "\n");
                logArea.append("=".repeat(80) + "\n\n");
                
                JOptionPane.showMessageDialog(
                    ModelTrainingPanel.this,
                    "Model training completed successfully!\nModel saved to trainer/multi.yml",
                    "Training Complete",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
        };
        
        worker.execute();
    }
    
    private void clearLogs() {
        logArea.setText("Logs cleared.\nReady to train models.\n");
        progressBar.setValue(0);
        statusLabel.setText("Status: Ready");
        statusLabel.setForeground(new Color(41, 128, 185));
    }
    
    private void redirectSystemOutToLogArea() {
        PrintStream printStream = new PrintStream(new OutputStream() {
            @Override
            public void write(int b) {
                // Redirect output to log area
                SwingUtilities.invokeLater(() -> {
                    logArea.append(String.valueOf((char) b));
                    logArea.setCaretPosition(logArea.getDocument().getLength());
                });
            }
        });
        // Note: This will redirect ALL System.out calls
        // You may want to make this optional or use a separate logger
        // System.setOut(printStream);
    }
    
    public void resetPanel() {
        loadStudents();
        trainAllCheckBox.setSelected(true);
        studentComboBox.setEnabled(false);
        progressBar.setValue(0);
        statusLabel.setText("Status: Ready");
        statusLabel.setForeground(new Color(41, 128, 185));
    }
}

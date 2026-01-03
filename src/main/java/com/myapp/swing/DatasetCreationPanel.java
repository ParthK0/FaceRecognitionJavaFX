package com.myapp.swing;

import com.myapp.DatasetCreator;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Dataset Creation Panel - GUI for capturing student face images
 * Provides a complete interface for creating training datasets
 */
public class DatasetCreationPanel extends JPanel {
    
    private MainDashboard parentDashboard;
    private JTextField studentNameField;
    private JSpinner imageCountSpinner;
    private JButton captureButton;
    private JButton viewDatasetButton;
    private JTextArea statusArea;
    private JLabel datasetStatsLabel;
    
    public DatasetCreationPanel(MainDashboard parent) {
        this.parentDashboard = parent;
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        initializeComponents();
        updateDatasetStats();
    }
    
    private void initializeComponents() {
        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Center Panel
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Input Form Panel
        JPanel formPanel = createFormPanel();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        centerPanel.add(formPanel, gbc);
        
        // Status Panel
        JPanel statusPanel = createStatusPanel();
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        centerPanel.add(statusPanel, gbc);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Bottom Panel
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JLabel titleLabel = new JLabel("📸 Dataset Creation");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(41, 128, 185));
        
        JLabel subtitleLabel = new JLabel("Capture face images for training the recognition model");
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
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(41, 128, 185), 2),
            "Capture Settings",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14),
            new Color(41, 128, 185)
        ));
        
        // Student Name Field
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        namePanel.setBackground(Color.WHITE);
        JLabel nameLabel = new JLabel("Student Name:");
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        nameLabel.setPreferredSize(new Dimension(120, 25));
        namePanel.add(nameLabel);
        
        studentNameField = new JTextField(20);
        studentNameField.setFont(new Font("Arial", Font.PLAIN, 14));
        studentNameField.setToolTipText("Enter student's name (lowercase, no spaces)");
        namePanel.add(studentNameField);
        
        JLabel hintLabel = new JLabel("(e.g., john, mary)");
        hintLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        hintLabel.setForeground(Color.GRAY);
        namePanel.add(hintLabel);
        
        panel.add(namePanel);
        
        // Image Count Spinner
        JPanel countPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        countPanel.setBackground(Color.WHITE);
        JLabel countLabel = new JLabel("Number of Images:");
        countLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        countLabel.setPreferredSize(new Dimension(120, 25));
        countPanel.add(countLabel);
        
        SpinnerModel spinnerModel = new SpinnerNumberModel(50, 10, 200, 10);
        imageCountSpinner = new JSpinner(spinnerModel);
        imageCountSpinner.setFont(new Font("Arial", Font.PLAIN, 14));
        ((JSpinner.DefaultEditor) imageCountSpinner.getEditor()).getTextField().setColumns(5);
        countPanel.add(imageCountSpinner);
        
        JLabel countHintLabel = new JLabel("(Recommended: 50-100)");
        countHintLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        countHintLabel.setForeground(Color.GRAY);
        countPanel.add(countHintLabel);
        
        panel.add(countPanel);
        
        // Instructions
        JPanel instructionsPanel = new JPanel();
        instructionsPanel.setLayout(new BoxLayout(instructionsPanel, BoxLayout.Y_AXIS));
        instructionsPanel.setBackground(new Color(240, 248, 255));
        instructionsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(173, 216, 230)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel instructionTitle = new JLabel("📋 Instructions:");
        instructionTitle.setFont(new Font("Arial", Font.BOLD, 13));
        instructionsPanel.add(instructionTitle);
        
        String[] instructions = {
            "1. Enter student name in lowercase without spaces",
            "2. Set the number of images to capture (50-100 recommended)",
            "3. Click 'Start Capture' to begin",
            "4. Look at the camera and move your head slightly",
            "5. Press 'q' in the camera window to stop early",
            "6. After capture, train the model from the Training menu"
        };
        
        for (String instruction : instructions) {
            JLabel instrLabel = new JLabel(instruction);
            instrLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            instructionsPanel.add(Box.createVerticalStrut(3));
            instructionsPanel.add(instrLabel);
        }
        
        panel.add(Box.createVerticalStrut(10));
        panel.add(instructionsPanel);
        
        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonsPanel.setBackground(Color.WHITE);
        
        captureButton = new JButton("📸 Start Capture");
        captureButton.setFont(new Font("Arial", Font.BOLD, 14));
        captureButton.setPreferredSize(new Dimension(160, 40));
        captureButton.setBackground(new Color(46, 204, 113));
        captureButton.setForeground(Color.WHITE);
        captureButton.setFocusPainted(false);
        captureButton.setBorderPainted(false);
        captureButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        captureButton.addActionListener(e -> startCapture());
        buttonsPanel.add(captureButton);
        
        viewDatasetButton = new JButton("📁 View Dataset");
        viewDatasetButton.setFont(new Font("Arial", Font.PLAIN, 13));
        viewDatasetButton.setPreferredSize(new Dimension(140, 40));
        viewDatasetButton.setBackground(new Color(52, 152, 219));
        viewDatasetButton.setForeground(Color.WHITE);
        viewDatasetButton.setFocusPainted(false);
        viewDatasetButton.setBorderPainted(false);
        viewDatasetButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        viewDatasetButton.addActionListener(e -> viewDataset());
        buttonsPanel.add(viewDatasetButton);
        
        panel.add(buttonsPanel);
        
        return panel;
    }
    
    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(52, 73, 94), 2),
            "Status & Logs",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14),
            new Color(52, 73, 94)
        ));
        
        statusArea = new JTextArea();
        statusArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        statusArea.setEditable(false);
        statusArea.setLineWrap(true);
        statusArea.setWrapStyleWord(true);
        statusArea.setBackground(new Color(245, 245, 245));
        statusArea.setText("Ready to capture dataset.\nEnter student name and click 'Start Capture'.\n");
        
        JScrollPane scrollPane = new JScrollPane(statusArea);
        scrollPane.setPreferredSize(new Dimension(0, 200));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        datasetStatsLabel = new JLabel("Dataset Statistics: Loading...");
        datasetStatsLabel.setFont(new Font("Arial", Font.BOLD, 13));
        datasetStatsLabel.setForeground(new Color(41, 128, 185));
        panel.add(datasetStatsLabel, BorderLayout.WEST);
        
        JButton refreshButton = new JButton("🔄 Refresh Stats");
        refreshButton.setFont(new Font("Arial", Font.PLAIN, 12));
        refreshButton.setBackground(new Color(149, 165, 166));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setBorderPainted(false);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(e -> updateDatasetStats());
        panel.add(refreshButton, BorderLayout.EAST);
        
        return panel;
    }
    
    private void startCapture() {
        String studentName = studentNameField.getText().trim().toLowerCase();
        
        if (studentName.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Please enter a student name.",
                "Input Required",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        
        if (!studentName.matches("[a-z]+")) {
            JOptionPane.showMessageDialog(
                this,
                "Student name should contain only lowercase letters without spaces.",
                "Invalid Input",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        
        int imageCount = (int) imageCountSpinner.getValue();
        
        captureButton.setEnabled(false);
        statusArea.append("\n" + "=".repeat(60) + "\n");
        statusArea.append("Starting capture for: " + studentName + "\n");
        statusArea.append("Images to capture: " + imageCount + "\n");
        statusArea.append("=".repeat(60) + "\n\n");
        
        // Run capture in background thread
        SwingWorker<Void, String> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    publish("Initializing camera...\n");
                    DatasetCreator.capture(studentName, imageCount);
                    publish("\n✓ Capture completed successfully!\n");
                } catch (Exception ex) {
                    publish("\n❌ ERROR: " + ex.getMessage() + "\n");
                    ex.printStackTrace();
                }
                return null;
            }
            
            @Override
            protected void process(java.util.List<String> chunks) {
                for (String message : chunks) {
                    statusArea.append(message);
                }
                statusArea.setCaretPosition(statusArea.getDocument().getLength());
            }
            
            @Override
            protected void done() {
                captureButton.setEnabled(true);
                updateDatasetStats();
                
                statusArea.append("\nDataset creation complete.\n");
                statusArea.append("Next step: Train the model using the Training menu.\n");
                
                int result = JOptionPane.showConfirmDialog(
                    DatasetCreationPanel.this,
                    "Dataset created successfully for " + studentName + "!\n\n" +
                    "Would you like to train the model now?",
                    "Capture Complete",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE
                );
                
                if (result == JOptionPane.YES_OPTION) {
                    parentDashboard.showPanel("training");
                }
            }
        };
        
        worker.execute();
    }
    
    private void viewDataset() {
        File datasetDir = new File("dataset");
        if (datasetDir.exists()) {
            try {
                Desktop.getDesktop().open(datasetDir);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                    this,
                    "Could not open dataset folder.\nPath: " + datasetDir.getAbsolutePath(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        } else {
            JOptionPane.showMessageDialog(
                this,
                "Dataset folder does not exist yet.",
                "Not Found",
                JOptionPane.WARNING_MESSAGE
            );
        }
    }
    
    private void updateDatasetStats() {
        File datasetDir = new File("dataset");
        if (!datasetDir.exists() || !datasetDir.isDirectory()) {
            datasetStatsLabel.setText("Dataset Statistics: No dataset folder found");
            return;
        }
        
        File[] students = datasetDir.listFiles(File::isDirectory);
        if (students == null || students.length == 0) {
            datasetStatsLabel.setText("Dataset Statistics: 0 students, 0 images");
            return;
        }
        
        int totalImages = 0;
        for (File student : students) {
            File[] images = student.listFiles((dir, name) -> {
                String lower = name.toLowerCase();
                return lower.endsWith(".jpg") || lower.endsWith(".png") || lower.endsWith(".jpeg");
            });
            if (images != null) {
                totalImages += images.length;
            }
        }
        
        datasetStatsLabel.setText(String.format(
            "Dataset Statistics: %d students, %d total images",
            students.length,
            totalImages
        ));
    }
    
    public void resetPanel() {
        studentNameField.setText("");
        imageCountSpinner.setValue(50);
        updateDatasetStats();
    }
}

package com.myapp.gui;

import javax.swing.*;
import java.awt.*;
import com.myapp.util.DatabaseConnection;

/**
 * Main Dashboard Window - Entry point for the GUI application
 * Provides navigation to all major system functions
 */
public class MainDashboard extends JFrame {
    private static final String APP_TITLE = "Facial Recognition Based Smart Attendance System";
    private static final String VERSION = "v1.0 - Academic Project";
    
    private JPanel mainPanel;
    private CardLayout cardLayout;
    
    public MainDashboard() {
        initializeUI();
        initializeDatabase();
    }
    
    private void initializeUI() {
        setTitle(APP_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        // Create main panel with CardLayout for switching between screens
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Add dashboard home screen
        JPanel homePanel = createHomePanel();
        mainPanel.add(homePanel, "HOME");
        
        // Add other panels
        mainPanel.add(new StudentRegistrationPanel(this), "REGISTER");
        mainPanel.add(new AttendanceMarkingPanel(this), "MARK_ATTENDANCE");
        mainPanel.add(new AttendanceViewPanel(this), "VIEW_ATTENDANCE");
        
        add(mainPanel);
        
        // Show home panel by default
        cardLayout.show(mainPanel, "HOME");
    }
    
    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel(APP_TITLE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel versionLabel = new JLabel(VERSION);
        versionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        versionLabel.setForeground(Color.WHITE);
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel techLabel = new JLabel("Java • OpenCV • MySQL • Maven");
        techLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        techLabel.setForeground(new Color(236, 240, 241));
        techLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(versionLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(techLabel);
        
        // Navigation buttons panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(40, 100, 40, 100));
        
        // Create navigation buttons
        JButton btnRegister = createDashboardButton("Register Student", 
            "Register new students and capture facial data", "icons/student.png");
        JButton btnMarkAttendance = createDashboardButton("Mark Attendance", 
            "Mark attendance using face recognition", "icons/camera.png");
        JButton btnViewAttendance = createDashboardButton("View Attendance", 
            "View and filter attendance records", "icons/report.png");
        JButton btnExit = createDashboardButton("Exit Application", 
            "Close the application", "icons/exit.png");
        
        // Add action listeners
        btnRegister.addActionListener(e -> showPanel("REGISTER"));
        btnMarkAttendance.addActionListener(e -> showPanel("MARK_ATTENDANCE"));
        btnViewAttendance.addActionListener(e -> showPanel("VIEW_ATTENDANCE"));
        btnExit.addActionListener(e -> exitApplication());
        
        buttonPanel.add(btnRegister);
        buttonPanel.add(btnMarkAttendance);
        buttonPanel.add(btnViewAttendance);
        buttonPanel.add(btnExit);
        
        // Footer
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel footerLabel = new JLabel("© 2025 Facial Recognition Attendance System | Academic Project");
        footerLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        footerLabel.setForeground(Color.GRAY);
        footerPanel.add(footerLabel);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.CENTER);
        panel.add(footerPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JButton createDashboardButton(String title, String description, String iconPath) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout(10, 10));
        button.setPreferredSize(new Dimension(300, 120));
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(52, 73, 94));
        
        // Description
        JLabel descLabel = new JLabel("<html>" + description + "</html>");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        descLabel.setForeground(Color.GRAY);
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(descLabel);
        
        button.add(textPanel, BorderLayout.CENTER);
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(236, 240, 241));
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(41, 128, 185), 2),
                    BorderFactory.createEmptyBorder(15, 15, 15, 15)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.WHITE);
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
                    BorderFactory.createEmptyBorder(15, 15, 15, 15)
                ));
            }
        });
        
        return button;
    }
    
    public void showPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);
    }
    
    public void showHome() {
        cardLayout.show(mainPanel, "HOME");
    }
    
    private void initializeDatabase() {
        try {
            DatabaseConnection.initializeDatabase();
            System.out.println("Database initialized successfully");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Failed to initialize database:\n" + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
            System.err.println("Database initialization failed: " + e.getMessage());
        }
    }
    
    private void exitApplication() {
        int choice = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to exit?",
            "Confirm Exit",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (choice == JOptionPane.YES_OPTION) {
            System.out.println("Application closed by user");
            System.exit(0);
        }
    }
    
    public static void main(String[] args) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Launch GUI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MainDashboard dashboard = new MainDashboard();
            dashboard.setVisible(true);
        });
    }
}

package com.myapp.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Main Dashboard - Modern professional design with advanced UI
 */
public class MainDashboard extends JFrame {
    
    private JPanel contentPanel;
    private CardLayout cardLayout;
    
    // Panel references
    private StudentRegistrationPanel studentRegistrationPanel;
    private AdvancedAttendanceMarkingPanel advancedAttendanceMarkingPanel;
    private AttendanceViewerPanel attendanceViewerPanel;
    private DashboardHomePanel homePanel;
    private ModelTrainingPanel modelTrainingPanel;
    private DatasetCreationPanel datasetCreationPanel;
    // private ManageStudentsPanel manageStudentsPanel;
    
    public MainDashboard() {
        initializeFrame();
        createContent();
        showHomePanel();
    }
    
    private void initializeFrame() {
        setTitle("Facial Recognition Based Smart Attendance System - Professional Edition");
        setSize(1600, 950);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Start maximized
        
        // Set professional look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            // Modern UI tweaks
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 10);
            UIManager.put("ProgressBar.arc", 10);
            UIManager.put("TextComponent.arc", 10);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Set application icon if available
        try {
            setIconImage(Toolkit.getDefaultToolkit().createImage("icon.png"));
        } catch (Exception e) {
            // Icon not critical, continue without it
        }
    }
    
    private void createContent() {
        // Content panel with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Color.WHITE);
        
        // Initialize panels - use advanced attendance panel
        homePanel = new DashboardHomePanel(this);
        studentRegistrationPanel = new StudentRegistrationPanel(this);
        advancedAttendanceMarkingPanel = new AdvancedAttendanceMarkingPanel(this);
        attendanceViewerPanel = new AttendanceViewerPanel(this);
        modelTrainingPanel = new ModelTrainingPanel(this);
        datasetCreationPanel = new DatasetCreationPanel(this);
        // manageStudentsPanel = new ManageStudentsPanel(this);
        
        // Add panels to card layout
        contentPanel.add(homePanel, "home");
        contentPanel.add(studentRegistrationPanel, "registration");
        contentPanel.add(advancedAttendanceMarkingPanel, "marking");
        contentPanel.add(attendanceViewerPanel, "viewer");
        contentPanel.add(modelTrainingPanel, "training");
        contentPanel.add(datasetCreationPanel, "dataset");
        // contentPanel.add(manageStudentsPanel, "manage");
        
        add(contentPanel);
    }
    
    public void showPanel(String panelName) {
        cardLayout.show(contentPanel, panelName);
        
        // Refresh panel if needed
        switch(panelName) {
            case "viewer":
                attendanceViewerPanel.refreshData();
                break;
            case "marking":
                advancedAttendanceMarkingPanel.resetPanel();
                break;
            case "training":
                modelTrainingPanel.resetPanel();
                break;
            case "dataset":
                datasetCreationPanel.resetPanel();
                break;
            // case "manage":
            //     manageStudentsPanel.refreshData();
            //     break;
        }
    }
    
    public void showHome() {
        showPanel("home");
    }
    
    private void showHomePanel() {
        cardLayout.show(contentPanel, "home");
    }
    
    public void exitApplication() {
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to exit the application?",
            "Confirm Exit",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (choice == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainDashboard dashboard = new MainDashboard();
            dashboard.setVisible(true);
        });
    }
}

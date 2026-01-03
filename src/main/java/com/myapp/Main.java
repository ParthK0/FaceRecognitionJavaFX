package com.myapp;

import com.myapp.swing.MainDashboard;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Main Entry Point for Facial Recognition Based Smart Attendance System
 * Launches the professional Swing-based GUI dashboard
 */
public class Main {
    
    public static void main(String[] args) {
        // Set system look and feel for professional appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Could not set system look and feel: " + e.getMessage());
        }
        
        // Launch GUI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                MainDashboard dashboard = new MainDashboard();
                dashboard.setVisible(true);
                
                System.out.println("╔═══════════════════════════════════════════════════════════════╗");
                System.out.println("║  Facial Recognition Based Smart Attendance System Started    ║");
                System.out.println("╚═══════════════════════════════════════════════════════════════╝");
                System.out.println();
                System.out.println("System initialized successfully.");
                System.out.println("GUI dashboard is now running.");
                System.out.println();
                
            } catch (Exception e) {
                System.err.println("Failed to start application: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}

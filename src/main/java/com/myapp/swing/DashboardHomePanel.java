package com.myapp.swing;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Modern Dashboard Home Panel with premium design
 */
public class DashboardHomePanel extends JPanel {
    
    private MainDashboard parent;
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private static final Color WARNING_COLOR = new Color(230, 126, 34);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color CARD_BG = Color.WHITE;
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 36);
    private static final Font CARD_TITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font CARD_DESC_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    
    public DashboardHomePanel(MainDashboard parent) {
        this.parent = parent;
        setLayout(new BorderLayout());
        setBackground(new Color(236, 240, 241));
        initializeUI();
    }
    
    private void initializeUI() {
        // Top header with gradient effect
        JPanel headerPanel = createHeaderPanel();
        
        // Center panel with feature cards
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(236, 240, 241));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        
        // Welcome section
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBackground(new Color(236, 240, 241));
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JLabel welcomeLabel = new JLabel("Welcome to the Attendance System");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(new Color(44, 62, 80));
        
        JLabel subtitleLabel = new JLabel("Select an option below to get started");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(127, 140, 141));
        
        JPanel welcomeTextPanel = new JPanel();
        welcomeTextPanel.setLayout(new BoxLayout(welcomeTextPanel, BoxLayout.Y_AXIS));
        welcomeTextPanel.setBackground(new Color(236, 240, 241));
        welcomeTextPanel.add(welcomeLabel);
        welcomeTextPanel.add(Box.createVerticalStrut(5));
        welcomeTextPanel.add(subtitleLabel);
        
        welcomePanel.add(welcomeTextPanel, BorderLayout.WEST);
        
        // Feature cards in grid - 3 rows x 3 columns for 9 enterprise features
        JPanel cardsPanel = new JPanel(new GridLayout(3, 3, 20, 20));
        cardsPanel.setBackground(new Color(236, 240, 241));
        
        // Card 1: Mark Attendance (Primary action)
        cardsPanel.add(createFeatureCard(
            "🎥 Mark Attendance",
            "Live face recognition with DNN detection for accurate attendance marking",
            PRIMARY_COLOR,
            e -> parent.showPanel("marking")
        ));
        
        // Card 2: Analytics Dashboard (Enterprise)
        cardsPanel.add(createFeatureCard(
            "📊 Analytics Dashboard",
            "⭐ Enterprise - Real-time charts, statistics and attendance insights",
            new Color(142, 68, 173),
            e -> parent.showPanel("analytics")
        ));
        
        // Card 3: Multi-Camera System (Enterprise)
        cardsPanel.add(createFeatureCard(
            "📹 Multi-Camera",
            "⭐ Enterprise - Manage multiple cameras for large-scale deployments",
            new Color(41, 128, 185),
            e -> parent.showPanel("multicamera")
        ));
        
        // Card 4: Register Student
        cardsPanel.add(createFeatureCard(
            "👤 Register Student",
            "Enroll new students with facial biometric data capture",
            SUCCESS_COLOR,
            e -> parent.showPanel("registration")
        ));
        
        // Card 5: Advanced Reports (Enterprise)
        cardsPanel.add(createFeatureCard(
            "📄 Advanced Reports",
            "⭐ Enterprise - Generate professional reports in multiple formats",
            new Color(230, 126, 34),
            e -> parent.showPanel("reports")
        ));
        
        // Card 6: System Monitor (Enterprise)
        cardsPanel.add(createFeatureCard(
            "🔍 System Monitor",
            "⭐ Enterprise - Real-time health monitoring and performance metrics",
            new Color(26, 188, 156),
            e -> parent.showPanel("monitor")
        ));
        
        // Card 7: Admin Panel (Enterprise)
        cardsPanel.add(createFeatureCard(
            "⚙️ Admin Panel",
            "⭐ Enterprise - User management with role-based access control",
            new Color(192, 57, 43),
            e -> parent.showPanel("admin")
        ));
        
        // Card 8: Audit Logs (Enterprise)
        cardsPanel.add(createFeatureCard(
            "🔒 Audit Logs",
            "⭐ Enterprise - Security audit trail and activity logging",
            new Color(231, 76, 60),
            e -> parent.showPanel("audit")
        ));
        
        // Card 9: API Server (Enterprise)
        cardsPanel.add(createFeatureCard(
            "🔌 API Server",
            "⭐ Enterprise - REST API for third-party integrations",
            new Color(52, 73, 94),
            e -> parent.showPanel("api")
        ));
        
        centerPanel.add(welcomePanel, BorderLayout.NORTH);
        centerPanel.add(cardsPanel, BorderLayout.CENTER);
        
        add(headerPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        
        // Footer with system info
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_COLOR);
        panel.setPreferredSize(new Dimension(getWidth(), 140));
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(SECONDARY_COLOR, 3),
            BorderFactory.createEmptyBorder(30, 50, 30, 50)
        ));
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(PRIMARY_COLOR);
        
        JLabel titleLabel = new JLabel("🎓 Smart Attendance System");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(TITLE_FONT);
        
        JLabel subtitleLabel = new JLabel("Facial Recognition Based Automated Attendance");
        subtitleLabel.setForeground(new Color(236, 240, 241));
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(subtitleLabel);
        
        panel.add(textPanel, BorderLayout.WEST);
        
        // Status indicators
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        statusPanel.setBackground(PRIMARY_COLOR);
        
        JLabel systemStatus = new JLabel("● System Online");
        systemStatus.setFont(new Font("Segoe UI", Font.BOLD, 14));
        systemStatus.setForeground(SUCCESS_COLOR);
        
        JLabel dbStatus = new JLabel("● Database Connected");
        dbStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dbStatus.setForeground(Color.WHITE);
        
        JLabel aiStatus = new JLabel("● AI Model Ready");
        aiStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        aiStatus.setForeground(Color.WHITE);
        
        statusPanel.add(systemStatus);
        statusPanel.add(Box.createVerticalStrut(5));
        statusPanel.add(dbStatus);
        statusPanel.add(Box.createVerticalStrut(5));
        statusPanel.add(aiStatus);
        
        panel.add(statusPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createFeatureCard(String title, String description, 
                                     Color accentColor, ActionListener action) {
        JPanel card = new JPanel(new BorderLayout(15, 15));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(189, 195, 199), 1, true),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Color bar on left
        JPanel colorBar = new JPanel();
        colorBar.setBackground(accentColor);
        colorBar.setPreferredSize(new Dimension(6, 100));
        
        // Content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(CARD_BG);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(CARD_TITLE_FONT);
        titleLabel.setForeground(new Color(44, 62, 80));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel descLabel = new JLabel("<html><div style='width: 200px;'>" + 
                                      description + "</div></html>");
        descLabel.setFont(CARD_DESC_FONT);
        descLabel.setForeground(new Color(127, 140, 141));
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(descLabel);
        
        card.add(colorBar, BorderLayout.WEST);
        card.add(contentPanel, BorderLayout.CENTER);
        
        // Hover effects
        card.addMouseListener(new MouseAdapter() {
            Color originalBorder = new Color(189, 195, 199);
            
            @Override
            public void mouseClicked(MouseEvent e) {
                action.actionPerformed(null);
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(accentColor, 3, true),
                    BorderFactory.createEmptyBorder(23, 23, 23, 23)
                ));
                card.setBackground(new Color(248, 249, 250));
                contentPanel.setBackground(new Color(248, 249, 250));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(originalBorder, 1, true),
                    BorderFactory.createEmptyBorder(25, 25, 25, 25)
                ));
                card.setBackground(CARD_BG);
                contentPanel.setBackground(CARD_BG);
            }
        });
        
        return card;
    }
    
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(44, 62, 80));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 50, 15, 50));
        
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        leftPanel.setBackground(new Color(44, 62, 80));
        
        JLabel versionLabel = new JLabel("Version 3.0 Professional Edition");
        versionLabel.setForeground(new Color(189, 195, 199));
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        JLabel techLabel = new JLabel("Powered by OpenCV DNN & Deep Learning");
        techLabel.setForeground(new Color(149, 165, 166));
        techLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        leftPanel.add(versionLabel);
        leftPanel.add(new JLabel("|") {{ setForeground(new Color(127, 140, 141)); }});
        leftPanel.add(techLabel);
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(new Color(44, 62, 80));
        
        JButton exitButton = new JButton("Exit Application");
        exitButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        exitButton.setBackground(DANGER_COLOR);
        exitButton.setForeground(Color.WHITE);
        exitButton.setFocusPainted(false);
        exitButton.setBorderPainted(false);
        exitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        exitButton.addActionListener(e -> parent.exitApplication());
        
        rightPanel.add(exitButton);
        
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);
        
        return panel;
    }
}

package com.myapp.swing;

import com.myapp.dao.AttendanceDAO;
import com.myapp.model.Attendance;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Attendance Viewer Panel - Display and filter attendance records
 */
public class AttendanceViewerPanel extends JPanel {
    
    private MainDashboard parent;
    private AttendanceDAO attendanceDAO;
    
    private JTable attendanceTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> filterCombo;
    private JLabel recordCountLabel;
    
    private final String[] columnNames = {
        "ID", "Student Name", "Admission No", "Course", 
        "Date", "Time", "Status"
    };
    
    public AttendanceViewerPanel(MainDashboard parent) {
        this.parent = parent;
        this.attendanceDAO = new AttendanceDAO();
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        initializeUI();
    }
    
    private void initializeUI() {
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 73, 94));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 60));
        
        JLabel titleLabel = new JLabel("  Attendance Records");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Filter and search panel
        JPanel filterPanel = createFilterPanel();
        headerPanel.add(filterPanel, BorderLayout.EAST);
        
        // Table
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        attendanceTable = new JTable(tableModel);
        attendanceTable.setFont(new Font("Arial", Font.PLAIN, 12));
        attendanceTable.setRowHeight(25);
        attendanceTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        attendanceTable.getTableHeader().setBackground(new Color(52, 73, 94));
        attendanceTable.getTableHeader().setForeground(Color.WHITE);
        attendanceTable.setSelectionBackground(new Color(52, 152, 219));
        attendanceTable.setSelectionForeground(Color.WHITE);
        attendanceTable.setGridColor(new Color(189, 195, 199));
        
        // Enable sorting
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        attendanceTable.setRowSorter(sorter);
        
        JScrollPane tableScroll = new JScrollPane(attendanceTable);
        tableScroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Footer with record count and actions
        JPanel footerPanel = createFooterPanel();
        
        add(headerPanel, BorderLayout.NORTH);
        add(tableScroll, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panel.setBackground(new Color(52, 73, 94));
        
        JLabel filterLabel = new JLabel("Filter:");
        filterLabel.setForeground(Color.WHITE);
        filterLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        filterCombo = new JComboBox<>(new String[]{
            "All Records", "Today", "This Week", "This Month"
        });
        filterCombo.setFont(new Font("Arial", Font.PLAIN, 12));
        filterCombo.addActionListener(e -> applyFilter());
        
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setForeground(Color.WHITE);
        searchLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        searchField = new JTextField(15);
        searchField.setFont(new Font("Arial", Font.PLAIN, 12));
        
        JButton searchButton = new JButton("🔍");
        searchButton.setFont(new Font("Arial", Font.PLAIN, 12));
        searchButton.setFocusPainted(false);
        searchButton.addActionListener(e -> performSearch());
        
        panel.add(filterLabel);
        panel.add(filterCombo);
        panel.add(searchLabel);
        panel.add(searchField);
        panel.add(searchButton);
        
        return panel;
    }
    
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setPreferredSize(new Dimension(getWidth(), 60));
        
        // Record count on left
        recordCountLabel = new JLabel("Total Records: 0");
        recordCountLabel.setFont(new Font("Arial", Font.BOLD, 13));
        
        // Action buttons on right
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(new Color(236, 240, 241));
        
        JButton refreshButton = new JButton("🔄 Refresh");
        refreshButton.setFont(new Font("Arial", Font.PLAIN, 13));
        refreshButton.setBackground(new Color(52, 152, 219));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(e -> refreshData());
        
        JButton exportButton = new JButton("📊 Export");
        exportButton.setFont(new Font("Arial", Font.PLAIN, 13));
        exportButton.setBackground(new Color(46, 204, 113));
        exportButton.setForeground(Color.WHITE);
        exportButton.setFocusPainted(false);
        exportButton.addActionListener(e -> exportData());
        
        JButton deleteButton = new JButton("🗑 Delete");
        deleteButton.setFont(new Font("Arial", Font.PLAIN, 13));
        deleteButton.setBackground(new Color(231, 76, 60));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);
        deleteButton.addActionListener(e -> deleteSelected());
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(exportButton);
        buttonPanel.add(deleteButton);
        
        panel.add(recordCountLabel, BorderLayout.WEST);
        panel.add(buttonPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    public void refreshData() {
        try {
            // Clear existing data
            tableModel.setRowCount(0);
            
            // Fetch attendance records
            List<Attendance> attendanceList = attendanceDAO.getAllAttendance();
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            
            // Populate table
            for (Attendance attendance : attendanceList) {
                Object[] row = {
                    attendance.getAttendanceId(),
                    attendance.getStudentName() != null ? attendance.getStudentName() : "Unknown",
                    "N/A", // Admission number - would need to join with students
                    attendance.getCourseName() != null ? attendance.getCourseName() : "N/A",
                    dateFormat.format(attendance.getAttendanceDate()),
                    attendance.getAttendanceTime() != null ? 
                        timeFormat.format(attendance.getAttendanceTime()) : "N/A",
                    attendance.getStatus() != null ? attendance.getStatus().toString() : "Present"
                };
                tableModel.addRow(row);
            }
            
            // Update record count
            recordCountLabel.setText("Total Records: " + attendanceList.size());
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error loading attendance records:\n" + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void applyFilter() {
        String filter = (String) filterCombo.getSelectedItem();
        // TODO: Implement date-based filtering
        refreshData();
    }
    
    private void performSearch() {
        String searchText = searchField.getText().trim().toLowerCase();
        
        if (searchText.isEmpty()) {
            refreshData();
            return;
        }
        
        // Simple row filtering
        TableRowSorter<DefaultTableModel> sorter = 
            (TableRowSorter<DefaultTableModel>) attendanceTable.getRowSorter();
        
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
        recordCountLabel.setText("Filtered Records: " + attendanceTable.getRowCount());
    }
    
    private void exportData() {
        JOptionPane.showMessageDialog(this,
            "Export feature will allow exporting data to:\n" +
            "• Excel (XLSX)\n" +
            "• PDF Report\n" +
            "• CSV File\n\n" +
            "Feature pending implementation.",
            "Export Data",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void deleteSelected() {
        int selectedRow = attendanceTable.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a record to delete.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this attendance record?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int attendanceId = (Integer) tableModel.getValueAt(selectedRow, 0);
                boolean deleted = attendanceDAO.deleteAttendance(attendanceId);
                
                if (deleted) {
                    tableModel.removeRow(selectedRow);
                    JOptionPane.showMessageDialog(this,
                        "Record deleted successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Failed to delete record.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Error deleting record:\n" + ex.getMessage(),
                    "Delete Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

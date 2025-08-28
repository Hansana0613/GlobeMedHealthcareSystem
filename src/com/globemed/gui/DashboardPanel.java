/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.gui;

import com.globemed.database.*;
import com.globemed.models.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Dashboard panel showing system overview
 *
 * @author Hansana
 */
public class DashboardPanel extends JPanel {

    private MainFrame parentFrame;
    private PatientDAO patientDAO;
    private AppointmentDAO appointmentDAO;
    private BillDAO billDAO;
    private StaffDAO staffDAO;

    // Dashboard components
    private JLabel totalPatientsLabel;
    private JLabel todayAppointmentsLabel;
    private JLabel pendingBillsLabel;
    private JLabel totalStaffLabel;
    private JTextArea recentActivityArea;
    private JPanel statsPanel;
    private JPanel chartsPanel;
    private JPanel activityPanel;

    public DashboardPanel(MainFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.patientDAO = new PatientDAO();
        this.appointmentDAO = new AppointmentDAO();
        this.billDAO = new BillDAO();
        this.staffDAO = new StaffDAO();

        initializeComponents();
        layoutComponents();
        setupEventHandlers();
        refreshData();
    }

    private void initializeComponents() {
        // Statistics labels
        totalPatientsLabel = new JLabel("0", SwingConstants.CENTER);
        todayAppointmentsLabel = new JLabel("0", SwingConstants.CENTER);
        pendingBillsLabel = new JLabel("0", SwingConstants.CENTER);
        totalStaffLabel = new JLabel("0", SwingConstants.CENTER);

        // Recent activity area
        recentActivityArea = new JTextArea(10, 50);
        recentActivityArea.setEditable(false);
        recentActivityArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        recentActivityArea.setBackground(new Color(248, 248, 248));

        // Set label fonts
        Font statsFont = new Font("Arial", Font.BOLD, 24);
        totalPatientsLabel.setFont(statsFont);
        todayAppointmentsLabel.setFont(statsFont);
        pendingBillsLabel.setFont(statsFont);
        totalStaffLabel.setFont(statsFont);
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Main content
        JPanel contentPanel = new JPanel(new BorderLayout());

        // Statistics panel
        statsPanel = createStatsPanel();
        contentPanel.add(statsPanel, BorderLayout.NORTH);

        // Center panel with charts and activity
        JPanel centerPanel = new JPanel(new BorderLayout());

        // Charts panel (placeholder for now)
        chartsPanel = createChartsPanel();
        centerPanel.add(chartsPanel, BorderLayout.CENTER);

        // Activity panel
        activityPanel = createActivityPanel();
        centerPanel.add(activityPanel, BorderLayout.EAST);

        contentPanel.add(centerPanel, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(0, 100, 150));

        JLabel titleLabel = new JLabel("System Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);

        JLabel dateLabel = new JLabel(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")));
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        dateLabel.setForeground(Color.WHITE);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(0, 100, 150));
        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(dateLabel, BorderLayout.EAST);

        panel.add(titlePanel, BorderLayout.CENTER);

        // Refresh button
        JButton refreshButton = new JButton("Refresh Data");
        refreshButton.addActionListener(e -> refreshData());
        panel.add(refreshButton, BorderLayout.EAST);

        return panel;
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // Total Patients card
        JPanel patientsCard = createStatCard("Total Patients", totalPatientsLabel,
                new Color(52, 152, 219), "👥");

        // Today's Appointments card
        JPanel appointmentsCard = createStatCard("Today's Appointments", todayAppointmentsLabel,
                new Color(46, 204, 113), "📅");

        // Pending Bills card
        JPanel billsCard = createStatCard("Pending Bills", pendingBillsLabel,
                new Color(230, 126, 34), "💰");

        // Total Staff card
        JPanel staffCard = createStatCard("Total Staff", totalStaffLabel,
                new Color(155, 89, 182), "👨‍⚕️");

        panel.add(patientsCard);
        panel.add(appointmentsCard);
        panel.add(billsCard);
        panel.add(staffCard);

        return panel;
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color color, String icon) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Header with icon and title
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(Color.GRAY);

        headerPanel.add(iconLabel, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // Value
        valueLabel.setForeground(color);

        card.add(headerPanel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createChartsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("System Overview"));
        panel.setBackground(Color.WHITE);

        // Placeholder for charts - in a real application, you'd use a charting library
        JPanel chartArea = new JPanel();
        chartArea.setBackground(new Color(245, 245, 245));
        chartArea.setBorder(BorderFactory.createLoweredBevelBorder());

        JLabel chartLabel = new JLabel("Charts and Analytics Area", SwingConstants.CENTER);
        chartLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        chartLabel.setForeground(Color.GRAY);

        chartArea.add(chartLabel);

        // Add some mock chart buttons
        JPanel chartButtons = new JPanel(new FlowLayout());
        JButton patientTrendsBtn = new JButton("Patient Trends");
        JButton appointmentStatsBtn = new JButton("Appointment Statistics");
        JButton revenueBtn = new JButton("Revenue Analysis");

        patientTrendsBtn.addActionListener(e -> showMockChart("Patient Registration Trends"));
        appointmentStatsBtn.addActionListener(e -> showMockChart("Appointment Statistics"));
        revenueBtn.addActionListener(e -> showMockChart("Revenue Analysis"));

        chartButtons.add(patientTrendsBtn);
        chartButtons.add(appointmentStatsBtn);
        chartButtons.add(revenueBtn);

        panel.add(chartArea, BorderLayout.CENTER);
        panel.add(chartButtons, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createActivityPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Recent Activity"));
        panel.setPreferredSize(new Dimension(350, 400));
        panel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(recentActivityArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        panel.add(scrollPane, BorderLayout.CENTER);

        // Activity controls
        JPanel controlPanel = new JPanel(new FlowLayout());
        JButton clearActivityBtn = new JButton("Clear");
        JButton exportActivityBtn = new JButton("Export");

        clearActivityBtn.addActionListener(e -> recentActivityArea.setText(""));
        exportActivityBtn.addActionListener(e -> exportActivity());

        controlPanel.add(clearActivityBtn);
        controlPanel.add(exportActivityBtn);

        panel.add(controlPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void setupEventHandlers() {
        // Timer for auto-refresh every 5 minutes
        Timer refreshTimer = new Timer(300000, e -> refreshData());
        refreshTimer.start();
    }

    public void refreshData() {
        SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                publish("Refreshing dashboard data...");

                // Load statistics
                try {
                    List<Patient> patients = patientDAO.getAllPatients();
                    SwingUtilities.invokeLater(() -> {
                        totalPatientsLabel.setText(String.valueOf(patients.size()));
                    });

                    List<Appointment> allAppointments = appointmentDAO.getAllAppointments();
                    long todayAppointments = allAppointments.stream()
                            .filter(apt -> apt.getAppointmentTime().toLocalDate().equals(LocalDate.now()))
                            .count();
                    SwingUtilities.invokeLater(() -> {
                        todayAppointmentsLabel.setText(String.valueOf(todayAppointments));
                    });

                    List<Bill> bills = billDAO.getAllBills();
                    long pendingBills = bills.stream()
                            .filter(bill -> "PENDING".equals(bill.getClaimStatus()))
                            .count();
                    SwingUtilities.invokeLater(() -> {
                        pendingBillsLabel.setText(String.valueOf(pendingBills));
                    });

                    List<Staff> staff = staffDAO.getAllStaff();
                    SwingUtilities.invokeLater(() -> {
                        totalStaffLabel.setText(String.valueOf(staff.size()));
                    });

                    publish("Data refreshed successfully");

                    // Update recent activity
                    updateRecentActivity(patients, allAppointments, bills, staff);

                } catch (SQLException e) {
                    publish("Error refreshing data: " + e.getMessage());
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void process(List<String> chunks) {
                for (String message : chunks) {
                    parentFrame.setStatus(message);
                }
            }

            @Override
            protected void done() {
                parentFrame.setStatus("Dashboard ready");
            }
        };

        worker.execute();
    }

    private void updateRecentActivity(List<Patient> patients, List<Appointment> appointments,
            List<Bill> bills, List<Staff> staff) {
        StringBuilder activity = new StringBuilder();
        activity.append("=== RECENT SYSTEM ACTIVITY ===\n\n");

        // Recent patients (last 5)
        activity.append("📋 Recent Patients:\n");
        patients.stream()
                .sorted((p1, p2) -> Long.compare(p2.getId(), p1.getId()))
                .limit(5)
                .forEach(p -> activity.append("  • ").append(p.getName())
                .append(" (ID: ").append(p.getId()).append(")\n"));

        activity.append("\n📅 Today's Appointments:\n");
        appointments.stream()
                .filter(apt -> apt.getAppointmentTime().toLocalDate().equals(LocalDate.now()))
                .sorted((a1, a2) -> a1.getAppointmentTime().compareTo(a2.getAppointmentTime()))
                .forEach(apt -> activity.append("  • Patient ID: ").append(apt.getPatientId())
                .append(" at ").append(apt.getAppointmentTime().toLocalTime())
                .append(" (").append(apt.getStatus()).append(")\n"));

        activity.append("\n💰 Recent Bills:\n");
        bills.stream()
                .sorted((b1, b2) -> Long.compare(b2.getId(), b1.getId()))
                .limit(5)
                .forEach(bill -> activity.append("  • Bill ID: ").append(bill.getId())
                .append(" - $").append(bill.getTotalAmount())
                .append(" (").append(bill.getClaimStatus()).append(")\n"));

        activity.append("\n👥 System Statistics:\n");
        activity.append("  • Total Patients: ").append(patients.size()).append("\n");
        activity.append("  • Total Appointments: ").append(appointments.size()).append("\n");
        activity.append("  • Total Bills: ").append(bills.size()).append("\n");
        activity.append("  • Active Staff: ").append(staff.size()).append("\n");

        activity.append("\n⚡ Quick Actions:\n");
        activity.append("  • Click 'Patients' to manage patient records\n");
        activity.append("  • Click 'Appointments' to schedule appointments\n");
        activity.append("  • Click 'Billing' to process payments\n");
        activity.append("  • Click 'Reports' to generate reports\n");

        SwingUtilities.invokeLater(() -> {
            recentActivityArea.setText(activity.toString());
            recentActivityArea.setCaretPosition(0);
        });
    }

    private void showMockChart(String chartType) {
        JDialog chartDialog = new JDialog(parentFrame, chartType, true);
        chartDialog.setSize(600, 400);
        chartDialog.setLocationRelativeTo(parentFrame);

        JPanel content = new JPanel(new BorderLayout());
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel mockChart = new JLabel("<html><div style='text-align: center;'>"
                + "<h2>" + chartType + "</h2><br>"
                + "<p>📊 Chart visualization would appear here</p><br>"
                + "<p>In a production system, this would show:</p><br>"
                + "<ul>"
                + "<li>Interactive charts and graphs</li>"
                + "<li>Data visualization components</li>"
                + "<li>Trend analysis</li>"
                + "<li>Statistical summaries</li>"
                + "</ul>"
                + "</div></html>");
        mockChart.setHorizontalAlignment(SwingConstants.CENTER);

        content.add(mockChart, BorderLayout.CENTER);

        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> chartDialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeBtn);
        content.add(buttonPanel, BorderLayout.SOUTH);

        chartDialog.add(content);
        chartDialog.setVisible(true);
    }

    private void exportActivity() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new java.io.File("dashboard_activity.txt"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.FileWriter writer = new java.io.FileWriter(fileChooser.getSelectedFile());
                writer.write(recentActivityArea.getText());
                writer.close();

                JOptionPane.showMessageDialog(this,
                        "Activity log exported successfully!",
                        "Export Complete",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error exporting activity log: " + e.getMessage(),
                        "Export Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

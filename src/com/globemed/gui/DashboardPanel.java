/*
 * Enhanced Dashboard Panel with Modern UI/UX Design
 * Following medical color scheme and improved user experience
 */
package com.globemed.gui;

import com.globemed.database.*;
import com.globemed.models.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Enhanced Dashboard panel with modern medical UI design
 *
 * @author Hansana
 */
public class DashboardPanel extends JPanel {

    // Color Constants - Medical Theme
    private static final Color PRIMARY_BLUE = new Color(46, 134, 171); // #2E86AB
    private static final Color HEALTHCARE_GREEN = new Color(76, 175, 80); // #4CAF50
    private static final Color WARNING_AMBER = new Color(255, 152, 0); // #FF9800
    private static final Color ERROR_RED = new Color(244, 67, 54); // #F44336
    private static final Color BACKGROUND_LIGHT = new Color(245, 245, 245); // #F5F5F5
    private static final Color CARD_WHITE = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(33, 33, 33);
    private static final Color TEXT_SECONDARY = new Color(117, 117, 117);
    private static final Color HOVER_COLOR = new Color(240, 248, 255);

    private MainFrame parentFrame;
    private PatientDAO patientDAO;
    private AppointmentDAO appointmentDAO;
    private BillDAO billDAO;
    private StaffDAO staffDAO;

    // Enhanced Dashboard components
    private JLabel totalPatientsLabel;
    private JLabel todayAppointmentsLabel;
    private JLabel pendingBillsLabel;
    private JLabel totalStaffLabel;
    private JTextArea recentActivityArea;
    private JPanel statsPanel;
    private JPanel quickActionsPanel;
    private JPanel activityPanel;
    private JProgressBar dataLoadingBar;

    public DashboardPanel(MainFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.patientDAO = new PatientDAO();
        this.appointmentDAO = new AppointmentDAO();
        this.billDAO = new BillDAO();
        this.staffDAO = new StaffDAO();

        initializeComponents();
        layoutComponents();
        setupEventHandlers();
        SwingUtilities.invokeLater(() -> refreshData());
    }

    private void initializeComponents() {
        setBackground(BACKGROUND_LIGHT);

        // Enhanced Statistics labels with modern typography
        Font statsFont = new Font("Sansation", Font.BOLD, 32);
        Font statsLabelFont = new Font("Sansation", Font.PLAIN, 14);

        totalPatientsLabel = createStatsLabel("0", statsFont);
        todayAppointmentsLabel = createStatsLabel("0", statsFont);
        pendingBillsLabel = createStatsLabel("0", statsFont);
        totalStaffLabel = createStatsLabel("0", statsFont);

        // Enhanced Recent activity area
        recentActivityArea = new JTextArea(15, 40);
        recentActivityArea.setEditable(false);
        recentActivityArea.setFont(new Font("Sansation", Font.PLAIN, 12));
        recentActivityArea.setBackground(CARD_WHITE);
        recentActivityArea.setBorder(new EmptyBorder(16, 16, 16, 16));
        recentActivityArea.setLineWrap(true);
        recentActivityArea.setWrapStyleWord(true);

        // Loading progress bar
        dataLoadingBar = new JProgressBar();
        dataLoadingBar.setIndeterminate(true);
        dataLoadingBar.setStringPainted(true);
        dataLoadingBar.setString("Loading dashboard data...");
        dataLoadingBar.setVisible(false);
        dataLoadingBar.setBackground(CARD_WHITE);
        dataLoadingBar.setForeground(PRIMARY_BLUE);
    }

    private JLabel createStatsLabel(String text, Font font) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(font);
        label.setForeground(TEXT_PRIMARY);
        return label;
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(0, 16));
        setBorder(new EmptyBorder(24, 24, 24, 24));

        // Enhanced Header
        JPanel headerPanel = createModernHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Main content with improved spacing
        JPanel contentPanel = new JPanel(new BorderLayout(0, 24));
        contentPanel.setBackground(BACKGROUND_LIGHT);

        // Statistics panel with cards
        statsPanel = createModernStatsPanel();
        contentPanel.add(statsPanel, BorderLayout.NORTH);

        // Center area with quick actions and activity
        JPanel centerPanel = new JPanel(new BorderLayout(24, 0));
        centerPanel.setBackground(BACKGROUND_LIGHT);

        // Quick actions panel
        quickActionsPanel = createQuickActionsPanel();
        centerPanel.add(quickActionsPanel, BorderLayout.CENTER);

        // Activity panel
        activityPanel = createModernActivityPanel();
        centerPanel.add(activityPanel, BorderLayout.EAST);

        contentPanel.add(centerPanel, BorderLayout.CENTER);

        // Loading bar
        contentPanel.add(dataLoadingBar, BorderLayout.SOUTH);

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createModernHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_BLUE);
        panel.setBorder(new EmptyBorder(24, 32, 24, 32));

        // Title section
        JPanel titleSection = new JPanel(new BorderLayout());
        titleSection.setBackground(PRIMARY_BLUE);

        JLabel titleLabel = new JLabel("Healthcare Dashboard");
        titleLabel.setFont(new Font("Sansation", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("System Overview & Quick Actions");
        subtitleLabel.setFont(new Font("Sansation", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(220, 240, 255));

        JPanel titleContainer = new JPanel(new BorderLayout());
        titleContainer.setBackground(PRIMARY_BLUE);
        titleContainer.add(titleLabel, BorderLayout.NORTH);
        titleContainer.add(subtitleLabel, BorderLayout.SOUTH);

        titleSection.add(titleContainer, BorderLayout.WEST);

        // Date and refresh section
        JPanel actionSection = new JPanel(new BorderLayout());
        actionSection.setBackground(PRIMARY_BLUE);

        JLabel dateLabel = new JLabel(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")));
        dateLabel.setFont(new Font("Sansation", Font.PLAIN, 14));
        dateLabel.setForeground(Color.WHITE);

        JButton refreshButton = createModernButton("Refresh Data", HEALTHCARE_GREEN);
        refreshButton.addActionListener(e -> refreshData());

        JPanel refreshPanel = new JPanel(new BorderLayout(16, 0));
        refreshPanel.setBackground(PRIMARY_BLUE);
        refreshPanel.add(dateLabel, BorderLayout.WEST);
        refreshPanel.add(refreshButton, BorderLayout.EAST);

        actionSection.add(refreshPanel, BorderLayout.EAST);

        panel.add(titleSection, BorderLayout.WEST);
        panel.add(actionSection, BorderLayout.EAST);

        return panel;
    }

    private JPanel createModernStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 24, 0));
        panel.setBackground(BACKGROUND_LIGHT);
        panel.setBorder(new EmptyBorder(0, 0, 24, 0));

        // Enhanced stat cards with modern design
        JPanel patientsCard = createModernStatCard("Total Patients", totalPatientsLabel,
                PRIMARY_BLUE, "👥", "Active patient records");

        JPanel appointmentsCard = createModernStatCard("Today's Appointments", todayAppointmentsLabel,
                HEALTHCARE_GREEN, "📅", "Scheduled for today");

        JPanel billsCard = createModernStatCard("Pending Bills", pendingBillsLabel,
                WARNING_AMBER, "💰", "Awaiting processing");

        JPanel staffCard = createModernStatCard("Active Staff", totalStaffLabel,
                new Color(156, 39, 176), "👨‍⚕️", "Healthcare professionals");

        panel.add(patientsCard);
        panel.add(appointmentsCard);
        panel.add(billsCard);
        panel.add(staffCard);

        return panel;
    }

    private JPanel createModernStatCard(String title, JLabel valueLabel, Color accentColor, String icon, String subtitle) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(230, 230, 230), 1, true),
                new EmptyBorder(24, 20, 24, 20)
        ));

        // Add subtle shadow effect
        card.setPreferredSize(new Dimension(250, 140));

        // Header section
        JPanel headerSection = new JPanel(new BorderLayout());
        headerSection.setBackground(CARD_WHITE);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(CARD_WHITE);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Sansation", Font.BOLD, 14));
        titleLabel.setForeground(TEXT_PRIMARY);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Sansation", Font.PLAIN, 11));
        subtitleLabel.setForeground(TEXT_SECONDARY);

        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);

        headerSection.add(iconLabel, BorderLayout.WEST);
        headerSection.add(Box.createHorizontalStrut(12), BorderLayout.CENTER);
        headerSection.add(titlePanel, BorderLayout.EAST);

        // Value section
        JPanel valueSection = new JPanel(new BorderLayout());
        valueSection.setBackground(CARD_WHITE);
        valueSection.setBorder(new EmptyBorder(16, 0, 0, 0));

        valueLabel.setForeground(accentColor);
        valueLabel.setHorizontalAlignment(SwingConstants.LEFT);

        valueSection.add(valueLabel, BorderLayout.WEST);

        card.add(headerSection, BorderLayout.NORTH);
        card.add(valueSection, BorderLayout.CENTER);

        // Add hover effect
        addHoverEffect(card);

        return card;
    }

    private JPanel createQuickActionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(230, 230, 230), 1, true),
                new EmptyBorder(24, 24, 24, 24)
        ));

        // Header
        JLabel headerLabel = new JLabel("Quick Actions");
        headerLabel.setFont(new Font("Sansation", Font.BOLD, 18));
        headerLabel.setForeground(TEXT_PRIMARY);
        headerLabel.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Action buttons grid
        JPanel actionsGrid = new JPanel(new GridLayout(2, 3, 16, 16));
        actionsGrid.setBackground(CARD_WHITE);

        // Quick action buttons
        JButton newPatientBtn = createQuickActionButton("New Patient", "👤", PRIMARY_BLUE);
        JButton scheduleApptBtn = createQuickActionButton("Schedule Appointment", "📅", HEALTHCARE_GREEN);
        JButton processBillBtn = createQuickActionButton("Process Bill", "💳", WARNING_AMBER);
        JButton viewReportsBtn = createQuickActionButton("View Reports", "📊", new Color(156, 39, 176));
        JButton manageStaffBtn = createQuickActionButton("Manage Staff", "👥", new Color(96, 125, 139));
        JButton systemSettingsBtn = createQuickActionButton("Settings", "⚙️", TEXT_SECONDARY);

        // Add action listeners
        newPatientBtn.addActionListener(e -> navigateToModule("PATIENTS"));
        scheduleApptBtn.addActionListener(e -> navigateToModule("APPOINTMENTS"));
        processBillBtn.addActionListener(e -> navigateToModule("BILLING"));
        viewReportsBtn.addActionListener(e -> navigateToModule("REPORTS"));
        manageStaffBtn.addActionListener(e -> navigateToModule("STAFF"));
        systemSettingsBtn.addActionListener(e -> showSettingsDialog());

        actionsGrid.add(newPatientBtn);
        actionsGrid.add(scheduleApptBtn);
        actionsGrid.add(processBillBtn);
        actionsGrid.add(viewReportsBtn);
        actionsGrid.add(manageStaffBtn);
        actionsGrid.add(systemSettingsBtn);

        panel.add(headerLabel, BorderLayout.NORTH);
        panel.add(actionsGrid, BorderLayout.CENTER);

        return panel;
    }

    private JButton createQuickActionButton(String text, String icon, Color color) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout());
        button.setBackground(CARD_WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(16, 12, 16, 12)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Icon
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Text
        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Sansation", Font.BOLD, 12));
        textLabel.setForeground(color);
        textLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel content = new JPanel(new BorderLayout(0, 8));
        content.setBackground(CARD_WHITE);
        content.add(iconLabel, BorderLayout.NORTH);
        content.add(textLabel, BorderLayout.SOUTH);

        button.add(content, BorderLayout.CENTER);

        // Enhanced hover effects
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(HOVER_COLOR);
                button.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(color, 2, true),
                        new EmptyBorder(15, 11, 15, 11)
                ));
                content.setBackground(HOVER_COLOR);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(CARD_WHITE);
                button.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(new Color(220, 220, 220), 1, true),
                        new EmptyBorder(16, 12, 16, 12)
                ));
                content.setBackground(CARD_WHITE);
            }
        });

        return button;
    }

    private JPanel createModernActivityPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(230, 230, 230), 1, true),
                new EmptyBorder(24, 24, 24, 24)
        ));
        panel.setPreferredSize(new Dimension(400, 600));

        // Header with title and controls
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CARD_WHITE);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Recent Activity");
        titleLabel.setFont(new Font("Sansation", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_PRIMARY);

        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        controlsPanel.setBackground(CARD_WHITE);

        JButton clearBtn = createIconButton("Clear", "🗑️", TEXT_SECONDARY);
        JButton exportBtn = createIconButton("Export", "📤", PRIMARY_BLUE);

        clearBtn.addActionListener(e -> {
            recentActivityArea.setText("");
            parentFrame.setStatus("Activity log cleared");
        });
        exportBtn.addActionListener(e -> exportActivity());

        controlsPanel.add(clearBtn);
        controlsPanel.add(Box.createHorizontalStrut(8));
        controlsPanel.add(exportBtn);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(controlsPanel, BorderLayout.EAST);

        // Activity content with custom scroll
        JScrollPane scrollPane = new JScrollPane(recentActivityArea);
        scrollPane.setBorder(null);
        scrollPane.setBackground(CARD_WHITE);
        scrollPane.getViewport().setBackground(CARD_WHITE);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Custom scrollbar styling
        customizeScrollBar(scrollPane);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JButton createIconButton(String text, String icon, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Sansation", Font.PLAIN, 11));
        button.setForeground(color);
        button.setBackground(CARD_WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(color, 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color);
                button.setForeground(CARD_WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(CARD_WHITE);
                button.setForeground(color);
            }
        });

        return button;
    }

    private JButton createModernButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Sansation", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setBorder(new EmptyBorder(12, 24, 12, 24));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Modern hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });

        return button;
    }

    private void customizeScrollBar(JScrollPane scrollPane) {
        JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
        verticalBar.setPreferredSize(new Dimension(8, 0));
        verticalBar.setBackground(BACKGROUND_LIGHT);
        verticalBar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(180, 180, 180);
                this.trackColor = BACKGROUND_LIGHT;
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                return button;
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                return button;
            }
        });
    }

    private void addHoverEffect(JPanel card) {
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(PRIMARY_BLUE, 1, true),
                        new EmptyBorder(23, 19, 23, 19)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(new Color(230, 230, 230), 1, true),
                        new EmptyBorder(24, 20, 24, 20)
                ));
            }
        });
    }

    private void setupEventHandlers() {
        // Auto-refresh timer with longer interval
        Timer refreshTimer = new Timer(300000, e -> refreshData()); // 5 minutes
        refreshTimer.start();
    }

    public void refreshData() {
        // Show loading state
        dataLoadingBar.setVisible(true);
        revalidate();

        SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                publish("Loading dashboard data...");
                Thread.sleep(500); // Brief delay for visual feedback

                try {
                    // Load all data
                    List<Patient> patients = patientDAO.getAllPatients();
                    List<Appointment> allAppointments = appointmentDAO.getAllAppointments();
                    List<Bill> bills = billDAO.getAllBills();
                    List<Staff> staff = staffDAO.getAllStaff();

                    // Update UI on EDT
                    SwingUtilities.invokeLater(() -> {
                        totalPatientsLabel.setText(String.valueOf(patients.size()));

                        long todayAppointments = allAppointments.stream()
                                .filter(apt -> apt.getAppointmentTime().toLocalDate().equals(LocalDate.now()))
                                .count();
                        todayAppointmentsLabel.setText(String.valueOf(todayAppointments));

                        long pendingBills = bills.stream()
                                .filter(bill -> "PENDING".equals(bill.getClaimStatus()))
                                .count();
                        pendingBillsLabel.setText(String.valueOf(pendingBills));

                        totalStaffLabel.setText(String.valueOf(staff.size()));

                        // Update activity with enhanced formatting
                        updateEnhancedActivity(patients, allAppointments, bills, staff);
                    });

                    publish("Dashboard updated successfully");

                } catch (SQLException e) {
                    publish("Error loading data: " + e.getMessage());
                    SwingUtilities.invokeLater(() -> {
                        showErrorState(e.getMessage());
                    });
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
                dataLoadingBar.setVisible(false);
                parentFrame.setStatus("Dashboard ready");
                revalidate();
            }
        };

        worker.execute();
    }

    private void updateEnhancedActivity(List<Patient> patients, List<Appointment> appointments,
            List<Bill> bills, List<Staff> staff) {
        StringBuilder activity = new StringBuilder();

        // Modern header
        activity.append("📊 SYSTEM ACTIVITY OVERVIEW\n");
        activity.append("═══════════════════════════════════════\n\n");

        // Today's summary
        activity.append("📅 TODAY'S SUMMARY\n");
        activity.append("─────────────────────────────────────\n");

        long todayAppointments = appointments.stream()
                .filter(apt -> apt.getAppointmentTime().toLocalDate().equals(LocalDate.now()))
                .count();

        activity.append(String.format("• Appointments scheduled: %d\n", todayAppointments));
        activity.append(String.format("• Total active patients: %d\n", patients.size()));
        activity.append(String.format("• Bills pending review: %d\n",
                bills.stream().filter(b -> "PENDING".equals(b.getClaimStatus())).count()));

        activity.append("\n📋 RECENT PATIENTS\n");
        activity.append("─────────────────────────────────────\n");
        patients.stream()
                .sorted((p1, p2) -> Long.compare(p2.getId(), p1.getId()))
                .limit(5)
                .forEach(p -> activity.append(String.format("• %s (ID: #%d)\n",
                p.getName(), p.getId())));

        if (todayAppointments > 0) {
            activity.append("\n⏰ TODAY'S SCHEDULE\n");
            activity.append("─────────────────────────────────────\n");
            appointments.stream()
                    .filter(apt -> apt.getAppointmentTime().toLocalDate().equals(LocalDate.now()))
                    .sorted((a1, a2) -> a1.getAppointmentTime().compareTo(a2.getAppointmentTime()))
                    .limit(10)
                    .forEach(apt -> activity.append(String.format("• %s - Patient #%d (%s)\n",
                    apt.getAppointmentTime().toLocalTime().toString(),
                    apt.getPatientId(),
                    apt.getStatus())));
        }

        activity.append("\n💰 BILLING STATUS\n");
        activity.append("─────────────────────────────────────\n");
        bills.stream()
                .sorted((b1, b2) -> Long.compare(b2.getId(), b1.getId()))
                .limit(5)
                .forEach(bill -> activity.append(String.format("• Bill #%d: $%.2f (%s)\n",
                bill.getId(), bill.getTotalAmount(), bill.getClaimStatus())));

        activity.append("\n🏥 SYSTEM HEALTH\n");
        activity.append("─────────────────────────────────────\n");
        activity.append(String.format("• Database connections: Active\n"));
        activity.append(String.format("• Staff online: %d\n", staff.size()));
        activity.append(String.format("• Last refresh: %s\n",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))));

        SwingUtilities.invokeLater(() -> {
            recentActivityArea.setText(activity.toString());
            recentActivityArea.setCaretPosition(0);
        });
    }

    private void showErrorState(String errorMessage) {
        totalPatientsLabel.setText("—");
        todayAppointmentsLabel.setText("—");
        pendingBillsLabel.setText("—");
        totalStaffLabel.setText("—");

        recentActivityArea.setText("❌ ERROR LOADING DATA\n\n"
                + "Unable to connect to database.\n"
                + "Error: " + errorMessage + "\n\n"
                + "Please check your database connection and try refreshing.");
    }

    private void navigateToModule(String module) {
        parentFrame.setStatus("Opening " + module.toLowerCase() + " module...");
        // Implementation would depend on MainFrame's navigation method
        // This is a placeholder for the navigation logic
    }

    private void showSettingsDialog() {
        JDialog settingsDialog = new JDialog(parentFrame, "System Settings", true);
        settingsDialog.setSize(500, 400);
        settingsDialog.setLocationRelativeTo(parentFrame);

        JPanel content = new JPanel(new BorderLayout());
        content.setBorder(new EmptyBorder(24, 24, 24, 24));
        content.setBackground(CARD_WHITE);

        JLabel titleLabel = new JLabel("System Configuration");
        titleLabel.setFont(new Font("Sansation", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_PRIMARY);

        JTextArea settingsText = new JTextArea(
                "⚙️ System Settings\n\n"
                + "Database Configuration:\n"
                + "• Connection pool size: 10\n"
                + "• Timeout: 30 seconds\n"
                + "• Auto-backup: Enabled\n\n"
                + "Security Settings:\n"
                + "• Session timeout: 30 minutes\n"
                + "• Password policy: Strong\n"
                + "• Audit logging: Enabled\n\n"
                + "Interface Settings:\n"
                + "• Theme: Medical Professional\n"
                + "• Auto-refresh: 5 minutes\n"
                + "• Notifications: Enabled"
        );
        settingsText.setFont(new Font("Sansation", Font.PLAIN, 12));
        settingsText.setEditable(false);
        settingsText.setBackground(BACKGROUND_LIGHT);
        settingsText.setBorder(new EmptyBorder(16, 16, 16, 16));

        JScrollPane scrollPane = new JScrollPane(settingsText);
        scrollPane.setBorder(new LineBorder(new Color(230, 230, 230), 1));

        JButton closeBtn = createModernButton("Close", PRIMARY_BLUE);
        closeBtn.addActionListener(e -> settingsDialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(CARD_WHITE);
        buttonPanel.add(closeBtn);

        content.add(titleLabel, BorderLayout.NORTH);
        content.add(scrollPane, BorderLayout.CENTER);
        content.add(buttonPanel, BorderLayout.SOUTH);

        settingsDialog.add(content);
        settingsDialog.setVisible(true);
    }

    private void exportActivity() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new java.io.File("dashboard_activity_"
                + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".txt"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.FileWriter writer = new java.io.FileWriter(fileChooser.getSelectedFile());
                writer.write("GlobeMed Healthcare System - Activity Export\n");
                writer.write("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n");
                writer.write("═══════════════════════════════════════════════════════\n\n");
                writer.write(recentActivityArea.getText());
                writer.close();

                parentFrame.setStatus("Activity log exported successfully");

                // Show success notification
                JOptionPane.showMessageDialog(this,
                        "Activity log has been exported successfully!",
                        "Export Complete",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception e) {
                parentFrame.setStatus("Export failed: " + e.getMessage());

                JOptionPane.showMessageDialog(this,
                        "Error exporting activity log:\n" + e.getMessage(),
                        "Export Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Public methods for external access
    public void setStatus(String status) {
        if (parentFrame != null) {
            parentFrame.setStatus(status);
        }
    }

    public void showNotification(String message, String type) {
        Color bgColor = HEALTHCARE_GREEN;
        if ("warning".equals(type)) {
            bgColor = WARNING_AMBER;
        } else if ("error".equals(type)) {
            bgColor = ERROR_RED;
        }

        JPanel notification = new JPanel(new BorderLayout());
        notification.setBackground(bgColor);
        notification.setBorder(new EmptyBorder(8, 16, 8, 16));

        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("Sansation", Font.PLAIN, 12));
        messageLabel.setForeground(Color.WHITE);

        notification.add(messageLabel, BorderLayout.CENTER);

        // Add to top of the panel temporarily
        add(notification, BorderLayout.NORTH);
        revalidate();

        // Remove after 3 seconds
        Timer timer = new Timer(3000, e -> {
            remove(notification);
            revalidate();
            repaint();
        });
        timer.setRepeats(false);
        timer.start();
    }

    // Utility method to get current statistics
    public DashboardStats getCurrentStats() {
        return new DashboardStats(
                totalPatientsLabel.getText(),
                todayAppointmentsLabel.getText(),
                pendingBillsLabel.getText(),
                totalStaffLabel.getText()
        );
    }

    // Inner class for dashboard statistics
    public static class DashboardStats {

        public final String totalPatients;
        public final String todayAppointments;
        public final String pendingBills;
        public final String totalStaff;

        public DashboardStats(String totalPatients, String todayAppointments,
                String pendingBills, String totalStaff) {
            this.totalPatients = totalPatients;
            this.todayAppointments = todayAppointments;
            this.pendingBills = pendingBills;
            this.totalStaff = totalStaff;
        }
    }
}

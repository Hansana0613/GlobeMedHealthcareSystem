/*
 * Enhanced Reports Management Panel
 * Improved UI/UX with medical color scheme and better layout
 */
package com.globemed.gui;

import com.globemed.models.*;
import com.globemed.services.ReportService;
import com.globemed.database.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import javax.swing.ListSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Enhanced Reports Management Panel with improved UI/UX
 *
 * @author Hansana
 */
public class ReportsPanel extends JPanel {

    // Color Scheme Constants
    private static final Color PRIMARY_COLOR = new Color(46, 134, 171);      // Medical Blue
    private static final Color SECONDARY_COLOR = new Color(255, 255, 255);   // Clean White
    private static final Color ACCENT_COLOR = new Color(76, 175, 80);        // Healthcare Green
    private static final Color WARNING_COLOR = new Color(255, 152, 0);       // Amber
    private static final Color ERROR_COLOR = new Color(244, 67, 54);         // Medical Red
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);  // Light Gray
    private static final Color CARD_COLOR = new Color(255, 255, 255);        // White cards
    private static final Color BORDER_COLOR = new Color(224, 224, 224);      // Light border

    // Typography Constants
    private static final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 16);
    private static final Font BODY_FONT = new Font("SansSerif", Font.PLAIN, 12);
    private static final Font LABEL_FONT = new Font("SansSerif", Font.BOLD, 11);
    private static final Font DATA_FONT = new Font("SansSerif", Font.PLAIN, 11);
    private static final Font MONO_FONT = new Font("Consolas", Font.PLAIN, 11);

    private ReportService reportService;
    private PatientDAO patientDAO;
    private AppointmentDAO appointmentDAO;
    private BillDAO billDAO;
    private StaffDAO staffDAO;

    private Staff currentUser;
    private JTable reportsTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> reportTypeFilter;
    private JComboBox<Patient> patientComboBox;
    private JComboBox<Appointment> appointmentComboBox;
    private JComboBox<Bill> billComboBox;
    private JTextArea reportContentArea;
    private JButton generateTreatmentBtn;
    private JButton generateFinancialBtn;
    private JButton generateDiagnosticBtn;
    private JButton deleteReportBtn;
    private JButton refreshBtn;

    private final String[] reportTypes = {"All", "TREATMENT_SUMMARY", "FINANCIAL", "DIAGNOSTIC_RESULTS"};

    public ReportsPanel(Staff currentUser) {
        this.currentUser = currentUser;
        this.reportService = new ReportService();
        this.patientDAO = new PatientDAO();
        this.appointmentDAO = new AppointmentDAO();
        this.billDAO = new BillDAO();
        this.staffDAO = new StaffDAO();

        setBackground(BACKGROUND_COLOR);
        initializeComponents();
        setupLayout();
        setupEventHandlers();

        if (currentUser != null) {
            loadData();
            applyPermissions();
        } else {
            setDefaultState();
        }
    }

    private void initializeComponents() {
        // Enhanced Table setup
        String[] columnNames = {"ID", "Type", "Title", "Patient", "Generated At", "Summary"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        reportsTable = new JTable(tableModel);
        styleTable();

        // Enhanced Filter components
        reportTypeFilter = new JComboBox<>(reportTypes);
        styleComboBox(reportTypeFilter);

        // Enhanced Combo boxes
        patientComboBox = new JComboBox<>();
        appointmentComboBox = new JComboBox<>();
        billComboBox = new JComboBox<>();

        styleComboBox(patientComboBox);
        styleComboBox(appointmentComboBox);
        styleComboBox(billComboBox);

        // Enhanced Report content display
        reportContentArea = new JTextArea(15, 50);
        reportContentArea.setEditable(false);
        reportContentArea.setFont(MONO_FONT);
        reportContentArea.setBackground(CARD_COLOR);
        reportContentArea.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        reportContentArea.setLineWrap(true);
        reportContentArea.setWrapStyleWord(true);

        // Enhanced Buttons
        setupButtons();
    }

    private void styleTable() {
        reportsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        reportsTable.setRowSorter(new TableRowSorter<>(tableModel));
        reportsTable.setRowHeight(32);
        reportsTable.setFont(DATA_FONT);
        reportsTable.setBackground(SECONDARY_COLOR);
        reportsTable.setGridColor(BORDER_COLOR);
        reportsTable.setSelectionBackground(new Color(PRIMARY_COLOR.getRed(), PRIMARY_COLOR.getGreen(), PRIMARY_COLOR.getBlue(), 30));
        reportsTable.setSelectionForeground(Color.BLACK);

        // Style table header
        JTableHeader header = reportsTable.getTableHeader();
        header.setFont(LABEL_FONT);
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(SECONDARY_COLOR);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 36));

        // Custom cell renderer for report type column
        reportsTable.getColumnModel().getColumn(1).setCellRenderer(new ReportTypeCellRenderer());
    }

    private void styleComboBox(JComboBox<?> combo) {
        combo.setFont(DATA_FONT);
        combo.setBackground(SECONDARY_COLOR);
        combo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        combo.setPreferredSize(new Dimension(combo.getPreferredSize().width, 32));
    }

    private void setupButtons() {
        generateTreatmentBtn = createStyledButton("📄 Treatment Report", ACCENT_COLOR, "treatment");
        generateFinancialBtn = createStyledButton("💰 Financial Report", PRIMARY_COLOR, "financial");
        generateDiagnosticBtn = createStyledButton("🔬 Diagnostic Report", new Color(156, 39, 176), "diagnostic");
        deleteReportBtn = createStyledButton("🗑️ Delete Report", ERROR_COLOR, "delete");
        refreshBtn = createStyledButton("🔄 Refresh", new Color(96, 125, 139), "refresh");
    }

    private JButton createStyledButton(String text, Color bgColor, String type) {
        JButton button = new JButton(text);
        button.setFont(LABEL_FONT);
        button.setBackground(bgColor);
        button.setForeground(SECONDARY_COLOR);
        button.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effects
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(bgColor.brighter());
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(bgColor);
                }
            }
        });

        return button;
    }

    private void setDefaultState() {
        generateTreatmentBtn.setEnabled(false);
        generateFinancialBtn.setEnabled(false);
        generateDiagnosticBtn.setEnabled(false);
        deleteReportBtn.setEnabled(false);

        String tooltipText = "Please log in to access this feature";
        generateTreatmentBtn.setToolTipText(tooltipText);
        generateFinancialBtn.setToolTipText(tooltipText);
        generateDiagnosticBtn.setToolTipText(tooltipText);
        deleteReportBtn.setToolTipText(tooltipText);

        tableModel.setRowCount(0);
        reportContentArea.setText("🔒 Please log in to access reports.");
        reportContentArea.setForeground(new Color(128, 128, 128));
    }

    public void updateUser(Staff newUser) {
        this.currentUser = newUser;
        if (currentUser != null) {
            loadData();
            applyPermissions();
            reportContentArea.setForeground(Color.BLACK);
        } else {
            setDefaultState();
        }
    }

    private void setupLayout() {
        setLayout(new BorderLayout(0, 16));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // Header Panel
        add(createHeaderPanel(), BorderLayout.NORTH);

        // Main Content Panel
        add(createMainContentPanel(), BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        // Title with icon
        JLabel titleLabel = new JLabel("📊 Reports Management");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(PRIMARY_COLOR);

        // Status info
        JLabel statusLabel = new JLabel(currentUser != null ? "👤 " + currentUser.getName() : "🔒 Not logged in");
        statusLabel.setFont(LABEL_FONT);
        statusLabel.setForeground(new Color(96, 125, 139));

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(statusLabel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createMainContentPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(16, 0));
        mainPanel.setBackground(BACKGROUND_COLOR);

        // Top panel - Filters and Generation
        mainPanel.add(createControlPanel(), BorderLayout.NORTH);

        // Center panel - Reports table and content
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(650);
        splitPane.setBackground(BACKGROUND_COLOR);

        splitPane.setLeftComponent(createReportsTableCard());
        splitPane.setRightComponent(createReportContentCard());

        mainPanel.add(splitPane, BorderLayout.CENTER);

        return mainPanel;
    }

    private JPanel createControlPanel() {
        JPanel controlCard = createCard("🔧 Report Generation & Filters");
        controlCard.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Row 1: Filters
        gbc.gridx = 0;
        gbc.gridy = 0;
        controlCard.add(createLabel("Report Type:"), gbc);
        gbc.gridx = 1;
        controlCard.add(reportTypeFilter, gbc);

        gbc.gridx = 2;
        controlCard.add(createLabel("Patient:"), gbc);
        gbc.gridx = 3;
        controlCard.add(patientComboBox, gbc);

        gbc.gridx = 4;
        controlCard.add(refreshBtn, gbc);

        // Row 2: Generation parameters
        gbc.gridx = 0;
        gbc.gridy = 1;
        controlCard.add(createLabel("Appointment:"), gbc);
        gbc.gridx = 1;
        controlCard.add(appointmentComboBox, gbc);

        gbc.gridx = 2;
        controlCard.add(createLabel("Bill:"), gbc);
        gbc.gridx = 3;
        controlCard.add(billComboBox, gbc);

        // Row 3: Generation buttons
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        buttonPanel.setBackground(CARD_COLOR);
        buttonPanel.add(generateTreatmentBtn);
        buttonPanel.add(generateFinancialBtn);
        buttonPanel.add(generateDiagnosticBtn);
        buttonPanel.add(deleteReportBtn);

        controlCard.add(buttonPanel, gbc);

        return controlCard;
    }

    private JPanel createReportsTableCard() {
        JPanel card = createCard("📋 Generated Reports");
        card.setLayout(new BorderLayout(0, 8));

        // Table with scroll
        JScrollPane tableScrollPane = new JScrollPane(reportsTable);
        tableScrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        tableScrollPane.setPreferredSize(new Dimension(0, 400));
        card.add(tableScrollPane, BorderLayout.CENTER);

        // Table info panel
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        infoPanel.setBackground(CARD_COLOR);

        JLabel infoLabel = new JLabel("📊 Click on a report to view its content");
        infoLabel.setFont(DATA_FONT);
        infoLabel.setForeground(new Color(96, 125, 139));
        infoPanel.add(infoLabel);

        card.add(infoPanel, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createReportContentCard() {
        JPanel card = createCard("📄 Report Content");
        card.setLayout(new BorderLayout(0, 8));

        // Content area with scroll
        JScrollPane contentScrollPane = new JScrollPane(reportContentArea);
        contentScrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        contentScrollPane.setPreferredSize(new Dimension(400, 400));
        card.add(contentScrollPane, BorderLayout.CENTER);

        // Content toolbar
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        toolbarPanel.setBackground(CARD_COLOR);

        JButton exportBtn = createStyledButton("📤 Export", new Color(96, 125, 139), "export");
        exportBtn.setEnabled(false); // Placeholder for future functionality
        toolbarPanel.add(exportBtn);

        card.add(toolbarPanel, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createCard(String title) {
        JPanel card = new JPanel();
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createTitledBorder(
                        BorderFactory.createEmptyBorder(8, 16, 16, 16),
                        title,
                        0,
                        0,
                        HEADER_FONT,
                        PRIMARY_COLOR
                )
        ));
        return card;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        label.setForeground(new Color(64, 64, 64));
        return label;
    }

    // Report type cell renderer for color-coded types
    private class ReportTypeCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value != null) {
                String type = value.toString();
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(new Font("SansSerif", Font.BOLD, 10));

                if (!isSelected) {
                    switch (type) {
                        case "TREATMENT_SUMMARY":
                            setBackground(new Color(ACCENT_COLOR.getRed(), ACCENT_COLOR.getGreen(), ACCENT_COLOR.getBlue(), 30));
                            setForeground(ACCENT_COLOR);
                            setText("📄 TREATMENT");
                            break;
                        case "FINANCIAL":
                            setBackground(new Color(PRIMARY_COLOR.getRed(), PRIMARY_COLOR.getGreen(), PRIMARY_COLOR.getBlue(), 30));
                            setForeground(PRIMARY_COLOR);
                            setText("💰 FINANCIAL");
                            break;
                        case "DIAGNOSTIC_RESULTS":
                            setBackground(new Color(156, 39, 176, 30));
                            setForeground(new Color(156, 39, 176));
                            setText("🔬 DIAGNOSTIC");
                            break;
                        default:
                            setBackground(SECONDARY_COLOR);
                            setForeground(Color.BLACK);
                            setText(type);
                    }
                } else {
                    setText(getIconForReportType(type) + " " + type);
                }
            }
            return this;
        }
    }

    private String getIconForReportType(String type) {
        switch (type) {
            case "TREATMENT_SUMMARY":
                return "📄";
            case "FINANCIAL":
                return "💰";
            case "DIAGNOSTIC_RESULTS":
                return "🔬";
            default:
                return "📋";
        }
    }

    private void setupEventHandlers() {
        // Table selection
        reportsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                displaySelectedReport();
            }
        });

        // Filter change
        reportTypeFilter.addActionListener(e -> filterReports());

        // Patient selection change
        patientComboBox.addActionListener(e -> loadAppointmentsAndBills());

        // Button handlers
        generateTreatmentBtn.addActionListener(e -> generateTreatmentReport());
        generateFinancialBtn.addActionListener(e -> generateFinancialReport());
        generateDiagnosticBtn.addActionListener(e -> generateDiagnosticReport());
        deleteReportBtn.addActionListener(e -> deleteSelectedReport());
        refreshBtn.addActionListener(e -> loadData());
    }

    private void loadData() {
        loadPatients();
        loadReports();
    }

    private void loadPatients() {
        try {
            List<Patient> patients = patientDAO.getAllPatients();
            patientComboBox.removeAllItems();
            patientComboBox.addItem(null); // Empty option
            for (Patient patient : patients) {
                patientComboBox.addItem(patient);
            }
        } catch (SQLException e) {
            showErrorDialog("Error loading patients: " + e.getMessage());
        }
    }

    private void loadAppointmentsAndBills() {
        Patient selectedPatient = (Patient) patientComboBox.getSelectedItem();

        appointmentComboBox.removeAllItems();
        billComboBox.removeAllItems();

        if (selectedPatient != null) {
            try {
                List<Appointment> appointments = appointmentDAO.getAppointmentsByPatientId(selectedPatient.getId());
                appointmentComboBox.addItem(null);
                for (Appointment appointment : appointments) {
                    appointmentComboBox.addItem(appointment);
                }

                List<Bill> bills = billDAO.getBillsByPatientId(selectedPatient.getId());
                billComboBox.addItem(null);
                for (Bill bill : bills) {
                    billComboBox.addItem(bill);
                }
            } catch (SQLException e) {
                showErrorDialog("Error loading appointments/bills: " + e.getMessage());
            }
        }
    }

    private void loadReports() {
        SwingWorker<List<Report>, Void> worker = new SwingWorker<List<Report>, Void>() {
            @Override
            protected List<Report> doInBackground() throws Exception {
                return reportService.getAllReports();
            }

            @Override
            protected void done() {
                try {
                    List<Report> reports = get();
                    updateTableModel(reports);
                } catch (Exception e) {
                    showErrorDialog("Error loading reports: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void updateTableModel(List<Report> reports) {
        tableModel.setRowCount(0);

        SwingWorker<Void, Void> dataLoader = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                for (Report report : reports) {
                    String patientName = "Unknown";
                    if (report.getPatientId() != null) {
                        try {
                            Patient patient = patientDAO.getPatientById(report.getPatientId());
                            if (patient != null) {
                                patientName = patient.getName();
                            }
                        } catch (SQLException e) {
                            // Handle silently
                        }
                    }

                    Object[] row = {
                        report.getId(),
                        report.getType(),
                        report.getTitle(),
                        patientName,
                        report.getGeneratedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                        report.getSummary() != null ? (report.getSummary().length() > 50
                        ? report.getSummary().substring(0, 50) + "..." : report.getSummary()) : ""
                    };

                    SwingUtilities.invokeLater(() -> tableModel.addRow(row));
                }
                return null;
            }
        };
        dataLoader.execute();
    }

    private void filterReports() {
        String selectedType = (String) reportTypeFilter.getSelectedItem();

        SwingWorker<List<Report>, Void> worker = new SwingWorker<List<Report>, Void>() {
            @Override
            protected List<Report> doInBackground() throws Exception {
                if ("All".equals(selectedType)) {
                    return reportService.getAllReports();
                } else {
                    return reportService.getReportsByType(selectedType);
                }
            }

            @Override
            protected void done() {
                try {
                    List<Report> reports = get();
                    updateTableModel(reports);
                } catch (Exception e) {
                    showErrorDialog("Error filtering reports: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void displaySelectedReport() {
        int selectedRow = reportsTable.getSelectedRow();
        if (selectedRow >= 0) {
            Long reportId = (Long) tableModel.getValueAt(selectedRow, 0);

            SwingWorker<Report, Void> worker = new SwingWorker<Report, Void>() {
                @Override
                protected Report doInBackground() throws Exception {
                    List<Report> allReports = reportService.getAllReports();
                    return allReports.stream()
                            .filter(r -> r.getId().equals(reportId))
                            .findFirst()
                            .orElse(null);
                }

                @Override
                protected void done() {
                    try {
                        Report selectedReport = get();
                        if (selectedReport != null) {
                            reportContentArea.setText(selectedReport.getContent());
                            reportContentArea.setCaretPosition(0);
                        }
                    } catch (Exception e) {
                        showErrorDialog("Error displaying report: " + e.getMessage());
                    }
                }
            };
            worker.execute();
        } else {
            reportContentArea.setText("");
        }
    }

    private void generateTreatmentReport() {
        Patient patient = (Patient) patientComboBox.getSelectedItem();
        Appointment appointment = (Appointment) appointmentComboBox.getSelectedItem();

        if (patient == null || appointment == null) {
            showWarningDialog("Please select both a patient and an appointment.");
            return;
        }

        if (!reportService.hasPermission(currentUser, "GENERATE_REPORTS")) {
            showErrorDialog("You don't have permission to generate reports.");
            return;
        }

        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                Report report = reportService.generateTreatmentReport(patient.getId(), appointment.getId());
                return report != null;
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        showSuccessDialog("Treatment report generated successfully.");
                        loadReports();
                    } else {
                        showErrorDialog("Failed to generate treatment report.");
                    }
                } catch (Exception e) {
                    showErrorDialog("Error generating treatment report: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void generateFinancialReport() {
        Patient patient = (Patient) patientComboBox.getSelectedItem();

        if (patient == null) {
            showWarningDialog("Please select a patient.");
            return;
        }

        if (!reportService.hasPermission(currentUser, "GENERATE_REPORTS")) {
            showErrorDialog("You don't have permission to generate reports.");
            return;
        }

        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                LocalDate startDate = LocalDate.now().minusMonths(3);
                LocalDate endDate = LocalDate.now();
                Report report = reportService.generateFinancialReport(patient.getId(), startDate, endDate);
                return report != null;
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        showSuccessDialog("Financial report generated successfully.");
                        loadReports();
                    } else {
                        showErrorDialog("Failed to generate financial report.");
                    }
                } catch (Exception e) {
                    showErrorDialog("Error generating financial report: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void generateDiagnosticReport() {
        Patient patient = (Patient) patientComboBox.getSelectedItem();
        Appointment appointment = (Appointment) appointmentComboBox.getSelectedItem();
        Bill bill = (Bill) billComboBox.getSelectedItem();

        if (patient == null || appointment == null || bill == null) {
            showWarningDialog("Please select a patient, appointment, and bill.");
            return;
        }

        if (!reportService.hasPermission(currentUser, "GENERATE_REPORTS")) {
            showErrorDialog("You don't have permission to generate reports.");
            return;
        }

        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                Report report = reportService.generateDiagnosticReport(patient.getId(), appointment.getId(), bill.getId());
                return report != null;
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        showSuccessDialog("Diagnostic report generated successfully.");
                        loadReports();
                    } else {
                        showErrorDialog("Failed to generate diagnostic report.");
                    }
                } catch (Exception e) {
                    showErrorDialog("Error generating diagnostic report: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void deleteSelectedReport() {
        int selectedRow = reportsTable.getSelectedRow();
        if (selectedRow < 0) {
            showWarningDialog("Please select a report to delete.");
            return;
        }

        if (!reportService.hasPermission(currentUser, "DELETE_REPORTS")) {
            showErrorDialog("You don't have permission to delete reports.");
            return;
        }

        Long reportId = (Long) tableModel.getValueAt(selectedRow, 0);
        String reportTitle = (String) tableModel.getValueAt(selectedRow, 2);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "🗑️ Are you sure you want to delete the report:\n\"" + reportTitle + "\"?\n\nThis action cannot be undone.",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return reportService.deleteReport(reportId);
                }

                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            showSuccessDialog("Report deleted successfully.");
                            loadReports();
                            reportContentArea.setText("");
                        } else {
                            showErrorDialog("Failed to delete report.");
                        }
                    } catch (Exception e) {
                        showErrorDialog("Error deleting report: " + e.getMessage());
                    }
                }
            };
            worker.execute();
        }
    }

    private void applyPermissions() {
        boolean canGenerate = reportService.hasPermission(currentUser, "GENERATE_REPORTS");
        boolean canDelete = reportService.hasPermission(currentUser, "DELETE_REPORTS");

        generateTreatmentBtn.setEnabled(canGenerate);
        generateFinancialBtn.setEnabled(canGenerate);
        generateDiagnosticBtn.setEnabled(canGenerate);
        deleteReportBtn.setEnabled(canDelete);

        if (!canGenerate) {
            String tooltip = "Insufficient permissions to generate reports";
            generateTreatmentBtn.setToolTipText(tooltip);
            generateFinancialBtn.setToolTipText(tooltip);
            generateDiagnosticBtn.setToolTipText(tooltip);

            // Visual indication of disabled state
            generateTreatmentBtn.setBackground(new Color(200, 200, 200));
            generateFinancialBtn.setBackground(new Color(200, 200, 200));
            generateDiagnosticBtn.setBackground(new Color(200, 200, 200));
        }

        if (!canDelete) {
            deleteReportBtn.setToolTipText("Insufficient permissions to delete reports");
            deleteReportBtn.setBackground(new Color(200, 200, 200));
        }
    }

    // Enhanced dialog methods
    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this,
                "⚠️ " + message,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccessDialog(String message) {
        JOptionPane.showMessageDialog(this,
                "✅ " + message,
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showWarningDialog(String message) {
        JOptionPane.showMessageDialog(this,
                "⚠️ " + message,
                "Warning",
                JOptionPane.WARNING_MESSAGE);
    }
}

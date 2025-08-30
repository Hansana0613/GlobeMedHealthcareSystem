/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.gui;

import com.globemed.models.*;
import com.globemed.services.ReportService;
import com.globemed.database.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.ListSelectionModel;
import java.awt.*;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 *
 * @author Hansana
 */
public class ReportsPanel extends JPanel {

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

    public ReportsPanel(Staff currentUser) {
        this.currentUser = currentUser;
        this.reportService = new ReportService();
        this.patientDAO = new PatientDAO();
        this.appointmentDAO = new AppointmentDAO();
        this.billDAO = new BillDAO();
        this.staffDAO = new StaffDAO();

        initializeComponents();
        setupLayout();
        setupEventHandlers();
        
        // Only load data and apply permissions if user is logged in
        if (currentUser != null) {
            loadData();
            applyPermissions();
        } else {
            // Set default state for when user is not logged in
            setDefaultState();
        }
    }

    private void initializeComponents() {
        // Table setup
        String[] columnNames = {"ID", "Type", "Title", "Patient", "Generated At", "Summary"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        reportsTable = new JTable(tableModel);
        reportsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        reportsTable.setRowSorter(new TableRowSorter<>(tableModel));

        // Filter components
        reportTypeFilter = new JComboBox<>(new String[]{"All", "TREATMENT_SUMMARY", "FINANCIAL", "DIAGNOSTIC_RESULTS"});

        // Combo boxes
        patientComboBox = new JComboBox<>();
        appointmentComboBox = new JComboBox<>();
        billComboBox = new JComboBox<>();

        // Report content display
        reportContentArea = new JTextArea(15, 50);
        reportContentArea.setEditable(false);
        reportContentArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        // Buttons
        generateTreatmentBtn = new JButton("Generate Treatment Report");
        generateFinancialBtn = new JButton("Generate Financial Report");
        generateDiagnosticBtn = new JButton("Generate Diagnostic Report");
        deleteReportBtn = new JButton("Delete Report");
        refreshBtn = new JButton("Refresh");

        // Style buttons
        generateTreatmentBtn.setBackground(new Color(76, 175, 80));
        generateTreatmentBtn.setForeground(Color.WHITE);
        generateFinancialBtn.setBackground(new Color(33, 150, 243));
        generateFinancialBtn.setForeground(Color.WHITE);
        generateDiagnosticBtn.setBackground(new Color(156, 39, 176));
        generateDiagnosticBtn.setForeground(Color.WHITE);
        deleteReportBtn.setBackground(new Color(244, 67, 54));
        deleteReportBtn.setForeground(Color.WHITE);
    }

    private void setDefaultState() {
        // Disable all buttons when no user is logged in
        generateTreatmentBtn.setEnabled(false);
        generateFinancialBtn.setEnabled(false);
        generateDiagnosticBtn.setEnabled(false);
        deleteReportBtn.setEnabled(false);
        
        // Set tooltips
        generateTreatmentBtn.setToolTipText("Please log in to generate reports");
        generateFinancialBtn.setToolTipText("Please log in to generate reports");
        generateDiagnosticBtn.setToolTipText("Please log in to generate reports");
        deleteReportBtn.setToolTipText("Please log in to delete reports");
        
        // Clear table and content
        tableModel.setRowCount(0);
        reportContentArea.setText("Please log in to access reports.");
    }

    // Method to update the panel when user logs in
    public void updateUser(Staff newUser) {
        this.currentUser = newUser;
        if (currentUser != null) {
            loadData();
            applyPermissions();
        } else {
            setDefaultState();
        }
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Top panel - Filters and Generation
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        topPanel.setBorder(BorderFactory.createTitledBorder("Report Generation & Filters"));

        gbc.insets = new Insets(5, 5, 5, 5);

        // Filter row
        gbc.gridx = 0;
        gbc.gridy = 0;
        topPanel.add(new JLabel("Report Type:"), gbc);
        gbc.gridx = 1;
        topPanel.add(reportTypeFilter, gbc);

        gbc.gridx = 2;
        topPanel.add(new JLabel("Patient:"), gbc);
        gbc.gridx = 3;
        topPanel.add(patientComboBox, gbc);

        gbc.gridx = 4;
        topPanel.add(refreshBtn, gbc);

        // Generation row
        gbc.gridx = 0;
        gbc.gridy = 1;
        topPanel.add(new JLabel("Appointment:"), gbc);
        gbc.gridx = 1;
        topPanel.add(appointmentComboBox, gbc);

        gbc.gridx = 2;
        topPanel.add(new JLabel("Bill:"), gbc);
        gbc.gridx = 3;
        topPanel.add(billComboBox, gbc);

        // Button row
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(generateTreatmentBtn);
        buttonPanel.add(generateFinancialBtn);
        buttonPanel.add(generateDiagnosticBtn);
        buttonPanel.add(deleteReportBtn);
        topPanel.add(buttonPanel, gbc);

        add(topPanel, BorderLayout.NORTH);

        // Center panel - Split pane with table and content
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        // Left side - Reports table
        JScrollPane tableScrollPane = new JScrollPane(reportsTable);
        tableScrollPane.setPreferredSize(new Dimension(600, 400));
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Reports"));
        splitPane.setLeftComponent(tableScrollPane);

        // Right side - Report content
        JScrollPane contentScrollPane = new JScrollPane(reportContentArea);
        contentScrollPane.setPreferredSize(new Dimension(400, 400));
        contentScrollPane.setBorder(BorderFactory.createTitledBorder("Report Content"));
        splitPane.setRightComponent(contentScrollPane);

        splitPane.setDividerLocation(650);
        add(splitPane, BorderLayout.CENTER);
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
            showError("Error loading patients: " + e.getMessage());
        }
    }

    private void loadAppointmentsAndBills() {
        Patient selectedPatient = (Patient) patientComboBox.getSelectedItem();

        appointmentComboBox.removeAllItems();
        billComboBox.removeAllItems();

        if (selectedPatient != null) {
            try {
                // Load appointments for selected patient
                List<Appointment> appointments = appointmentDAO.getAppointmentsByPatientId(selectedPatient.getId());
                appointmentComboBox.addItem(null); // Empty option
                for (Appointment appointment : appointments) {
                    appointmentComboBox.addItem(appointment);
                }

                // Load bills for selected patient
                List<Bill> bills = billDAO.getBillsByPatientId(selectedPatient.getId());
                billComboBox.addItem(null); // Empty option
                for (Bill bill : bills) {
                    billComboBox.addItem(bill);
                }
            } catch (SQLException e) {
                showError("Error loading appointments/bills: " + e.getMessage());
            }
        }
    }

    private void loadReports() {
        try {
            List<Report> reports = reportService.getAllReports();
            updateTableModel(reports);
        } catch (SQLException e) {
            showError("Error loading reports: " + e.getMessage());
        }
    }

    private void updateTableModel(List<Report> reports) {
        tableModel.setRowCount(0);

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

            tableModel.addRow(row);
        }
    }

    private void filterReports() {
        String selectedType = (String) reportTypeFilter.getSelectedItem();

        try {
            List<Report> reports;
            if ("All".equals(selectedType)) {
                reports = reportService.getAllReports();
            } else {
                reports = reportService.getReportsByType(selectedType);
            }
            updateTableModel(reports);
        } catch (SQLException e) {
            showError("Error filtering reports: " + e.getMessage());
        }
    }

    private void displaySelectedReport() {
        int selectedRow = reportsTable.getSelectedRow();
        if (selectedRow >= 0) {
            Long reportId = (Long) tableModel.getValueAt(selectedRow, 0);

            try {
                List<Report> allReports = reportService.getAllReports();
                Report selectedReport = allReports.stream()
                        .filter(r -> r.getId().equals(reportId))
                        .findFirst()
                        .orElse(null);

                if (selectedReport != null) {
                    reportContentArea.setText(selectedReport.getContent());
                    reportContentArea.setCaretPosition(0);
                }
            } catch (SQLException e) {
                showError("Error displaying report: " + e.getMessage());
            }
        } else {
            reportContentArea.setText("");
        }
    }

    private void generateTreatmentReport() {
        Patient patient = (Patient) patientComboBox.getSelectedItem();
        Appointment appointment = (Appointment) appointmentComboBox.getSelectedItem();

        if (patient == null || appointment == null) {
            showError("Please select both a patient and an appointment.");
            return;
        }

        if (!reportService.hasPermission(currentUser, "GENERATE_REPORTS")) {
            showError("You don't have permission to generate reports.");
            return;
        }

        try {
            Report report = reportService.generateTreatmentReport(patient.getId(), appointment.getId());
            showSuccess("Treatment report generated successfully.");
            loadReports();
        } catch (SQLException e) {
            showError("Error generating treatment report: " + e.getMessage());
        }
    }

    private void generateFinancialReport() {
        Patient patient = (Patient) patientComboBox.getSelectedItem();

        if (patient == null) {
            showError("Please select a patient.");
            return;
        }

        if (!reportService.hasPermission(currentUser, "GENERATE_REPORTS")) {
            showError("You don't have permission to generate reports.");
            return;
        }

        try {
            LocalDate startDate = LocalDate.now().minusMonths(3);
            LocalDate endDate = LocalDate.now();

            Report report = reportService.generateFinancialReport(patient.getId(), startDate, endDate);
            showSuccess("Financial report generated successfully.");
            loadReports();
        } catch (SQLException e) {
            showError("Error generating financial report: " + e.getMessage());
        }
    }

    private void generateDiagnosticReport() {
        Patient patient = (Patient) patientComboBox.getSelectedItem();
        Appointment appointment = (Appointment) appointmentComboBox.getSelectedItem();
        Bill bill = (Bill) billComboBox.getSelectedItem();

        if (patient == null || appointment == null || bill == null) {
            showError("Please select a patient, appointment, and bill.");
            return;
        }

        if (!reportService.hasPermission(currentUser, "GENERATE_REPORTS")) {
            showError("You don't have permission to generate reports.");
            return;
        }

        try {
            Report report = reportService.generateDiagnosticReport(patient.getId(), appointment.getId(), bill.getId());
            showSuccess("Diagnostic report generated successfully.");
            loadReports();
        } catch (SQLException e) {
            showError("Error generating diagnostic report: " + e.getMessage());
        }
    }

    private void deleteSelectedReport() {
        int selectedRow = reportsTable.getSelectedRow();
        if (selectedRow < 0) {
            showError("Please select a report to delete.");
            return;
        }

        if (!reportService.hasPermission(currentUser, "DELETE_REPORTS")) {
            showError("You don't have permission to delete reports.");
            return;
        }

        Long reportId = (Long) tableModel.getValueAt(selectedRow, 0);
        String reportTitle = (String) tableModel.getValueAt(selectedRow, 2);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete the report: " + reportTitle + "?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (reportService.deleteReport(reportId)) {
                    showSuccess("Report deleted successfully.");
                    loadReports();
                    reportContentArea.setText("");
                } else {
                    showError("Failed to delete report.");
                }
            } catch (SQLException e) {
                showError("Error deleting report: " + e.getMessage());
            }
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
            generateTreatmentBtn.setToolTipText("Insufficient permissions");
            generateFinancialBtn.setToolTipText("Insufficient permissions");
            generateDiagnosticBtn.setToolTipText("Insufficient permissions");
        }

        if (!canDelete) {
            deleteReportBtn.setToolTipText("Insufficient permissions");
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}

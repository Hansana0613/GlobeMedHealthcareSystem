/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.gui;

import com.globemed.database.BillDAO;
import com.globemed.database.AppointmentDAO;
import com.globemed.database.PatientDAO;
import com.globemed.models.Bill;
import com.globemed.models.BillItem;
import com.globemed.models.Appointment;
import com.globemed.models.Patient;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Hansana
 */
public class BillingPanel extends JPanel {

    private MainFrame parentFrame;
    private BillDAO billDAO;
    private AppointmentDAO appointmentDAO;
    private PatientDAO patientDAO;

    // Components
    private JTable billsTable;
    private DefaultTableModel billsTableModel;
    private JTable billItemsTable;
    private DefaultTableModel billItemsTableModel;

    private JTextField searchField;
    private JComboBox<String> statusFilter;
    private JButton searchButton;
    private JButton refreshButton;

    // Bill management buttons
    private JButton viewBillButton;
    private JButton editBillButton;
    private JButton deleteBillButton;
    private JButton generateReportButton;

    // Bill item management
    private JButton addItemButton;
    private JButton removeItemButton;

    // Bill details panel
    private JPanel billDetailsPanel;
    private JLabel billIdLabel;
    private JLabel patientNameLabel;
    private JLabel appointmentDateLabel;
    private JLabel totalAmountLabel;
    private JComboBox<String> claimStatusCombo;
    private JTextArea insuranceDetailsArea;

    // New bill item panel
    private JPanel addItemPanel;
    private JComboBox<String> itemTypeCombo;
    private JTextField descriptionField;
    private JTextField costField;
    private JButton saveItemButton;
    private JButton cancelItemButton;

    private Bill currentBill;
    private DecimalFormat currencyFormat;

    public BillingPanel(MainFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.billDAO = new BillDAO();
        this.appointmentDAO = new AppointmentDAO();
        this.patientDAO = new PatientDAO();
        this.currencyFormat = new DecimalFormat("$#,##0.00");

        initializeComponents();
        layoutComponents();
        setupEventHandlers();
        loadBills();
    }

    private void initializeComponents() {
        // Main table for bills
        String[] billColumns = {"ID", "Patient", "Appointment Date", "Total Amount", "Status", "Insurance"};
        billsTableModel = new DefaultTableModel(billColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        billsTable = new JTable(billsTableModel);
        billsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        billsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedBillDetails();
            }
        });

        // Bill items table
        String[] itemColumns = {"Type", "Description", "Cost"};
        billItemsTableModel = new DefaultTableModel(itemColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        billItemsTable = new JTable(billItemsTableModel);

        // Search and filter components
        searchField = new JTextField(20);
        statusFilter = new JComboBox<>(new String[]{"All", "PENDING", "APPROVED", "REJECTED", "PAID"});
        searchButton = new JButton("Search");
        refreshButton = new JButton("Refresh");

        // Button components
        viewBillButton = new JButton("View Details");
        editBillButton = new JButton("Edit Bill");
        deleteBillButton = new JButton("Delete Bill");
        generateReportButton = new JButton("Generate Report");
        addItemButton = new JButton("Add Item");
        removeItemButton = new JButton("Remove Item");

        // Bill details components
        billIdLabel = new JLabel("N/A");
        patientNameLabel = new JLabel("N/A");
        appointmentDateLabel = new JLabel("N/A");
        totalAmountLabel = new JLabel("$0.00");
        claimStatusCombo = new JComboBox<>(new String[]{"PENDING", "APPROVED", "REJECTED", "PAID"});
        insuranceDetailsArea = new JTextArea(3, 20);
        insuranceDetailsArea.setLineWrap(true);
        insuranceDetailsArea.setWrapStyleWord(true);

        // Add item components
        itemTypeCombo = new JComboBox<>(new String[]{"CONSULTATION", "TREATMENT", "MEDICATION", "DIAGNOSTIC"});
        descriptionField = new JTextField(15);
        costField = new JTextField(10);
        saveItemButton = new JButton("Save Item");
        cancelItemButton = new JButton("Cancel");

        // Initially disable buttons
        updateButtonStates(false);
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());

        // Top panel - Search and filters
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // Center panel - Split pane with bills and details
        JSplitPane centerSplitPane = createCenterPanel();
        add(centerSplitPane, BorderLayout.CENTER);

        // Bottom panel - Action buttons
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Search & Filter"));

        panel.add(new JLabel("Search:"));
        panel.add(searchField);
        panel.add(new JLabel("Status:"));
        panel.add(statusFilter);
        panel.add(searchButton);
        panel.add(refreshButton);

        return panel;
    }

    private JSplitPane createCenterPanel() {
        // Left side - Bills table
        JScrollPane billsScrollPane = new JScrollPane(billsTable);
        billsScrollPane.setPreferredSize(new Dimension(600, 300));
        billsScrollPane.setBorder(BorderFactory.createTitledBorder("Bills"));

        // Right side - Bill details and items
        JPanel rightPanel = new JPanel(new BorderLayout());

        // Bill details panel
        billDetailsPanel = createBillDetailsPanel();
        rightPanel.add(billDetailsPanel, BorderLayout.NORTH);

        // Bill items panel
        JPanel itemsPanel = createBillItemsPanel();
        rightPanel.add(itemsPanel, BorderLayout.CENTER);

        // Add item panel (initially hidden)
        addItemPanel = createAddItemPanel();
        addItemPanel.setVisible(false);
        rightPanel.add(addItemPanel, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, billsScrollPane, rightPanel);
        splitPane.setDividerLocation(600);
        splitPane.setResizeWeight(0.6);

        return splitPane;
    }

    private JPanel createBillDetailsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Bill Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Row 0
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Bill ID:"), gbc);
        gbc.gridx = 1;
        panel.add(billIdLabel, gbc);

        // Row 1
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Patient:"), gbc);
        gbc.gridx = 1;
        panel.add(patientNameLabel, gbc);

        // Row 2
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Appointment:"), gbc);
        gbc.gridx = 1;
        panel.add(appointmentDateLabel, gbc);

        // Row 3
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Total Amount:"), gbc);
        gbc.gridx = 1;
        totalAmountLabel.setFont(totalAmountLabel.getFont().deriveFont(Font.BOLD, 14f));
        panel.add(totalAmountLabel, gbc);

        // Row 4
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Claim Status:"), gbc);
        gbc.gridx = 1;
        panel.add(claimStatusCombo, gbc);

        // Row 5
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Insurance:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panel.add(new JScrollPane(insuranceDetailsArea), gbc);

        return panel;
    }

    private JPanel createBillItemsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Bill Items"));

        JScrollPane itemsScrollPane = new JScrollPane(billItemsTable);
        itemsScrollPane.setPreferredSize(new Dimension(400, 200));
        panel.add(itemsScrollPane, BorderLayout.CENTER);

        JPanel itemButtonPanel = new JPanel(new FlowLayout());
        itemButtonPanel.add(addItemButton);
        itemButtonPanel.add(removeItemButton);
        panel.add(itemButtonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createAddItemPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Add New Item"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Row 0
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Type:"), gbc);
        gbc.gridx = 1;
        panel.add(itemTypeCombo, gbc);

        // Row 1
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(descriptionField, gbc);

        // Row 2
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JLabel("Cost:"), gbc);
        gbc.gridx = 1;
        panel.add(costField, gbc);

        // Row 3 - Buttons
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(saveItemButton);
        buttonPanel.add(cancelItemButton);
        panel.add(buttonPanel, gbc);

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.add(viewBillButton);
        panel.add(editBillButton);
        panel.add(deleteBillButton);
        panel.add(generateReportButton);

        return panel;
    }

    private void setupEventHandlers() {
        // Search functionality
        searchButton.addActionListener(e -> performSearch());
        statusFilter.addActionListener(e -> performSearch());

        // Refresh button
        refreshButton.addActionListener(e -> loadBills());

        // Bill management buttons
        viewBillButton.addActionListener(e -> viewBillDetails());
        editBillButton.addActionListener(e -> editBill());
        deleteBillButton.addActionListener(e -> deleteBill());
        generateReportButton.addActionListener(e -> generateBillReport());

        // Bill item management
        addItemButton.addActionListener(e -> showAddItemPanel());
        removeItemButton.addActionListener(e -> removeSelectedItem());
        saveItemButton.addActionListener(e -> saveNewItem());
        cancelItemButton.addActionListener(e -> hideAddItemPanel());

        // Claim status change
        claimStatusCombo.addActionListener(e -> updateClaimStatus());
    }

    private void loadBills() {
        try {
            List<Bill> bills = billDAO.getAllBills();
            billsTableModel.setRowCount(0);

            for (Bill bill : bills) {
                // Get patient and appointment details
                String patientName = "Unknown";
                String appointmentDate = "N/A";

                try {
                    Appointment appointment = appointmentDAO.getAppointmentById(bill.getAppointmentId());
                    if (appointment != null) {
                        Patient patient = patientDAO.getPatientById(appointment.getPatientId());
                        if (patient != null) {
                            patientName = patient.getName();
                        }
                        appointmentDate = appointment.getAppointmentTime()
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                    }
                } catch (SQLException e) {
                    System.err.println("Error loading bill details: " + e.getMessage());
                }

                Object[] row = {
                    bill.getId(),
                    patientName,
                    appointmentDate,
                    currencyFormat.format(bill.getTotalAmount()),
                    bill.getClaimStatus(),
                    bill.getInsuranceDetails()
                };
                billsTableModel.addRow(row);
            }

            // Adjust column widths
            adjustColumnWidths();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading bills: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void adjustColumnWidths() {
        TableColumnModel columnModel = billsTable.getColumnModel();
        int[] widths = {50, 150, 120, 100, 80, 150};

        for (int i = 0; i < Math.min(widths.length, columnModel.getColumnCount()); i++) {
            columnModel.getColumn(i).setPreferredWidth(widths[i]);
        }
    }

    private void performSearch() {
        String searchText = searchField.getText().toLowerCase().trim();
        String statusFilter = (String) this.statusFilter.getSelectedItem();

        try {
            List<Bill> allBills = billDAO.getAllBills();
            billsTableModel.setRowCount(0);

            for (Bill bill : allBills) {
                // Filter by status
                if (!"All".equals(statusFilter) && !statusFilter.equals(bill.getClaimStatus())) {
                    continue;
                }

                // Get patient details for search
                String patientName = "Unknown";
                String appointmentDate = "N/A";

                try {
                    Appointment appointment = appointmentDAO.getAppointmentById(bill.getAppointmentId());
                    if (appointment != null) {
                        Patient patient = patientDAO.getPatientById(appointment.getPatientId());
                        if (patient != null) {
                            patientName = patient.getName();
                        }
                        appointmentDate = appointment.getAppointmentTime()
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                    }
                } catch (SQLException e) {
                    System.err.println("Error loading search details: " + e.getMessage());
                }

                // Search filter
                if (!searchText.isEmpty()) {
                    boolean matches = patientName.toLowerCase().contains(searchText)
                            || bill.getId().toString().contains(searchText)
                            || (bill.getInsuranceDetails() != null
                            && bill.getInsuranceDetails().toLowerCase().contains(searchText));

                    if (!matches) {
                        continue;
                    }
                }

                Object[] row = {
                    bill.getId(),
                    patientName,
                    appointmentDate,
                    currencyFormat.format(bill.getTotalAmount()),
                    bill.getClaimStatus(),
                    bill.getInsuranceDetails()
                };
                billsTableModel.addRow(row);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error performing search: " + e.getMessage(),
                    "Search Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadSelectedBillDetails() {
        int selectedRow = billsTable.getSelectedRow();
        if (selectedRow == -1) {
            clearBillDetails();
            updateButtonStates(false);
            return;
        }

        Long billId = (Long) billsTable.getValueAt(selectedRow, 0);

        try {
            currentBill = billDAO.getBillById(billId);
            if (currentBill != null) {
                displayBillDetails(currentBill);
                loadBillItems(currentBill.getId());
                updateButtonStates(true);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading bill details: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayBillDetails(Bill bill) {
        billIdLabel.setText(bill.getId().toString());
        totalAmountLabel.setText(currencyFormat.format(bill.getTotalAmount()));
        claimStatusCombo.setSelectedItem(bill.getClaimStatus());
        insuranceDetailsArea.setText(bill.getInsuranceDetails() != null ? bill.getInsuranceDetails() : "");

        // Load patient and appointment info
        try {
            Appointment appointment = appointmentDAO.getAppointmentById(bill.getAppointmentId());
            if (appointment != null) {
                appointmentDateLabel.setText(appointment.getAppointmentTime()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

                Patient patient = patientDAO.getPatientById(appointment.getPatientId());
                if (patient != null) {
                    patientNameLabel.setText(patient.getName());
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading bill details: " + e.getMessage());
        }
    }

    private void loadBillItems(Long billId) {
        try {
            List<BillItem> items = billDAO.getBillItemsByBillId(billId);
            billItemsTableModel.setRowCount(0);

            for (BillItem item : items) {
                Object[] row = {
                    item.getItemType(),
                    item.getDescription(),
                    currencyFormat.format(item.getCost())
                };
                billItemsTableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading bill items: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearBillDetails() {
        billIdLabel.setText("N/A");
        patientNameLabel.setText("N/A");
        appointmentDateLabel.setText("N/A");
        totalAmountLabel.setText("$0.00");
        claimStatusCombo.setSelectedIndex(0);
        insuranceDetailsArea.setText("");
        billItemsTableModel.setRowCount(0);
        currentBill = null;
    }

    private void updateButtonStates(boolean hasBillSelected) {
        viewBillButton.setEnabled(hasBillSelected);
        editBillButton.setEnabled(hasBillSelected);
        deleteBillButton.setEnabled(hasBillSelected);
        generateReportButton.setEnabled(hasBillSelected);
        addItemButton.setEnabled(hasBillSelected);
        removeItemButton.setEnabled(hasBillSelected && billItemsTable.getSelectedRow() != -1);
        claimStatusCombo.setEnabled(hasBillSelected);
        insuranceDetailsArea.setEnabled(hasBillSelected);
    }

    private void viewBillDetails() {
        if (currentBill == null) {
            return;
        }

        JDialog detailDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Bill Details - ID: " + currentBill.getId(), true);
        detailDialog.setLayout(new BorderLayout());
        detailDialog.setSize(500, 400);
        detailDialog.setLocationRelativeTo(this);

        // Create detailed view
        JTextArea detailArea = new JTextArea();
        detailArea.setEditable(false);
        detailArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        StringBuilder details = new StringBuilder();
        details.append("GLOBEMED HEALTHCARE SYSTEM\n");
        details.append("============================\n\n");
        details.append("BILL DETAILS\n");
        details.append("Bill ID: ").append(currentBill.getId()).append("\n");
        details.append("Patient: ").append(patientNameLabel.getText()).append("\n");
        details.append("Appointment: ").append(appointmentDateLabel.getText()).append("\n");
        details.append("Claim Status: ").append(currentBill.getClaimStatus()).append("\n");
        details.append("Insurance: ").append(currentBill.getInsuranceDetails() != null
                ? currentBill.getInsuranceDetails() : "None").append("\n\n");

        details.append("ITEMIZED CHARGES\n");
        details.append("================\n");

        BigDecimal totalCost = BigDecimal.ZERO;
        for (BillItem item : currentBill.getBillItems()) {
            details.append(String.format("%-15s %-25s %10s\n",
                    item.getItemType(),
                    item.getDescription(),
                    currencyFormat.format(item.getCost())));
            totalCost = totalCost.add(item.getCost());
        }

        details.append("                                        ----------\n");
        details.append(String.format("%-40s %10s\n", "TOTAL AMOUNT:", currencyFormat.format(totalCost)));

        detailArea.setText(details.toString());

        JScrollPane scrollPane = new JScrollPane(detailArea);
        detailDialog.add(scrollPane, BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> detailDialog.dispose());
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(closeButton);
        detailDialog.add(buttonPanel, BorderLayout.SOUTH);

        detailDialog.setVisible(true);
    }

    private void editBill() {
        if (currentBill == null) {
            return;
        }

        int result = JOptionPane.showConfirmDialog(this,
                "Do you want to update this bill's insurance details and claim status?",
                "Edit Bill", JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            try {
                currentBill.setClaimStatus((String) claimStatusCombo.getSelectedItem());
                currentBill.setInsuranceDetails(insuranceDetailsArea.getText());

                if (billDAO.updateBill(currentBill)) {
                    JOptionPane.showMessageDialog(this, "Bill updated successfully!");
                    loadBills(); // Refresh the table
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update bill.",
                            "Update Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                        "Error updating bill: " + e.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteBill() {
        if (currentBill == null) {
            return;
        }

        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this bill?\nThis action cannot be undone.",
                "Delete Bill", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            try {
                // Note: In a real system, you might want to archive instead of delete
                JOptionPane.showMessageDialog(this,
                        "Bill deletion is restricted for audit purposes.\nPlease mark as 'REJECTED' instead.",
                        "Delete Restricted", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error deleting bill: " + e.getMessage(),
                        "Delete Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void generateBillReport() {
        if (currentBill == null) {
            return;
        }

        // Create a simple report dialog
        JDialog reportDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Bill Report", true);
        reportDialog.setSize(600, 500);
        reportDialog.setLocationRelativeTo(this);
        reportDialog.setLayout(new BorderLayout());

        JTextArea reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));

        StringBuilder report = new StringBuilder();
        report.append("GLOBEMED HEALTHCARE SYSTEM\n");
        report.append("BILLING REPORT\n");
        report.append("Generated: ").append(java.time.LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        report.append("=".repeat(50)).append("\n\n");

        report.append("BILL INFORMATION\n");
        report.append("Bill ID: ").append(currentBill.getId()).append("\n");
        report.append("Patient: ").append(patientNameLabel.getText()).append("\n");
        report.append("Appointment Date: ").append(appointmentDateLabel.getText()).append("\n");
        report.append("Claim Status: ").append(currentBill.getClaimStatus()).append("\n");
        report.append("Insurance Details: ").append(currentBill.getInsuranceDetails() != null
                ? currentBill.getInsuranceDetails() : "None").append("\n\n");

        report.append("DETAILED CHARGES\n");
        report.append(String.format("%-15s %-30s %10s\n", "TYPE", "DESCRIPTION", "AMOUNT"));
        report.append("-".repeat(50)).append("\n");

        BigDecimal total = BigDecimal.ZERO;
        for (BillItem item : currentBill.getBillItems()) {
            report.append(String.format("%-15s %-30s %10s\n",
                    item.getItemType(),
                    item.getDescription(),
                    currencyFormat.format(item.getCost())));
            total = total.add(item.getCost());
        }

        report.append("-".repeat(50)).append("\n");
        report.append(String.format("%-45s %10s\n", "TOTAL AMOUNT:", currencyFormat.format(total)));
        report.append("\n\nThis is an official billing statement from GlobeMed Healthcare System.");

        reportArea.setText(report.toString());

        JScrollPane scrollPane = new JScrollPane(reportArea);
        reportDialog.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton printButton = new JButton("Print");
        JButton closeButton = new JButton("Close");

        printButton.addActionListener(e -> {
            // In a real system, implement actual printing
            JOptionPane.showMessageDialog(reportDialog, "Print functionality would be implemented here.");
        });
        closeButton.addActionListener(e -> reportDialog.dispose());

        buttonPanel.add(printButton);
        buttonPanel.add(closeButton);
        reportDialog.add(buttonPanel, BorderLayout.SOUTH);

        reportDialog.setVisible(true);
    }

    private void showAddItemPanel() {
        addItemPanel.setVisible(true);
        descriptionField.setText("");
        costField.setText("");
        itemTypeCombo.setSelectedIndex(0);
        revalidate();
        repaint();
    }

    private void hideAddItemPanel() {
        addItemPanel.setVisible(false);
        revalidate();
        repaint();
    }

    private void saveNewItem() {
        if (currentBill == null) {
            return;
        }

        String description = descriptionField.getText().trim();
        String costText = costField.getText().trim();

        if (description.isEmpty() || costText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            BigDecimal cost = new BigDecimal(costText);
            if (cost.compareTo(BigDecimal.ZERO) < 0) {
                JOptionPane.showMessageDialog(this, "Cost cannot be negative.",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            BillItem newItem = new BillItem();
            newItem.setItemType((String) itemTypeCombo.getSelectedItem());
            newItem.setDescription(description);
            newItem.setCost(cost);

            Long itemId = billDAO.insertBillItem(currentBill.getId(), newItem);
            if (itemId != null) {
                // Reload the current bill to get updated total
                currentBill = billDAO.getBillById(currentBill.getId());
                displayBillDetails(currentBill);
                loadBillItems(currentBill.getId());
                hideAddItemPanel();

                // Update the main table
                int selectedRow = billsTable.getSelectedRow();
                if (selectedRow != -1) {
                    billsTableModel.setValueAt(currencyFormat.format(currentBill.getTotalAmount()),
                            selectedRow, 3);
                }

                JOptionPane.showMessageDialog(this, "Bill item added successfully!");
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid cost amount.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error saving bill item: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeSelectedItem() {
        int selectedRow = billItemsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to remove.",
                    "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to remove this item?",
                "Remove Item", JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            // In a real system, you'd implement removeItem in BillDAO
            JOptionPane.showMessageDialog(this, "Item removal functionality would be implemented here.");
            // For now, just refresh the view
            loadBillItems(currentBill.getId());
        }
    }

    private void updateClaimStatus() {
        if (currentBill == null) {
            return;
        }

        String newStatus = (String) claimStatusCombo.getSelectedItem();
        if (!newStatus.equals(currentBill.getClaimStatus())) {
            try {
                currentBill.setClaimStatus(newStatus);
                if (billDAO.updateBill(currentBill)) {
                    // Update the main table
                    int selectedRow = billsTable.getSelectedRow();
                    if (selectedRow != -1) {
                        billsTableModel.setValueAt(newStatus, selectedRow, 4);
                    }

                    System.out.println("Claim status updated to: " + newStatus);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update claim status.",
                            "Update Error", JOptionPane.ERROR_MESSAGE);
                    // Revert the combo box
                    claimStatusCombo.setSelectedItem(currentBill.getClaimStatus());
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                        "Error updating claim status: " + e.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
                // Revert the combo box
                claimStatusCombo.setSelectedItem(currentBill.getClaimStatus());
            }
        }
    }

    // Public methods for external access
    public void refreshData() {
        loadBills();
    }

    public void selectBillById(Long billId) {
        for (int i = 0; i < billsTable.getRowCount(); i++) {
            if (billsTable.getValueAt(i, 0).equals(billId)) {
                billsTable.setRowSelectionInterval(i, i);
                billsTable.scrollRectToVisible(billsTable.getCellRect(i, 0, true));
                break;
            }
        }
    }

    public Bill getCurrentBill() {
        return currentBill;
    }

    public boolean hasBillSelected() {
        return billsTable.getSelectedRow() != -1;
    }
}

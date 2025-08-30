package com.globemed.gui;

import com.globemed.services.BillingService;
import com.globemed.models.Bill;
import com.globemed.models.BillItem;
import com.globemed.patterns.composite.MasterBill;
import com.globemed.patterns.chainofresponsibility.ClaimResult;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.SwingWorker;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

public class BillingPanel extends JPanel {

    private BillingService billingService;
    private JTable billsTable;
    private JTable billItemsTable;
    private DefaultTableModel billsModel;
    private DefaultTableModel billItemsModel;
    private TableRowSorter<DefaultTableModel> billsSorter;

    // Form components
    private JTextField searchField;
    private JComboBox<String> statusFilter;
    private JSpinner fromDateSpinner;
    private JSpinner toDateSpinner;
    private JLabel totalRevenueLabel;

    // Bill creation components
    private JSpinner appointmentIdSpinner;
    private JTextField insuranceField;
    private JTextField itemDescriptionField;
    private JTextField itemCostField;
    private JComboBox<String> itemTypeCombo;

    // Claim processing components
    private JComboBox<String> claimTypeCombo;
    private JTextField insuranceProviderField;
    private JTextField policyNumberField;

    public BillingPanel(MainFrame mainFrame) {
        this.billingService = new BillingService();
        initializeComponents();
        layoutComponents();
        setupEventHandlers();
        loadBillsData();
        updateTotalRevenue();
    }

    private void initializeComponents() {
        // Initialize tables
        String[] billColumns = {"ID", "Appointment ID", "Total Amount", "Status", "Insurance", "Date Created"};
        billsModel = new DefaultTableModel(billColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        billsTable = new JTable(billsModel);
        billsSorter = new TableRowSorter<>(billsModel);
        billsTable.setRowSorter(billsSorter);
        billsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        String[] itemColumns = {"ID", "Type", "Description", "Cost"};
        billItemsModel = new DefaultTableModel(itemColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        billItemsTable = new JTable(billItemsModel);

        // Initialize search and filter components
        searchField = new JTextField(20);
        statusFilter = new JComboBox<>(new String[]{"All", "PENDING", "APPROVED", "REJECTED", "PAID", "CANCELLED"});
        fromDateSpinner = new JSpinner(new SpinnerDateModel());
        toDateSpinner = new JSpinner(new SpinnerDateModel());
        totalRevenueLabel = new JLabel("Total Revenue: $0.00");

        // Initialize bill creation components
        appointmentIdSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 999999, 1));
        insuranceField = new JTextField(20);
        itemDescriptionField = new JTextField(20);
        itemCostField = new JTextField(10);
        itemTypeCombo = new JComboBox<>(new String[]{"CONSULTATION", "TREATMENT", "MEDICATION", "DIAGNOSTIC", "TAX", "DISCOUNT", "LATE_FEE"});

        // Initialize claim processing components
        claimTypeCombo = new JComboBox<>(new String[]{"INSURANCE", "DIRECT_PAY", "PARTIAL_INSURANCE"});
        insuranceProviderField = new JTextField(15);
        policyNumberField = new JTextField(15);

        // Configure date spinners
        JSpinner.DateEditor fromDateEditor = new JSpinner.DateEditor(fromDateSpinner, "yyyy-MM-dd");
        JSpinner.DateEditor toDateEditor = new JSpinner.DateEditor(toDateSpinner, "yyyy-MM-dd");
        fromDateSpinner.setEditor(fromDateEditor);
        toDateSpinner.setEditor(toDateEditor);

        // Set some styling
        billsTable.setRowHeight(25);
        billItemsTable.setRowHeight(25);
        totalRevenueLabel.setFont(totalRevenueLabel.getFont().deriveFont(Font.BOLD, 16));
        totalRevenueLabel.setForeground(new Color(0, 120, 0));
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());

        // Create main tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();

        // Bills Management Tab
        JPanel billsManagementPanel = createBillsManagementPanel();
        tabbedPane.addTab("Bills Management", billsManagementPanel);

        // Create Bill Tab
        JPanel createBillPanel = createBillCreationPanel();
        tabbedPane.addTab("Create Bill", createBillPanel);

        // Process Claims Tab
        JPanel processClaimsPanel = createClaimProcessingPanel();
        tabbedPane.addTab("Process Claims", processClaimsPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createBillsManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Search and filter panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search & Filter"));

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(new JLabel("Status:"));
        searchPanel.add(statusFilter);
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(new JLabel("From:"));
        searchPanel.add(fromDateSpinner);
        searchPanel.add(new JLabel("To:"));
        searchPanel.add(toDateSpinner);

        JButton searchButton = new JButton("Search");
        JButton resetButton = new JButton("Reset");
        searchPanel.add(searchButton);
        searchPanel.add(resetButton);

        panel.add(searchPanel, BorderLayout.NORTH);

        // Main content panel
        JPanel mainContent = new JPanel(new GridLayout(2, 1));

        // Bills table panel
        JPanel billsTablePanel = new JPanel(new BorderLayout());
        billsTablePanel.setBorder(BorderFactory.createTitledBorder("Bills"));
        billsTablePanel.add(new JScrollPane(billsTable), BorderLayout.CENTER);

        // Bills action panel
        JPanel billsActionPanel = new JPanel(new FlowLayout());
        JButton viewBillButton = new JButton("View Details");
        JButton updateStatusButton = new JButton("Update Status");
        JButton deleteBillButton = new JButton("Delete Bill");
        JButton refreshButton = new JButton("Refresh");
        JButton refreshAllButton = new JButton("Refresh All Data");

        billsActionPanel.add(viewBillButton);
        billsActionPanel.add(updateStatusButton);
        billsActionPanel.add(deleteBillButton);
        billsActionPanel.add(refreshButton);
        billsActionPanel.add(refreshAllButton);
        billsTablePanel.add(billsActionPanel, BorderLayout.SOUTH);

        // Bill items table panel
        JPanel itemsTablePanel = new JPanel(new BorderLayout());
        itemsTablePanel.setBorder(BorderFactory.createTitledBorder("Bill Items"));
        itemsTablePanel.add(new JScrollPane(billItemsTable), BorderLayout.CENTER);

        mainContent.add(billsTablePanel);
        mainContent.add(itemsTablePanel);

        panel.add(mainContent, BorderLayout.CENTER);

        // Revenue panel
        JPanel revenuePanel = new JPanel(new FlowLayout());
        revenuePanel.add(totalRevenueLabel);
        panel.add(revenuePanel, BorderLayout.SOUTH);

        // Event handlers for bills management
        searchButton.addActionListener(e -> performSearch());
        resetButton.addActionListener(e -> resetFilters());
        viewBillButton.addActionListener(e -> viewBillDetails());
        updateStatusButton.addActionListener(e -> updateBillStatus());
        deleteBillButton.addActionListener(e -> deleteBill());
        refreshButton.addActionListener(e -> refreshBillsTable());
        refreshAllButton.addActionListener(e -> refreshAllBillData());

        billsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadBillItemsForSelectedBill();
            }
        });

        return panel;
    }

    private JPanel createBillCreationPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Bill creation form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Create New Bill"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Appointment ID
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Appointment ID:"), gbc);
        gbc.gridx = 1;
        formPanel.add(appointmentIdSpinner, gbc);

        // Insurance Details
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Insurance Details:"), gbc);
        gbc.gridx = 1;
        formPanel.add(insuranceField, gbc);

        // Buttons
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton createBillButton = new JButton("Create Bill");
        JButton clearFormButton = new JButton("Clear Form");
        buttonPanel.add(createBillButton);
        buttonPanel.add(clearFormButton);
        formPanel.add(buttonPanel, gbc);

        panel.add(formPanel, BorderLayout.NORTH);

        // Bill items management
        JPanel itemsPanel = new JPanel(new BorderLayout());
        itemsPanel.setBorder(BorderFactory.createTitledBorder("Add Bill Items"));

        JPanel itemFormPanel = new JPanel(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Item Type
        gbc.gridx = 0;
        gbc.gridy = 0;
        itemFormPanel.add(new JLabel("Type:"), gbc);
        gbc.gridx = 1;
        itemFormPanel.add(itemTypeCombo, gbc);

        // Item Description
        gbc.gridx = 0;
        gbc.gridy = 1;
        itemFormPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        itemFormPanel.add(itemDescriptionField, gbc);

        // Item Cost
        gbc.gridx = 0;
        gbc.gridy = 2;
        itemFormPanel.add(new JLabel("Cost:"), gbc);
        gbc.gridx = 1;
        itemFormPanel.add(itemCostField, gbc);

        // Add item button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JButton addItemButton = new JButton("Add Item to Selected Bill");
        itemFormPanel.add(addItemButton, gbc);

        itemsPanel.add(itemFormPanel, BorderLayout.CENTER);
        panel.add(itemsPanel, BorderLayout.CENTER);

        // Event handlers for bill creation
        createBillButton.addActionListener(e -> createNewBill());
        clearFormButton.addActionListener(e -> clearBillForm());
        addItemButton.addActionListener(e -> addItemToBill());

        return panel;
    }

    private JPanel createClaimProcessingPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Process Insurance Claims"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Claim Type
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Claim Type:"), gbc);
        gbc.gridx = 1;
        formPanel.add(claimTypeCombo, gbc);

        // Insurance Provider
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Insurance Provider:"), gbc);
        gbc.gridx = 1;
        formPanel.add(insuranceProviderField, gbc);

        // Policy Number
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Policy Number:"), gbc);
        gbc.gridx = 1;
        formPanel.add(policyNumberField, gbc);

        // Process buttons
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JPanel processButtonPanel = new JPanel(new FlowLayout());
        JButton processClaimButton = new JButton("Process Selected Bill Claim");
        JButton processComplexBillButton = new JButton("Process with Decorators");
        processButtonPanel.add(processClaimButton);
        processButtonPanel.add(processComplexBillButton);
        formPanel.add(processButtonPanel, gbc);

        panel.add(formPanel, BorderLayout.NORTH);

        // Results area
        JTextArea resultsArea = new JTextArea(15, 50);
        resultsArea.setEditable(false);
        resultsArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane resultsScroll = new JScrollPane(resultsArea);
        resultsScroll.setBorder(BorderFactory.createTitledBorder("Processing Results"));
        panel.add(resultsScroll, BorderLayout.CENTER);

        // Event handlers for claim processing
        processClaimButton.addActionListener(e -> processSelectedBillClaim(resultsArea));
        processComplexBillButton.addActionListener(e -> processComplexBill(resultsArea));

        return panel;
    }

    private void setupEventHandlers() {
        // Additional global event handlers can be added here
        statusFilter.addActionListener(e -> performSearch());
    }

    private void loadBillsData() {
        try {
            List<Bill> bills = billingService.getAllBills();
            billsModel.setRowCount(0);

            for (Bill bill : bills) {
                Object[] row = {
                    bill.getId(),
                    bill.getAppointmentId(),
                    "$" + bill.getTotalAmount().toString(),
                    bill.getClaimStatus(),
                    bill.getInsuranceDetails() != null ? bill.getInsuranceDetails() : "Direct Pay",
                    "N/A" // Would need created_at field in database
                };
                billsModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading bills: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadBillItemsForSelectedBill() {
        int selectedRow = billsTable.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                Long billId = (Long) billsModel.getValueAt(selectedRow, 0);
                Bill bill = billingService.getBillById(billId);

                billItemsModel.setRowCount(0);
                if (bill != null && bill.getBillItems() != null) {
                    for (BillItem item : bill.getBillItems()) {
                        Object[] row = {
                            item.getId(),
                            item.getItemType(),
                            item.getDescription(),
                            "$" + item.getCost().toString()
                        };
                        billItemsModel.addRow(row);
                    }
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                        "Error loading bill items: " + e.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void performSearch() {
        String searchText = searchField.getText().trim().toLowerCase();
        String selectedStatus = (String) statusFilter.getSelectedItem();

        if (searchText.isEmpty() && "All".equals(selectedStatus)) {
            billsSorter.setRowFilter(null);
        } else {
            billsSorter.setRowFilter(new RowFilter<DefaultTableModel, Object>() {
                @Override
                public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
                    boolean matchesSearch = searchText.isEmpty();
                    boolean matchesStatus = "All".equals(selectedStatus);

                    if (!searchText.isEmpty()) {
                        for (int i = 0; i < entry.getValueCount(); i++) {
                            if (entry.getStringValue(i).toLowerCase().contains(searchText)) {
                                matchesSearch = true;
                                break;
                            }
                        }
                    }

                    if (!"All".equals(selectedStatus)) {
                        matchesStatus = selectedStatus.equals(entry.getStringValue(3));
                    }

                    return matchesSearch && matchesStatus;
                }
            });
        }
    }

    private void resetFilters() {
        searchField.setText("");
        statusFilter.setSelectedIndex(0);
        fromDateSpinner.setValue(new java.util.Date());
        toDateSpinner.setValue(new java.util.Date());
        billsSorter.setRowFilter(null);
    }

    private void viewBillDetails() {
        int selectedRow = billsTable.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                Long billId = (Long) billsModel.getValueAt(selectedRow, 0);
                Bill bill = billingService.getBillById(billId);

                if (bill != null) {
                    StringBuilder details = new StringBuilder();
                    details.append("Bill ID: ").append(bill.getId()).append("\n");
                    details.append("Appointment ID: ").append(bill.getAppointmentId()).append("\n");
                    details.append("Total Amount: $").append(bill.getTotalAmount()).append("\n");
                    details.append("Status: ").append(bill.getClaimStatus()).append("\n");
                    details.append("Insurance: ").append(bill.getInsuranceDetails() != null ? bill.getInsuranceDetails() : "Direct Pay").append("\n\n");
                    details.append("Bill Items:\n");

                    for (BillItem item : bill.getBillItems()) {
                        details.append("- ").append(item.getDescription())
                                .append(" (").append(item.getItemType()).append("): $")
                                .append(item.getCost()).append("\n");
                    }

                    JTextArea textArea = new JTextArea(details.toString());
                    textArea.setEditable(false);
                    textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

                    JScrollPane scrollPane = new JScrollPane(textArea);
                    scrollPane.setPreferredSize(new Dimension(400, 300));

                    JOptionPane.showMessageDialog(this, scrollPane, "Bill Details", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                        "Error loading bill details: " + e.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a bill to view details.", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateBillStatus() {
        int selectedRow = billsTable.getSelectedRow();
        if (selectedRow >= 0) {
            String[] statuses = {"PENDING", "APPROVED", "REJECTED", "PAID", "CANCELLED"};
            String newStatus = (String) JOptionPane.showInputDialog(
                    this,
                    "Select new status:",
                    "Update Bill Status",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    statuses,
                    statuses[0]
            );

            if (newStatus != null) {
                try {
                    Long billId = (Long) billsModel.getValueAt(selectedRow, 0);
                    boolean success = billingService.updateBillStatus(billId, newStatus);

                    if (success) {
                        billsModel.setValueAt(newStatus, selectedRow, 3);
                        JOptionPane.showMessageDialog(this, "Bill status updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        updateTotalRevenue();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to update bill status.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this,
                            "Error updating bill status: " + e.getMessage(),
                            "Database Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a bill to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteBill() {
        int selectedRow = billsTable.getSelectedRow();
        if (selectedRow >= 0) {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete this bill? This action cannot be undone.",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    Long billId = (Long) billsModel.getValueAt(selectedRow, 0);
                    billingService.deleteBill(billId);

                    billsModel.removeRow(selectedRow);
                    billItemsModel.setRowCount(0);
                    updateTotalRevenue();

                    JOptionPane.showMessageDialog(this, "Bill deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this,
                            "Error deleting bill: " + e.getMessage(),
                            "Database Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a bill to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void createNewBill() {
        try {
            Long appointmentId = ((Number) appointmentIdSpinner.getValue()).longValue();
            String insurance = insuranceField.getText().trim();

            if (insurance.isEmpty()) {
                insurance = null; // For direct pay
            }

            MasterBill masterBill = billingService.createMasterBill(appointmentId, insurance);

            // Add the new bill to the table
            Object[] row = {
                masterBill.getBillId(),
                masterBill.getAppointmentId(),
                "$" + masterBill.getCost().toString(),
                masterBill.getStatus(),
                masterBill.getInsuranceDetails() != null ? masterBill.getInsuranceDetails() : "Direct Pay",
                "Just Created"
            };
            billsModel.addRow(row);

            updateTotalRevenue();
            clearBillForm();

            JOptionPane.showMessageDialog(this,
                    "Bill created successfully!\nBill ID: " + masterBill.getBillId(),
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error creating bill: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Invalid input: " + e.getMessage(),
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearBillForm() {
        appointmentIdSpinner.setValue(1);
        insuranceField.setText("");
        itemDescriptionField.setText("");
        itemCostField.setText("");
        itemTypeCombo.setSelectedIndex(0);
    }

    private void addItemToBill() {
        int selectedRow = billsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a bill first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String description = itemDescriptionField.getText().trim();
        String costText = itemCostField.getText().trim();

        if (description.isEmpty() || costText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both description and cost.", "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Long billId = (Long) billsModel.getValueAt(selectedRow, 0);
            String itemType = (String) itemTypeCombo.getSelectedItem();
            BigDecimal cost = new BigDecimal(costText);

            billingService.addBillItem(billId, itemType, description, cost);

            // Refresh the bill items display
            loadBillItemsForSelectedBill();

            // Update the total in the bills table
            Bill updatedBill = billingService.getBillById(billId);
            billsModel.setValueAt("$" + updatedBill.getTotalAmount().toString(), selectedRow, 2);

            // Clear form
            itemDescriptionField.setText("");
            itemCostField.setText("");

            updateTotalRevenue();

            JOptionPane.showMessageDialog(this, "Item added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid cost amount.", "Invalid Cost", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error adding bill item: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void processSelectedBillClaim(JTextArea resultsArea) {
        int selectedRow = billsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a bill first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String claimType = (String) claimTypeCombo.getSelectedItem();
        String provider = insuranceProviderField.getText().trim();
        String policyNumber = policyNumberField.getText().trim();

        if ("INSURANCE".equals(claimType) || "PARTIAL_INSURANCE".equals(claimType)) {
            if (provider.isEmpty() || policyNumber.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please enter insurance provider and policy number for insurance claims.",
                        "Missing Information",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        try {
            Long billId = (Long) billsModel.getValueAt(selectedRow, 0);
            Bill bill = billingService.getBillById(billId);
            MasterBill masterBill = billingService.convertFromDbBill(bill);

            ClaimResult result = billingService.processClaim(masterBill, claimType, provider, policyNumber);

            // Display results
            StringBuilder output = new StringBuilder();
            output.append("=== CLAIM PROCESSING RESULT ===\n");
            output.append("Bill ID: ").append(billId).append("\n");
            output.append("Claim Type: ").append(claimType).append("\n");
            output.append("Insurance Provider: ").append(provider.isEmpty() ? "N/A" : provider).append("\n");
            output.append("Policy Number: ").append(policyNumber.isEmpty() ? "N/A" : policyNumber).append("\n");
            output.append("Status: ").append(result.isApproved() ? "APPROVED" : "REJECTED").append("\n");
            output.append("Message: ").append(result.getMessage()).append("\n");

            if (result.isApproved()) {
                output.append("Approved Amount: $").append(result.getApprovedAmount()).append("\n");
                if (result.getPatientResponsibility().compareTo(BigDecimal.ZERO) > 0) {
                    output.append("Patient Responsibility: $").append(result.getPatientResponsibility()).append("\n");
                }
            }

            if (result.getProcessingNotes() != null) {
                output.append("Notes: ").append(result.getProcessingNotes()).append("\n");
            }

            output.append("\n");
            resultsArea.append(output.toString());
            resultsArea.setCaretPosition(resultsArea.getDocument().getLength());

            // Update bill status if approved
            if (result.isApproved()) {
                billingService.updateBillStatus(billId, "APPROVED");
                billsModel.setValueAt("APPROVED", selectedRow, 3);
            }

        } catch (SQLException e) {
            resultsArea.append("ERROR: " + e.getMessage() + "\n\n");
            JOptionPane.showMessageDialog(this,
                    "Error processing claim: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void processComplexBill(JTextArea resultsArea) {
        int selectedRow = billsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a bill first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Ask user which decorators to apply
        JCheckBox discountCheck = new JCheckBox("Apply Senior Citizen Discount (10%)");
        JCheckBox taxCheck = new JCheckBox("Apply State Tax (8.25%)");
        JCheckBox lateFeeCheck = new JCheckBox("Check for Late Fees");

        JPanel checkPanel = new JPanel(new GridLayout(3, 1));
        checkPanel.add(discountCheck);
        checkPanel.add(taxCheck);
        checkPanel.add(lateFeeCheck);

        int option = JOptionPane.showConfirmDialog(
                this,
                checkPanel,
                "Select Bill Processing Options",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (option == JOptionPane.OK_OPTION) {
            try {
                Long billId = (Long) billsModel.getValueAt(selectedRow, 0);
                Bill bill = billingService.getBillById(billId);
                MasterBill masterBill = billingService.convertFromDbBill(bill);

                // Store original total for comparison
                BigDecimal originalTotal = masterBill.getCost();

                // Process with decorators
                MasterBill processedBill = billingService.processComplexBill(
                        masterBill,
                        discountCheck.isSelected(),
                        taxCheck.isSelected(),
                        lateFeeCheck.isSelected()
                );

                // Display processing results
                StringBuilder output = new StringBuilder();
                output.append("=== COMPLEX BILL PROCESSING ===\n");
                output.append("Bill ID: ").append(billId).append("\n");
                output.append("Original Total: $").append(originalTotal).append("\n");
                output.append("Processed Total: $").append(processedBill.getCost()).append("\n");
                output.append("Difference: $").append(processedBill.getCost().subtract(originalTotal)).append("\n");
                output.append("\nApplied Decorators:\n");

                if (discountCheck.isSelected()) {
                    output.append("- Senior Citizen Discount (10%)\n");
                }
                if (taxCheck.isSelected()) {
                    output.append("- State Tax (8.25%)\n");
                }
                if (lateFeeCheck.isSelected()) {
                    output.append("- Late Fee Check\n");
                }

                output.append("\nBill Structure:\n");
                processedBill.print("");

                output.append("\n");
                resultsArea.append(output.toString());
                resultsArea.setCaretPosition(resultsArea.getDocument().getLength());

                // Update the displayed bill amount
                billsModel.setValueAt("$" + processedBill.getCost().toString(), selectedRow, 2);

                // Refresh bill items display
                loadBillItemsForSelectedBill();
                updateTotalRevenue();

            } catch (SQLException e) {
                resultsArea.append("ERROR: " + e.getMessage() + "\n\n");
                JOptionPane.showMessageDialog(this,
                        "Error processing complex bill: " + e.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateTotalRevenue() {
        try {
            BigDecimal totalRevenue = billingService.calculateTotalRevenue();
            totalRevenueLabel.setText("Total Revenue (Paid Bills): $" + totalRevenue.toString());
        } catch (SQLException e) {
            totalRevenueLabel.setText("Total Revenue: Error calculating");
        }
    }

    /**
     * Refresh the entire bills table from the database
     */
    private void refreshBillsTable() {
        try {
            loadBillsData();
            updateTotalRevenue();
            
            // Clear bill items table if no bill is selected
            int selectedRow = billsTable.getSelectedRow();
            if (selectedRow < 0) {
                billItemsModel.setRowCount(0);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error refreshing bills table: " + e.getMessage(),
                    "Refresh Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Refresh all bill data from the database and recalculate totals
     */
    private void refreshAllBillData() {
        try {
            // Show progress dialog
            JDialog progressDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Refreshing Data", true);
            progressDialog.setLayout(new BorderLayout());
            progressDialog.add(new JLabel("Refreshing all bill data from database..."), BorderLayout.CENTER);
            progressDialog.setSize(300, 100);
            progressDialog.setLocationRelativeTo(this);
            
            // Run the refresh operation in a background thread
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    billingService.refreshAllBillData();
                    return null;
                }
                
                @Override
                protected void done() {
                    progressDialog.dispose();
                    try {
                        get(); // Check for exceptions
                        refreshBillsTable();
                        JOptionPane.showMessageDialog(BillingPanel.this,
                                "All bill data refreshed successfully!",
                                "Refresh Complete",
                                JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(BillingPanel.this,
                                "Error refreshing bill data: " + e.getMessage(),
                                "Refresh Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            
            worker.execute();
            progressDialog.setVisible(true);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error refreshing all bill data: " + e.getMessage(),
                    "Refresh Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.gui;

import com.globemed.database.*;
import com.globemed.models.*;
import com.globemed.patterns.composite.*;
import com.globemed.patterns.chainofresponsibility.*;
import com.globemed.patterns.decorator.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/**
 * Billing Management Panel implementing Composite, Chain of Responsibility, and
 * Decorator patterns
 *
 * @author Hansana
 */
public class BillingPanel extends JPanel {

    private MainFrame parentFrame;
    private BillDAO billDAO;
    private AppointmentDAO appointmentDAO;
    private PatientDAO patientDAO;

    // GUI Components
    private JTable billTable;
    private DefaultTableModel billTableModel;
    private JTable billItemTable;
    private DefaultTableModel billItemTableModel;

    private JComboBox<String> appointmentComboBox;
    private JTextField insuranceField;
    private JComboBox<String> claimStatusComboBox;
    private JTextField totalAmountField;

    // Bill item form
    private JComboBox<String> itemTypeComboBox;
    private JTextField descriptionField;
    private JTextField costField;

    private JButton createBillButton, addItemButton, removeItemButton;
    private JButton processClaim, applyDiscountButton, calculateTotalButton;
    private JButton refreshButton;

    private JTextArea claimProcessingArea;

    // Current bill being edited
    private Long currentBillId = null;
    private BillComposite currentBillComposite = null;

    // Table columns
    private final String[] billColumns = {"ID", "Appointment", "Patient", "Total", "Status", "Insurance"};
    private final String[] itemColumns = {"ID", "Type", "Description", "Cost"};

    // Item types
    private final String[] itemTypes = {"CONSULTATION", "TREATMENT", "MEDICATION", "DIAGNOSTIC", "SURGERY", "THERAPY"};
    private final String[] claimStatuses = {"PENDING", "APPROVED", "REJECTED", "PAID", "PROCESSING"};

    public BillingPanel(MainFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.billDAO = new BillDAO();
        this.appointmentDAO = new AppointmentDAO();
        this.patientDAO = new PatientDAO();

        initializeComponents();
        layoutComponents();
        setupEventHandlers();
    }

    private void initializeComponents() {
        // Bills table
        billTableModel = new DefaultTableModel(billColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        billTable = new JTable(billTableModel);
        billTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        billTable.setRowHeight(25);

        // Bill items table
        billItemTableModel = new DefaultTableModel(itemColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        billItemTable = new JTable(billItemTableModel);
        billItemTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        billItemTable.setRowHeight(25);

        // Form components
        appointmentComboBox = new JComboBox<>();
        insuranceField = new JTextField(20);
        claimStatusComboBox = new JComboBox<>(claimStatuses);
        totalAmountField = new JTextField(10);
        totalAmountField.setEditable(false);

        // Bill item form
        itemTypeComboBox = new JComboBox<>(itemTypes);
        descriptionField = new JTextField(20);
        costField = new JTextField(10);

        // Buttons
        createBillButton = new JButton("Create New Bill");
        addItemButton = new JButton("Add Item");
        removeItemButton = new JButton("Remove Item");
        processClaim = new JButton("Process Insurance Claim");
        applyDiscountButton = new JButton("Apply Discount");
        calculateTotalButton = new JButton("Calculate Total");
        refreshButton = new JButton("Refresh");

        // Initially disable some buttons
        removeItemButton.setEnabled(false);
        processClaim.setEnabled(false);

        // Claim processing area
        claimProcessingArea = new JTextArea(6, 40);
        claimProcessingArea.setEditable(false);
        claimProcessingArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        claimProcessingArea.setBackground(new Color(250, 250, 250));

        loadAppointmentComboBox();
    }

    private void loadAppointmentComboBox() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    List<Appointment> appointments = appointmentDAO.getAllAppointments();
                    SwingUtilities.invokeLater(() -> {
                        appointmentComboBox.removeAllItems();
                        appointmentComboBox.addItem("Select Appointment");
                        for (Appointment apt : appointments) {
                            if ("COMPLETED".equals(apt.getStatus())) {
                                appointmentComboBox.addItem(apt.getId() + " - Patient ID: " + apt.getPatientId()
                                        + " (" + apt.getAppointmentTime().toLocalDate() + ")");
                            }
                        }
                    });
                } catch (SQLException e) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(BillingPanel.this,
                                "Error loading appointments: " + e.getMessage(),
                                "Database Error", JOptionPane.ERROR_MESSAGE);
                    });
                }
                return null;
            }
        };
        worker.execute();
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Billing & Insurance Claims Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton patternsDemo = new JButton("Show Patterns Demo");
        patternsDemo.addActionListener(e -> demonstrateBillingPatterns());
        headerPanel.add(patternsDemo, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Main content - split into three sections
        JPanel mainContent = new JPanel(new BorderLayout());

        // Top: Bills table
        JPanel billsPanel = new JPanel(new BorderLayout());
        billsPanel.setBorder(BorderFactory.createTitledBorder("Bills Overview"));
        billsPanel.setPreferredSize(new Dimension(0, 250));

        JScrollPane billsScroll = new JScrollPane(billTable);
        billsPanel.add(billsScroll, BorderLayout.CENTER);

        JPanel billsButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        billsButtons.add(refreshButton);
        billsButtons.add(processClaim);
        billsPanel.add(billsButtons, BorderLayout.SOUTH);

        mainContent.add(billsPanel, BorderLayout.NORTH);

        // Center: Bill creation and item management
        JSplitPane centerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        centerSplit.setDividerLocation(400);

        // Left: Bill form
        JPanel billFormPanel = createBillForm();
        centerSplit.setLeftComponent(billFormPanel);

        // Right: Bill items
        JPanel itemsPanel = createBillItemsPanel();
        centerSplit.setRightComponent(itemsPanel);

        mainContent.add(centerSplit, BorderLayout.CENTER);

        // Bottom: Claim processing
        JPanel claimPanel = createClaimProcessingPanel();
        mainContent.add(claimPanel, BorderLayout.SOUTH);

        add(mainContent, BorderLayout.CENTER);
    }

    private JPanel createBillForm() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Bill Information"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Appointment
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Appointment:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(appointmentComboBox, gbc);

        // Insurance details
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Insurance Details:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(insuranceField, gbc);

        // Claim status
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Claim Status:"), gbc);
        gbc.gridx = 1;
        formPanel.add(claimStatusComboBox, gbc);

        // Total amount
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Total Amount:"), gbc);
        gbc.gridx = 1;
        formPanel.add(totalAmountField, gbc);

        gbc.gridx = 2;
        formPanel.add(calculateTotalButton, gbc);

        // Buttons
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(createBillButton);

        JButton clearBillForm = new JButton("Clear");
        clearBillForm.addActionListener(e -> clearBillForm());
        buttonPanel.add(clearBillForm);

        formPanel.add(buttonPanel, gbc);

        return formPanel;
    }

    private JPanel createBillItemsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Bill Items"));

        // Items table
        JScrollPane itemsScroll = new JScrollPane(billItemTable);
        itemsScroll.setPreferredSize(new Dimension(400, 200));
        panel.add(itemsScroll, BorderLayout.CENTER);

        // Item form
        JPanel itemFormPanel = new JPanel(new GridBagLayout());
        itemFormPanel.setBorder(BorderFactory.createTitledBorder("Add Item"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        itemFormPanel.add(new JLabel("Type:"), gbc);
        gbc.gridx = 1;
        itemFormPanel.add(itemTypeComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        itemFormPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        itemFormPanel.add(descriptionField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        itemFormPanel.add(new JLabel("Cost:"), gbc);
        gbc.gridx = 1;
        itemFormPanel.add(costField, gbc);

        gbc.gridx = 2;
        itemFormPanel.add(addItemButton, gbc);

        panel.add(itemFormPanel, BorderLayout.SOUTH);

        // Item management buttons
        JPanel itemButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        itemButtons.add(removeItemButton);
        itemButtons.add(applyDiscountButton);
        panel.add(itemButtons, BorderLayout.NORTH);

        return panel;
    }

    private JPanel createClaimProcessingPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Insurance Claim Processing"));
        panel.setPreferredSize(new Dimension(0, 150));

        JScrollPane claimScroll = new JScrollPane(claimProcessingArea);
        panel.add(claimScroll, BorderLayout.CENTER);

        JPanel claimButtons = new JPanel(new FlowLayout());
        JButton processClaimBtn = new JButton("Process Selected Claim");
        JButton clearLogBtn = new JButton("Clear Log");
        JButton exportLogBtn = new JButton("Export Log");

        processClaimBtn.addActionListener(e -> processInsuranceClaim());
        clearLogBtn.addActionListener(e -> claimProcessingArea.setText(""));
        exportLogBtn.addActionListener(e -> exportClaimLog());

        claimButtons.add(processClaimBtn);
        claimButtons.add(clearLogBtn);
        claimButtons.add(exportLogBtn);

        panel.add(claimButtons, BorderLayout.SOUTH);

        return panel;
    }

    private void setupEventHandlers() {
        // Table selections
        billTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = billTable.getSelectedRow() != -1;
                processClaim.setEnabled(hasSelection);

                if (hasSelection) {
                    loadSelectedBillItems();
                }
            }
        });

        billItemTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                removeItemButton.setEnabled(billItemTable.getSelectedRow() != -1);
            }
        });

        // Button listeners
        createBillButton.addActionListener(e -> createNewBill());
        addItemButton.addActionListener(e -> addBillItem());
        removeItemButton.addActionListener(e -> removeBillItem());
        calculateTotalButton.addActionListener(e -> calculateTotal());
        applyDiscountButton.addActionListener(e -> applyDiscount());
        refreshButton.addActionListener(e -> refreshData());
    }

    private void createNewBill() {
        if (!validateBillForm()) {
            return;
        }

        try {
            Long appointmentId = extractIdFromComboBox(appointmentComboBox);
            if (appointmentId == null) {
                JOptionPane.showMessageDialog(this, "Please select an appointment!",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Bill bill = new Bill();
            bill.setAppointmentId(appointmentId);
            bill.setInsuranceDetails(insuranceField.getText().trim());
            bill.setClaimStatus("PENDING");
            bill.setTotalAmount(BigDecimal.ZERO);

            SwingWorker<Long, Void> worker = new SwingWorker<Long, Void>() {
                @Override
                protected Long doInBackground() throws Exception {
                    return billDAO.insertBill(bill);
                }

                @Override
                protected void done() {
                    try {
                        Long billId = get();
                        if (billId != null) {
                            currentBillId = billId;
                            currentBillComposite = new BillComposite(
                        
                    
                
            
        
    
"Bill

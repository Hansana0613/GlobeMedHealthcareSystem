package com.globemed.gui;

import com.globemed.services.BillingService;
import com.globemed.models.Bill;
import com.globemed.models.BillItem;
import com.globemed.patterns.composite.MasterBill;
import com.globemed.patterns.chainofresponsibility.ClaimResult;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

public class BillingPanel extends JPanel {
    
    // Medical UI Color Scheme
    private static final Color PRIMARY_BLUE = new Color(46, 134, 171);
    private static final Color SECONDARY_WHITE = Color.WHITE;
    private static final Color ACCENT_GREEN = new Color(76, 175, 80);
    private static final Color WARNING_AMBER = new Color(255, 152, 0);
    private static final Color ERROR_RED = new Color(244, 67, 54);
    private static final Color BACKGROUND_GRAY = new Color(245, 245, 245);
    private static final Color TEXT_DARK = new Color(33, 37, 41);
    private static final Color BORDER_LIGHT = new Color(220, 220, 220);
    
    // Fonts
    private static final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 16);
    private static final Font BODY_FONT = new Font("SansSerif", Font.PLAIN, 12);
    private static final Font LABEL_FONT = new Font("SansSerif", Font.BOLD, 11);
    private static final Font DATA_FONT = new Font("SansSerif", Font.PLAIN, 11);

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

    // Appointment search components
    private JTextField appointmentSearchField;
    private JButton searchAppointmentButton;
    private JTable appointmentSearchResultsTable;
    private DefaultTableModel appointmentSearchModel;
    private JLabel selectedAppointmentLabel;
    private Long selectedAppointmentId;

    // Claim processing components
    private JComboBox<String> claimTypeCombo;
    private JTextField insuranceProviderField;
    private JTextField policyNumberField;

    public BillingPanel(MainFrame mainFrame) {
        this.billingService = new BillingService();
        setBackground(BACKGROUND_GRAY);
        initializeComponents();
        layoutComponents();
        setupEventHandlers();
        loadBillsData();
        updateTotalRevenue();
    }

    private void initializeComponents() {
        // Initialize tables with enhanced styling
        String[] billColumns = {"Bill ID", "Appointment", "Total Amount", "Status", "Insurance", "Created"};
        billsModel = new DefaultTableModel(billColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        billsTable = createStyledTable(billsModel);
        billsSorter = new TableRowSorter<>(billsModel);
        billsTable.setRowSorter(billsSorter);

        String[] itemColumns = {"Item ID", "Type", "Description", "Cost"};
        billItemsModel = new DefaultTableModel(itemColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        billItemsTable = createStyledTable(billItemsModel);

        // Initialize search and filter components
        searchField = createStyledTextField(20, "Search bills...");
        statusFilter = createStyledComboBox(new String[]{"All Statuses", "PENDING", "APPROVED", "REJECTED", "PAID", "CANCELLED"});
        
        fromDateSpinner = createStyledDateSpinner();
        toDateSpinner = createStyledDateSpinner();
        
        totalRevenueLabel = new JLabel("Total Revenue: $0.00");
        totalRevenueLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        totalRevenueLabel.setForeground(ACCENT_GREEN);

        // Initialize bill creation components
        appointmentIdSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 999999, 1));
        styleSpinner(appointmentIdSpinner);
        
        insuranceField = createStyledTextField(20, "Insurance details (optional)");
        itemDescriptionField = createStyledTextField(20, "Item description");
        itemCostField = createStyledTextField(10, "0.00");
        itemTypeCombo = createStyledComboBox(new String[]{"CONSULTATION", "TREATMENT", "MEDICATION", "DIAGNOSTIC", "TAX", "DISCOUNT", "LATE_FEE"});

        // Initialize appointment search components
        appointmentSearchField = createStyledTextField(20, "Search by appointment ID");
        searchAppointmentButton = createStyledButton("Search", PRIMARY_BLUE);
        
        appointmentSearchResultsTable = createStyledTable(null);
        appointmentSearchModel = new DefaultTableModel(new String[]{"ID", "Patient Name", "Date", "Time", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        appointmentSearchResultsTable.setModel(appointmentSearchModel);
        
        selectedAppointmentLabel = new JLabel("No appointment selected");
        selectedAppointmentLabel.setFont(DATA_FONT);
        selectedAppointmentLabel.setForeground(TEXT_DARK);
        selectedAppointmentId = null;

        // Initialize claim processing components
        claimTypeCombo = createStyledComboBox(new String[]{"INSURANCE", "DIRECT_PAY", "PARTIAL_INSURANCE"});
        insuranceProviderField = createStyledTextField(15, "Provider name");
        policyNumberField = createStyledTextField(15, "Policy number");
    }

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(32);
        table.setFont(DATA_FONT);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(new Color(PRIMARY_BLUE.getRed(), PRIMARY_BLUE.getGreen(), PRIMARY_BLUE.getBlue(), 40));
        table.setSelectionForeground(TEXT_DARK);
        table.setGridColor(BORDER_LIGHT);
        table.setBackground(SECONDARY_WHITE);
        table.setIntercellSpacing(new Dimension(1, 1));
        
        // Style header
        table.getTableHeader().setFont(LABEL_FONT);
        table.getTableHeader().setBackground(PRIMARY_BLUE);
        table.getTableHeader().setForeground(SECONDARY_WHITE);
        table.getTableHeader().setBorder(new LineBorder(PRIMARY_BLUE));
        table.getTableHeader().setPreferredSize(new Dimension(0, 36));
        
        // Custom cell renderer for status column
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected && column == 3 && value != null) { // Status column
                    String status = value.toString();
                    switch (status) {
                        case "APPROVED":
                        case "PAID":
                            c.setBackground(new Color(ACCENT_GREEN.getRed(), ACCENT_GREEN.getGreen(), ACCENT_GREEN.getBlue(), 40));
                            break;
                        case "REJECTED":
                        case "CANCELLED":
                            c.setBackground(new Color(ERROR_RED.getRed(), ERROR_RED.getGreen(), ERROR_RED.getBlue(), 40));
                            break;
                        case "PENDING":
                            c.setBackground(new Color(WARNING_AMBER.getRed(), WARNING_AMBER.getGreen(), WARNING_AMBER.getBlue(), 40));
                            break;
                        default:
                            c.setBackground(SECONDARY_WHITE);
                    }
                } else if (!isSelected) {
                    c.setBackground(SECONDARY_WHITE);
                }
                
                return c;
            }
        });
        
        return table;
    }

    private JTextField createStyledTextField(int columns, String placeholder) {
        JTextField field = new JTextField(columns);
        field.setFont(DATA_FONT);
        field.setBorder(new CompoundBorder(
            new LineBorder(BORDER_LIGHT, 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        field.setBackground(SECONDARY_WHITE);
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 36));
        
        // Add placeholder text effect
        field.setForeground(Color.GRAY);
        field.setText(placeholder);
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(TEXT_DARK);
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (field.getText().isEmpty()) {
                    field.setForeground(Color.GRAY);
                    field.setText(placeholder);
                }
            }
        });
        
        return field;
    }

    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(DATA_FONT);
        combo.setBackground(SECONDARY_WHITE);
        combo.setBorder(new LineBorder(BORDER_LIGHT, 1));
        combo.setPreferredSize(new Dimension(combo.getPreferredSize().width, 36));
        return combo;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(LABEL_FONT);
        button.setBackground(bgColor);
        button.setForeground(SECONDARY_WHITE);
        button.setBorder(new EmptyBorder(8, 16, 8, 16));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(button.getPreferredSize().width, 36));
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }

    private JSpinner createStyledDateSpinner() {
        JSpinner spinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "yyyy-MM-dd");
        spinner.setEditor(editor);
        styleSpinner(spinner);
        return spinner;
    }

    private void styleSpinner(JSpinner spinner) {
        spinner.setFont(DATA_FONT);
        spinner.setBorder(new LineBorder(BORDER_LIGHT, 1));
        spinner.setPreferredSize(new Dimension(spinner.getPreferredSize().width, 36));
        
        // Style the editor component
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            ((JSpinner.DefaultEditor) editor).getTextField().setBorder(new EmptyBorder(8, 12, 8, 12));
        }
    }

    private JPanel createStyledPanel(String title) {
        JPanel panel = new JPanel();
        panel.setBackground(SECONDARY_WHITE);
        panel.setBorder(new CompoundBorder(
            new LineBorder(BORDER_LIGHT, 1),
            new EmptyBorder(16, 16, 16, 16)
        ));
        
        if (title != null) {
            panel.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(BORDER_LIGHT, 1), 
                title, 
                0, 
                0, 
                HEADER_FONT, 
                PRIMARY_BLUE
            ));
        }
        
        return panel;
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(0, 8));
        setBorder(new EmptyBorder(16, 16, 16, 16));

        // Create header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_GRAY);
        
        JLabel titleLabel = new JLabel("Billing Management");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_BLUE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        add(headerPanel, BorderLayout.NORTH);

        // Create main tabbed pane with enhanced styling
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(LABEL_FONT);
        tabbedPane.setBackground(BACKGROUND_GRAY);
        tabbedPane.setBorder(new EmptyBorder(8, 0, 0, 0));

        // Bills Management Tab
        JPanel billsManagementPanel = createBillsManagementPanel();
        tabbedPane.addTab("📋 Bills Overview", billsManagementPanel);

        // Create Bill Tab
        JPanel createBillPanel = createBillCreationPanel();
        tabbedPane.addTab("➕ Create Bill", createBillPanel);

        // Process Claims Tab
        JPanel processClaimsPanel = createClaimProcessingPanel();
        tabbedPane.addTab("🔄 Process Claims", processClaimsPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createBillsManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(BACKGROUND_GRAY);
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));

        // Search and filter panel
        JPanel searchPanel = createStyledPanel("Search & Filter");
        searchPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(LABEL_FONT);
        searchPanel.add(searchLabel, gbc);
        
        gbc.gridx = 1;
        searchPanel.add(searchField, gbc);

        gbc.gridx = 2;
        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setFont(LABEL_FONT);
        searchPanel.add(statusLabel, gbc);
        
        gbc.gridx = 3;
        searchPanel.add(statusFilter, gbc);

        gbc.gridx = 4;
        JLabel fromLabel = new JLabel("From:");
        fromLabel.setFont(LABEL_FONT);
        searchPanel.add(fromLabel, gbc);
        
        gbc.gridx = 5;
        searchPanel.add(fromDateSpinner, gbc);

        gbc.gridx = 6;
        JLabel toLabel = new JLabel("To:");
        toLabel.setFont(LABEL_FONT);
        searchPanel.add(toLabel, gbc);
        
        gbc.gridx = 7;
        searchPanel.add(toDateSpinner, gbc);

        gbc.gridx = 8;
        JButton searchButton = createStyledButton("Search", PRIMARY_BLUE);
        searchPanel.add(searchButton, gbc);
        
        gbc.gridx = 9;
        JButton resetButton = createStyledButton("Reset", Color.GRAY);
        searchPanel.add(resetButton, gbc);

        panel.add(searchPanel, BorderLayout.NORTH);

        // Main content panel
        JPanel mainContent = new JPanel(new GridLayout(2, 1, 0, 8));
        mainContent.setBackground(BACKGROUND_GRAY);

        // Bills table panel
        JPanel billsTablePanel = createStyledPanel("Bills");
        billsTablePanel.setLayout(new BorderLayout(0, 8));
        
        JScrollPane billsScrollPane = new JScrollPane(billsTable);
        billsScrollPane.setBorder(new LineBorder(BORDER_LIGHT, 1));
        billsScrollPane.getViewport().setBackground(SECONDARY_WHITE);
        billsTablePanel.add(billsScrollPane, BorderLayout.CENTER);

        // Bills action panel
        JPanel billsActionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        billsActionPanel.setBackground(SECONDARY_WHITE);
        
        JButton viewBillButton = createStyledButton("View Details", PRIMARY_BLUE);
        JButton updateStatusButton = createStyledButton("Update Status", WARNING_AMBER);
        JButton deleteBillButton = createStyledButton("Delete", ERROR_RED);
        JButton refreshButton = createStyledButton("Refresh", Color.GRAY);

        billsActionPanel.add(viewBillButton);
        billsActionPanel.add(updateStatusButton);
        billsActionPanel.add(deleteBillButton);
        billsActionPanel.add(refreshButton);
        
        billsTablePanel.add(billsActionPanel, BorderLayout.SOUTH);

        // Bill items table panel
        JPanel itemsTablePanel = createStyledPanel("Bill Items");
        itemsTablePanel.setLayout(new BorderLayout());
        
        JScrollPane itemsScrollPane = new JScrollPane(billItemsTable);
        itemsScrollPane.setBorder(new LineBorder(BORDER_LIGHT, 1));
        itemsScrollPane.getViewport().setBackground(SECONDARY_WHITE);
        itemsTablePanel.add(itemsScrollPane, BorderLayout.CENTER);

        mainContent.add(billsTablePanel);
        mainContent.add(itemsTablePanel);

        panel.add(mainContent, BorderLayout.CENTER);

        // Revenue panel
        JPanel revenuePanel = createStyledPanel(null);
        revenuePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        revenuePanel.add(totalRevenueLabel);
        panel.add(revenuePanel, BorderLayout.SOUTH);

        // Event handlers for bills management
        searchButton.addActionListener(e -> performSearch());
        resetButton.addActionListener(e -> resetFilters());
        viewBillButton.addActionListener(e -> viewBillDetails());
        updateStatusButton.addActionListener(e -> updateBillStatus());
        deleteBillButton.addActionListener(e -> deleteBill());
        refreshButton.addActionListener(e -> refreshBillsTable());

        billsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadBillItemsForSelectedBill();
            }
        });

        // Add mouse shortcuts for the bills table
        billsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showContextMenu(e);
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showContextMenu(e);
                }
            }
        });

        return panel;
    }

    private void showContextMenu(MouseEvent e) {
        int row = billsTable.rowAtPoint(e.getPoint());
        if (row >= 0) {
            billsTable.setRowSelectionInterval(row, row);
            
            JPopupMenu contextMenu = new JPopupMenu();
            contextMenu.setBorder(new LineBorder(BORDER_LIGHT, 1));
            
            JMenuItem addBillItemsItem = new JMenuItem("➕ Add Bill Items");
            addBillItemsItem.setFont(DATA_FONT);
            addBillItemsItem.addActionListener(evt -> {
                JTabbedPane tabbedPane = (JTabbedPane) SwingUtilities.getAncestorOfClass(JTabbedPane.class, billsTable);
                if (tabbedPane != null) {
                    tabbedPane.setSelectedIndex(1);
                }
            });
            
            JMenuItem processClaimItem = new JMenuItem("🔄 Process Claims");
            processClaimItem.setFont(DATA_FONT);
            processClaimItem.addActionListener(evt -> {
                JTabbedPane tabbedPane = (JTabbedPane) SwingUtilities.getAncestorOfClass(JTabbedPane.class, billsTable);
                if (tabbedPane != null) {
                    tabbedPane.setSelectedIndex(2);
                }
            });
            
            JMenuItem viewDetailsItem = new JMenuItem("👁 View Details");
            viewDetailsItem.setFont(DATA_FONT);
            viewDetailsItem.addActionListener(evt -> viewBillDetails());
            
            JMenuItem updateStatusItem = new JMenuItem("📝 Update Status");
            updateStatusItem.setFont(DATA_FONT);
            updateStatusItem.addActionListener(evt -> updateBillStatus());
            
            JMenuItem deleteItem = new JMenuItem("🗑 Delete Bill");
            deleteItem.setFont(DATA_FONT);
            deleteItem.setForeground(ERROR_RED);
            deleteItem.addActionListener(evt -> deleteBill());
            
            contextMenu.add(addBillItemsItem);
            contextMenu.add(processClaimItem);
            contextMenu.addSeparator();
            contextMenu.add(viewDetailsItem);
            contextMenu.add(updateStatusItem);
            contextMenu.addSeparator();
            contextMenu.add(deleteItem);
            
            contextMenu.show(billsTable, e.getX(), e.getY());
        }
    }

    private JPanel createBillCreationPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(BACKGROUND_GRAY);
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));

        // Bill creation form
        JPanel formPanel = createStyledPanel("Create New Bill");
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Appointment ID
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel appointmentLabel = new JLabel("Appointment ID:");
        appointmentLabel.setFont(LABEL_FONT);
        formPanel.add(appointmentLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(appointmentIdSpinner, gbc);

        // Insurance Details
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel insuranceLabel = new JLabel("Insurance Details:");
        insuranceLabel.setFont(LABEL_FONT);
        formPanel.add(insuranceLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(insuranceField, gbc);

        // Appointment Search
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel searchLabel = new JLabel("Search Appointment:");
        searchLabel.setFont(LABEL_FONT);
        formPanel.add(searchLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(appointmentSearchField, gbc);
        gbc.gridx = 2;
        formPanel.add(searchAppointmentButton, gbc);

        // Selected Appointment Info
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 3;
        JPanel selectedPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectedPanel.setBackground(SECONDARY_WHITE);
        selectedPanel.setBorder(new CompoundBorder(
            new LineBorder(ACCENT_GREEN, 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        selectedPanel.add(selectedAppointmentLabel);
        formPanel.add(selectedPanel, gbc);

        // Buttons
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 3;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(SECONDARY_WHITE);
        
        JButton createBillButton = createStyledButton("Create Bill", ACCENT_GREEN);
        JButton clearFormButton = createStyledButton("Clear Form", Color.GRAY);
        
        buttonPanel.add(createBillButton);
        buttonPanel.add(clearFormButton);
        formPanel.add(buttonPanel, gbc);

        panel.add(formPanel, BorderLayout.NORTH);

        // Appointment search results panel
        JPanel searchResultsPanel = createStyledPanel("Appointment Search Results");
        searchResultsPanel.setLayout(new BorderLayout(0, 8));
        
        JScrollPane searchResultsScroll = new JScrollPane(appointmentSearchResultsTable);
        searchResultsScroll.setBorder(new LineBorder(BORDER_LIGHT, 1));
        searchResultsScroll.getViewport().setBackground(SECONDARY_WHITE);
        searchResultsScroll.setPreferredSize(new Dimension(500, 200));
        searchResultsPanel.add(searchResultsScroll, BorderLayout.CENTER);
        
        panel.add(searchResultsPanel, BorderLayout.CENTER);

        // Bill items management
        JPanel itemsPanel = createStyledPanel("Add Bill Items");
        itemsPanel.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        // Item Type
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel typeLabel = new JLabel("Type:");
        typeLabel.setFont(LABEL_FONT);
        itemsPanel.add(typeLabel, gbc);
        gbc.gridx = 1;
        itemsPanel.add(itemTypeCombo, gbc);

        // Item Description
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel descLabel = new JLabel("Description:");
        descLabel.setFont(LABEL_FONT);
        itemsPanel.add(descLabel, gbc);
        gbc.gridx = 1;
        itemsPanel.add(itemDescriptionField, gbc);

        // Item Cost
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel costLabel = new JLabel("Cost:");
        costLabel.setFont(LABEL_FONT);
        itemsPanel.add(costLabel, gbc);
        gbc.gridx = 1;
        itemsPanel.add(itemCostField, gbc);

        // Add item button
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        JButton addItemButton = createStyledButton("Add Item to Selected Bill", PRIMARY_BLUE);
        itemsPanel.add(addItemButton, gbc);

        panel.add(itemsPanel, BorderLayout.SOUTH);

        // Event handlers for bill creation
        createBillButton.addActionListener(e -> createNewBill());
        clearFormButton.addActionListener(e -> clearBillForm());
        addItemButton.addActionListener(e -> addItemToBill());

        // Event handlers for appointment search
        searchAppointmentButton.addActionListener(e -> searchAppointments());
        appointmentSearchField.addActionListener(e -> searchAppointments());
        
        // Selection handler for appointment table
        appointmentSearchResultsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectAppointment();
            }
        });

        return panel;
    }

    private JPanel createClaimProcessingPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(BACKGROUND_GRAY);
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel formPanel = createStyledPanel("Process Insurance Claims");
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Claim Type
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel claimLabel = new JLabel("Claim Type:");
        claimLabel.setFont(LABEL_FONT);
        formPanel.add(claimLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(claimTypeCombo, gbc);

        // Insurance Provider
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel providerLabel = new JLabel("Insurance Provider:");
        providerLabel.setFont(LABEL_FONT);
        formPanel.add(providerLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(insuranceProviderField, gbc);

        // Policy Number
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel policyLabel = new JLabel("Policy Number:");
        policyLabel.setFont(LABEL_FONT);
        formPanel.add(policyLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(policyNumberField, gbc);

        // Process buttons
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        JPanel processButtonPanel = new JPanel(new FlowLayout());
        processButtonPanel.setBackground(SECONDARY_WHITE);
        
        JButton processClaimButton = createStyledButton("Process Selected Bill", ACCENT_GREEN);
        JButton processComplexBillButton = createStyledButton("Process with Decorators", PRIMARY_BLUE);
        
        processButtonPanel.add(processClaimButton);
        processButtonPanel.add(processComplexBillButton);
        formPanel.add(processButtonPanel, gbc);

        panel.add(formPanel, BorderLayout.NORTH);

        // Results area
        JTextArea resultsArea = new JTextArea(15, 50);
        resultsArea.setEditable(false);
        resultsArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        resultsArea.setBackground(SECONDARY_WHITE);
        resultsArea.setBorder(new EmptyBorder(12, 12, 12, 12));
        
        JScrollPane resultsScroll = new JScrollPane(resultsArea);
        resultsScroll.setBorder(new CompoundBorder(
            BorderFactory.createTitledBorder(
                new LineBorder(BORDER_LIGHT, 1), 
                "Processing Results", 
                0, 0, HEADER_FONT, PRIMARY_BLUE
            ),
            new EmptyBorder(8, 8, 8, 8)
        ));
        resultsScroll.getViewport().setBackground(SECONDARY_WHITE);
        
        panel.add(resultsScroll, BorderLayout.CENTER);

        // Event handlers for claim processing
        processClaimButton.addActionListener(e -> processSelectedBillClaim(resultsArea));
        processComplexBillButton.addActionListener(e -> processComplexBill(resultsArea));

        return panel;
    }

    private void setupEventHandlers() {
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
                    "N/A"
                };
                billsModel.addRow(row);
            }
        } catch (SQLException e) {
            showErrorDialog("Error loading bills: " + e.getMessage());
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
                showErrorDialog("Error loading bill items: " + e.getMessage());
            }
        }
    }

    private void performSearch() {
        final String searchText = searchField.getText().trim().toLowerCase();
        final String finalSearchText = searchText.equals("search bills...") ? "" : searchText;
        
        final String selectedStatus = (String) statusFilter.getSelectedItem();
        final String finalSelectedStatus = "All Statuses".equals(selectedStatus) ? "All" : selectedStatus;

        if (finalSearchText.isEmpty() && "All".equals(finalSelectedStatus)) {
            billsSorter.setRowFilter(null);
        } else {
            billsSorter.setRowFilter(new RowFilter<DefaultTableModel, Object>() {
                @Override
                public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
                    boolean matchesSearch = finalSearchText.isEmpty();
                    boolean matchesStatus = "All".equals(finalSelectedStatus);

                    if (!finalSearchText.isEmpty()) {
                        for (int i = 0; i < entry.getValueCount(); i++) {
                            if (entry.getStringValue(i).toLowerCase().contains(finalSearchText)) {
                                matchesSearch = true;
                                break;
                            }
                        }
                    }

                    if (!"All".equals(finalSelectedStatus)) {
                        matchesStatus = finalSelectedStatus.equals(entry.getStringValue(3));
                    }

                    return matchesSearch && matchesStatus;
                }
            });
        }
    }

    private void resetFilters() {
        searchField.setText("Search bills...");
        searchField.setForeground(Color.GRAY);
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
                    showBillDetailsDialog(bill);
                }
            } catch (SQLException e) {
                showErrorDialog("Error loading bill details: " + e.getMessage());
            }
        } else {
            showWarningDialog("Please select a bill to view details.");
        }
    }

    private void showBillDetailsDialog(Bill bill) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Bill Details", true);
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(BACKGROUND_GRAY);

        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setBorder(new EmptyBorder(20, 20, 20, 20));
        content.setBackground(BACKGROUND_GRAY);

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_BLUE);
        headerPanel.setBorder(new EmptyBorder(16, 20, 16, 20));
        
        JLabel titleLabel = new JLabel("Bill #" + bill.getId());
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(SECONDARY_WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JLabel statusLabel = new JLabel(bill.getClaimStatus());
        statusLabel.setFont(LABEL_FONT);
        statusLabel.setForeground(SECONDARY_WHITE);
        headerPanel.add(statusLabel, BorderLayout.EAST);

        content.add(headerPanel, BorderLayout.NORTH);

        // Details panel
        JPanel detailsPanel = createStyledPanel(null);
        detailsPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        addDetailRow(detailsPanel, gbc, 0, "Appointment ID:", String.valueOf(bill.getAppointmentId()));
        addDetailRow(detailsPanel, gbc, 1, "Total Amount:", "$" + bill.getTotalAmount().toString());
        addDetailRow(detailsPanel, gbc, 2, "Insurance:", bill.getInsuranceDetails() != null ? bill.getInsuranceDetails() : "Direct Pay");

        // Items section
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; gbc.weighty = 1.0;

        JPanel itemsPanel = new JPanel(new BorderLayout());
        itemsPanel.setBackground(SECONDARY_WHITE);
        itemsPanel.setBorder(new CompoundBorder(
            new LineBorder(BORDER_LIGHT, 1),
            new EmptyBorder(8, 8, 8, 8)
        ));

        JLabel itemsLabel = new JLabel("Bill Items");
        itemsLabel.setFont(HEADER_FONT);
        itemsLabel.setForeground(PRIMARY_BLUE);
        itemsPanel.add(itemsLabel, BorderLayout.NORTH);

        StringBuilder itemsText = new StringBuilder();
        if (bill.getBillItems() != null) {
            for (BillItem item : bill.getBillItems()) {
                itemsText.append("• ").append(item.getDescription())
                        .append(" (").append(item.getItemType()).append("): $")
                        .append(item.getCost()).append("\n");
            }
        }

        JTextArea itemsArea = new JTextArea(itemsText.toString());
        itemsArea.setEditable(false);
        itemsArea.setFont(DATA_FONT);
        itemsArea.setBackground(SECONDARY_WHITE);
        itemsArea.setBorder(new EmptyBorder(8, 8, 8, 8));

        JScrollPane itemsScroll = new JScrollPane(itemsArea);
        itemsScroll.setBorder(null);
        itemsPanel.add(itemsScroll, BorderLayout.CENTER);

        detailsPanel.add(itemsPanel, gbc);
        content.add(detailsPanel, BorderLayout.CENTER);

        // Close button
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(BACKGROUND_GRAY);
        JButton closeButton = createStyledButton("Close", Color.GRAY);
        closeButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeButton);

        content.add(buttonPanel, BorderLayout.SOUTH);
        dialog.add(content);
        dialog.setVisible(true);
    }

    private void addDetailRow(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0; gbc.weighty = 0;

        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(LABEL_FONT);
        labelComponent.setForeground(TEXT_DARK);
        panel.add(labelComponent, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JLabel valueComponent = new JLabel(value != null ? value : "");
        valueComponent.setFont(DATA_FONT);
        valueComponent.setForeground(TEXT_DARK);
        panel.add(valueComponent, gbc);
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
                        showSuccessDialog("Bill status updated successfully!");
                        updateTotalRevenue();
                    } else {
                        showErrorDialog("Failed to update bill status.");
                    }
                } catch (SQLException e) {
                    showErrorDialog("Error updating bill status: " + e.getMessage());
                }
            }
        } else {
            showWarningDialog("Please select a bill to update.");
        }
    }

    private void deleteBill() {
        int selectedRow = billsTable.getSelectedRow();
        if (selectedRow >= 0) {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete this bill?\nThis action cannot be undone.",
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

                    showSuccessDialog("Bill deleted successfully!");
                } catch (SQLException e) {
                    showErrorDialog("Error deleting bill: " + e.getMessage());
                }
            }
        } else {
            showWarningDialog("Please select a bill to delete.");
        }
    }

    private void createNewBill() {
        try {
            Long appointmentId;
            
            if (selectedAppointmentId != null) {
                appointmentId = selectedAppointmentId;
            } else {
                appointmentId = ((Number) appointmentIdSpinner.getValue()).longValue();
            }
            
            String insurance = insuranceField.getText().trim();
            if (insurance.equals("Insurance details (optional)") || insurance.isEmpty()) {
                insurance = null;
            }

            MasterBill masterBill = billingService.createMasterBill(appointmentId, insurance);

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

            showSuccessDialog("Bill created successfully!\nBill ID: " + masterBill.getBillId());

        } catch (SQLException e) {
            showErrorDialog("Error creating bill: " + e.getMessage());
        } catch (Exception e) {
            showErrorDialog("Invalid input: " + e.getMessage());
        }
    }

    private void clearBillForm() {
        appointmentIdSpinner.setValue(1);
        resetTextFieldPlaceholder(insuranceField, "Insurance details (optional)");
        resetTextFieldPlaceholder(itemDescriptionField, "Item description");
        resetTextFieldPlaceholder(itemCostField, "0.00");
        itemTypeCombo.setSelectedIndex(0);
        
        resetTextFieldPlaceholder(appointmentSearchField, "Search by appointment ID");
        appointmentSearchModel.setRowCount(0);
        selectedAppointmentLabel.setText("No appointment selected");
        selectedAppointmentId = null;
    }

    private void resetTextFieldPlaceholder(JTextField field, String placeholder) {
        field.setText(placeholder);
        field.setForeground(Color.GRAY);
    }

    private void addItemToBill() {
        int selectedRow = billsTable.getSelectedRow();
        if (selectedRow < 0) {
            showWarningDialog("Please select a bill first.");
            return;
        }

        String description = itemDescriptionField.getText().trim();
        String costText = itemCostField.getText().trim();

        if (description.equals("Item description") || description.isEmpty() || 
            costText.equals("0.00") || costText.isEmpty()) {
            showWarningDialog("Please enter both description and cost.");
            return;
        }

        try {
            Long billId = (Long) billsModel.getValueAt(selectedRow, 0);
            String itemType = (String) itemTypeCombo.getSelectedItem();
            BigDecimal cost = new BigDecimal(costText);

            billingService.addBillItem(billId, itemType, description, cost);

            loadBillItemsForSelectedBill();
            Bill updatedBill = billingService.getBillById(billId);
            billsModel.setValueAt("$" + updatedBill.getTotalAmount().toString(), selectedRow, 2);

            resetTextFieldPlaceholder(itemDescriptionField, "Item description");
            resetTextFieldPlaceholder(itemCostField, "0.00");

            updateTotalRevenue();
            showSuccessDialog("Item added successfully!");

        } catch (NumberFormatException e) {
            showErrorDialog("Please enter a valid cost amount.");
        } catch (SQLException e) {
            showErrorDialog("Error adding bill item: " + e.getMessage());
        }
    }

    private void processSelectedBillClaim(JTextArea resultsArea) {
        int selectedRow = billsTable.getSelectedRow();
        if (selectedRow < 0) {
            showWarningDialog("Please select a bill first.");
            return;
        }

        String claimType = (String) claimTypeCombo.getSelectedItem();
        String provider = insuranceProviderField.getText().trim();
        String policyNumber = policyNumberField.getText().trim();

        if (provider.equals("Provider name")) provider = "";
        if (policyNumber.equals("Policy number")) policyNumber = "";

        if ("INSURANCE".equals(claimType) || "PARTIAL_INSURANCE".equals(claimType)) {
            if (provider.isEmpty() || policyNumber.isEmpty()) {
                showWarningDialog("Please enter insurance provider and policy number for insurance claims.");
                return;
            }
        }

        try {
            Long billId = (Long) billsModel.getValueAt(selectedRow, 0);
            Bill bill = billingService.getBillById(billId);
            MasterBill masterBill = billingService.convertFromDbBill(bill);

            ClaimResult result = billingService.processClaim(masterBill, claimType, provider, policyNumber);

            // Display results with enhanced formatting
            StringBuilder output = new StringBuilder();
            output.append("╔══════════════════════════════════════╗\n");
            output.append("║          CLAIM PROCESSING RESULT     ║\n");
            output.append("╚══════════════════════════════════════╝\n\n");
            output.append("📄 Bill ID: ").append(billId).append("\n");
            output.append("🏥 Claim Type: ").append(claimType).append("\n");
            output.append("🏢 Insurance Provider: ").append(provider.isEmpty() ? "N/A" : provider).append("\n");
            output.append("📋 Policy Number: ").append(policyNumber.isEmpty() ? "N/A" : policyNumber).append("\n");
            output.append("📊 Status: ").append(result.isApproved() ? "✅ APPROVED" : "❌ REJECTED").append("\n");
            output.append("💬 Message: ").append(result.getMessage()).append("\n");

            if (result.isApproved()) {
                output.append("💰 Approved Amount: $").append(result.getApprovedAmount()).append("\n");
                if (result.getPatientResponsibility().compareTo(BigDecimal.ZERO) > 0) {
                    output.append("👤 Patient Responsibility: $").append(result.getPatientResponsibility()).append("\n");
                }
            }

            if (result.getProcessingNotes() != null) {
                output.append("📝 Notes: ").append(result.getProcessingNotes()).append("\n");
            }

            output.append("\n" + "=".repeat(50) + "\n\n");
            resultsArea.append(output.toString());
            resultsArea.setCaretPosition(resultsArea.getDocument().getLength());

            if (result.isApproved()) {
                billingService.updateBillStatus(billId, "APPROVED");
                billsModel.setValueAt("APPROVED", selectedRow, 3);
            }

        } catch (SQLException e) {
            resultsArea.append("❌ ERROR: " + e.getMessage() + "\n\n");
            showErrorDialog("Error processing claim: " + e.getMessage());
        }
    }

    private void processComplexBill(JTextArea resultsArea) {
        int selectedRow = billsTable.getSelectedRow();
        if (selectedRow < 0) {
            showWarningDialog("Please select a bill first.");
            return;
        }

        // Enhanced decorator selection dialog
        JDialog decoratorDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Bill Processing Options", true);
        decoratorDialog.setSize(400, 300);
        decoratorDialog.setLocationRelativeTo(this);
        decoratorDialog.getContentPane().setBackground(BACKGROUND_GRAY);

        JPanel dialogContent = new JPanel(new BorderLayout(0, 16));
        dialogContent.setBorder(new EmptyBorder(20, 20, 20, 20));
        dialogContent.setBackground(BACKGROUND_GRAY);

        JLabel dialogTitle = new JLabel("Select Processing Options");
        dialogTitle.setFont(HEADER_FONT);
        dialogTitle.setForeground(PRIMARY_BLUE);
        dialogContent.add(dialogTitle, BorderLayout.NORTH);

        JPanel checkPanel = createStyledPanel(null);
        checkPanel.setLayout(new GridLayout(3, 1, 0, 8));

        JCheckBox discountCheck = new JCheckBox("Apply Senior Citizen Discount (10%)");
        JCheckBox taxCheck = new JCheckBox("Apply State Tax (8.25%)");
        JCheckBox lateFeeCheck = new JCheckBox("Check for Late Fees");

        styleCheckBox(discountCheck);
        styleCheckBox(taxCheck);
        styleCheckBox(lateFeeCheck);

        checkPanel.add(discountCheck);
        checkPanel.add(taxCheck);
        checkPanel.add(lateFeeCheck);

        dialogContent.add(checkPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(BACKGROUND_GRAY);
        
        JButton processButton = createStyledButton("Process", ACCENT_GREEN);
        JButton cancelButton = createStyledButton("Cancel", Color.GRAY);
        
        buttonPanel.add(processButton);
        buttonPanel.add(cancelButton);
        dialogContent.add(buttonPanel, BorderLayout.SOUTH);

        decoratorDialog.add(dialogContent);

        processButton.addActionListener(e -> {
            decoratorDialog.dispose();
            executeComplexBillProcessing(selectedRow, discountCheck.isSelected(), 
                    taxCheck.isSelected(), lateFeeCheck.isSelected(), resultsArea);
        });
        
        cancelButton.addActionListener(e -> decoratorDialog.dispose());
        
        decoratorDialog.setVisible(true);
    }

    private void styleCheckBox(JCheckBox checkBox) {
        checkBox.setFont(DATA_FONT);
        checkBox.setBackground(SECONDARY_WHITE);
        checkBox.setForeground(TEXT_DARK);
        checkBox.setBorder(new EmptyBorder(8, 8, 8, 8));
    }

    private void executeComplexBillProcessing(int selectedRow, boolean discount, boolean tax, boolean lateFee, JTextArea resultsArea) {
        try {
            Long billId = (Long) billsModel.getValueAt(selectedRow, 0);
            Bill bill = billingService.getBillById(billId);
            MasterBill masterBill = billingService.convertFromDbBill(bill);

            BigDecimal originalTotal = masterBill.getCost();

            MasterBill processedBill = billingService.processComplexBill(masterBill, discount, tax, lateFee);

            // Enhanced results display
            StringBuilder output = new StringBuilder();
            output.append("╔══════════════════════════════════════╗\n");
            output.append("║       COMPLEX BILL PROCESSING        ║\n");
            output.append("╚══════════════════════════════════════╝\n\n");
            output.append("📄 Bill ID: ").append(billId).append("\n");
            output.append("💰 Original Total: $").append(originalTotal).append("\n");
            output.append("💯 Processed Total: $").append(processedBill.getCost()).append("\n");
            
            BigDecimal difference = processedBill.getCost().subtract(originalTotal);
            String diffIcon = difference.compareTo(BigDecimal.ZERO) >= 0 ? "📈" : "📉";
            output.append(diffIcon).append(" Difference: $").append(difference).append("\n\n");
            
            output.append("🔧 Applied Decorators:\n");
            if (discount) output.append("  • Senior Citizen Discount (10%)\n");
            if (tax) output.append("  • State Tax (8.25%)\n");
            if (lateFee) output.append("  • Late Fee Check\n");

            output.append("\n📊 Bill Structure:\n");
            processedBill.print("");

            output.append("\n" + "=".repeat(50) + "\n\n");
            resultsArea.append(output.toString());
            resultsArea.setCaretPosition(resultsArea.getDocument().getLength());

            billsModel.setValueAt("$" + processedBill.getCost().toString(), selectedRow, 2);
            loadBillItemsForSelectedBill();
            updateTotalRevenue();

        } catch (SQLException e) {
            resultsArea.append("❌ ERROR: " + e.getMessage() + "\n\n");
            showErrorDialog("Error processing complex bill: " + e.getMessage());
        }
    }

    private void updateTotalRevenue() {
        try {
            BigDecimal totalRevenue = billingService.calculateTotalRevenue();
            totalRevenueLabel.setText("💰 Total Revenue (Paid Bills): $" + totalRevenue.toString());
        } catch (SQLException e) {
            totalRevenueLabel.setText("💰 Total Revenue: Error calculating");
        }
    }

    private void refreshBillsTable() {
        try {
            loadBillsData();
            updateTotalRevenue();
            
            int selectedRow = billsTable.getSelectedRow();
            if (selectedRow < 0) {
                billItemsModel.setRowCount(0);
            }
        } catch (Exception e) {
            showErrorDialog("Error refreshing bills table: " + e.getMessage());
        }
    }

    private void searchAppointments() {
        String searchText = appointmentSearchField.getText().trim();
        
        if (searchText.equals("Search by appointment ID") || searchText.isEmpty()) {
            showWarningDialog("Please enter an appointment ID to search.");
            return;
        }
        
        try {
            appointmentSearchModel.setRowCount(0);
            
            try {
                Long appointmentId = Long.parseLong(searchText);
                com.globemed.database.AppointmentDAO appointmentDAO = new com.globemed.database.AppointmentDAO();
                com.globemed.database.PatientDAO patientDAO = new com.globemed.database.PatientDAO();
                
                com.globemed.models.Appointment appointment = appointmentDAO.getAppointmentById(appointmentId);
                
                if (appointment != null) {
                    com.globemed.models.Patient patient = patientDAO.getPatientById(appointment.getPatientId());
                    String patientName = patient != null ? patient.getName() : "Unknown Patient";
                    
                    Object[] row = {
                        appointment.getId(),
                        patientName,
                        appointment.getAppointmentTime().toLocalDate().toString(),
                        appointment.getAppointmentTime().toLocalTime().toString(),
                        appointment.getStatus()
                    };
                    appointmentSearchModel.addRow(row);
                } else {
                    showInfoDialog("No appointment found with ID: " + appointmentId);
                }
                
            } catch (NumberFormatException e) {
                showWarningDialog("Please enter a valid appointment ID (number).");
            }
            
        } catch (SQLException e) {
            showErrorDialog("Error searching appointments: " + e.getMessage());
        }
    }

    private void selectAppointment() {
        int selectedRow = appointmentSearchResultsTable.getSelectedRow();
        if (selectedRow >= 0) {
            Long appointmentId = (Long) appointmentSearchModel.getValueAt(selectedRow, 0);
            String patientName = (String) appointmentSearchModel.getValueAt(selectedRow, 1);
            String date = (String) appointmentSearchModel.getValueAt(selectedRow, 2);
            String time = (String) appointmentSearchModel.getValueAt(selectedRow, 3);
            String status = (String) appointmentSearchModel.getValueAt(selectedRow, 4);
            
            selectedAppointmentId = appointmentId;
            appointmentIdSpinner.setValue(appointmentId.intValue());
            selectedAppointmentLabel.setText("✅ ID: " + appointmentId + " | " + patientName + " | " + date + " " + time);
        }
    }

    // Enhanced dialog methods
    private void showSuccessDialog(String message) {
        JOptionPane optionPane = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE);
        JDialog dialog = optionPane.createDialog(this, "Success");
        dialog.getContentPane().setBackground(BACKGROUND_GRAY);
        dialog.setVisible(true);
    }

    private void showErrorDialog(String message) {
        JOptionPane optionPane = new JOptionPane(message, JOptionPane.ERROR_MESSAGE);
        JDialog dialog = optionPane.createDialog(this, "Error");
        dialog.getContentPane().setBackground(BACKGROUND_GRAY);
        dialog.setVisible(true);
    }

    private void showWarningDialog(String message) {
        JOptionPane optionPane = new JOptionPane(message, JOptionPane.WARNING_MESSAGE);
        JDialog dialog = optionPane.createDialog(this, "Warning");
        dialog.getContentPane().setBackground(BACKGROUND_GRAY);
        dialog.setVisible(true);
    }

    private void showInfoDialog(String message) {
        JOptionPane optionPane = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE);
        JDialog dialog = optionPane.createDialog(this, "Information");
        dialog.getContentPane().setBackground(BACKGROUND_GRAY);
        dialog.setVisible(true);
    }
}
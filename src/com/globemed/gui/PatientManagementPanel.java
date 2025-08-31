package com.globemed.gui;

import com.globemed.models.Patient;
import com.globemed.services.SecurePatientService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Enhanced Patient Management Panel with modern medical UI design
 * Following medical application color scheme and typography guidelines
 *
 * @author Hansana
 */
public class PatientManagementPanel extends JPanel {

    // Medical UI Color Scheme
    private static final Color PRIMARY_BLUE = new Color(46, 134, 171);
    private static final Color SECONDARY_WHITE = Color.WHITE;
    private static final Color ACCENT_GREEN = new Color(76, 175, 80);
    private static final Color WARNING_AMBER = new Color(255, 152, 0);
    private static final Color ERROR_RED = new Color(244, 67, 54);
    private static final Color BACKGROUND_GRAY = new Color(245, 245, 245);
    private static final Color TEXT_DARK = new Color(33, 37, 41);
    private static final Color BORDER_LIGHT = new Color(220, 220, 220);
    private static final Color INFO_BLUE = new Color(23, 162, 184);
    
    // Fonts
    private static final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 16);
    private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 24);
    private static final Font BODY_FONT = new Font("SansSerif", Font.PLAIN, 12);
    private static final Font LABEL_FONT = new Font("SansSerif", Font.BOLD, 11);
    private static final Font DATA_FONT = new Font("SansSerif", Font.PLAIN, 11);

    private MainFrame parentFrame;
    private SecurePatientService securePatientService;

    // GUI Components
    private JTable patientTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField searchField;
    private JButton addButton, editButton, deleteButton, viewButton, refreshButton;
    private JPanel formPanel;
    private JTextField nameField, dobField, addressField, phoneField;
    private JTextArea medicalHistoryArea;
    private boolean isEditing = false;
    private Long editingPatientId = null;

    // Table columns
    private final String[] columnNames = {"ID", "Patient Name", "Date of Birth", "Phone", "Address"};

    public PatientManagementPanel(MainFrame parentFrame) {
        this.parentFrame = parentFrame;
        setBackground(BACKGROUND_GRAY);
        initializeComponents();
        layoutComponents();
        setupEventHandlers();
    }

    private void initializeSecureService() {
        if (parentFrame.getCurrentUser() != null) {
            securePatientService = new SecurePatientService(parentFrame.getCurrentUser());
        }
    }

    private void initializeComponents() {
        // Enhanced table setup
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        patientTable = createStyledTable();
        patientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Setup table sorter
        sorter = new TableRowSorter<>(tableModel);
        patientTable.setRowSorter(sorter);

        // Enhanced search field
        searchField = createStyledTextField(25, "Search patients by name, phone, or address...");

        // Enhanced buttons
        addButton = createStyledButton("➕ Add Patient", ACCENT_GREEN);
        editButton = createStyledButton("✏️ Edit Patient", PRIMARY_BLUE);
        deleteButton = createStyledButton("🗑️ Delete Patient", ERROR_RED);
        viewButton = createStyledButton("👁️ View Details", INFO_BLUE);
        refreshButton = createStyledButton("🔄 Refresh", Color.GRAY);

        // Initially disable edit/delete buttons
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        viewButton.setEnabled(false);

        // Enhanced form components
        nameField = createStyledTextField(20, "Enter patient full name");
        dobField = createStyledTextField(10, "YYYY-MM-DD");
        addressField = createStyledTextField(30, "Enter full address");
        phoneField = createStyledTextField(15, "Enter phone number");
        
        medicalHistoryArea = new JTextArea(4, 30);
        medicalHistoryArea.setLineWrap(true);
        medicalHistoryArea.setWrapStyleWord(true);
        medicalHistoryArea.setFont(DATA_FONT);
        medicalHistoryArea.setBorder(new CompoundBorder(
            new LineBorder(BORDER_LIGHT, 1),
            new EmptyBorder(8, 12, 8, 12)
        ));

        setupFormPanel();
    }

    private JTable createStyledTable() {
        JTable table = new JTable(tableModel);
        table.setRowHeight(32);
        table.setFont(DATA_FONT);
        table.setSelectionBackground(new Color(PRIMARY_BLUE.getRed(), PRIMARY_BLUE.getGreen(), PRIMARY_BLUE.getBlue(), 40));
        table.setSelectionForeground(TEXT_DARK);
        table.setGridColor(BORDER_LIGHT);
        table.setBackground(SECONDARY_WHITE);
        table.setIntercellSpacing(new Dimension(1, 1));
        table.getTableHeader().setReorderingAllowed(false);
        
        // Style header
        table.getTableHeader().setFont(LABEL_FONT);
        table.getTableHeader().setBackground(PRIMARY_BLUE);
        table.getTableHeader().setForeground(SECONDARY_WHITE);
        table.getTableHeader().setBorder(new LineBorder(PRIMARY_BLUE));
        table.getTableHeader().setPreferredSize(new Dimension(0, 36));
        
        // Alternating row colors
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(SECONDARY_WHITE);
                    } else {
                        c.setBackground(new Color(248, 249, 250));
                    }
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
        
        // Placeholder effect
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

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(LABEL_FONT);
        button.setBackground(bgColor);
        button.setForeground(SECONDARY_WHITE);
        button.setBorder(new EmptyBorder(8, 16, 8, 16));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(button.getPreferredSize().width, 44)); // Minimum touch target
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(bgColor.darker());
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(bgColor);
                }
            }
        });
        
        return button;
    }

    private JPanel createStyledPanel(String title) {
        JPanel panel = new JPanel();
        panel.setBackground(SECONDARY_WHITE);
        panel.setBorder(new CompoundBorder(
            new LineBorder(BORDER_LIGHT, 1),
            new EmptyBorder(16, 16, 16, 16)
        ));
        
        if (title != null && !title.isEmpty()) {
            panel.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(BORDER_LIGHT, 1), 
                title, 
                0, 0, HEADER_FONT, PRIMARY_BLUE
            ));
        }
        
        return panel;
    }

    private void setupFormPanel() {
        formPanel = createStyledPanel("Patient Information");
        formPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Name
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setFont(LABEL_FONT);
        nameLabel.setForeground(TEXT_DARK);
        formPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);

        // Date of Birth
        gbc.gridx = 2;
        JLabel dobLabel = new JLabel("Date of Birth:");
        dobLabel.setFont(LABEL_FONT);
        dobLabel.setForeground(TEXT_DARK);
        formPanel.add(dobLabel, gbc);
        gbc.gridx = 3;
        formPanel.add(dobField, gbc);

        // Phone
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel phoneLabel = new JLabel("Phone Number:");
        phoneLabel.setFont(LABEL_FONT);
        phoneLabel.setForeground(TEXT_DARK);
        formPanel.add(phoneLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(phoneField, gbc);

        // Address
        gbc.gridx = 2;
        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setFont(LABEL_FONT);
        addressLabel.setForeground(TEXT_DARK);
        formPanel.add(addressLabel, gbc);
        gbc.gridx = 3;
        formPanel.add(addressField, gbc);

        // Medical History
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        JLabel historyLabel = new JLabel("Medical History:");
        historyLabel.setFont(LABEL_FONT);
        historyLabel.setForeground(TEXT_DARK);
        formPanel.add(historyLabel, gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; gbc.weighty = 0.3;
        
        JScrollPane medicalScrollPane = new JScrollPane(medicalHistoryArea);
        medicalScrollPane.setBorder(new LineBorder(BORDER_LIGHT, 1));
        medicalScrollPane.setPreferredSize(new Dimension(400, 120));
        medicalScrollPane.getViewport().setBackground(SECONDARY_WHITE);
        formPanel.add(medicalScrollPane, gbc);

        // Form buttons
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0; gbc.weighty = 0;

        JPanel formButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        formButtonPanel.setBackground(SECONDARY_WHITE);
        
        JButton saveButton = createStyledButton("💾 Save Patient", ACCENT_GREEN);
        JButton cancelButton = createStyledButton("❌ Cancel", Color.GRAY);
        JButton clearButton = createStyledButton("🔄 Clear Form", WARNING_AMBER);

        saveButton.addActionListener(e -> savePatient());
        cancelButton.addActionListener(e -> cancelEdit());
        clearButton.addActionListener(e -> clearForm());

        formButtonPanel.add(saveButton);
        formButtonPanel.add(cancelButton);
        formButtonPanel.add(clearButton);

        formPanel.add(formButtonPanel, gbc);
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(0, 8));
        setBorder(new EmptyBorder(16, 16, 16, 16));

        // Enhanced header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_GRAY);
        headerPanel.setBorder(new EmptyBorder(0, 0, 16, 0));

        JLabel titleLabel = new JLabel("Patient Management");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_BLUE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Search panel with enhanced styling
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchPanel.setBackground(BACKGROUND_GRAY);
        
        JLabel searchLabel = new JLabel("🔍");
        searchLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        
        headerPanel.add(searchPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Center panel with enhanced split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(320);
        splitPane.setResizeWeight(0.6);
        splitPane.setBackground(BACKGROUND_GRAY);
        splitPane.setBorder(null);

        // Enhanced table panel
        JPanel tablePanel = createStyledPanel("Patient Records");
        tablePanel.setLayout(new BorderLayout(0, 8));

        JScrollPane tableScrollPane = new JScrollPane(patientTable);
        tableScrollPane.setBorder(new LineBorder(BORDER_LIGHT, 1));
        tableScrollPane.getViewport().setBackground(SECONDARY_WHITE);
        tableScrollPane.setPreferredSize(new Dimension(800, 280));
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);

        // Enhanced button panel
        JPanel tableButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        tableButtonPanel.setBackground(SECONDARY_WHITE);
        
        tableButtonPanel.add(addButton);
        tableButtonPanel.add(editButton);
        tableButtonPanel.add(viewButton);
        tableButtonPanel.add(deleteButton);
        tableButtonPanel.add(Box.createHorizontalStrut(16)); // Spacer
        tableButtonPanel.add(refreshButton);

        tablePanel.add(tableButtonPanel, BorderLayout.SOUTH);

        splitPane.setTopComponent(tablePanel);
        splitPane.setBottomComponent(formPanel);

        add(splitPane, BorderLayout.CENTER);

        // Status panel
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(BACKGROUND_GRAY);
        statusPanel.setBorder(new EmptyBorder(8, 0, 0, 0));
        
        JLabel statusLabel = new JLabel("Ready");
        statusLabel.setFont(DATA_FONT);
        statusLabel.setForeground(TEXT_DARK);
        statusPanel.add(statusLabel, BorderLayout.WEST);
        
        add(statusPanel, BorderLayout.SOUTH);
    }

    private void setupEventHandlers() {
        // Table selection listener
        patientTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = patientTable.getSelectedRow() != -1;
                editButton.setEnabled(hasSelection);
                deleteButton.setEnabled(hasSelection);
                viewButton.setEnabled(hasSelection);

                if (hasSelection && !isEditing) {
                    loadSelectedPatientToForm();
                }
            }
        });

        // Enhanced double-click functionality
        patientTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && patientTable.getSelectedRow() != -1) {
                    viewPatientDetails();
                }
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showTableContextMenu(e);
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showTableContextMenu(e);
                }
            }
        });

        // Enhanced search functionality
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filterTable();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filterTable();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filterTable();
            }
        });

        // Button listeners
        addButton.addActionListener(e -> startAddPatient());
        editButton.addActionListener(e -> startEditPatient());
        deleteButton.addActionListener(e -> deletePatient());
        viewButton.addActionListener(e -> viewPatientDetails());
        refreshButton.addActionListener(e -> refreshData());
    }

    private void showTableContextMenu(MouseEvent e) {
        int row = patientTable.rowAtPoint(e.getPoint());
        if (row >= 0) {
            patientTable.setRowSelectionInterval(row, row);
            
            JPopupMenu contextMenu = new JPopupMenu();
            contextMenu.setBorder(new LineBorder(BORDER_LIGHT, 1));
            
            JMenuItem viewItem = new JMenuItem("👁️ View Details");
            viewItem.setFont(DATA_FONT);
            viewItem.addActionListener(evt -> viewPatientDetails());
            
            JMenuItem editItem = new JMenuItem("✏️ Edit Patient");
            editItem.setFont(DATA_FONT);
            editItem.addActionListener(evt -> startEditPatient());
            
            JMenuItem deleteItem = new JMenuItem("🗑️ Delete Patient");
            deleteItem.setFont(DATA_FONT);
            deleteItem.setForeground(ERROR_RED);
            deleteItem.addActionListener(evt -> deletePatient());
            
            contextMenu.add(viewItem);
            contextMenu.add(editItem);
            contextMenu.addSeparator();
            contextMenu.add(deleteItem);
            
            contextMenu.show(patientTable, e.getX(), e.getY());
        }
    }

    private void filterTable() {
        String text = searchField.getText().trim();
        if (text.equals("Search patients by name, phone, or address...") || text.length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }

    public void refreshData() {
        initializeSecureService();
        if (securePatientService == null) {
            showWarningDialog("Please log in to access patient data.");
            return;
        }

        // Show loading indicator
        JDialog loadingDialog = createLoadingDialog("Loading patient data...");
        
        SwingWorker<List<Patient>, Void> worker = new SwingWorker<List<Patient>, Void>() {
            @Override
            protected List<Patient> doInBackground() throws Exception {
                return securePatientService.getAllPatients();
            }

            @Override
            protected void done() {
                loadingDialog.dispose();
                try {
                    List<Patient> patients = get();
                    updateTableData(patients);
                    parentFrame.setStatus("✅ Patient data refreshed - " + patients.size() + " patients loaded");
                } catch (Exception e) {
                    String errorMsg = e.getCause() instanceof SecurityException
                            ? "🔒 Access denied: " + e.getCause().getMessage()
                            : "❌ Error loading patients: " + e.getMessage();

                    showErrorDialog(errorMsg);
                    parentFrame.setStatus("❌ Error loading patient data");
                }
            }
        };

        worker.execute();
        loadingDialog.setVisible(true);
    }

    private JDialog createLoadingDialog(String message) {
        JDialog dialog = new JDialog(parentFrame, "Loading", true);
        dialog.setSize(300, 120);
        dialog.setLocationRelativeTo(this);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.getContentPane().setBackground(BACKGROUND_GRAY);
        
        JPanel content = new JPanel(new BorderLayout());
        content.setBorder(new EmptyBorder(20, 20, 20, 20));
        content.setBackground(BACKGROUND_GRAY);
        
        JLabel loadingLabel = new JLabel(message);
        loadingLabel.setFont(BODY_FONT);
        loadingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setBackground(SECONDARY_WHITE);
        progressBar.setForeground(PRIMARY_BLUE);
        
        content.add(loadingLabel, BorderLayout.CENTER);
        content.add(progressBar, BorderLayout.SOUTH);
        
        dialog.add(content);
        return dialog;
    }

    private void updateTableData(List<Patient> patients) {
        tableModel.setRowCount(0);
        for (Patient patient : patients) {
            Object[] row = {
                patient.getId(),
                patient.getName(),
                patient.getDob() != null ? patient.getDob().toString() : "",
                patient.getPhone() != null ? patient.getPhone() : "",
                patient.getAddress() != null ? patient.getAddress() : ""
            };
            tableModel.addRow(row);
        }
    }

    private void startAddPatient() {
        isEditing = true;
        editingPatientId = null;
        clearForm();
        nameField.requestFocus();
        parentFrame.setStatus("➕ Adding new patient...");
    }

    private void startEditPatient() {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        int modelRow = patientTable.convertRowIndexToModel(selectedRow);
        editingPatientId = (Long) tableModel.getValueAt(modelRow, 0);
        isEditing = true;
        loadSelectedPatientToForm();
        nameField.requestFocus();
        parentFrame.setStatus("✏️ Editing patient ID: " + editingPatientId);
    }

    private void loadSelectedPatientToForm() {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        int modelRow = patientTable.convertRowIndexToModel(selectedRow);
        Long patientId = (Long) tableModel.getValueAt(modelRow, 0);

        SwingWorker<Patient, Void> worker = new SwingWorker<Patient, Void>() {
            @Override
            protected Patient doInBackground() throws Exception {
                return securePatientService.getPatientById(patientId);
            }

            @Override
            protected void done() {
                try {
                    Patient patient = get();
                    if (patient != null) {
                        populateForm(patient);
                    }
                } catch (Exception e) {
                    showErrorDialog("Error loading patient details: " + e.getMessage());
                }
            }
        };

        worker.execute();
    }

    private void populateForm(Patient patient) {
        setFieldValue(nameField, patient.getName(), "Enter patient full name");
        setFieldValue(dobField, patient.getDob() != null ? patient.getDob().toString() : "", "YYYY-MM-DD");
        setFieldValue(phoneField, patient.getPhone(), "Enter phone number");
        setFieldValue(addressField, patient.getAddress(), "Enter full address");
        
        medicalHistoryArea.setText(patient.getMedicalHistory() != null ? patient.getMedicalHistory() : "");
        medicalHistoryArea.setForeground(TEXT_DARK);
    }

    private void setFieldValue(JTextField field, String value, String placeholder) {
        if (value != null && !value.isEmpty()) {
            field.setText(value);
            field.setForeground(TEXT_DARK);
        } else {
            field.setText(placeholder);
            field.setForeground(Color.GRAY);
        }
    }

    private void savePatient() {
        if (!validateForm()) {
            return;
        }

        Patient patient = createPatientFromForm();
        if (editingPatientId != null) {
            patient.setId(editingPatientId);
        }

        JDialog savingDialog = createLoadingDialog(editingPatientId == null ? "Creating patient..." : "Updating patient...");

        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                if (editingPatientId == null) {
                    Long newId = securePatientService.createPatient(patient);
                    return newId != null;
                } else {
                    return securePatientService.updatePatient(patient);
                }
            }

            @Override
            protected void done() {
                savingDialog.dispose();
                try {
                    boolean success = get();
                    if (success) {
                        String action = editingPatientId == null ? "added" : "updated";
                        showSuccessDialog("✅ Patient " + action + " successfully!");
                        cancelEdit();
                        refreshData();
                    } else {
                        showErrorDialog("❌ Failed to save patient!");
                    }
                } catch (Exception e) {
                    showErrorDialog("❌ Error saving patient: " + e.getMessage());
                }
            }
        };

        worker.execute();
        savingDialog.setVisible(true);
    }

    private boolean validateForm() {
        String name = nameField.getText().trim();
        if (name.equals("Enter patient full name") || name.isEmpty()) {
            showErrorDialog("⚠️ Patient name is required!");
            nameField.requestFocus();
            return false;
        }

        String dobText = dobField.getText().trim();
        if (!dobText.equals("YYYY-MM-DD") && !dobText.isEmpty()) {
            try {
                LocalDate.parse(dobText);
            } catch (DateTimeParseException e) {
                showErrorDialog("⚠️ Invalid date format! Please use YYYY-MM-DD");
                dobField.requestFocus();
                return false;
            }
        }

        return true;
    }

    private Patient createPatientFromForm() {
        Patient patient = new Patient();
        
        String name = nameField.getText().trim();
        patient.setName(name.equals("Enter patient full name") ? "" : name);

        String dobText = dobField.getText().trim();
        if (!dobText.equals("YYYY-MM-DD") && !dobText.isEmpty()) {
            patient.setDob(LocalDate.parse(dobText));
        }

        String phone = phoneField.getText().trim();
        patient.setPhone(phone.equals("Enter phone number") ? "" : phone);
        
        String address = addressField.getText().trim();
        patient.setAddress(address.equals("Enter full address") ? "" : address);
        
        patient.setMedicalHistory(medicalHistoryArea.getText().trim());

        return patient;
    }

    private void deletePatient() {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        int modelRow = patientTable.convertRowIndexToModel(selectedRow);
        Long patientId = (Long) tableModel.getValueAt(modelRow, 0);
        String patientName = (String) tableModel.getValueAt(modelRow, 1);

        // Enhanced confirmation dialog
        JDialog confirmDialog = new JDialog(parentFrame, "Confirm Delete", true);
        confirmDialog.setSize(400, 200);
        confirmDialog.setLocationRelativeTo(this);
        confirmDialog.getContentPane().setBackground(BACKGROUND_GRAY);

        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setBorder(new EmptyBorder(20, 20, 20, 20));
        content.setBackground(BACKGROUND_GRAY);

        JLabel warningIcon = new JLabel("⚠️");
        warningIcon.setFont(new Font("SansSerif", Font.PLAIN, 32));
        warningIcon.setHorizontalAlignment(SwingConstants.CENTER);
        content.add(warningIcon, BorderLayout.NORTH);

        JLabel messageLabel = new JLabel("<html><center>Are you sure you want to delete<br><b>" + patientName + "</b>?<br><br>This action cannot be undone!</center></html>");
        messageLabel.setFont(BODY_FONT);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        content.add(messageLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(BACKGROUND_GRAY);
        
        JButton deleteConfirmButton = createStyledButton("🗑️ Delete", ERROR_RED);
        JButton cancelButton = createStyledButton("❌ Cancel", Color.GRAY);
        
        buttonPanel.add(deleteConfirmButton);
        buttonPanel.add(cancelButton);
        content.add(buttonPanel, BorderLayout.SOUTH);

        confirmDialog.add(content);

        deleteConfirmButton.addActionListener(e -> {
            confirmDialog.dispose();
            executePatientDeletion(patientId, patientName);
        });
        
        cancelButton.addActionListener(e -> confirmDialog.dispose());
        
        confirmDialog.setVisible(true);
    }

    private void executePatientDeletion(Long patientId, String patientName) {
        JDialog deletingDialog = createLoadingDialog("Deleting patient...");
        
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return securePatientService.deletePatient(patientId);
            }

            @Override
            protected void done() {
                deletingDialog.dispose();
                try {
                    boolean success = get();
                    if (success) {
                        showSuccessDialog("✅ Patient '" + patientName + "' deleted successfully!");
                        clearForm();
                        refreshData();
                    } else {
                        showErrorDialog("❌ Failed to delete patient!");
                    }
                } catch (Exception e) {
                    showErrorDialog("❌ Error deleting patient: " + e.getMessage());
                }
            }
        };

        worker.execute();
        deletingDialog.setVisible(true);
    }

    private void viewPatientDetails() {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        int modelRow = patientTable.convertRowIndexToModel(selectedRow);
        Long patientId = (Long) tableModel.getValueAt(modelRow, 0);

        SwingWorker<Patient, Void> worker = new SwingWorker<Patient, Void>() {
            @Override
            protected Patient doInBackground() throws Exception {
                return securePatientService.getPatientById(patientId);
            }

            @Override
            protected void done() {
                try {
                    Patient patient = get();
                    if (patient != null) {
                        showPatientDetailsDialog(patient);
                    }
                } catch (Exception e) {
                    showErrorDialog("❌ Error loading patient details: " + e.getMessage());
                }
            }
        };

        worker.execute();
    }

    private void showPatientDetailsDialog(Patient patient) {
        JDialog dialog = new JDialog(parentFrame, "Patient Details", true);
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(BACKGROUND_GRAY);

        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setBorder(new EmptyBorder(20, 20, 20, 20));
        content.setBackground(BACKGROUND_GRAY);

        // Enhanced header with patient info
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_BLUE);
        headerPanel.setBorder(new EmptyBorder(16, 20, 16, 20));
        
        JLabel titleLabel = new JLabel("👤 " + patient.getName());
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(SECONDARY_WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JLabel idLabel = new JLabel("ID: " + patient.getId());
        idLabel.setFont(LABEL_FONT);
        idLabel.setForeground(SECONDARY_WHITE);
        headerPanel.add(idLabel, BorderLayout.EAST);

        content.add(headerPanel, BorderLayout.NORTH);

        // Enhanced details panel
        JPanel detailsPanel = createStyledPanel(null);
        detailsPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.anchor = GridBagConstraints.WEST;

        addDetailRow(detailsPanel, gbc, 0, "📋 Patient ID:", String.valueOf(patient.getId()));
        addDetailRow(detailsPanel, gbc, 1, "👤 Full Name:", patient.getName());
        addDetailRow(detailsPanel, gbc, 2, "🎂 Date of Birth:", 
                patient.getDob() != null ? patient.getDob().toString() : "Not specified");
        addDetailRow(detailsPanel, gbc, 3, "📞 Phone Number:", 
                patient.getPhone() != null ? patient.getPhone() : "Not specified");
        addDetailRow(detailsPanel, gbc, 4, "🏠 Address:", 
                patient.getAddress() != null ? patient.getAddress() : "Not specified");

        // Enhanced medical history section
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        JLabel historyLabel = new JLabel("🏥 Medical History:");
        historyLabel.setFont(LABEL_FONT);
        historyLabel.setForeground(TEXT_DARK);
        detailsPanel.add(historyLabel, gbc);

        gbc.gridx = 1; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; gbc.weighty = 1.0;

        JTextArea historyArea = new JTextArea(patient.getMedicalHistory() != null
                ? patient.getMedicalHistory() : "No medical history recorded");
        historyArea.setEditable(false);
        historyArea.setLineWrap(true);
        historyArea.setWrapStyleWord(true);
        historyArea.setFont(DATA_FONT);
        historyArea.setBackground(new Color(248, 249, 250));
        historyArea.setBorder(new EmptyBorder(12, 12, 12, 12));

        JScrollPane historyScroll = new JScrollPane(historyArea);
        historyScroll.setBorder(new LineBorder(BORDER_LIGHT, 1));
        historyScroll.setPreferredSize(new Dimension(350, 120));
        historyScroll.getViewport().setBackground(new Color(248, 249, 250));
        detailsPanel.add(historyScroll, gbc);

        content.add(detailsPanel, BorderLayout.CENTER);

        // Enhanced action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        buttonPanel.setBackground(BACKGROUND_GRAY);
        
        JButton editButton = createStyledButton("✏️ Edit Patient", PRIMARY_BLUE);
        JButton closeButton = createStyledButton("❌ Close", Color.GRAY);
        
        editButton.addActionListener(e -> {
            dialog.dispose();
            startEditPatient();
        });
        closeButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(editButton);
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

        gbc.gridx = 1; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel valueComponent = new JLabel(value != null ? value : "");
        valueComponent.setFont(DATA_FONT);
        valueComponent.setForeground(TEXT_DARK);
        panel.add(valueComponent, gbc);
    }

    private void cancelEdit() {
        isEditing = false;
        editingPatientId = null;
        clearForm();
        parentFrame.setStatus("✅ Edit cancelled");
    }

    private void clearForm() {
        resetTextFieldPlaceholder(nameField, "Enter patient full name");
        resetTextFieldPlaceholder(dobField, "YYYY-MM-DD");
        resetTextFieldPlaceholder(phoneField, "Enter phone number");
        resetTextFieldPlaceholder(addressField, "Enter full address");
        medicalHistoryArea.setText("");
        medicalHistoryArea.setForeground(Color.GRAY);
    }

    private void resetTextFieldPlaceholder(JTextField field, String placeholder) {
        field.setText(placeholder);
        field.setForeground(Color.GRAY);
    }

    // Enhanced dialog methods
    private void showSuccessDialog(String message) {
        createStyledMessageDialog(message, "Success", ACCENT_GREEN, "✅");
    }

    private void showErrorDialog(String message) {
        createStyledMessageDialog(message, "Error", ERROR_RED, "❌");
    }

    private void showWarningDialog(String message) {
        createStyledMessageDialog(message, "Warning", WARNING_AMBER, "⚠️");
    }

    private void showInfoDialog(String message) {
        createStyledMessageDialog(message, "Information", INFO_BLUE, "ℹ️");
    }

    private void createStyledMessageDialog(String message, String title, Color themeColor, String icon) {
        JDialog dialog = new JDialog(parentFrame, title, true);
        dialog.setSize(400, 180);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(BACKGROUND_GRAY);

        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setBorder(new EmptyBorder(20, 20, 20, 20));
        content.setBackground(BACKGROUND_GRAY);

        // Icon and message panel
        JPanel messagePanel = new JPanel(new BorderLayout(16, 0));
        messagePanel.setBackground(BACKGROUND_GRAY);
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 32));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messagePanel.add(iconLabel, BorderLayout.WEST);

        JLabel messageLabel = new JLabel("<html>" + message + "</html>");
        messageLabel.setFont(BODY_FONT);
        messageLabel.setForeground(TEXT_DARK);
        messagePanel.add(messageLabel, BorderLayout.CENTER);

        content.add(messagePanel, BorderLayout.CENTER);

        // OK button
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(BACKGROUND_GRAY);
        
        JButton okButton = createStyledButton("OK", themeColor);
        okButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(okButton);

        content.add(buttonPanel, BorderLayout.SOUTH);
        dialog.add(content);
        dialog.setVisible(true);
    }
}
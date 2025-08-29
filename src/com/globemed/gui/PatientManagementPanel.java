/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.gui;

import com.globemed.models.Patient;
import com.globemed.services.SecurePatientService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
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
 * Patient Management Panel with Chain of Responsibility and Decorator patterns
 *
 * @author Hansana
 */
public class PatientManagementPanel extends JPanel {

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
    private final String[] columnNames = {"ID", "Name", "Date of Birth", "Phone", "Address"};

    public PatientManagementPanel(MainFrame parentFrame) {
        this.parentFrame = parentFrame;
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
        // Table setup
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };

        patientTable = new JTable(tableModel);
        patientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        patientTable.setRowHeight(25);
        patientTable.getTableHeader().setReorderingAllowed(false);

        // Setup table sorter
        sorter = new TableRowSorter<>(tableModel);
        patientTable.setRowSorter(sorter);

        // Search field
        searchField = new JTextField(20);
        searchField.setToolTipText("Search patients by name, phone, or address");

        // Buttons
        addButton = new JButton("Add Patient");
        editButton = new JButton("Edit Patient");
        deleteButton = new JButton("Delete Patient");
        viewButton = new JButton("View Details");
        refreshButton = new JButton("Refresh");

        // Initially disable edit/delete buttons
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        viewButton.setEnabled(false);

        // Form components
        nameField = new JTextField(20);
        dobField = new JTextField(10);
        dobField.setToolTipText("Format: YYYY-MM-DD");
        addressField = new JTextField(30);
        phoneField = new JTextField(15);
        medicalHistoryArea = new JTextArea(4, 30);
        medicalHistoryArea.setLineWrap(true);
        medicalHistoryArea.setWrapStyleWord(true);

        setupFormPanel();
    }

    private void setupFormPanel() {
        formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Patient Information"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);

        // Date of Birth
        gbc.gridx = 2;
        formPanel.add(new JLabel("Date of Birth:"), gbc);
        gbc.gridx = 3;
        formPanel.add(dobField, gbc);

        // Phone
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        formPanel.add(phoneField, gbc);

        // Address
        gbc.gridx = 2;
        formPanel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 3;
        formPanel.add(addressField, gbc);

        // Medical History
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("Medical History:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        JScrollPane medicalScrollPane = new JScrollPane(medicalHistoryArea);
        medicalScrollPane.setPreferredSize(new Dimension(400, 100));
        formPanel.add(medicalScrollPane, gbc);

        // Form buttons
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel formButtonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        JButton clearButton = new JButton("Clear");

        saveButton.addActionListener(e -> savePatient());
        cancelButton.addActionListener(e -> cancelEdit());
        clearButton.addActionListener(e -> clearForm());

        formButtonPanel.add(saveButton);
        formButtonPanel.add(cancelButton);
        formButtonPanel.add(clearButton);

        formPanel.add(formButtonPanel, gbc);
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Patient Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        headerPanel.add(searchPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Center panel with split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.6);

        // Table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Patient List"));

        JScrollPane tableScrollPane = new JScrollPane(patientTable);
        tableScrollPane.setPreferredSize(new Dimension(800, 250));
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);

        // Table button panel
        JPanel tableButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tableButtonPanel.add(addButton);
        tableButtonPanel.add(editButton);
        tableButtonPanel.add(deleteButton);
        tableButtonPanel.add(viewButton);
        tableButtonPanel.add(refreshButton);

        tablePanel.add(tableButtonPanel, BorderLayout.SOUTH);

        splitPane.setTopComponent(tablePanel);
        splitPane.setBottomComponent(formPanel);

        add(splitPane, BorderLayout.CENTER);
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

        // Double-click to view details
        patientTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && patientTable.getSelectedRow() != -1) {
                    viewPatientDetails();
                }
            }
        });

        // Search functionality
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

    private void filterTable() {
        String text = searchField.getText();
        if (text.trim().length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }

    public void refreshData() {
        initializeSecureService();
        if (securePatientService == null) {
            JOptionPane.showMessageDialog(this, "Please log in to access patient data.",
                    "Access Denied", JOptionPane.WARNING_MESSAGE);
            return;
        }

        SwingWorker<List<Patient>, Void> worker = new SwingWorker<List<Patient>, Void>() {
            @Override
            protected List<Patient> doInBackground() throws Exception {
                return securePatientService.getAllPatients();
            }

            @Override
            protected void done() {
                try {
                    List<Patient> patients = get();
                    updateTableData(patients);
                    parentFrame.setStatus("Patient data refreshed - " + patients.size() + " patients loaded");
                } catch (Exception e) {
                    String errorMsg = e.getCause() instanceof SecurityException
                            ? "Access denied: " + e.getCause().getMessage()
                            : "Error loading patients: " + e.getMessage();

                    JOptionPane.showMessageDialog(PatientManagementPanel.this,
                            errorMsg, "Error", JOptionPane.ERROR_MESSAGE);
                    parentFrame.setStatus("Error loading patient data");
                }
            }
        };

        worker.execute();
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
        parentFrame.setStatus("Adding new patient...");
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
        parentFrame.setStatus("Editing patient ID: " + editingPatientId);
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
                    JOptionPane.showMessageDialog(PatientManagementPanel.this,
                            "Error loading patient details: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();
    }

    private void populateForm(Patient patient) {
        nameField.setText(patient.getName() != null ? patient.getName() : "");
        dobField.setText(patient.getDob() != null ? patient.getDob().toString() : "");
        phoneField.setText(patient.getPhone() != null ? patient.getPhone() : "");
        addressField.setText(patient.getAddress() != null ? patient.getAddress() : "");
        medicalHistoryArea.setText(patient.getMedicalHistory() != null ? patient.getMedicalHistory() : "");
    }

    private void savePatient() {
        if (!validateForm()) {
            return;
        }

        Patient patient = createPatientFromForm();
        if (editingPatientId != null) {
            patient.setId(editingPatientId);
        }

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
                try {
                    boolean success = get();
                    if (success) {
                        String action = editingPatientId == null ? "added" : "updated";
                        JOptionPane.showMessageDialog(PatientManagementPanel.this,
                                "Patient " + action + " successfully!",
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                        cancelEdit();
                        refreshData();
                    } else {
                        JOptionPane.showMessageDialog(PatientManagementPanel.this,
                                "Failed to save patient!",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(PatientManagementPanel.this,
                            "Error saving patient: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();
    }

    private boolean validateForm() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name is required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return false;
        }

        String dobText = dobField.getText().trim();
        if (!dobText.isEmpty()) {
            try {
                LocalDate.parse(dobText);
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, "Invalid date format! Use YYYY-MM-DD",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                dobField.requestFocus();
                return false;
            }
        }

        return true;
    }

    private Patient createPatientFromForm() {
        Patient patient = new Patient();
        patient.setName(nameField.getText().trim());

        String dobText = dobField.getText().trim();
        if (!dobText.isEmpty()) {
            patient.setDob(LocalDate.parse(dobText));
        }

        patient.setPhone(phoneField.getText().trim());
        patient.setAddress(addressField.getText().trim());
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

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete patient '" + patientName + "'?\n"
                + "This action cannot be undone!",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return securePatientService.deletePatient(patientId);
                }

                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            JOptionPane.showMessageDialog(PatientManagementPanel.this,
                                    "Patient deleted successfully!",
                                    "Success", JOptionPane.INFORMATION_MESSAGE);
                            clearForm();
                            refreshData();
                        } else {
                            JOptionPane.showMessageDialog(PatientManagementPanel.this,
                                    "Failed to delete patient!",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(PatientManagementPanel.this,
                                "Error deleting patient: " + e.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };

            worker.execute();
        }
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
                    JOptionPane.showMessageDialog(PatientManagementPanel.this,
                            "Error loading patient details: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();
    }

    private void showPatientDetailsDialog(Patient patient) {
        JDialog dialog = new JDialog(parentFrame, "Patient Details: " + patient.getName(), true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JPanel content = new JPanel(new BorderLayout());
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Patient info panel
        JPanel infoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        addDetailRow(infoPanel, gbc, 0, "Patient ID:", String.valueOf(patient.getId()));
        addDetailRow(infoPanel, gbc, 1, "Name:", patient.getName());
        addDetailRow(infoPanel, gbc, 2, "Date of Birth:",
                patient.getDob() != null ? patient.getDob().toString() : "Not specified");
        addDetailRow(infoPanel, gbc, 3, "Phone:",
                patient.getPhone() != null ? patient.getPhone() : "Not specified");
        addDetailRow(infoPanel, gbc, 4, "Address:",
                patient.getAddress() != null ? patient.getAddress() : "Not specified");

        // Medical history
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        infoPanel.add(new JLabel("Medical History:"), gbc);

        JTextArea historyArea = new JTextArea(patient.getMedicalHistory() != null
                ? patient.getMedicalHistory() : "No medical history recorded");
        historyArea.setEditable(false);
        historyArea.setLineWrap(true);
        historyArea.setWrapStyleWord(true);
        historyArea.setBackground(getBackground());

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        JScrollPane historyScroll = new JScrollPane(historyArea);
        historyScroll.setPreferredSize(new Dimension(300, 100));
        infoPanel.add(historyScroll, gbc);

        content.add(infoPanel, BorderLayout.CENTER);

        // Close button
        JPanel buttonPanel = new JPanel();
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeButton);

        content.add(buttonPanel, BorderLayout.SOUTH);
        dialog.add(content);
        dialog.setVisible(true);
    }

    private void addDetailRow(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.weighty = 0;

        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(labelComponent, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel valueComponent = new JLabel(value != null ? value : "");
        panel.add(valueComponent, gbc);
    }

    private void cancelEdit() {
        isEditing = false;
        editingPatientId = null;
        clearForm();
        parentFrame.setStatus("Edit cancelled");
    }

    private void clearForm() {
        nameField.setText("");
        dobField.setText("");
        phoneField.setText("");
        addressField.setText("");
        medicalHistoryArea.setText("");
    }
}

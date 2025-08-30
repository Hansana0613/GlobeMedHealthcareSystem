/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.gui;

import com.globemed.database.StaffDAO;
import com.globemed.models.Staff;
import com.globemed.patterns.decorator.SecureService;
import com.globemed.services.RoleManagementService;
import com.globemed.services.IntegratedSecurityService;
import com.globemed.utils.SecurityUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Hansana
 */
public class StaffManagementPanel extends JPanel {

    private MainFrame parentFrame;
    private StaffDAO staffDAO;
    private RoleManagementService roleService;
    private IntegratedSecurityService securityService;

    // GUI Components
    private JTable staffTable;
    private DefaultTableModel tableModel;
    private JTextField nameField, usernameField, passwordField, emailField;
    private JComboBox<String> roleComboBox;
    private JButton addButton, editButton, deleteButton, refreshButton;
    private boolean isEditing = false;
    private Long editingStaffId = null;

    // Table columns
    private final String[] columnNames = {"ID", "Name", "Role", "Username", "Email"};

    public StaffManagementPanel(MainFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.staffDAO = new StaffDAO();
        this.roleService = parentFrame.getRoleService();
        this.securityService = new IntegratedSecurityService();
        initializeComponents();
        layoutComponents();
        setupEventHandlers();
        refreshData();
    }

    private void initializeComponents() {
        // Table setup
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        staffTable = new JTable(tableModel);
        staffTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        staffTable.setRowHeight(25);
        staffTable.getTableHeader().setReorderingAllowed(false);

        // Form components
        nameField = new JTextField(20);
        usernameField = new JTextField(15);
        passwordField = new JTextField(15);
        emailField = new JTextField(20);
        roleComboBox = new JComboBox<>();
        loadRoles();

        // Buttons
        addButton = new JButton("Add Staff");
        editButton = new JButton("Edit Staff");
        deleteButton = new JButton("Delete Staff");
        refreshButton = new JButton("Refresh");

        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }

    private void loadRoles() {
        List<String> roleNames = roleService.getAllRoleNames();
        roleComboBox.removeAllItems();
        roleComboBox.addItem("Select Role");
        for (String role : roleNames) {
            roleComboBox.addItem(role);
        }
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Staff Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(refreshButton, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Table panel
        JScrollPane tableScroll = new JScrollPane(staffTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Staff Members"));
        add(tableScroll, BorderLayout.CENTER);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Staff Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        formPanel.add(roleComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.SOUTH);
    }

    private void setupEventHandlers() {
        staffTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (staffTable.getSelectedRow() != -1) {
                    editButton.setEnabled(true);
                    deleteButton.setEnabled(true);
                    loadSelectedStaff();
                } else {
                    editButton.setEnabled(false);
                    deleteButton.setEnabled(false);
                }
            }
        });

        addButton.addActionListener(e -> saveStaff(false));
        editButton.addActionListener(e -> saveStaff(true));
        deleteButton.addActionListener(e -> deleteStaff());
        refreshButton.addActionListener(e -> refreshData());
    }

    private void loadSelectedStaff() {
        int selectedRow = staffTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        int modelRow = staffTable.convertRowIndexToModel(selectedRow);
        Long staffId = (Long) tableModel.getValueAt(modelRow, 0);

        SwingWorker<Staff, Void> worker = new SwingWorker<Staff, Void>() {
            @Override
            protected Staff doInBackground() throws Exception {
                return staffDAO.getStaffById(staffId);
            }

            @Override
            protected void done() {
                try {
                    Staff staff = get();
                    if (staff != null) {
                        isEditing = true;
                        editingStaffId = staff.getId();
                        nameField.setText(staff.getName());
                        usernameField.setText(staff.getUsername());
                        passwordField.setText(staff.getPassword());
                        emailField.setText(staff.getEmail());
                        roleComboBox.setSelectedItem(roleService.getRoleById(staff.getRoleId()).getRoleName());
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(StaffManagementPanel.this,
                            "Error loading staff: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void saveStaff(boolean isUpdate) {
        try {
            String name = nameField.getText().trim();
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();
            String email = emailField.getText().trim();
            String roleName = (String) roleComboBox.getSelectedItem();

            if (name.isEmpty() || username.isEmpty() || password.isEmpty() || email.isEmpty() || roleName.equals("Select Role")) {
                JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Staff staff = new Staff();
            staff.setName(name);
            staff.setUsername(username);
            staff.setPassword(SecurityUtils.encrypt(password)); // Encrypt password
            staff.setEmail(email);
            staff.setRoleId(getRoleIdByName(roleName));
            if (isUpdate) {
                staff.setId(editingStaffId);
            }

            Staff secureStaff = parentFrame.getCurrentUser();
            Set<String> permissions = roleService.getStaffPermissions(secureStaff);
            SecureService<Staff> secureService = securityService.createSecureService(
                    "StaffService", "SYSTEM_ADMIN", permissions);

            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    secureService.execute(staff);
                    return isUpdate ? staffDAO.updateStaff(staff) : staffDAO.insertStaff(staff) != null;
                }

                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            JOptionPane.showMessageDialog(StaffManagementPanel.this,
                                    "Staff " + (isUpdate ? "updated" : "added") + " successfully!",
                                    "Success", JOptionPane.INFORMATION_MESSAGE);
                            clearForm();
                            refreshData();
                        } else {
                            JOptionPane.showMessageDialog(StaffManagementPanel.this,
                                    "Failed to " + (isUpdate ? "update" : "add") + " staff!",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(StaffManagementPanel.this,
                                "Error: " + e.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteStaff() {
        int selectedRow = staffTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        int modelRow = staffTable.convertRowIndexToModel(selectedRow);
        Long staffId = (Long) tableModel.getValueAt(modelRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this staff member?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            Staff secureStaff = parentFrame.getCurrentUser();
            Set<String> permissions = roleService.getStaffPermissions(secureStaff);
            SecureService<Staff> secureService = securityService.createSecureService(
                    "StaffService", "SYSTEM_ADMIN", permissions);

            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    Staff staff = staffDAO.getStaffById(staffId);
                    if (staff != null) {
                        secureService.execute(staff);
                        // Note: Actual deletion logic should include checks for dependencies
                        return staffDAO.updateStaff(staff); // Soft delete or implement actual delete in DAO
                    }
                    return false;
                }

                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            JOptionPane.showMessageDialog(StaffManagementPanel.this,
                                    "Staff deleted successfully!",
                                    "Success", JOptionPane.INFORMATION_MESSAGE);
                            clearForm();
                            refreshData();
                        } else {
                            JOptionPane.showMessageDialog(StaffManagementPanel.this,
                                    "Failed to delete staff!",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(StaffManagementPanel.this,
                                "Error deleting staff: " + e.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
    }

    public void refreshData() {
        SwingWorker<List<Staff>, Void> worker = new SwingWorker<List<Staff>, Void>() {
            @Override
            protected List<Staff> doInBackground() throws Exception {
                return staffDAO.getAllStaff();
            }

            @Override
            protected void done() {
                try {
                    List<Staff> staffList = get();
                    tableModel.setRowCount(0);
                    for (Staff staff : staffList) {
                        String roleName = roleService.getRoleById(staff.getRoleId()).getRoleName();
                        tableModel.addRow(new Object[]{
                            staff.getId(),
                            staff.getName(),
                            roleName,
                            staff.getUsername(),
                            staff.getEmail()
                        });
                    }
                    parentFrame.setStatus("Staff list refreshed successfully");
                } catch (Exception e) {
                    parentFrame.setStatus("Error refreshing staff list: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private Long getRoleIdByName(String roleName) {
        switch (roleName) {
            case "Administrator":
                return 1L;
            case "Doctor":
                return 2L;
            case "Nurse":
                return 3L;
            case "Pharmacist":
                return 4L;
            case "Surgeon":
                return 5L;
            case "Specialist":
                return 6L;
            default:
                return null;
        }
    }

    private void clearForm() {
        nameField.setText("");
        usernameField.setText("");
        passwordField.setText("");
        emailField.setText("");
        roleComboBox.setSelectedIndex(0);
        isEditing = false;
        editingStaffId = null;
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }
}

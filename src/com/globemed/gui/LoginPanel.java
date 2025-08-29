/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.gui;

import com.globemed.database.StaffDAO;
import com.globemed.models.Staff;
import com.globemed.utils.SecurityUtils;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

/**
 * Login panel for user authentication
 *
 * @author Hansana
 */
public class LoginPanel extends JPanel {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton cancelButton;
    private JLabel statusLabel;
    private MainFrame parentFrame;
    private StaffDAO staffDAO;

    public LoginPanel(MainFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.staffDAO = new StaffDAO();
        initializeComponents();
        layoutComponents();
        setupEventHandlers();
    }

    private void initializeComponents() {
        // Initialize components
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Login");
        cancelButton = new JButton("Cancel");
        statusLabel = new JLabel(" ");

        // Set component properties
        loginButton.setPreferredSize(new Dimension(100, 30));
        cancelButton.setPreferredSize(new Dimension(100, 30));
        statusLabel.setForeground(Color.RED);

        // Set default button
        //getRootPane().setDefaultButton(loginButton);
    }

    public JButton getLoginButton() {
        return loginButton;
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        // Main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Title
        JLabel titleLabel = new JLabel("GlobeMed Healthcare System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 100, 150));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(titleLabel, gbc);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Please enter your credentials to continue");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.GRAY);
        gbc.gridy = 1;
        mainPanel.add(subtitleLabel, gbc);

        // Username
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridy = 2;
        gbc.gridx = 0;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        mainPanel.add(usernameLabel, gbc);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 1;
        mainPanel.add(usernameField, gbc);

        // Password
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridy = 3;
        gbc.gridx = 0;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        mainPanel.add(passwordLabel, gbc);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(buttonPanel, gbc);

        // Status label
        gbc.gridy = 5;
        mainPanel.add(statusLabel, gbc);

        // Add main panel to center
        add(mainPanel, BorderLayout.CENTER);

        // Add some padding
        add(new JPanel(), BorderLayout.NORTH);
        add(new JPanel(), BorderLayout.SOUTH);
        add(new JPanel(), BorderLayout.EAST);
        add(new JPanel(), BorderLayout.WEST);
    }

    private void setupEventHandlers() {
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        // Enter key support
        usernameField.addActionListener(e -> passwordField.requestFocus());
        passwordField.addActionListener(e -> performLogin());
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showStatus("Please enter both username and password", true);
            return;
        }

        // Show loading
        loginButton.setEnabled(false);
        showStatus("Authenticating...", false);

        SwingWorker<Staff, Void> worker = new SwingWorker<Staff, Void>() {
            @Override
            protected Staff doInBackground() throws Exception {
                return authenticateUser(username, password);
            }

            @Override
            protected void done() {
                loginButton.setEnabled(true);
                try {
                    Staff staff = get();
                    if (staff != null) {
                        showStatus("Login successful! Welcome " + staff.getName(), false);
                        parentFrame.setCurrentUser(staff);
                        parentFrame.showMainInterface();
                    } else {
                        showStatus("Invalid username or password", true);
                        passwordField.setText("");
                        usernameField.requestFocus();
                    }
                } catch (Exception e) {
                    showStatus("Login error: " + e.getMessage(), true);
                    e.printStackTrace();
                }
            }
        };

        worker.execute();
    }

    private Staff authenticateUser(String username, String password) throws SQLException {
        Staff staff = staffDAO.getStaffByUsername(username);

        if (staff != null) {
            // In a real system, passwords would be hashed
            // For demo purposes, we'll check plain text or basic encoding
            if (staff.getPassword().equals(password)
                    || staff.getPassword().equals(SecurityUtils.encrypt(password))) {
                return staff;
            }
        }

        return null;
    }

    private void showStatus(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.setForeground(isError ? Color.RED : new Color(0, 150, 0));
    }

    public void reset() {
        usernameField.setText("");
        passwordField.setText("");
        statusLabel.setText(" ");
        usernameField.requestFocus();
    }
}

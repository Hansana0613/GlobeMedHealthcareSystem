/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.gui;

import com.globemed.database.StaffDAO;
import com.globemed.models.Staff;
import com.globemed.utils.SecurityUtils;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.SQLException;

/**
 * Login panel for user authentication with enhanced UI/UX
 *
 * @author Hansana
 */
public class LoginPanel extends JPanel {

    // Color scheme constants
    private static final Color PRIMARY_BLUE = new Color(46, 134, 171);
    private static final Color CLEAN_WHITE = new Color(255, 255, 255);
    private static final Color HEALTHCARE_GREEN = new Color(76, 175, 80);
    private static final Color WARNING_AMBER = new Color(255, 152, 0);
    private static final Color ERROR_RED = new Color(244, 67, 54);
    private static final Color BACKGROUND_GRAY = new Color(245, 245, 245);
    private static final Color TEXT_GRAY = new Color(117, 117, 117);
    private static final Color BORDER_GRAY = new Color(224, 224, 224);

    // Font constants
    private static final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 18);
    private static final Font BODY_FONT = new Font("SansSerif", Font.PLAIN, 15);
    private static final Font LABEL_FONT = new Font("SansSerif", Font.BOLD, 13);
    private static final Font FIELD_FONT = new Font("SansSerif", Font.PLAIN, 13);

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
        // Initialize text fields with enhanced styling
        usernameField = createStyledTextField();
        passwordField = createStyledPasswordField();

        // Initialize buttons with enhanced styling
        loginButton = createPrimaryButton("Login");
        cancelButton = createSecondaryButton("Cancel");

        // Initialize status label
        statusLabel = new JLabel(" ");
        statusLabel.setFont(BODY_FONT);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setForeground(TEXT_GRAY);
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20);
        field.setFont(FIELD_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_GRAY, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setPreferredSize(new Dimension(240, 44));
        field.setBackground(CLEAN_WHITE);

        // Add focus effects
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(PRIMARY_BLUE, 2),
                        BorderFactory.createEmptyBorder(7, 11, 7, 11)
                ));
            }

            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER_GRAY, 1),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
        });

        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField(20);
        field.setFont(FIELD_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_GRAY, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setPreferredSize(new Dimension(240, 44));
        field.setBackground(CLEAN_WHITE);

        // Add focus effects
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(PRIMARY_BLUE, 2),
                        BorderFactory.createEmptyBorder(7, 11, 7, 11)
                ));
            }

            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER_GRAY, 1),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
        });

        return field;
    }

    private JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(LABEL_FONT);
        button.setPreferredSize(new Dimension(120, 44));
        button.setBackground(PRIMARY_BLUE);
        button.setForeground(CLEAN_WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effects
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(PRIMARY_BLUE.darker());
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(PRIMARY_BLUE);
                }
            }
        });

        return button;
    }

    private JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(LABEL_FONT);
        button.setPreferredSize(new Dimension(120, 44));
        button.setBackground(CLEAN_WHITE);
        button.setForeground(TEXT_GRAY);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_GRAY, 1),
                BorderFactory.createEmptyBorder(11, 23, 11, 23)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effects
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(BACKGROUND_GRAY);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(CLEAN_WHITE);
            }
        });

        return button;
    }

    public JButton getLoginButton() {
        return loginButton;
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_GRAY);

        // Create main container with proper spacing
        JPanel mainContainer = new JPanel(new GridBagLayout());
        mainContainer.setBackground(BACKGROUND_GRAY);

        // Create login card
        JPanel loginCard = new JPanel();
        loginCard.setLayout(new BoxLayout(loginCard, BoxLayout.Y_AXIS));
        loginCard.setBackground(CLEAN_WHITE);
        loginCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_GRAY, 1),
                new EmptyBorder(40, 40, 40, 40)
        ));
        loginCard.setPreferredSize(new Dimension(400, 480));

        // Header section
        JPanel headerPanel = createHeaderPanel();
        loginCard.add(headerPanel);
        loginCard.add(Box.createVerticalStrut(32));

        // Form section
        JPanel formPanel = createFormPanel();
        loginCard.add(formPanel);
        loginCard.add(Box.createVerticalStrut(24));

        // Button section
        JPanel buttonPanel = createButtonPanel();
        loginCard.add(buttonPanel);
        loginCard.add(Box.createVerticalStrut(16));

        // Status section
        loginCard.add(statusLabel);

        // Add login card to main container
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        mainContainer.add(loginCard, gbc);

        add(mainContainer, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(CLEAN_WHITE);

        // Title
        JLabel titleLabel = new JLabel("GlobeMed Healthcare");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_BLUE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Staff Login Portal");
        subtitleLabel.setFont(HEADER_FONT);
        subtitleLabel.setForeground(TEXT_GRAY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(8));
        headerPanel.add(subtitleLabel);

        return headerPanel;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CLEAN_WHITE);
        GridBagConstraints gbc = new GridBagConstraints();

        // Username field
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 8, 0);
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(LABEL_FONT);
        usernameLabel.setForeground(TEXT_GRAY);
        formPanel.add(usernameLabel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 16, 0);
        formPanel.add(usernameField, gbc);

        // Password field
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 8, 0);
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(LABEL_FONT);
        passwordLabel.setForeground(TEXT_GRAY);
        formPanel.add(passwordLabel, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 0, 0);
        formPanel.add(passwordField, gbc);

        return formPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        buttonPanel.setBackground(CLEAN_WHITE);
        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);
        return buttonPanel;
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

        // Enhanced keyboard navigation
        usernameField.addActionListener(e -> passwordField.requestFocus());
        passwordField.addActionListener(e -> performLogin());

        // Set initial focus
        SwingUtilities.invokeLater(() -> usernameField.requestFocus());
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showStatus("Please enter both username and password", true);
            return;
        }

        // Show loading state
        setLoginState(false);
        showStatus("Authenticating...", false);

        SwingWorker<Staff, Void> worker = new SwingWorker<Staff, Void>() {
            @Override
            protected Staff doInBackground() throws Exception {
                return authenticateUser(username, password);
            }

            @Override
            protected void done() {
                setLoginState(true);
                try {
                    Staff staff = get();
                    if (staff != null) {
                        showStatus("Login successful! Welcome " + staff.getName(), false);
                        statusLabel.setForeground(HEALTHCARE_GREEN);

                        // Delay to show success message
                        Timer timer = new Timer(1000, e -> {
                            parentFrame.setCurrentUser(staff);
                            parentFrame.showMainInterface();
                        });
                        timer.setRepeats(false);
                        timer.start();
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

    private void setLoginState(boolean enabled) {
        loginButton.setEnabled(enabled);
        usernameField.setEnabled(enabled);
        passwordField.setEnabled(enabled);

        if (!enabled) {
            loginButton.setText("Authenticating...");
            loginButton.setBackground(PRIMARY_BLUE.brighter());
        } else {
            loginButton.setText("Login");
            loginButton.setBackground(PRIMARY_BLUE);
        }
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
        if (isError) {
            statusLabel.setForeground(ERROR_RED);
        } else if (message.contains("successful")) {
            statusLabel.setForeground(HEALTHCARE_GREEN);
        } else {
            statusLabel.setForeground(TEXT_GRAY);
        }
    }

    public void reset() {
        usernameField.setText("");
        passwordField.setText("");
        statusLabel.setText(" ");
        statusLabel.setForeground(TEXT_GRAY);
        setLoginState(true);
        SwingUtilities.invokeLater(() -> usernameField.requestFocus());
    }
}

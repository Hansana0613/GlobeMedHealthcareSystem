/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.gui;

import com.globemed.models.Staff;
import com.globemed.services.RoleManagementService;
import com.globemed.patterns.bridge.DatabasePermissionImpl;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Main application frame
 *
 * @author Hansana
 */
public class MainFrame extends JFrame {

    private Staff currentUser;
    private RoleManagementService roleService;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JMenuBar menuBar;
    private JToolBar toolBar;
    private JLabel statusLabel;
    private JLabel userLabel;

    // Panels
    private LoginPanel loginPanel;
    private PatientManagementPanel patientPanel;
    private AppointmentPanel appointmentPanel;
    private BillingPanel billingPanel;
    private ReportsPanel reportsPanel;
    private DashboardPanel dashboardPanel;

    // Cards
    private static final String LOGIN_CARD = "LOGIN";
    private static final String DASHBOARD_CARD = "DASHBOARD";
    private static final String PATIENTS_CARD = "PATIENTS";
    private static final String APPOINTMENTS_CARD = "APPOINTMENTS";
    private static final String BILLING_CARD = "BILLING";
    private static final String STAFF_CARD = "STAFF";
    private static final String REPORTS_CARD = "REPORTS";

    public MainFrame() {
        initializeServices();
        initializeComponents();
        layoutComponents();
        setupEventHandlers();
        // Defer showing login interface to ensure all components are initialized
        SwingUtilities.invokeLater(() -> showLoginInterface());
    }

    private void initializeServices() {
        roleService = new RoleManagementService(new DatabasePermissionImpl());
    }

    private void initializeComponents() {
        setTitle("GlobeMed Healthcare Management System");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Initialize layout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Initialize panels
        loginPanel = new LoginPanel(this);
        dashboardPanel = new DashboardPanel(this);
        patientPanel = new PatientManagementPanel(this);
        appointmentPanel = new AppointmentPanel(this);
        billingPanel = new BillingPanel(this);
        reportsPanel = new ReportsPanel(getCurrentUser());

        // Add panels to card layout
        mainPanel.add(loginPanel, LOGIN_CARD);

        // Set default button
        if (loginPanel != null) {
            getRootPane().setDefaultButton(loginPanel.getLoginButton());
        }
        mainPanel.add(dashboardPanel, DASHBOARD_CARD);
        mainPanel.add(patientPanel, PATIENTS_CARD);
        mainPanel.add(appointmentPanel, APPOINTMENTS_CARD);
        mainPanel.add(billingPanel, BILLING_CARD);
        mainPanel.add(reportsPanel, REPORTS_CARD);

        // Initialize menu bar (hidden initially)
        createMenuBar();

        // Initialize toolbar (hidden initially)
        createToolBar();

        // Initialize status bar
        createStatusBar();
    }

    private void createMenuBar() {
        menuBar = new JMenuBar();

        // File Menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');

        JMenuItem exitItem = new JMenuItem("Exit", 'x');
        exitItem.addActionListener(e -> exitApplication());

        fileMenu.add(exitItem);

        // Modules Menu
        JMenu modulesMenu = new JMenu("Modules");
        modulesMenu.setMnemonic('M');

        JMenuItem dashboardItem = new JMenuItem("Dashboard", 'D');
        dashboardItem.addActionListener(e -> showCard(DASHBOARD_CARD));

        JMenuItem patientsItem = new JMenuItem("Patient Management", 'P');
        patientsItem.addActionListener(e -> showCard(PATIENTS_CARD));

        JMenuItem appointmentsItem = new JMenuItem("Appointments", 'A');
        appointmentsItem.addActionListener(e -> showCard(APPOINTMENTS_CARD));

        JMenuItem billingItem = new JMenuItem("Billing", 'B');
        billingItem.addActionListener(e -> showCard(BILLING_CARD));

        JMenuItem staffItem = new JMenuItem("Staff Management", 'S');
        staffItem.addActionListener(e -> showCard(STAFF_CARD));

        JMenuItem reportsItem = new JMenuItem("Reports", 'R');
        reportsItem.addActionListener(e -> showCard(REPORTS_CARD));

        modulesMenu.add(dashboardItem);
        modulesMenu.addSeparator();
        modulesMenu.add(patientsItem);
        modulesMenu.add(appointmentsItem);
        modulesMenu.add(billingItem);
        modulesMenu.addSeparator();
        modulesMenu.add(staffItem);
        modulesMenu.add(reportsItem);

        // Tools Menu
        JMenu toolsMenu = new JMenu("Tools");
        toolsMenu.setMnemonic('T');

        JMenuItem securityDemoItem = new JMenuItem("Security Demo");
        securityDemoItem.addActionListener(e -> showSecurityDemo());

        JMenuItem patternDemoItem = new JMenuItem("Design Patterns Demo");
        patternDemoItem.addActionListener(e -> showPatternDemo());

        toolsMenu.add(securityDemoItem);
        toolsMenu.add(patternDemoItem);

        // Help Menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic('H');

        JMenuItem aboutItem = new JMenuItem("About", 'A');
        aboutItem.addActionListener(e -> showAboutDialog());

        helpMenu.add(aboutItem);

        // Add menus to menu bar
        if (menuBar != null) {
            menuBar.add(fileMenu);
            menuBar.add(modulesMenu);
            menuBar.add(toolsMenu);
            menuBar.add(Box.createHorizontalGlue());
            menuBar.add(helpMenu);
        }

        if (menuBar != null) {
            setJMenuBar(menuBar);
        }
    }

    private void createToolBar() {
        toolBar = new JToolBar();
        toolBar.setFloatable(false);

        // Dashboard button
        JButton dashboardBtn = createToolBarButton("Dashboard", "dashboard.png",
                e -> showCard(DASHBOARD_CARD));

        // Patient Management button
        JButton patientsBtn = createToolBarButton("Patients", "patients.png",
                e -> showCard(PATIENTS_CARD));

        // Appointments button
        JButton appointmentsBtn = createToolBarButton("Appointments", "appointments.png",
                e -> showCard(APPOINTMENTS_CARD));

        // Billing button
        JButton billingBtn = createToolBarButton("Billing", "billing.png",
                e -> showCard(BILLING_CARD));

        // Staff button
        JButton staffBtn = createToolBarButton("Staff", "staff.png",
                e -> showCard(STAFF_CARD));

        // Reports button
        JButton reportsBtn = createToolBarButton("Reports", "reports.png",
                e -> showCard(REPORTS_CARD));

        if (toolBar != null) {
            toolBar.add(dashboardBtn);
            toolBar.addSeparator();
            toolBar.add(patientsBtn);
            toolBar.add(appointmentsBtn);
            toolBar.add(billingBtn);
            toolBar.addSeparator();
            toolBar.add(staffBtn);
            toolBar.add(reportsBtn);

            // Add spacer and logout button
            toolBar.add(Box.createHorizontalGlue());

            JButton logoutBtn = createToolBarButton("Logout", "logout.png",
                    e -> logout());
            logoutBtn.setForeground(Color.RED);
            toolBar.add(logoutBtn);
        }
    }

    private JButton createToolBarButton(String text, String iconName, ActionListener action) {
        JButton button = new JButton(text);
        button.setToolTipText(text);
        button.addActionListener(action);
        button.setFocusable(false);
        return button;
    }

    private void createStatusBar() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createLoweredBevelBorder());

        statusLabel = new JLabel("Ready");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));

        userLabel = new JLabel();
        userLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        userLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        if (statusLabel != null && userLabel != null) {
            statusPanel.add(statusLabel, BorderLayout.WEST);
            statusPanel.add(userLabel, BorderLayout.EAST);
        }

        add(statusPanel, BorderLayout.SOUTH);
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());
        if (mainPanel != null) {
            add(mainPanel, BorderLayout.CENTER);
        }

        // Initially hide menu bar and toolbar
        if (menuBar != null) {
            menuBar.setVisible(false);
        }
        if (toolBar != null) {
            toolBar.setVisible(false);
        }
    }

    private void setupEventHandlers() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });
    }

    public void showLoginInterface() {
        if (menuBar != null) {
            menuBar.setVisible(false);
        }
        if (toolBar != null && toolBar.getParent() != null) {
            remove(toolBar);
        }
        if (statusLabel != null) {
            statusLabel.setText("Please log in to continue");
        }
        if (userLabel != null) {
            userLabel.setText("");
        }
        cardLayout.show(mainPanel, LOGIN_CARD);
        if (loginPanel != null) {
            loginPanel.reset();
        }
        revalidate();
        repaint();
    }

    public void showMainInterface() {
        if (menuBar != null) {
            menuBar.setVisible(true);
        }
        if (toolBar != null) {
            add(toolBar, BorderLayout.NORTH);
        }
        updateUserInfo();
        showCard(DASHBOARD_CARD);
        revalidate();
        repaint();
    }

    private void showCard(String cardName) {
        if (cardLayout != null && mainPanel != null) {
            cardLayout.show(mainPanel, cardName);
        }
        updateStatusBasedOnCard(cardName);

        // Refresh panel data if needed
        switch (cardName) {
            case PATIENTS_CARD:
//                patientPanel.refreshData();
                break;
            case APPOINTMENTS_CARD:
//                appointmentPanel.refreshData();
                break;
            case BILLING_CARD:
//                billingPanel.refreshData();
                break;
            case REPORTS_CARD:
//                reportsPanel.refreshData();
                break;
            case DASHBOARD_CARD:
                if (dashboardPanel != null) {
                    dashboardPanel.refreshData();
                }
                break;
        }
    }

    private void updateStatusBasedOnCard(String cardName) {
        if (statusLabel == null) {
            return;
        }

        switch (cardName) {
            case DASHBOARD_CARD:
                statusLabel.setText("Dashboard - System Overview");
                break;
            case PATIENTS_CARD:
                statusLabel.setText("Patient Management - Manage patient records");
                break;
            case APPOINTMENTS_CARD:
                statusLabel.setText("Appointment Management - Schedule and manage appointments");
                break;
            case BILLING_CARD:
                statusLabel.setText("Billing Management - Process bills and insurance claims");
                break;
            case STAFF_CARD:
                statusLabel.setText("Staff Management - Manage staff and roles");
                break;
            case REPORTS_CARD:
                statusLabel.setText("Reports - Generate system reports");
                break;
        }
    }

    private void updateUserInfo() {
        if (currentUser != null && userLabel != null && statusLabel != null) {
            userLabel.setText("Logged in as: " + currentUser.getName() + " | Role ID: " + currentUser.getRoleId());
            statusLabel.setText("Welcome to GlobeMed Healthcare System");
        }
    }

    private void logout() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            currentUser = null;
            showLoginInterface();
        }
    }

    private void exitApplication() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to exit the application?",
                "Confirm Exit",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            dispose();
            System.exit(0);
        }
    }

    private void showSecurityDemo() {
        JOptionPane.showMessageDialog(this,
                "Security Demo:\n\n"
                + "• Chain of Responsibility: Access control pipeline\n"
                + "• Decorator: Security layers (encryption, audit, access control)\n"
                + "• Memento: Patient data versioning\n"
                + "• Flyweight: Optimized permission objects\n\n"
                + "Check console output for detailed security operations.",
                "Security Patterns Demo",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showPatternDemo() {
        StringBuilder demo = new StringBuilder();
        demo.append("Design Patterns Implementation:\n\n");
        demo.append("Part A - Patient Records:\n");
        demo.append("• Chain of Responsibility: Access control\n");
        demo.append("• Decorator: Security layers\n\n");
        demo.append("Part B - Appointments:\n");
        demo.append("• Mediator: Component communication\n");
        demo.append("• Observer: Real-time notifications\n\n");
        demo.append("Part C - Billing:\n");
        demo.append("• Composite: Hierarchical bill structure\n");
        demo.append("• Chain of Responsibility: Claim processing\n\n");
        demo.append("Part D - Staff & Roles:\n");
        demo.append("• Composite: Role hierarchy\n");
        demo.append("• Bridge: Permission abstraction\n\n");
        demo.append("Part E - Reports:\n");
        demo.append("• Visitor: Report generation\n");
        demo.append("• Builder: Complex report construction\n\n");
        demo.append("Part F - Security:\n");
        demo.append("• Multiple patterns for comprehensive security\n");

        JOptionPane.showMessageDialog(this, demo.toString(),
                "Design Patterns Overview", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
                "GlobeMed Healthcare Management System\n\n"
                + "Version 1.0\n"
                + "Developed for Design Patterns Assignment\n\n"
                + "Features:\n"
                + "• Patient Record Management\n"
                + "• Appointment Scheduling\n"
                + "• Billing & Insurance Processing\n"
                + "• Staff & Role Management\n"
                + "• Report Generation\n"
                + "• Advanced Security Features\n\n"
                + "Built with Java Swing & MySQL",
                "About GlobeMed",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // Getters and setters
    public Staff getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(Staff currentUser) {
        this.currentUser = currentUser;

        // Update ReportsPanel with new user information
        if (reportsPanel != null) {
            reportsPanel.updateUser(currentUser);
        }
    }

    public RoleManagementService getRoleService() {
        return roleService;
    }

    public void setStatus(String status) {
        if (statusLabel != null) {
            statusLabel.setText(status);
        }
    }
}

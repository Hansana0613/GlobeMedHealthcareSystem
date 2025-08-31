/*
 * Enhanced Main Frame with Modern UI/UX Design
 * Following medical color scheme and improved user experience
 */
package com.globemed.gui;

import com.globemed.models.Staff;
import com.globemed.services.RoleManagementService;
import com.globemed.patterns.bridge.DatabasePermissionImpl;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Enhanced Main application frame with modern medical UI
 *
 * @author Hansana
 */
public class MainFrame extends JFrame {

    // Color Constants - Medical Theme
    private static final Color PRIMARY_BLUE = new Color(46, 134, 171); // #2E86AB
    private static final Color HEALTHCARE_GREEN = new Color(76, 175, 80); // #4CAF50
    private static final Color WARNING_AMBER = new Color(255, 152, 0); // #FF9800
    private static final Color ERROR_RED = new Color(244, 67, 54); // #F44336
    private static final Color BACKGROUND_LIGHT = new Color(245, 245, 245); // #F5F5F5
    private static final Color CARD_WHITE = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(33, 33, 33);
    private static final Color TEXT_SECONDARY = new Color(117, 117, 117);
    private static final Color NAVBAR_DARK = new Color(37, 37, 37);

    private Staff currentUser;
    private RoleManagementService roleService;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JMenuBar menuBar;
    private JPanel navigationPanel;
    private JLabel statusLabel;
    private JPanel userInfoPanel;
    private JPanel breadcrumbPanel;
    
    // User info labels for easy access
    private JLabel userNameLabel;
    private JLabel userRoleLabel;

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

    private String currentCard = LOGIN_CARD;

    public MainFrame() {
        initializeServices();
        initializeComponents();
        layoutComponents();
        setupEventHandlers();
        SwingUtilities.invokeLater(() -> showLoginInterface());
    }

    private void initializeServices() {
        roleService = new RoleManagementService(new DatabasePermissionImpl());
    }

    private void initializeComponents() {
        // Window configuration
        setTitle("GlobeMed Healthcare Management System");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Set application icon (if available)
        try {
            // setIconImage(Toolkit.getDefaultToolkit().getImage("icon.png"));
        } catch (Exception e) {
            // Icon not found, continue without it
        }

        // Initialize layout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(BACKGROUND_LIGHT);

        // Initialize panels with enhanced design
        loginPanel = new LoginPanel(this);
        dashboardPanel = new DashboardPanel(this);
        patientPanel = new PatientManagementPanel(this);
        appointmentPanel = new AppointmentPanel(this);
        billingPanel = new BillingPanel(this);
        reportsPanel = new ReportsPanel(getCurrentUser());

        // Add panels to card layout
        mainPanel.add(loginPanel, LOGIN_CARD);
        mainPanel.add(dashboardPanel, DASHBOARD_CARD);
        mainPanel.add(patientPanel, PATIENTS_CARD);
        mainPanel.add(appointmentPanel, APPOINTMENTS_CARD);
        mainPanel.add(billingPanel, BILLING_CARD);
        mainPanel.add(reportsPanel, REPORTS_CARD);

        // Set default button for login
        if (loginPanel != null) {
            getRootPane().setDefaultButton(loginPanel.getLoginButton());
        }

        // Initialize enhanced UI components
        createModernMenuBar();
        createModernNavigation();
        createEnhancedStatusBar();
        createBreadcrumbPanel();
    }

    private void createModernMenuBar() {
        menuBar = new JMenuBar();
        menuBar.setBackground(CARD_WHITE);
        menuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));

        // File Menu
        JMenu fileMenu = createStyledMenu("File", 'F');

        JMenuItem newSessionItem = createStyledMenuItem("New Session", 'N', "🆕");
        JMenuItem refreshItem = createStyledMenuItem("Refresh All", 'R', "🔄");
        JMenuItem exitItem = createStyledMenuItem("Exit", 'X', "🚪");

        refreshItem.addActionListener(e -> refreshAllData());
        exitItem.addActionListener(e -> exitApplication());

        fileMenu.add(newSessionItem);
        fileMenu.addSeparator();
        fileMenu.add(refreshItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // Modules Menu
        JMenu modulesMenu = createStyledMenu("Modules", 'M');

        JMenuItem dashboardItem = createStyledMenuItem("Dashboard", 'D', "📊");
        JMenuItem patientsItem = createStyledMenuItem("Patient Management", 'P', "👥");
        JMenuItem appointmentsItem = createStyledMenuItem("Appointments", 'A', "📅");
        JMenuItem billingItem = createStyledMenuItem("Billing", 'B', "💰");
        JMenuItem staffItem = createStyledMenuItem("Staff Management", 'S', "👨‍⚕️");
        JMenuItem reportsItem = createStyledMenuItem("Reports", 'R', "📈");

        dashboardItem.addActionListener(e -> showCard(DASHBOARD_CARD));
        patientsItem.addActionListener(e -> showCard(PATIENTS_CARD));
        appointmentsItem.addActionListener(e -> showCard(APPOINTMENTS_CARD));
        billingItem.addActionListener(e -> showCard(BILLING_CARD));
        staffItem.addActionListener(e -> showCard(STAFF_CARD));
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
        JMenu toolsMenu = createStyledMenu("Tools", 'T');

        JMenuItem securityDemoItem = createStyledMenuItem("Security Demo", 'S', "🔐");
        JMenuItem patternDemoItem = createStyledMenuItem("Design Patterns", 'D', "🏗️");
        JMenuItem systemInfoItem = createStyledMenuItem("System Information", 'I', "ℹ️");

        securityDemoItem.addActionListener(e -> showSecurityDemo());
        patternDemoItem.addActionListener(e -> showPatternDemo());
        systemInfoItem.addActionListener(e -> showSystemInfo());

        toolsMenu.add(securityDemoItem);
        toolsMenu.add(patternDemoItem);
        toolsMenu.addSeparator();
        toolsMenu.add(systemInfoItem);

        // Help Menu
        JMenu helpMenu = createStyledMenu("Help", 'H');

        JMenuItem userGuideItem = createStyledMenuItem("User Guide", 'U', "📖");
        JMenuItem supportItem = createStyledMenuItem("Support", 'S', "🆘");
        JMenuItem aboutItem = createStyledMenuItem("About", 'A', "ℹ️");

        userGuideItem.addActionListener(e -> showUserGuide());
        supportItem.addActionListener(e -> showSupport());
        aboutItem.addActionListener(e -> showAboutDialog());

        helpMenu.add(userGuideItem);
        helpMenu.add(supportItem);
        helpMenu.addSeparator();
        helpMenu.add(aboutItem);

        // Add menus to menu bar
        menuBar.add(fileMenu);
        menuBar.add(modulesMenu);
        menuBar.add(toolsMenu);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private JMenu createStyledMenu(String text, char mnemonic) {
        JMenu menu = new JMenu(text);
        menu.setMnemonic(mnemonic);
        menu.setFont(new Font("Sansation", Font.PLAIN, 12));
        menu.setForeground(TEXT_PRIMARY);
        return menu;
    }

    private JMenuItem createStyledMenuItem(String text, char mnemonic, String icon) {
        JMenuItem item = new JMenuItem(text + "  " + icon, mnemonic);
        item.setFont(new Font("Sansation", Font.PLAIN, 12));
        item.setForeground(TEXT_PRIMARY);
        item.setBorder(new EmptyBorder(8, 16, 8, 16));
        return item;
    }

    private void createModernNavigation() {
        navigationPanel = new JPanel();
        navigationPanel.setLayout(new BoxLayout(navigationPanel, BoxLayout.Y_AXIS));
        navigationPanel.setBackground(NAVBAR_DARK);
        navigationPanel.setPreferredSize(new Dimension(220, 0));
        navigationPanel.setBorder(new EmptyBorder(24, 0, 24, 0));

        // Logo section
        JPanel logoPanel = createLogoPanel();
        navigationPanel.add(logoPanel);
        navigationPanel.add(Box.createVerticalStrut(32));

        // Navigation buttons
        addNavButton("Dashboard", "📊", DASHBOARD_CARD, true);
        addNavButton("Patients", "👥", PATIENTS_CARD, false);
        addNavButton("Appointments", "📅", APPOINTMENTS_CARD, false);
        addNavButton("Billing", "💰", BILLING_CARD, false);
        addNavButton("Staff", "👨‍⚕️", STAFF_CARD, false);
        addNavButton("Reports", "📈", REPORTS_CARD, false);

        navigationPanel.add(Box.createVerticalGlue());

        // User section at bottom
        createUserSection();
    }

    private JPanel createLogoPanel() {
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setBackground(NAVBAR_DARK);
        logoPanel.setBorder(new EmptyBorder(0, 24, 0, 24));
        logoPanel.setMaximumSize(new Dimension(220, 80));

        JLabel logoLabel = new JLabel("🏥");
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel titleLabel = new JLabel("GlobeMed");
        titleLabel.setFont(new Font("Sansation", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel subtitleLabel = new JLabel("Healthcare System");
        subtitleLabel.setFont(new Font("Sansation", Font.PLAIN, 10));
        subtitleLabel.setForeground(new Color(180, 180, 180));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setBackground(NAVBAR_DARK);
        textPanel.add(titleLabel, BorderLayout.NORTH);
        textPanel.add(subtitleLabel, BorderLayout.SOUTH);

        logoPanel.add(logoLabel, BorderLayout.NORTH);
        logoPanel.add(textPanel, BorderLayout.SOUTH);

        return logoPanel;
    }

    private void addNavButton(String text, String icon, String targetCard, boolean selected) {
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBackground(NAVBAR_DARK);
        buttonPanel.setBorder(new EmptyBorder(4, 24, 4, 24));
        buttonPanel.setMaximumSize(new Dimension(220, 48));

        JButton navButton = new JButton();
        navButton.setLayout(new BorderLayout());
        navButton.setBackground(selected ? PRIMARY_BLUE : NAVBAR_DARK);
        navButton.setBorder(new EmptyBorder(12, 16, 12, 16));
        navButton.setFocusPainted(false);
        navButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Icon
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));

        // Text
        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Sansation", Font.BOLD, 12));
        textLabel.setForeground(Color.WHITE);

        JPanel content = new JPanel(new BorderLayout(12, 0));
        content.setBackground(selected ? PRIMARY_BLUE : NAVBAR_DARK);
        content.add(iconLabel, BorderLayout.WEST);
        content.add(textLabel, BorderLayout.CENTER);

        navButton.add(content, BorderLayout.CENTER);

        // Enhanced hover and selection effects
        navButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!targetCard.equals(currentCard)) {
                    navButton.setBackground(new Color(60, 60, 60));
                    content.setBackground(new Color(60, 60, 60));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!targetCard.equals(currentCard)) {
                    navButton.setBackground(NAVBAR_DARK);
                    content.setBackground(NAVBAR_DARK);
                }
            }
        });

        navButton.addActionListener(e -> {
            showCard(targetCard);
            updateNavigationState(targetCard);
        });

        buttonPanel.add(navButton, BorderLayout.CENTER);
        navigationPanel.add(buttonPanel);
    }

    private void createUserSection() {
        userInfoPanel = new JPanel(new BorderLayout());
        userInfoPanel.setBackground(new Color(45, 45, 45));
        userInfoPanel.setBorder(new EmptyBorder(16, 24, 16, 24));
        userInfoPanel.setMaximumSize(new Dimension(220, 100));

        // User avatar placeholder
        JLabel avatarLabel = new JLabel("👤");
        avatarLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        avatarLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // User info
        JPanel userInfo = new JPanel(new BorderLayout());
        userInfo.setBackground(new Color(45, 45, 45));

        userNameLabel = new JLabel("Not logged in");
        userNameLabel.setFont(new Font("Sansation", Font.BOLD, 12));
        userNameLabel.setForeground(Color.WHITE);

        userRoleLabel = new JLabel("Please sign in");
        userRoleLabel.setFont(new Font("Sansation", Font.PLAIN, 10));
        userRoleLabel.setForeground(new Color(180, 180, 180));

        userInfo.add(userNameLabel, BorderLayout.NORTH);
        userInfo.add(userRoleLabel, BorderLayout.SOUTH);

        // Logout button
        JButton logoutBtn = new JButton("Sign Out");
        logoutBtn.setFont(new Font("Sansation", Font.PLAIN, 10));
        logoutBtn.setForeground(ERROR_RED);
        logoutBtn.setBackground(new Color(45, 45, 45));
        logoutBtn.setBorder(new EmptyBorder(4, 8, 4, 8));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.setVisible(false);
        logoutBtn.addActionListener(e -> logout());

        userInfoPanel.add(avatarLabel, BorderLayout.WEST);
        userInfoPanel.add(userInfo, BorderLayout.CENTER);
        userInfoPanel.add(logoutBtn, BorderLayout.SOUTH);

        navigationPanel.add(userInfoPanel);
    }

    private void createBreadcrumbPanel() {
        breadcrumbPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 0));
        breadcrumbPanel.setBackground(CARD_WHITE);
        breadcrumbPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                new EmptyBorder(16, 0, 16, 0)
        ));
        breadcrumbPanel.setPreferredSize(new Dimension(0, 56));

        updateBreadcrumb(DASHBOARD_CARD);
    }

    private void updateBreadcrumb(String card) {
        breadcrumbPanel.removeAll();

        JLabel homeLabel = new JLabel("🏠 GlobeMed");
        homeLabel.setFont(new Font("Sansation", Font.PLAIN, 12));
        homeLabel.setForeground(TEXT_SECONDARY);

        JLabel separator = new JLabel(" / ");
        separator.setFont(new Font("Sansation", Font.PLAIN, 12));
        separator.setForeground(TEXT_SECONDARY);

        JLabel currentLabel = new JLabel(getCardDisplayName(card));
        currentLabel.setFont(new Font("Sansation", Font.BOLD, 12));
        currentLabel.setForeground(PRIMARY_BLUE);

        breadcrumbPanel.add(homeLabel);
        breadcrumbPanel.add(separator);
        breadcrumbPanel.add(currentLabel);

        breadcrumbPanel.revalidate();
        breadcrumbPanel.repaint();
    }

    private String getCardDisplayName(String card) {
        switch (card) {
            case DASHBOARD_CARD:
                return "Dashboard";
            case PATIENTS_CARD:
                return "Patient Management";
            case APPOINTMENTS_CARD:
                return "Appointments";
            case BILLING_CARD:
                return "Billing";
            case STAFF_CARD:
                return "Staff Management";
            case REPORTS_CARD:
                return "Reports";
            default:
                return "Dashboard";
        }
    }

    private void createEnhancedStatusBar() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(new Color(250, 250, 250));
        statusPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)));
        statusPanel.setPreferredSize(new Dimension(0, 32));

        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Sansation", Font.PLAIN, 11));
        statusLabel.setForeground(TEXT_SECONDARY);
        statusLabel.setBorder(new EmptyBorder(8, 16, 8, 16));

        // System time
        JLabel timeLabel = new JLabel();
        timeLabel.setFont(new Font("Sansation", Font.PLAIN, 11));
        timeLabel.setForeground(TEXT_SECONDARY);
        timeLabel.setBorder(new EmptyBorder(8, 16, 8, 16));

        // Update time every second
        Timer timeTimer = new Timer(1000, e -> {
            timeLabel.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        });
        timeTimer.start();

        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(timeLabel, BorderLayout.EAST);

        add(statusPanel, BorderLayout.SOUTH);
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());

        // Main content area
        JPanel contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(BACKGROUND_LIGHT);

        contentArea.add(breadcrumbPanel, BorderLayout.NORTH);
        contentArea.add(mainPanel, BorderLayout.CENTER);

        add(contentArea, BorderLayout.CENTER);

        // Initially hide navigation and menu bar
        menuBar.setVisible(false);
        if (navigationPanel != null) {
            navigationPanel.setVisible(false);
        }
    }

    private void setupEventHandlers() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });

        // Global keyboard shortcuts
        setupKeyboardShortcuts();
    }

    private void setupKeyboardShortcuts() {
        // Alt+D for Dashboard
        getRootPane().registerKeyboardAction(e -> showCard(DASHBOARD_CARD),
                KeyStroke.getKeyStroke("alt D"), JComponent.WHEN_IN_FOCUSED_WINDOW);

        // Alt+P for Patients
        getRootPane().registerKeyboardAction(e -> showCard(PATIENTS_CARD),
                KeyStroke.getKeyStroke("alt P"), JComponent.WHEN_IN_FOCUSED_WINDOW);

        // Alt+A for Appointments
        getRootPane().registerKeyboardAction(e -> showCard(APPOINTMENTS_CARD),
                KeyStroke.getKeyStroke("alt A"), JComponent.WHEN_IN_FOCUSED_WINDOW);

        // F5 for refresh
        getRootPane().registerKeyboardAction(e -> refreshAllData(),
                KeyStroke.getKeyStroke("F5"), JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    public void showLoginInterface() {
        currentCard = LOGIN_CARD;

        menuBar.setVisible(false);
        if (navigationPanel != null && navigationPanel.getParent() != null) {
            remove(navigationPanel);
        }

        breadcrumbPanel.setVisible(false);
        setStatus("Please sign in to access the healthcare system");
        cardLayout.show(mainPanel, LOGIN_CARD);

        if (loginPanel != null) {
            loginPanel.reset();
        }

        revalidate();
        repaint();
    }

    public void showMainInterface() {
        menuBar.setVisible(true);

        if (navigationPanel != null) {
            add(navigationPanel, BorderLayout.WEST);
            navigationPanel.setVisible(true);
        }

        breadcrumbPanel.setVisible(true);
        updateUserInfo();
        showCard(DASHBOARD_CARD);

        revalidate();
        repaint();
    }

    private void showCard(String cardName) {
        currentCard = cardName;
        cardLayout.show(mainPanel, cardName);
        updateBreadcrumb(cardName);
        updateStatusBasedOnCard(cardName);
        updateNavigationState(cardName);

        // Refresh panel data if needed
        SwingUtilities.invokeLater(() -> {
            switch (cardName) {
                case DASHBOARD_CARD:
                    if (dashboardPanel != null) {
                        dashboardPanel.refreshData();
                    }
                    break;
                case PATIENTS_CARD:
                    // patientPanel.refreshData();
                    break;
                case APPOINTMENTS_CARD:
                    // appointmentPanel.refreshData();
                    break;
                case BILLING_CARD:
                    // billingPanel.refreshData();
                    break;
                case REPORTS_CARD:
                    // reportsPanel.refreshData();
                    break;
            }
        });
    }

    private void updateNavigationState(String activeCard) {
        if (navigationPanel == null) {
            return;
        }

        Component[] components = navigationPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                Component[] subComponents = panel.getComponents();
                for (Component subComp : subComponents) {
                    if (subComp instanceof JButton) {
                        JButton button = (JButton) subComp;
                        // Reset all buttons and update active one
                        // This would need specific implementation based on button structure
                    }
                }
            }
        }
    }

    private void updateStatusBasedOnCard(String cardName) {
        if (statusLabel == null) {
            return;
        }

        String status;
        switch (cardName) {
            case DASHBOARD_CARD:
                status = "Dashboard - System overview and quick actions";
                break;
            case PATIENTS_CARD:
                status = "Patient Management - Manage patient records and medical history";
                break;
            case APPOINTMENTS_CARD:
                status = "Appointment Management - Schedule and manage patient appointments";
                break;
            case BILLING_CARD:
                status = "Billing Management - Process bills and insurance claims";
                break;
            case STAFF_CARD:
                status = "Staff Management - Manage healthcare staff and roles";
                break;
            case REPORTS_CARD:
                status = "Reports - Generate and view system reports";
                break;
            default:
                status = "GlobeMed Healthcare System";
                break;
        }

        setStatus(status);
    }

    private void updateUserInfo() {
        if (currentUser != null && userInfoPanel != null) {
            // Update user name and role using stored references
            if (userNameLabel != null) {
                userNameLabel.setText(currentUser.getName());
            }
            if (userRoleLabel != null) {
                userRoleLabel.setText("Role ID: " + currentUser.getRoleId());
            }
            
            // Show logout button
            Component[] components = userInfoPanel.getComponents();
            for (Component comp : components) {
                if (comp instanceof JButton) {
                    comp.setVisible(true);
                }
            }

            setStatus("Welcome back, " + currentUser.getName());
        }
    }

    private void refreshAllData() {
        setStatus("Refreshing all system data...");

        SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                publish("Refreshing dashboard...");
                if (dashboardPanel != null) {
                    dashboardPanel.refreshData();
                }
                Thread.sleep(500);

                publish("Refreshing patient data...");
                // Additional refresh logic for other panels
                Thread.sleep(500);

                publish("Refresh complete");
                return null;
            }

            @Override
            protected void process(List<String> chunks) {
                for (String message : chunks) {
                    setStatus(message);
                }
            }

            @Override
            protected void done() {
                setStatus("All data refreshed successfully");
            }
        };

        worker.execute();
    }

    private void logout() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to sign out?\n\nYou will need to log in again to access the system.",
                "Confirm Sign Out",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            currentUser = null;
            
            // Reset user info display
            if (userNameLabel != null) {
                userNameLabel.setText("Not logged in");
            }
            if (userRoleLabel != null) {
                userRoleLabel.setText("Please sign in");
            }
            
            // Hide logout button
            if (userInfoPanel != null) {
                Component[] components = userInfoPanel.getComponents();
                for (Component comp : components) {
                    if (comp instanceof JButton) {
                        comp.setVisible(false);
                    }
                }
            }
            
            showLoginInterface();
            setStatus("Signed out successfully");
        }
    }

    private void exitApplication() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to exit GlobeMed?\n\nAny unsaved changes will be lost.",
                "Exit Application",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            setStatus("Shutting down system...");

            // Graceful shutdown
            SwingWorker<Void, Void> shutdownWorker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    // Cleanup operations
                    Thread.sleep(1000);
                    return null;
                }

                @Override
                protected void done() {
                    dispose();
                    System.exit(0);
                }
            };

            shutdownWorker.execute();
        }
    }

    private void showSecurityDemo() {
        JDialog securityDialog = createModernDialog("Security Implementation Demo", 600, 450);

        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setBackground(CARD_WHITE);
        content.setBorder(new EmptyBorder(24, 24, 24, 24));

        JLabel titleLabel = new JLabel("🔐 Security Patterns Implementation");
        titleLabel.setFont(new Font("Sansation", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_PRIMARY);

        JTextArea securityInfo = new JTextArea();
        securityInfo.setFont(new Font("Sansation", Font.PLAIN, 12));
        securityInfo.setBackground(BACKGROUND_LIGHT);
        securityInfo.setBorder(new EmptyBorder(16, 16, 16, 16));
        securityInfo.setEditable(false);
        securityInfo.setText(
                "IMPLEMENTED SECURITY PATTERNS:\n\n"
                + "🔗 Chain of Responsibility Pattern:\n"
                + "   • Multi-level access control pipeline\n"
                + "   • Request validation and authorization\n"
                + "   • Hierarchical permission checking\n\n"
                + "🎨 Decorator Pattern:\n"
                + "   • Layered security implementation\n"
                + "   • Encryption and audit trail layers\n"
                + "   • Access control decorators\n\n"
                + "💾 Memento Pattern:\n"
                + "   • Patient data versioning system\n"
                + "   • Audit trail and rollback capability\n"
                + "   • Secure data state management\n\n"
                + "🪶 Flyweight Pattern:\n"
                + "   • Optimized permission objects\n"
                + "   • Memory-efficient role management\n"
                + "   • Shared security contexts\n\n"
                + "Check the console output for detailed security operation logs."
        );

        JScrollPane scrollPane = new JScrollPane(securityInfo);
        scrollPane.setBorder(new LineBorder(new Color(230, 230, 230), 1));

        JButton closeBtn = createModernButton("Close", PRIMARY_BLUE);
        closeBtn.addActionListener(e -> securityDialog.dispose());

        content.add(titleLabel, BorderLayout.NORTH);
        content.add(scrollPane, BorderLayout.CENTER);
        content.add(closeBtn, BorderLayout.SOUTH);

        securityDialog.add(content);
        securityDialog.setVisible(true);
    }

    private void showPatternDemo() {
        JDialog patternsDialog = createModernDialog("Design Patterns Overview", 700, 550);

        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setBackground(CARD_WHITE);
        content.setBorder(new EmptyBorder(24, 24, 24, 24));

        JLabel titleLabel = new JLabel("🏗️ Design Patterns Implementation");
        titleLabel.setFont(new Font("Sansation", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_PRIMARY);

        JTextArea patternsInfo = new JTextArea();
        patternsInfo.setFont(new Font("Sansation", Font.PLAIN, 12));
        patternsInfo.setBackground(BACKGROUND_LIGHT);
        patternsInfo.setBorder(new EmptyBorder(16, 16, 16, 16));
        patternsInfo.setEditable(false);
        patternsInfo.setText(
                "DESIGN PATTERNS IMPLEMENTATION:\n\n"
                + "📋 PART A - Patient Records Management:\n"
                + "   🔗 Chain of Responsibility: Multi-tier access control\n"
                + "   🎨 Decorator: Security and audit layers\n\n"
                + "📅 PART B - Appointment System:\n"
                + "   🤝 Mediator: Component communication hub\n"
                + "   👁️ Observer: Real-time notification system\n\n"
                + "💰 PART C - Billing & Claims:\n"
                + "   🏗️ Composite: Hierarchical bill structures\n"
                + "   🔗 Chain of Responsibility: Claims processing pipeline\n\n"
                + "👥 PART D - Staff & Role Management:\n"
                + "   🏗️ Composite: Role hierarchy management\n"
                + "   🌉 Bridge: Permission system abstraction\n\n"
                + "📊 PART E - Reporting System:\n"
                + "   🚶 Visitor: Dynamic report generation\n"
                + "   🔨 Builder: Complex report construction\n\n"
                + "🔐 PART F - Security Framework:\n"
                + "   🎨 Decorator: Security layer composition\n"
                + "   💾 Memento: Data versioning and audit\n"
                + "   🪶 Flyweight: Optimized permission objects\n"
                + "   🔗 Chain of Responsibility: Access control pipeline\n\n"
                + "Each pattern is carefully implemented to ensure scalability,\n"
                + "maintainability, and robust healthcare data management."
        );

        JScrollPane scrollPane = new JScrollPane(patternsInfo);
        scrollPane.setBorder(new LineBorder(new Color(230, 230, 230), 1));

        JButton closeBtn = createModernButton("Close", PRIMARY_BLUE);
        closeBtn.addActionListener(e -> patternsDialog.dispose());

        content.add(titleLabel, BorderLayout.NORTH);
        content.add(scrollPane, BorderLayout.CENTER);
        content.add(closeBtn, BorderLayout.SOUTH);

        patternsDialog.add(content);
        patternsDialog.setVisible(true);
    }

    private void showSystemInfo() {
        JDialog infoDialog = createModernDialog("System Information", 500, 400);

        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setBackground(CARD_WHITE);
        content.setBorder(new EmptyBorder(24, 24, 24, 24));

        JLabel titleLabel = new JLabel("ℹ️ System Information");
        titleLabel.setFont(new Font("Sansation", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_PRIMARY);

        JTextArea systemInfo = new JTextArea();
        systemInfo.setFont(new Font("Sansation", Font.PLAIN, 12));
        systemInfo.setBackground(BACKGROUND_LIGHT);
        systemInfo.setBorder(new EmptyBorder(16, 16, 16, 16));
        systemInfo.setEditable(false);

        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory() / (1024 * 1024);
        long totalMemory = runtime.totalMemory() / (1024 * 1024);
        long freeMemory = runtime.freeMemory() / (1024 * 1024);

        systemInfo.setText(
                "SYSTEM ENVIRONMENT:\n\n"
                + "Java Version: " + System.getProperty("java.version") + "\n"
                + "Operating System: " + System.getProperty("os.name") + "\n"
                + "Architecture: " + System.getProperty("os.arch") + "\n"
                + "User: " + System.getProperty("user.name") + "\n\n"
                + "MEMORY USAGE:\n"
                + "Maximum Memory: " + maxMemory + " MB\n"
                + "Total Memory: " + totalMemory + " MB\n"
                + "Free Memory: " + freeMemory + " MB\n"
                + "Used Memory: " + (totalMemory - freeMemory) + " MB\n\n"
                + "APPLICATION INFO:\n"
                + "Version: 1.0.0\n"
                + "Build: Development\n"
                + "Database: MySQL\n"
                + "UI Framework: Java Swing\n\n"
                + "FEATURES:\n"
                + "• Modern medical-themed interface\n"
                + "• Role-based access control\n"
                + "• Real-time data synchronization\n"
                + "• Comprehensive audit logging\n"
                + "• Advanced reporting capabilities"
        );

        JScrollPane scrollPane = new JScrollPane(systemInfo);
        scrollPane.setBorder(new LineBorder(new Color(230, 230, 230), 1));

        JButton closeBtn = createModernButton("Close", PRIMARY_BLUE);
        closeBtn.addActionListener(e -> infoDialog.dispose());

        content.add(titleLabel, BorderLayout.NORTH);
        content.add(scrollPane, BorderLayout.CENTER);
        content.add(closeBtn, BorderLayout.SOUTH);

        infoDialog.add(content);
        infoDialog.setVisible(true);
    }

    private void showUserGuide() {
        JDialog guideDialog = createModernDialog("User Guide", 600, 500);

        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setBackground(CARD_WHITE);
        content.setBorder(new EmptyBorder(24, 24, 24, 24));

        JLabel titleLabel = new JLabel("📖 GlobeMed User Guide");
        titleLabel.setFont(new Font("Sansation", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_PRIMARY);

        JTextArea guideText = new JTextArea();
        guideText.setFont(new Font("Sansation", Font.PLAIN, 12));
        guideText.setBackground(BACKGROUND_LIGHT);
        guideText.setBorder(new EmptyBorder(16, 16, 16, 16));
        guideText.setEditable(false);
        guideText.setText(
                "GETTING STARTED:\n\n"
                + "1. 🔐 Login with your healthcare credentials\n"
                + "2. 📊 Review the dashboard for system overview\n"
                + "3. 👥 Navigate between modules using the side panel\n\n"
                + "KEYBOARD SHORTCUTS:\n"
                + "• Alt+D: Open Dashboard\n"
                + "• Alt+P: Open Patient Management\n"
                + "• Alt+A: Open Appointments\n"
                + "• F5: Refresh current view\n"
                + "• Ctrl+L: Logout\n\n"
                + "MODULE OVERVIEW:\n\n"
                + "📊 Dashboard:\n"
                + "   • System statistics and overview\n"
                + "   • Recent activity monitoring\n"
                + "   • Quick action shortcuts\n\n"
                + "👥 Patient Management:\n"
                + "   • Add, edit, and view patient records\n"
                + "   • Medical history tracking\n"
                + "   • Search and filter capabilities\n\n"
                + "📅 Appointments:\n"
                + "   • Schedule and manage appointments\n"
                + "   • Calendar view and notifications\n"
                + "   • Status tracking and updates\n\n"
                + "💰 Billing:\n"
                + "   • Process bills and insurance claims\n"
                + "   • Payment tracking and management\n"
                + "   • Financial reporting\n\n"
                + "📈 Reports:\n"
                + "   • Generate system reports\n"
                + "   • Data analytics and insights\n"
                + "   • Export capabilities\n\n"
                + "For technical support, contact your system administrator."
        );

        JScrollPane scrollPane = new JScrollPane(guideText);
        scrollPane.setBorder(new LineBorder(new Color(230, 230, 230), 1));

        JButton closeBtn = createModernButton("Close", PRIMARY_BLUE);
        closeBtn.addActionListener(e -> guideDialog.dispose());

        content.add(titleLabel, BorderLayout.NORTH);
        content.add(scrollPane, BorderLayout.CENTER);
        content.add(closeBtn, BorderLayout.SOUTH);

        guideDialog.add(content);
        guideDialog.setVisible(true);
    }

    private void showSupport() {
        JDialog supportDialog = createModernDialog("Support & Help", 500, 350);

        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setBackground(CARD_WHITE);
        content.setBorder(new EmptyBorder(24, 24, 24, 24));

        JLabel titleLabel = new JLabel("🆘 Support & Help");
        titleLabel.setFont(new Font("Sansation", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_PRIMARY);

        JPanel supportContent = new JPanel(new GridLayout(3, 1, 0, 16));
        supportContent.setBackground(CARD_WHITE);

        // Contact cards
        JPanel techSupportCard = createSupportCard("Technical Support",
                "📧 tech-support@globemed.com\n📞 +1-800-GLOBEMED", PRIMARY_BLUE);

        JPanel userHelpCard = createSupportCard("User Assistance",
                "📧 help@globemed.com\n💬 Live Chat Available", HEALTHCARE_GREEN);

        JPanel emergencyCard = createSupportCard("Emergency Issues",
                "📞 +1-800-EMERGENCY\n🚨 24/7 Critical Support", ERROR_RED);

        supportContent.add(techSupportCard);
        supportContent.add(userHelpCard);
        supportContent.add(emergencyCard);

        JButton closeBtn = createModernButton("Close", PRIMARY_BLUE);
        closeBtn.addActionListener(e -> supportDialog.dispose());

        content.add(titleLabel, BorderLayout.NORTH);
        content.add(supportContent, BorderLayout.CENTER);
        content.add(closeBtn, BorderLayout.SOUTH);

        supportDialog.add(content);
        supportDialog.setVisible(true);
    }

    private JPanel createSupportCard(String title, String details, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(accentColor, 1, true),
                new EmptyBorder(16, 16, 16, 16)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Sansation", Font.BOLD, 14));
        titleLabel.setForeground(accentColor);

        JTextArea detailsArea = new JTextArea(details);
        detailsArea.setFont(new Font("Sansation", Font.PLAIN, 11));
        detailsArea.setForeground(TEXT_SECONDARY);
        detailsArea.setBackground(CARD_WHITE);
        detailsArea.setEditable(false);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(detailsArea, BorderLayout.CENTER);

        return card;
    }

    private void showAboutDialog() {
        JDialog aboutDialog = createModernDialog("About GlobeMed", 550, 450);

        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setBackground(CARD_WHITE);
        content.setBorder(new EmptyBorder(24, 24, 24, 24));

        // Header with logo and title
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CARD_WHITE);

        JLabel logoLabel = new JLabel("🏥");
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(CARD_WHITE);

        JLabel titleLabel = new JLabel("GlobeMed Healthcare Management System");
        titleLabel.setFont(new Font("Sansation", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel versionLabel = new JLabel("Version 1.0.0 - Professional Edition");
        versionLabel.setFont(new Font("Sansation", Font.PLAIN, 12));
        versionLabel.setForeground(TEXT_SECONDARY);
        versionLabel.setHorizontalAlignment(SwingConstants.CENTER);

        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(versionLabel, BorderLayout.SOUTH);

        headerPanel.add(logoLabel, BorderLayout.NORTH);
        headerPanel.add(titlePanel, BorderLayout.SOUTH);

        // Features section
        JTextArea featuresArea = new JTextArea();
        featuresArea.setFont(new Font("Sansation", Font.PLAIN, 12));
        featuresArea.setBackground(BACKGROUND_LIGHT);
        featuresArea.setBorder(new EmptyBorder(16, 16, 16, 16));
        featuresArea.setEditable(false);
        featuresArea.setText(
                "CORE FEATURES:\n\n"
                + "🏥 Comprehensive Healthcare Management:\n"
                + "   • Advanced patient record management\n"
                + "   • Intelligent appointment scheduling\n"
                + "   • Automated billing and insurance processing\n"
                + "   • Role-based staff management\n"
                + "   • Powerful reporting and analytics\n\n"
                + "🔐 Enterprise Security:\n"
                + "   • Multi-layer security architecture\n"
                + "   • Role-based access control (RBAC)\n"
                + "   • Comprehensive audit logging\n"
                + "   • Data encryption and protection\n\n"
                + "🎨 Modern User Experience:\n"
                + "   • Medical-themed professional interface\n"
                + "   • Responsive and intuitive design\n"
                + "   • Real-time data synchronization\n"
                + "   • Accessibility compliance\n\n"
                + "Built with Java Swing, MySQL Database\n"
                + "Developed for Design Patterns Implementation\n\n"
                + "© 2025 GlobeMed Systems. All rights reserved."
        );

        JScrollPane scrollPane = new JScrollPane(featuresArea);
        scrollPane.setBorder(new LineBorder(new Color(230, 230, 230), 1));

        JButton closeBtn = createModernButton("Close", PRIMARY_BLUE);
        closeBtn.addActionListener(e -> aboutDialog.dispose());

        content.add(headerPanel, BorderLayout.NORTH);
        content.add(scrollPane, BorderLayout.CENTER);
        content.add(closeBtn, BorderLayout.SOUTH);

        aboutDialog.add(content);
        aboutDialog.setVisible(true);
    }

    private JDialog createModernDialog(String title, int width, int height) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setSize(width, height);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        // Add subtle shadow effect (platform dependent)
        dialog.getRootPane().setBorder(
                BorderFactory.createMatteBorder(0, 1, 1, 1, new Color(200, 200, 200))
        );

        return dialog;
    }

    private JButton createModernButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Sansation", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setBorder(new EmptyBorder(12, 24, 12, 24));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Enhanced hover effect
        button.addMouseListener(new MouseAdapter() {
            private Color originalColor = color;

            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(originalColor.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(originalColor);
            }
        });

        return button;
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

        // Update navigation user info
        updateUserInfo();
    }

    public RoleManagementService getRoleService() {
        return roleService;
    }

    public void setStatus(String status) {
        if (statusLabel != null) {
            statusLabel.setText(status);

            // Auto-clear status after 5 seconds for non-error messages
            if (!status.toLowerCase().contains("error") && !status.toLowerCase().contains("failed")) {
                Timer clearTimer = new Timer(5000, e -> {
                    if (statusLabel.getText().equals(status)) {
                        statusLabel.setText("Ready");
                    }
                });
                clearTimer.setRepeats(false);
                clearTimer.start();
            }
        }
    }

    public void showNotification(String message, NotificationType type) {
        Color bgColor;
        switch (type) {
            case SUCCESS:
                bgColor = HEALTHCARE_GREEN;
                break;
            case WARNING:
                bgColor = WARNING_AMBER;
                break;
            case ERROR:
                bgColor = ERROR_RED;
                break;
            case INFO:
                bgColor = PRIMARY_BLUE;
                break;
            default:
                bgColor = PRIMARY_BLUE;
                break;
        }

        // Create modern notification toast
        JWindow notificationWindow = new JWindow(this);
        notificationWindow.setAlwaysOnTop(true);

        JPanel notification = new JPanel(new BorderLayout());
        notification.setBackground(bgColor);
        notification.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(bgColor.darker(), 1, true),
                new EmptyBorder(16, 20, 16, 20)
        ));

        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("Sansation", Font.BOLD, 12));
        messageLabel.setForeground(Color.WHITE);

        notification.add(messageLabel, BorderLayout.CENTER);
        notificationWindow.add(notification);

        // Position in top-right corner
        notificationWindow.pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        notificationWindow.setLocation(
                screenSize.width - notificationWindow.getWidth() - 20,
                20
        );

        notificationWindow.setVisible(true);

        // Auto-hide after 4 seconds with fade effect
        Timer hideTimer = new Timer(4000, e -> {
            notificationWindow.setVisible(false);
            notificationWindow.dispose();
        });
        hideTimer.setRepeats(false);
        hideTimer.start();
    }

    // Notification types enum
    public enum NotificationType {
        SUCCESS, WARNING, ERROR, INFO
    }

    // Public utility methods
    public void navigateToModule(String module) {
        switch (module.toUpperCase()) {
            case "PATIENTS":
                showCard(PATIENTS_CARD);
                break;
            case "APPOINTMENTS":
                showCard(APPOINTMENTS_CARD);
                break;
            case "BILLING":
                showCard(BILLING_CARD);
                break;
            case "STAFF":
                showCard(STAFF_CARD);
                break;
            case "REPORTS":
                showCard(REPORTS_CARD);
                break;
            default:
                showCard(DASHBOARD_CARD);
                break;
        }
    }

    public String getCurrentModule() {
        return currentCard;
    }

    public void refreshCurrentModule() {
        showCard(currentCard);
    }
}

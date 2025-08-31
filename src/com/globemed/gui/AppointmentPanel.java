/*
 * Enhanced Appointment Management Panel
 * Improved UI/UX with medical color scheme and better layout
 */
package com.globemed.gui;

import com.globemed.database.*;
import com.globemed.models.*;
import com.globemed.services.AppointmentService;
import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Enhanced Appointment Management Panel with improved UI/UX
 *
 * @author Hansana
 */
public class AppointmentPanel extends JPanel {

    // Color Scheme Constants
    private static final Color PRIMARY_COLOR = new Color(46, 134, 171);      // Medical Blue
    private static final Color SECONDARY_COLOR = new Color(255, 255, 255);   // Clean White
    private static final Color ACCENT_COLOR = new Color(76, 175, 80);        // Healthcare Green
    private static final Color WARNING_COLOR = new Color(255, 152, 0);       // Amber
    private static final Color ERROR_COLOR = new Color(244, 67, 54);         // Medical Red
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);  // Light Gray
    private static final Color CARD_COLOR = new Color(255, 255, 255);        // White cards
    private static final Color BORDER_COLOR = new Color(224, 224, 224);      // Light border

    // Typography Constants
    private static final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 16);
    private static final Font BODY_FONT = new Font("SansSerif", Font.PLAIN, 12);
    private static final Font LABEL_FONT = new Font("SansSerif", Font.BOLD, 11);
    private static final Font DATA_FONT = new Font("SansSerif", Font.PLAIN, 11);

    private MainFrame parentFrame;
    private AppointmentService appointmentService;
    private AppointmentDAO appointmentDAO;
    private PatientDAO patientDAO;
    private StaffDAO staffDAO;

    // GUI Components
    private JTable appointmentTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> patientComboBox;
    private JComboBox<String> doctorComboBox;
    private JComboBox<String> locationComboBox;
    private JComboBox<String> statusComboBox;
    private JButton scheduleButton, rescheduleButton, cancelButton, refreshButton;
    private JTextArea notificationsArea;
    private JDateChooser dateChooser;
    private JSpinner timeSpinner;

    // Table columns
    private final String[] columnNames = {"ID", "Patient", "Doctor", "Date & Time", "Location", "Status"};
    private final String[] statusOptions = {"SCHEDULED", "COMPLETED", "CANCELLED", "IN_PROGRESS"};

    public AppointmentPanel(MainFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.appointmentService = new AppointmentService();
        this.appointmentDAO = new AppointmentDAO();
        this.patientDAO = new PatientDAO();
        this.staffDAO = new StaffDAO();

        setBackground(BACKGROUND_COLOR);
        initializeComponents();
        layoutComponents();
        setupEventHandlers();
        refreshData();
    }

    private void initializeComponents() {
        // Enhanced Table setup
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        appointmentTable = new JTable(tableModel);
        styleTable();

        // Enhanced ComboBoxes
        patientComboBox = createStyledComboBox();
        doctorComboBox = createStyledComboBox();
        locationComboBox = createStyledComboBox();
        statusComboBox = new JComboBox<>(statusOptions);
        styleComboBox(statusComboBox);

        // Enhanced Date/Time components
        setupDateTimeComponents();

        // Enhanced Buttons
        setupButtons();

        // Enhanced Notifications area
        setupNotificationsArea();

        loadComboBoxData();
    }

    private void styleTable() {
        appointmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        appointmentTable.setRowHeight(32);
        appointmentTable.setFont(DATA_FONT);
        appointmentTable.setBackground(SECONDARY_COLOR);
        appointmentTable.setGridColor(BORDER_COLOR);
        appointmentTable.setSelectionBackground(new Color(PRIMARY_COLOR.getRed(), PRIMARY_COLOR.getGreen(), PRIMARY_COLOR.getBlue(), 30));
        appointmentTable.setSelectionForeground(Color.BLACK);

        // Style table header
        JTableHeader header = appointmentTable.getTableHeader();
        header.setFont(LABEL_FONT);
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(SECONDARY_COLOR);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 36));

        // Custom cell renderer for status column
        appointmentTable.getColumnModel().getColumn(5).setCellRenderer(new StatusCellRenderer());
    }

    private JComboBox<String> createStyledComboBox() {
        JComboBox<String> combo = new JComboBox<>();
        styleComboBox(combo);
        return combo;
    }

    private void styleComboBox(JComboBox<?> combo) {
        combo.setFont(DATA_FONT);
        combo.setBackground(SECONDARY_COLOR);
        combo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        combo.setPreferredSize(new Dimension(combo.getPreferredSize().width, 32));
    }

    private void setupDateTimeComponents() {
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");
        dateChooser.setDate(java.sql.Date.valueOf(LocalDate.now()));
        dateChooser.setFont(DATA_FONT);
        dateChooser.setPreferredSize(new Dimension(150, 32));

        SpinnerDateModel timeModel = new SpinnerDateModel();
        timeSpinner = new JSpinner(timeModel);
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm");
        timeSpinner.setEditor(timeEditor);
        timeSpinner.setValue(new java.util.Date());
        timeSpinner.setFont(DATA_FONT);
        timeSpinner.setPreferredSize(new Dimension(80, 32));
    }

    private void setupButtons() {
        scheduleButton = createStyledButton("Schedule Appointment", ACCENT_COLOR, "schedule");
        rescheduleButton = createStyledButton("Reschedule", WARNING_COLOR, "reschedule");
        cancelButton = createStyledButton("Cancel", ERROR_COLOR, "cancel");
        refreshButton = createStyledButton("Refresh", PRIMARY_COLOR, "refresh");

        rescheduleButton.setEnabled(false);
        cancelButton.setEnabled(false);
    }

    private JButton createStyledButton(String text, Color bgColor, String type) {
        JButton button = new JButton(text);
        button.setFont(LABEL_FONT);
        button.setBackground(bgColor);
        button.setForeground(SECONDARY_COLOR);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add icons based on type
        String iconText = getIconForButton(type);
        if (!iconText.isEmpty()) {
            button.setText(iconText + " " + text);
        }

        // Hover effects
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private String getIconForButton(String type) {
        switch (type) {
            case "schedule":
                return "📅";
            case "reschedule":
                return "🔄";
            case "cancel":
                return "❌";
            case "refresh":
                return "🔄";
            case "check":
                return "🔍";
            case "clear":
                return "🗑️";
            case "emergency":
                return "🚨";
            default:
                return "";
        }
    }

    private void setupNotificationsArea() {
        notificationsArea = new JTextArea(6, 25);
        notificationsArea.setEditable(false);
        notificationsArea.setFont(DATA_FONT);
        notificationsArea.setBackground(CARD_COLOR);
        notificationsArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(0, 16));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // Header Panel
        add(createHeaderPanel(), BorderLayout.NORTH);

        // Main Content Panel
        add(createMainContentPanel(), BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        // Title with icon
        JLabel titleLabel = new JLabel("🏥 Appointment Management");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(PRIMARY_COLOR);

        // Demo button
        JButton patternsDemo = createStyledButton("Design Patterns Demo", PRIMARY_COLOR, "");
        patternsDemo.addActionListener(e -> demonstratePatterns());

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(patternsDemo, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createMainContentPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(16, 0));
        mainPanel.setBackground(BACKGROUND_COLOR);

        // Left panel - Appointments and Form
        JPanel leftPanel = new JPanel(new BorderLayout(0, 16));
        leftPanel.setBackground(BACKGROUND_COLOR);

        // Appointments table card
        leftPanel.add(createAppointmentsTableCard(), BorderLayout.CENTER);

        // Scheduling form card
        leftPanel.add(createSchedulingFormCard(), BorderLayout.SOUTH);

        // Right panel - Notifications and Quick Actions
        JPanel rightPanel = new JPanel(new BorderLayout(0, 16));
        rightPanel.setBackground(BACKGROUND_COLOR);
        rightPanel.setPreferredSize(new Dimension(320, 0));

        rightPanel.add(createNotificationsCard(), BorderLayout.CENTER);
        rightPanel.add(createQuickActionsCard(), BorderLayout.SOUTH);

        mainPanel.add(leftPanel, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);

        return mainPanel;
    }

    private JPanel createAppointmentsTableCard() {
        JPanel card = createCard("📋 Scheduled Appointments");
        card.setLayout(new BorderLayout(0, 8));

        // Table with scroll
        JScrollPane tableScroll = new JScrollPane(appointmentTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        tableScroll.setPreferredSize(new Dimension(0, 280));
        card.add(tableScroll, BorderLayout.CENTER);

        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        buttonPanel.setBackground(CARD_COLOR);
        buttonPanel.add(refreshButton);
        buttonPanel.add(rescheduleButton);
        buttonPanel.add(cancelButton);

        card.add(buttonPanel, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createSchedulingFormCard() {
        JPanel card = createCard("➕ Schedule New Appointment");
        card.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Row 1: Patient and Doctor
        gbc.gridx = 0;
        gbc.gridy = 0;
        card.add(createLabel("Patient:"), gbc);
        gbc.gridx = 1;
        card.add(patientComboBox, gbc);

        gbc.gridx = 2;
        card.add(createLabel("Doctor:"), gbc);
        gbc.gridx = 3;
        card.add(doctorComboBox, gbc);

        // Row 2: Date and Time
        gbc.gridx = 0;
        gbc.gridy = 1;
        card.add(createLabel("Date:"), gbc);
        gbc.gridx = 1;
        card.add(dateChooser, gbc);

        gbc.gridx = 2;
        card.add(createLabel("Time:"), gbc);
        gbc.gridx = 3;
        card.add(timeSpinner, gbc);

        // Row 3: Location and Status
        gbc.gridx = 0;
        gbc.gridy = 2;
        card.add(createLabel("Location:"), gbc);
        gbc.gridx = 1;
        card.add(locationComboBox, gbc);

        gbc.gridx = 2;
        card.add(createLabel("Status:"), gbc);
        gbc.gridx = 3;
        card.add(statusComboBox, gbc);

        // Row 4: Action buttons
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        actionPanel.setBackground(CARD_COLOR);

        actionPanel.add(scheduleButton);

        JButton checkAvailabilityBtn = createStyledButton("Check Availability", PRIMARY_COLOR, "check");
        checkAvailabilityBtn.addActionListener(e -> checkAvailability());
        actionPanel.add(checkAvailabilityBtn);

        JButton clearFormBtn = createStyledButton("Clear Form", new Color(128, 128, 128), "clear");
        clearFormBtn.addActionListener(e -> clearSchedulingForm());
        actionPanel.add(clearFormBtn);

        card.add(actionPanel, gbc);
        return card;
    }

    private JPanel createNotificationsCard() {
        JPanel card = createCard("🔔 System Notifications");
        card.setLayout(new BorderLayout(0, 8));

        JScrollPane notifScroll = new JScrollPane(notificationsArea);
        notifScroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        notifScroll.setPreferredSize(new Dimension(0, 200));
        card.add(notifScroll, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        buttonPanel.setBackground(CARD_COLOR);

        JButton clearNotifBtn = createStyledButton("Clear", new Color(128, 128, 128), "clear");
        clearNotifBtn.addActionListener(e -> notificationsArea.setText(""));
        buttonPanel.add(clearNotifBtn);

        card.add(buttonPanel, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createQuickActionsCard() {
        JPanel card = createCard("⚡ Quick Actions");
        card.setLayout(new GridLayout(3, 1, 8, 8));

        JButton todayApptBtn = createStyledButton("Today's Appointments", ACCENT_COLOR, "");
        JButton emergencySlotBtn = createStyledButton("Emergency Slot", ERROR_COLOR, "emergency");
        JButton patternDemoBtn = createStyledButton("Pattern Demo", PRIMARY_COLOR, "");

        todayApptBtn.addActionListener(e -> showTodaysAppointments());
        emergencySlotBtn.addActionListener(e -> scheduleEmergencyAppointment());
        patternDemoBtn.addActionListener(e -> demonstratePatterns());

        card.add(todayApptBtn);
        card.add(emergencySlotBtn);
        card.add(patternDemoBtn);

        return card;
    }

    private JPanel createCard(String title) {
        JPanel card = new JPanel();
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));

        // Add title if provided
        if (title != null && !title.isEmpty()) {
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 1),
                    BorderFactory.createTitledBorder(
                            BorderFactory.createEmptyBorder(8, 16, 16, 16),
                            title,
                            0,
                            0,
                            HEADER_FONT,
                            PRIMARY_COLOR
                    )
            ));
        }

        return card;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        label.setForeground(new Color(64, 64, 64));
        return label;
    }

    // Status cell renderer for color-coded status
    private class StatusCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value != null) {
                String status = value.toString();
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(new Font("SansSerif", Font.BOLD, 10));

                if (!isSelected) {
                    switch (status) {
                        case "SCHEDULED":
                            setBackground(new Color(ACCENT_COLOR.getRed(), ACCENT_COLOR.getGreen(), ACCENT_COLOR.getBlue(), 30));
                            setForeground(ACCENT_COLOR);
                            break;
                        case "COMPLETED":
                            setBackground(new Color(76, 175, 80, 30));
                            setForeground(new Color(76, 175, 80));
                            break;
                        case "CANCELLED":
                            setBackground(new Color(ERROR_COLOR.getRed(), ERROR_COLOR.getGreen(), ERROR_COLOR.getBlue(), 30));
                            setForeground(ERROR_COLOR);
                            break;
                        case "IN_PROGRESS":
                            setBackground(new Color(WARNING_COLOR.getRed(), WARNING_COLOR.getGreen(), WARNING_COLOR.getBlue(), 30));
                            setForeground(WARNING_COLOR);
                            break;
                        default:
                            setBackground(SECONDARY_COLOR);
                            setForeground(Color.BLACK);
                    }
                }
            }
            return this;
        }
    }

    // Data loading methods (keeping existing logic)
    private void loadComboBoxData() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    List<Patient> patients = patientDAO.getAllPatients();
                    SwingUtilities.invokeLater(() -> {
                        patientComboBox.removeAllItems();
                        patientComboBox.addItem("Select Patient");
                        for (Patient patient : patients) {
                            patientComboBox.addItem(patient.getId() + " - " + patient.getName());
                        }
                    });

                    List<Staff> staff = staffDAO.getAllDoctors();
                    SwingUtilities.invokeLater(() -> {
                        doctorComboBox.removeAllItems();
                        doctorComboBox.addItem("Select Doctor");
                        for (Staff s : staff) {
                            doctorComboBox.addItem(s.getId() + " - " + s.getName());
                        }
                    });

                    List<String> rooms = appointmentService.getAvailableRooms();
                    SwingUtilities.invokeLater(() -> {
                        locationComboBox.removeAllItems();
                        locationComboBox.addItem("Select Location");
                        for (String room : rooms) {
                            locationComboBox.addItem(room);
                        }
                    });

                } catch (SQLException e) {
                    SwingUtilities.invokeLater(() -> {
                        showErrorDialog("Error loading data: " + e.getMessage());
                    });
                }
                return null;
            }
        };
        worker.execute();
    }

    private void setupEventHandlers() {
        appointmentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = appointmentTable.getSelectedRow() != -1;
                rescheduleButton.setEnabled(hasSelection);
                cancelButton.setEnabled(hasSelection);
            }
        });

        scheduleButton.addActionListener(e -> scheduleAppointment());
        rescheduleButton.addActionListener(e -> rescheduleAppointment());
        cancelButton.addActionListener(e -> cancelAppointment());
        refreshButton.addActionListener(e -> refreshData());

        dateChooser.setDate(java.sql.Date.valueOf(LocalDate.now()));
    }

    // Enhanced dialog methods
    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this,
                "⚠️ " + message,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccessDialog(String message) {
        JOptionPane.showMessageDialog(this,
                "✅ " + message,
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showWarningDialog(String message) {
        JOptionPane.showMessageDialog(this,
                "⚠️ " + message,
                "Warning",
                JOptionPane.WARNING_MESSAGE);
    }

    // Core appointment management methods (keeping existing logic with enhanced dialogs)
    private void scheduleAppointment() {
        if (!validateAppointmentForm()) {
            return;
        }

        try {
            Long patientId = extractIdFromComboBox(patientComboBox);
            Long staffId = extractIdFromComboBox(doctorComboBox);
            String location = (String) locationComboBox.getSelectedItem();
            LocalDateTime appointmentTime = parseDateTime();

            if (patientId == null || staffId == null || location.equals("Select Location")) {
                showErrorDialog("Please fill all required fields!");
                return;
            }

            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return appointmentService.scheduleAppointment(patientId, staffId, appointmentTime, location);
                }

                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            showSuccessDialog("Appointment scheduled successfully!");
                            addNotification("✅ Appointment scheduled for " + appointmentTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                            clearSchedulingForm();
                            refreshData();
                        } else {
                            showWarningDialog("Failed to schedule appointment. Time slot may not be available.");
                        }
                    } catch (Exception e) {
                        showErrorDialog("Error scheduling appointment: " + e.getMessage());
                    }
                }
            };
            worker.execute();

        } catch (DateTimeParseException e) {
            showErrorDialog("Invalid date/time format!");
        }
    }

    private void checkAvailability() {
        try {
            Long staffId = extractIdFromComboBox(doctorComboBox);
            String location = (String) locationComboBox.getSelectedItem();
            LocalDateTime appointmentTime = parseDateTime();

            if (staffId == null || location.equals("Select Location")) {
                showWarningDialog("Please select doctor and location first!");
                return;
            }

            boolean available = appointmentService.checkAvailability(staffId, location, appointmentTime);

            String message = available ? "Time slot is AVAILABLE" : "Time slot is NOT AVAILABLE";

            if (available) {
                showSuccessDialog(message);
            } else {
                showWarningDialog(message);
            }

            addNotification("🔍 Availability checked for " + appointmentTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + " - "
                    + (available ? "Available" : "Unavailable"));

        } catch (Exception e) {
            showErrorDialog("Error checking availability: " + e.getMessage());
        }
    }

    // Enhanced reschedule dialog
    private void rescheduleAppointment() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        int modelRow = appointmentTable.convertRowIndexToModel(selectedRow);
        Long appointmentId = (Long) tableModel.getValueAt(modelRow, 0);

        JDialog rescheduleDialog = createStyledDialog("Reschedule Appointment", 450, 300);
        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(CARD_COLOR);
        content.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        JDateChooser newDateChooser = new JDateChooser();
        newDateChooser.setDateFormatString("yyyy-MM-dd");
        newDateChooser.setDate(java.sql.Date.valueOf(LocalDate.now()));
        newDateChooser.setFont(DATA_FONT);
        newDateChooser.setPreferredSize(new Dimension(150, 32));

        SpinnerDateModel newTimeModel = new SpinnerDateModel();
        JSpinner newTimeSpinner = new JSpinner(newTimeModel);
        JSpinner.DateEditor newTimeEditor = new JSpinner.DateEditor(newTimeSpinner, "HH:mm");
        newTimeSpinner.setEditor(newTimeEditor);
        newTimeSpinner.setValue(new java.util.Date());
        newTimeSpinner.setFont(DATA_FONT);
        newTimeSpinner.setPreferredSize(new Dimension(80, 32));

        JComboBox<String> newLocationCombo = createStyledComboBox();
        for (String room : appointmentService.getAvailableRooms()) {
            newLocationCombo.addItem(room);
        }

        // Layout reschedule form
        gbc.gridx = 0;
        gbc.gridy = 0;
        content.add(createLabel("New Date:"), gbc);
        gbc.gridx = 1;
        content.add(newDateChooser, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        content.add(createLabel("New Time:"), gbc);
        gbc.gridx = 1;
        content.add(newTimeSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        content.add(createLabel("New Location:"), gbc);
        gbc.gridx = 1;
        content.add(newLocationCombo, gbc);

        // Buttons
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 16));
        buttons.setBackground(CARD_COLOR);

        JButton confirmBtn = createStyledButton("Confirm Reschedule", ACCENT_COLOR, "");
        JButton cancelBtn = createStyledButton("Cancel", new Color(128, 128, 128), "");

        confirmBtn.addActionListener(e -> {
            try {
                java.util.Date selectedDate = newDateChooser.getDate();
                java.util.Date selectedTime = (java.util.Date) newTimeSpinner.getValue();

                if (selectedDate == null || selectedTime == null) {
                    showWarningDialog("Please select both date and time!");
                    return;
                }

                LocalDate date = new java.sql.Date(selectedDate.getTime()).toLocalDate();
                LocalDateTime newDateTime = LocalDateTime.of(
                        date,
                        selectedTime.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalTime()
                );
                String newLocation = (String) newLocationCombo.getSelectedItem();

                boolean success = appointmentService.rescheduleAppointment(appointmentId, newDateTime, newLocation);
                if (success) {
                    addNotification("🔄 Appointment " + appointmentId + " rescheduled to "
                            + newDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                    refreshData();
                    rescheduleDialog.dispose();
                    showSuccessDialog("Appointment rescheduled successfully!");
                } else {
                    showErrorDialog("Failed to reschedule appointment!");
                }
            } catch (DateTimeParseException ex) {
                showErrorDialog("Invalid date/time format!");
            }
        });

        cancelBtn.addActionListener(e -> rescheduleDialog.dispose());

        buttons.add(confirmBtn);
        buttons.add(cancelBtn);
        content.add(buttons, gbc);

        rescheduleDialog.add(content);
        rescheduleDialog.setVisible(true);
    }

    private void cancelAppointment() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        int modelRow = appointmentTable.convertRowIndexToModel(selectedRow);
        Long appointmentId = (Long) tableModel.getValueAt(modelRow, 0);
        String patientInfo = (String) tableModel.getValueAt(modelRow, 1);
        String dateTime = (String) tableModel.getValueAt(modelRow, 3);

        int confirm = JOptionPane.showConfirmDialog(this,
                "❌ Cancel appointment for " + patientInfo + " on " + dateTime + "?",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = appointmentService.cancelAppointment(appointmentId);
            if (success) {
                addNotification("❌ Appointment " + appointmentId + " cancelled for " + patientInfo);
                refreshData();
                showSuccessDialog("Appointment cancelled successfully!");
            } else {
                showErrorDialog("Failed to cancel appointment!");
            }
        }
    }

    private JDialog createStyledDialog(String title, int width, int height) {
        JDialog dialog = new JDialog(parentFrame, title, true);
        dialog.setSize(width, height);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(BACKGROUND_COLOR);
        return dialog;
    }

    // Emergency appointment dialog
    private void scheduleEmergencyAppointment() {
        JDialog emergencyDialog = createStyledDialog("🚨 Emergency Appointment", 400, 280);

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(CARD_COLOR);
        content.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ERROR_COLOR, 2),
                BorderFactory.createEmptyBorder(24, 24, 24, 24)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        JComboBox<String> emergencyPatientCombo = createStyledComboBox();
        JComboBox<String> emergencyDoctorCombo = createStyledComboBox();

        try {
            List<Patient> patients = patientDAO.getAllPatients();
            for (Patient p : patients) {
                emergencyPatientCombo.addItem(p.getId() + " - " + p.getName());
            }

            List<Staff> doctors = staffDAO.getAllStaff();
            for (Staff s : doctors) {
                emergencyDoctorCombo.addItem(s.getId() + " - " + s.getName());
            }
        } catch (SQLException e) {
            showErrorDialog("Error loading data: " + e.getMessage());
        }

        gbc.gridx = 0;
        gbc.gridy = 0;
        content.add(createLabel("Patient:"), gbc);
        gbc.gridx = 1;
        content.add(emergencyPatientCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        content.add(createLabel("Available Doctor:"), gbc);
        gbc.gridx = 1;
        content.add(emergencyDoctorCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 16));
        buttons.setBackground(CARD_COLOR);

        JButton scheduleEmergencyBtn = createStyledButton("Schedule Emergency", ERROR_COLOR, "emergency");
        JButton cancelEmergencyBtn = createStyledButton("Cancel", new Color(128, 128, 128), "");

        scheduleEmergencyBtn.addActionListener(e -> {
            Long patientId = extractIdFromComboBox(emergencyPatientCombo);
            Long doctorId = extractIdFromComboBox(emergencyDoctorCombo);

            if (patientId != null && doctorId != null) {
                LocalDateTime emergencyTime = LocalDateTime.now().plusMinutes(15);
                boolean success = appointmentService.scheduleAppointment(patientId, doctorId, emergencyTime, "Emergency Room");

                if (success) {
                    addNotification("🚨 Emergency appointment scheduled for "
                            + emergencyTime.format(DateTimeFormatter.ofPattern("HH:mm")));
                    refreshData();
                    emergencyDialog.dispose();
                    showSuccessDialog("Emergency appointment scheduled successfully!");
                } else {
                    showErrorDialog("Failed to schedule emergency appointment!");
                }
            }
        });

        cancelEmergencyBtn.addActionListener(e -> emergencyDialog.dispose());

        buttons.add(scheduleEmergencyBtn);
        buttons.add(cancelEmergencyBtn);
        content.add(buttons, gbc);

        emergencyDialog.add(content);
        emergencyDialog.setVisible(true);
    }

    private void showTodaysAppointments() {
        SwingWorker<List<Appointment>, Void> worker = new SwingWorker<List<Appointment>, Void>() {
            @Override
            protected List<Appointment> doInBackground() throws Exception {
                return appointmentDAO.getAllAppointments();
            }

            @Override
            protected void done() {
                try {
                    List<Appointment> allAppointments = get();
                    List<Appointment> todayAppointments = allAppointments.stream()
                            .filter(apt -> apt.getAppointmentTime().toLocalDate().equals(LocalDate.now()))
                            .sorted((a1, a2) -> a1.getAppointmentTime().compareTo(a2.getAppointmentTime()))
                            .toList();

                    showTodaysAppointmentsDialog(todayAppointments);
                } catch (Exception e) {
                    showErrorDialog("Error loading today's appointments: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void showTodaysAppointmentsDialog(List<Appointment> todayAppointments) {
        JDialog dialog = createStyledDialog("📅 Today's Appointments - " + LocalDate.now(), 700, 500);

        JPanel mainPanel = new JPanel(new BorderLayout(16, 16));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // Header with count
        JLabel headerLabel = new JLabel("📋 " + todayAppointments.size() + " appointments scheduled for today");
        headerLabel.setFont(HEADER_FONT);
        headerLabel.setForeground(PRIMARY_COLOR);
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        String[] columns = {"Time", "Patient", "Doctor", "Location", "Status"};
        DefaultTableModel todayModel = new DefaultTableModel(columns, 0);
        JTable todayTable = new JTable(todayModel);

        // Style the today's table
        todayTable.setRowHeight(32);
        todayTable.setFont(DATA_FONT);
        todayTable.setBackground(SECONDARY_COLOR);
        todayTable.setGridColor(BORDER_COLOR);

        JTableHeader header = todayTable.getTableHeader();
        header.setFont(LABEL_FONT);
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(SECONDARY_COLOR);

        SwingWorker<Void, Void> dataLoader = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    List<Patient> allPatients = patientDAO.getAllPatients();
                    List<Staff> allStaff = staffDAO.getAllStaff();

                    java.util.Map<Long, Patient> patientMap = new java.util.HashMap<>();
                    java.util.Map<Long, Staff> staffMap = new java.util.HashMap<>();

                    for (Patient patient : allPatients) {
                        patientMap.put(patient.getId(), patient);
                    }
                    for (Staff staff : allStaff) {
                        staffMap.put(staff.getId(), staff);
                    }

                    for (Appointment apt : todayAppointments) {
                        Patient patient = patientMap.get(apt.getPatientId());
                        Staff staff = staffMap.get(apt.getStaffId());

                        Object[] row = {
                            apt.getAppointmentTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                            patient != null ? patient.getName() : "Unknown",
                            staff != null ? staff.getName() : "Unknown",
                            apt.getLocation(),
                            apt.getStatus()
                        };

                        SwingUtilities.invokeLater(() -> todayModel.addRow(row));
                    }
                } catch (SQLException e) {
                    SwingUtilities.invokeLater(() -> {
                        showErrorDialog("Error loading appointment details: " + e.getMessage());
                    });
                }
                return null;
            }
        };
        dataLoader.execute();

        JScrollPane scrollPane = new JScrollPane(todayTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 16));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        JButton closeBtn = createStyledButton("Close", PRIMARY_COLOR, "");
        closeBtn.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeBtn);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private void demonstratePatterns() {
        StringBuilder demo = new StringBuilder();
        demo.append("🎯 APPOINTMENT SYSTEM PATTERNS DEMO\n\n");
        demo.append("MEDIATOR PATTERN:\n");
        demo.append("• AppointmentMediator coordinates between Patient, Doctor, and Room components\n");
        demo.append("• Centralizes communication logic\n");
        demo.append("• Reduces coupling between components\n\n");

        demo.append("OBSERVER PATTERN:\n");
        demo.append("• PatientObserver receives appointment notifications\n");
        demo.append("• DoctorObserver gets schedule updates\n");
        demo.append("• AdminObserver monitors all appointment activities\n\n");

        demo.append("PATTERN INTERACTION:\n");
        demo.append("• When appointment is scheduled:\n");
        demo.append("  1. Mediator validates availability\n");
        demo.append("  2. Creates appointment\n");
        demo.append("  3. Notifies all observers\n");
        demo.append("  4. Updates GUI notifications\n\n");

        demo.append("BENEFITS:\n");
        demo.append("• Loose coupling between components\n");
        demo.append("• Real-time notifications\n");
        demo.append("• Extensible notification system\n");
        demo.append("• Centralized appointment logic\n");

        JDialog demoDialog = createStyledDialog("📖 Design Patterns Demo", 600, 500);

        JPanel mainPanel = new JPanel(new BorderLayout(16, 16));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JTextArea demoArea = new JTextArea(demo.toString());
        demoArea.setEditable(false);
        demoArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        demoArea.setBackground(CARD_COLOR);
        demoArea.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JScrollPane scrollPane = new JScrollPane(demoArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 16));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        JButton closeBtn = createStyledButton("Close", PRIMARY_COLOR, "");
        closeBtn.addActionListener(e -> demoDialog.dispose());
        buttonPanel.add(closeBtn);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        demoDialog.add(mainPanel);
        demoDialog.setVisible(true);

        addNotification("📖 Design patterns demonstration viewed");
    }

    // Utility methods
    private boolean validateAppointmentForm() {
        if (patientComboBox.getSelectedIndex() <= 0) {
            showErrorDialog("Please select a patient!");
            return false;
        }
        if (doctorComboBox.getSelectedIndex() <= 0) {
            showErrorDialog("Please select a doctor!");
            return false;
        }
        if (locationComboBox.getSelectedIndex() <= 0) {
            showErrorDialog("Please select a location!");
            return false;
        }
        try {
            parseDateTime();
            return true;
        } catch (DateTimeParseException e) {
            showErrorDialog("Invalid date/time format!\nDate: YYYY-MM-DD, Time: HH:MM");
            return false;
        }
    }

    private LocalDateTime parseDateTime() throws DateTimeParseException {
        java.util.Date selectedDate = dateChooser.getDate();
        java.util.Date selectedTime = (java.util.Date) timeSpinner.getValue();

        if (selectedDate == null || selectedTime == null) {
            throw new DateTimeParseException("Date or time not selected", "", 0);
        }

        LocalDate date = new java.sql.Date(selectedDate.getTime()).toLocalDate();
        LocalDateTime dateTime = LocalDateTime.of(
                date,
                selectedTime.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalTime()
        );
        return dateTime;
    }

    private Long extractIdFromComboBox(JComboBox<String> comboBox) {
        String selected = (String) comboBox.getSelectedItem();
        if (selected == null || selected.startsWith("Select")) {
            return null;
        }
        try {
            return Long.parseLong(selected.split(" - ")[0]);
        } catch (Exception e) {
            return null;
        }
    }

    public void refreshData() {
        SwingWorker<List<Appointment>, Void> worker = new SwingWorker<List<Appointment>, Void>() {
            @Override
            protected List<Appointment> doInBackground() throws Exception {
                return appointmentDAO.getAllAppointments();
            }

            @Override
            protected void done() {
                try {
                    List<Appointment> appointments = get();
                    updateTableData(appointments);
                    if (parentFrame != null) {
                        parentFrame.setStatus("✅ Appointments refreshed - " + appointments.size() + " appointments loaded");
                    }
                } catch (Exception e) {
                    showErrorDialog("Error loading appointments: " + e.getMessage());
                    if (parentFrame != null) {
                        parentFrame.setStatus("❌ Error loading appointments");
                    }
                }
            }
        };
        worker.execute();
    }

    private void updateTableData(List<Appointment> appointments) {
        tableModel.setRowCount(0);
        if (appointments == null || appointments.isEmpty()) {
            return;
        }

        SwingWorker<Void, Void> dataLoader = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    List<Patient> allPatients = patientDAO.getAllPatients();
                    List<Staff> allStaff = staffDAO.getAllStaff();

                    java.util.Map<Long, Patient> patientMap = new java.util.HashMap<>();
                    java.util.Map<Long, Staff> staffMap = new java.util.HashMap<>();

                    for (Patient patient : allPatients) {
                        patientMap.put(patient.getId(), patient);
                    }
                    for (Staff staff : allStaff) {
                        staffMap.put(staff.getId(), staff);
                    }

                    for (Appointment appointment : appointments) {
                        Patient patient = patientMap.get(appointment.getPatientId());
                        Staff staff = staffMap.get(appointment.getStaffId());

                        Object[] row = {
                            appointment.getId(),
                            patient != null ? patient.getName() : "Unknown Patient",
                            staff != null ? staff.getName() : "Unknown Doctor",
                            appointment.getAppointmentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                            appointment.getLocation(),
                            appointment.getStatus()
                        };

                        SwingUtilities.invokeLater(() -> tableModel.addRow(row));
                    }

                } catch (SQLException e) {
                    SwingUtilities.invokeLater(() -> {
                        showErrorDialog("Error loading appointment details: " + e.getMessage());
                    });
                }
                return null;
            }
        };
        dataLoader.execute();
    }

    private void addNotification(String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String notification = "[" + timestamp + "] " + message + "\n";

        SwingUtilities.invokeLater(() -> {
            notificationsArea.append(notification);
            notificationsArea.setCaretPosition(notificationsArea.getDocument().getLength());
        });
    }

    private void clearSchedulingForm() {
        patientComboBox.setSelectedIndex(0);
        doctorComboBox.setSelectedIndex(0);
        locationComboBox.setSelectedIndex(0);
        statusComboBox.setSelectedIndex(0);
        dateChooser.setDate(java.sql.Date.valueOf(LocalDate.now()));
        timeSpinner.setValue(new java.util.Date());
    }

    public void cleanup() {
        try {
            // Close database connections if needed
        } catch (Exception e) {
            System.err.println("Error during cleanup: " + e.getMessage());
        }
    }

    @Override
    public void removeNotify() {
        cleanup();
        super.removeNotify();
    }
}

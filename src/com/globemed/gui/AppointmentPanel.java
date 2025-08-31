/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.gui;

import com.globemed.database.*;
import com.globemed.models.*;
import com.globemed.services.AppointmentService;
import com.toedter.calendar.JCalendar;
import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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
 *
 * @author Hansana
 */
public class AppointmentPanel extends JPanel {

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
    private JCalendar calendar; // Custom calendar component
    private JDateChooser dateChooser;
    private JSpinner timeSpinner;

    // Table columns
    private final String[] columnNames = {"ID", "Patient", "Doctor", "Date & Time", "Location", "Status"};

    // Status options
    private final String[] statusOptions = {"SCHEDULED", "COMPLETED", "CANCELLED", "IN_PROGRESS"};

    public AppointmentPanel(MainFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.appointmentService = new AppointmentService();
        this.appointmentDAO = new AppointmentDAO();
        this.patientDAO = new PatientDAO();
        this.staffDAO = new StaffDAO();

        initializeComponents();
        layoutComponents();
        setupEventHandlers();

        // Initial data load
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

        appointmentTable = new JTable(tableModel);
        appointmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        appointmentTable.setRowHeight(25);

        // ComboBoxes
        patientComboBox = new JComboBox<>();
        doctorComboBox = new JComboBox<>();
        locationComboBox = new JComboBox<>();
        statusComboBox = new JComboBox<>(statusOptions);

        // Date chooser
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");
        dateChooser.setDate(java.sql.Date.valueOf(LocalDate.now())); // default today

        // Time spinner (HH:mm format)
        SpinnerDateModel timeModel = new SpinnerDateModel();
        timeSpinner = new JSpinner(timeModel);
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm");
        timeSpinner.setEditor(timeEditor);
        timeSpinner.setValue(new java.util.Date()); // default current time
        
        // Set step size to 30 minutes
        timeSpinner.addChangeListener(e -> {
            java.util.Date currentValue = (java.util.Date) timeSpinner.getValue();
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(currentValue);
            int minutes = cal.get(java.util.Calendar.MINUTE);
            int adjustedMinutes = ((minutes + 15) / 30) * 30; // Round to nearest 30 minutes
            if (adjustedMinutes >= 30) {
                adjustedMinutes = 0;
                cal.add(java.util.Calendar.HOUR, 1);
            }
            cal.set(java.util.Calendar.MINUTE, adjustedMinutes);
            cal.set(java.util.Calendar.SECOND, 0);
            cal.set(java.util.Calendar.MILLISECOND, 0);
            timeSpinner.setValue(cal.getTime());
        });

        // Buttons
        scheduleButton = new JButton("Schedule Appointment");
        rescheduleButton = new JButton("Reschedule");
        cancelButton = new JButton("Cancel Appointment");
        refreshButton = new JButton("Refresh");

        rescheduleButton.setEnabled(false);
        cancelButton.setEnabled(false);

        // Notifications area
        notificationsArea = new JTextArea(8, 30);
        notificationsArea.setEditable(false);
        notificationsArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        notificationsArea.setBackground(new Color(248, 248, 255));

        // Load combo box data
        loadComboBoxData();
    }

    private void loadComboBoxData() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    // Load patients
                    List<Patient> patients = patientDAO.getAllPatients();
                    SwingUtilities.invokeLater(() -> {
                        patientComboBox.removeAllItems();
                        patientComboBox.addItem("Select Patient");
                        for (Patient patient : patients) {
                            patientComboBox.addItem(patient.getId() + " - " + patient.getName());
                        }
                    });

                    // Load staff (doctors)
                    List<Staff> staff = staffDAO.getAllDoctors();
                    SwingUtilities.invokeLater(() -> {
                        doctorComboBox.removeAllItems();
                        doctorComboBox.addItem("Select Doctor");
                        for (Staff s : staff) {
                            doctorComboBox.addItem(s.getId() + " - " + s.getName());
                        }
                    });

                    // Load locations
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
                        JOptionPane.showMessageDialog(AppointmentPanel.this,
                                "Error loading data: " + e.getMessage(),
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

        JLabel titleLabel = new JLabel("Appointment Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton patternsDemo = new JButton("Show Mediator/Observer Demo");
        patternsDemo.addActionListener(e -> demonstratePatterns());
        headerPanel.add(patternsDemo, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Main content with split pane
        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplit.setDividerLocation(800);

        // Left panel - appointments table and form
        JPanel leftPanel = new JPanel(new BorderLayout());

        // Appointments table
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Scheduled Appointments"));

        JScrollPane tableScroll = new JScrollPane(appointmentTable);
        tableScroll.setPreferredSize(new Dimension(750, 300));
        tablePanel.add(tableScroll, BorderLayout.CENTER);

        JPanel tableButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tableButtons.add(refreshButton);
        tableButtons.add(rescheduleButton);
        tableButtons.add(cancelButton);
        tablePanel.add(tableButtons, BorderLayout.SOUTH);

        leftPanel.add(tablePanel, BorderLayout.CENTER);

        // Scheduling form
        JPanel formPanel = createSchedulingForm();
        leftPanel.add(formPanel, BorderLayout.SOUTH);

        mainSplit.setLeftComponent(leftPanel);

        // Right panel - notifications and calendar view
        JPanel rightPanel = new JPanel(new BorderLayout());

        // Notifications panel
        JPanel notificationsPanel = new JPanel(new BorderLayout());
        notificationsPanel.setBorder(BorderFactory.createTitledBorder("System Notifications"));

        JScrollPane notifScroll = new JScrollPane(notificationsArea);
        notifScroll.setPreferredSize(new Dimension(300, 200));
        notificationsPanel.add(notifScroll, BorderLayout.CENTER);

        JPanel notifButtons = new JPanel(new FlowLayout());
        JButton clearNotifBtn = new JButton("Clear");
        clearNotifBtn.addActionListener(e -> notificationsArea.setText(""));
        notifButtons.add(clearNotifBtn);
        notificationsPanel.add(notifButtons, BorderLayout.SOUTH);

        rightPanel.add(notificationsPanel, BorderLayout.CENTER);

        // Quick actions panel
        JPanel quickActionsPanel = createQuickActionsPanel();
        rightPanel.add(quickActionsPanel, BorderLayout.SOUTH);

        mainSplit.setRightComponent(rightPanel);
        add(mainSplit, BorderLayout.CENTER);
    }

    private JPanel createSchedulingForm() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Schedule New Appointment"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Row 1: Patient and Doctor
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Patient:"), gbc);
        gbc.gridx = 1;
        formPanel.add(patientComboBox, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Doctor:"), gbc);
        gbc.gridx = 3;
        formPanel.add(doctorComboBox, gbc);

        // Row 2: Date and Time
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Date:"), gbc);
        gbc.gridx = 1;
        formPanel.add(dateChooser, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Time:"), gbc);
        gbc.gridx = 3;
        formPanel.add(timeSpinner, gbc);

        // Row 3: Location and Status
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Location:"), gbc);
        gbc.gridx = 1;
        formPanel.add(locationComboBox, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 3;
        formPanel.add(statusComboBox, gbc);

        // Row 4: Buttons
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(scheduleButton);

        JButton checkAvailabilityBtn = new JButton("Check Availability");
        checkAvailabilityBtn.addActionListener(e -> checkAvailability());
        buttonPanel.add(checkAvailabilityBtn);

        JButton clearFormBtn = new JButton("Clear Form");
        clearFormBtn.addActionListener(e -> clearSchedulingForm());
        buttonPanel.add(clearFormBtn);

        formPanel.add(buttonPanel, gbc);

        return formPanel;
    }

    private JPanel createQuickActionsPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Quick Actions"));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JButton todayApptBtn = new JButton("Today's Appointments");
        JButton emergencySlotBtn = new JButton("Emergency Slot");
        JButton patternDemoBtn = new JButton("Pattern Demo");

        todayApptBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        emergencySlotBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        patternDemoBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        todayApptBtn.addActionListener(e -> showTodaysAppointments());
        emergencySlotBtn.addActionListener(e -> scheduleEmergencyAppointment());
        patternDemoBtn.addActionListener(e -> demonstratePatterns());

        panel.add(Box.createVerticalStrut(10));
        panel.add(todayApptBtn);
        panel.add(Box.createVerticalStrut(5));
        panel.add(emergencySlotBtn);
        panel.add(Box.createVerticalStrut(5));
        panel.add(patternDemoBtn);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private void setupEventHandlers() {
        // Table selection
        appointmentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = appointmentTable.getSelectedRow() != -1;
                rescheduleButton.setEnabled(hasSelection);
                cancelButton.setEnabled(hasSelection);
            }
        });

        // Button listeners
        scheduleButton.addActionListener(e -> scheduleAppointment());
        rescheduleButton.addActionListener(e -> rescheduleAppointment());
        cancelButton.addActionListener(e -> cancelAppointment());
        refreshButton.addActionListener(e -> refreshData());

        // Set today's date by default
        dateChooser.setDate(java.sql.Date.valueOf(LocalDate.now())); 

    }

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
                JOptionPane.showMessageDialog(this, "Please fill all required fields!",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
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
                            JOptionPane.showMessageDialog(AppointmentPanel.this,
                                    "Appointment scheduled successfully!",
                                    "Success", JOptionPane.INFORMATION_MESSAGE);
                            addNotification("✅ Appointment scheduled for " + appointmentTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                            clearSchedulingForm();
                            refreshData();
                        } else {
                            JOptionPane.showMessageDialog(AppointmentPanel.this,
                                    "Failed to schedule appointment. Time slot may not be available.",
                                    "Scheduling Error", JOptionPane.WARNING_MESSAGE);
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(AppointmentPanel.this,
                                "Error scheduling appointment: " + e.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };

            worker.execute();

        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date/time format!",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void rescheduleAppointment() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        int modelRow = appointmentTable.convertRowIndexToModel(selectedRow);
        Long appointmentId = (Long) tableModel.getValueAt(modelRow, 0);

        // Create reschedule dialog
        JDialog rescheduleDialog = new JDialog(parentFrame, "Reschedule Appointment", true);
        rescheduleDialog.setSize(400, 300);
        rescheduleDialog.setLocationRelativeTo(this);

        JPanel content = new JPanel(new GridBagLayout());
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JDateChooser newDateChooser = new JDateChooser();
        newDateChooser.setDateFormatString("yyyy-MM-dd");
        newDateChooser.setDate(java.sql.Date.valueOf(LocalDate.now()));
        
        SpinnerDateModel newTimeModel = new SpinnerDateModel();
        JSpinner newTimeSpinner = new JSpinner(newTimeModel);
        JSpinner.DateEditor newTimeEditor = new JSpinner.DateEditor(newTimeSpinner, "HH:mm");
        newTimeSpinner.setEditor(newTimeEditor);
        newTimeSpinner.setValue(new java.util.Date());
        
        JComboBox<String> newLocationCombo = new JComboBox<>();

        // Populate location combo
        for (String room : appointmentService.getAvailableRooms()) {
            newLocationCombo.addItem(room);
        }

        // Layout reschedule form
        gbc.gridx = 0;
        gbc.gridy = 0;
        content.add(new JLabel("New Date:"), gbc);
        gbc.gridx = 1;
        content.add(newDateChooser, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        content.add(new JLabel("New Time:"), gbc);
        gbc.gridx = 1;
        content.add(newTimeSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        content.add(new JLabel("New Location:"), gbc);
        gbc.gridx = 1;
        content.add(newLocationCombo, gbc);

        // Buttons
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttons = new JPanel(new FlowLayout());
        JButton confirmBtn = new JButton("Confirm Reschedule");
        JButton cancelBtn = new JButton("Cancel");

        confirmBtn.addActionListener(e -> {
            try {
                java.util.Date selectedDate = newDateChooser.getDate();
                java.util.Date selectedTime = (java.util.Date) newTimeSpinner.getValue();
                
                if (selectedDate == null || selectedTime == null) {
                    JOptionPane.showMessageDialog(rescheduleDialog,
                            "Please select both date and time!",
                            "Validation Error", JOptionPane.WARNING_MESSAGE);
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
                } else {
                    JOptionPane.showMessageDialog(rescheduleDialog,
                            "Failed to reschedule appointment!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(rescheduleDialog,
                        "Invalid date/time format!",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
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
                "Cancel appointment for " + patientInfo + " on " + dateTime + "?",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = appointmentService.cancelAppointment(appointmentId);
            if (success) {
                addNotification("❌ Appointment " + appointmentId + " cancelled for " + patientInfo);
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to cancel appointment!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void checkAvailability() {
        try {
            Long staffId = extractIdFromComboBox(doctorComboBox);
            String location = (String) locationComboBox.getSelectedItem();
            LocalDateTime appointmentTime = parseDateTime();

            if (staffId == null || location.equals("Select Location")) {
                JOptionPane.showMessageDialog(this, "Please select doctor and location first!",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean available = appointmentService.checkAvailability(staffId, location, appointmentTime);

            String message = available
                    ? "✅ Time slot is AVAILABLE"
                    : "❌ Time slot is NOT AVAILABLE";

            JOptionPane.showMessageDialog(this, message,
                    "Availability Check",
                    available ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);

            addNotification("🔍 Availability checked for " + appointmentTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + " - "
                    + (available ? "Available" : "Unavailable"));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error checking availability: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validateAppointmentForm() {
        if (patientComboBox.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(this, "Please select a patient!",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (doctorComboBox.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(this, "Please select a doctor!",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (locationComboBox.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(this, "Please select a location!",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            parseDateTime();
            return true;
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date/time format!\nDate: YYYY-MM-DD, Time: HH:MM",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
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
                    parentFrame.setStatus("Appointments refreshed - " + appointments.size() + " appointments loaded");
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(AppointmentPanel.this,
                            "Error loading appointments: " + e.getMessage(),
                            "Database Error", JOptionPane.ERROR_MESSAGE);
                    parentFrame.setStatus("Error loading appointments");
                }
            }
        };

        worker.execute();
    }

    // FIXED METHOD - Uses single database operation instead of multiple SwingWorkers
    private void updateTableData(List<Appointment> appointments) {
        // Clear the table first
        tableModel.setRowCount(0);

        if (appointments == null || appointments.isEmpty()) {
            return;
        }

        // Load all patient and staff data in a single operation to avoid multiple database calls
        SwingWorker<Void, Void> dataLoader = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    // Pre-load all patients and staff to avoid multiple database calls
                    List<Patient> allPatients = patientDAO.getAllPatients();
                    List<Staff> allStaff = staffDAO.getAllStaff();

                    // Create maps for quick lookup
                    java.util.Map<Long, Patient> patientMap = new java.util.HashMap<>();
                    java.util.Map<Long, Staff> staffMap = new java.util.HashMap<>();

                    for (Patient patient : allPatients) {
                        patientMap.put(patient.getId(), patient);
                    }

                    for (Staff staff : allStaff) {
                        staffMap.put(staff.getId(), staff);
                    }

                    // Process all appointments with the loaded data
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

                        // Add row to table model on EDT
                        SwingUtilities.invokeLater(() -> tableModel.addRow(row));
                    }

                } catch (SQLException e) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(AppointmentPanel.this,
                                "Error loading appointment details: " + e.getMessage(),
                                "Database Error", JOptionPane.ERROR_MESSAGE);
                    });
                }
                return null;
            }
        };

        dataLoader.execute();
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
                    JOptionPane.showMessageDialog(AppointmentPanel.this,
                            "Error loading today's appointments: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();
    }

    // FIXED METHOD - Uses single database operation instead of multiple SwingWorkers
    private void showTodaysAppointmentsDialog(List<Appointment> todayAppointments) {
        JDialog dialog = new JDialog(parentFrame, "Today's Appointments - " + LocalDate.now(), true);
        dialog.setSize(700, 500);
        dialog.setLocationRelativeTo(this);

        String[] columns = {"Time", "Patient", "Doctor", "Location", "Status"};
        DefaultTableModel todayModel = new DefaultTableModel(columns, 0);
        JTable todayTable = new JTable(todayModel);

        // Load all patient and staff data once
        SwingWorker<Void, Void> dataLoader = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    // Pre-load all patients and staff
                    List<Patient> allPatients = patientDAO.getAllPatients();
                    List<Staff> allStaff = staffDAO.getAllStaff();

                    // Create maps for quick lookup
                    java.util.Map<Long, Patient> patientMap = new java.util.HashMap<>();
                    java.util.Map<Long, Staff> staffMap = new java.util.HashMap<>();

                    for (Patient patient : allPatients) {
                        patientMap.put(patient.getId(), patient);
                    }

                    for (Staff staff : allStaff) {
                        staffMap.put(staff.getId(), staff);
                    }

                    // Process all today's appointments
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

                        // Add row on EDT
                        SwingUtilities.invokeLater(() -> todayModel.addRow(row));
                    }

                } catch (SQLException e) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(dialog,
                                "Error loading appointment details: " + e.getMessage(),
                                "Database Error", JOptionPane.ERROR_MESSAGE);
                    });
                }
                return null;
            }
        };

        dataLoader.execute();

        JScrollPane scrollPane = new JScrollPane(todayTable);
        dialog.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeBtn);

        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void scheduleEmergencyAppointment() {
        JDialog emergencyDialog = new JDialog(parentFrame, "Emergency Appointment", true);
        emergencyDialog.setSize(400, 250);
        emergencyDialog.setLocationRelativeTo(this);

        JPanel content = new JPanel(new GridBagLayout());
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JComboBox<String> emergencyPatientCombo = new JComboBox<>();
        JComboBox<String> emergencyDoctorCombo = new JComboBox<>();

        // Populate combos (simplified for emergency)
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
            JOptionPane.showMessageDialog(emergencyDialog, "Error loading data: " + e.getMessage());
        }

        // Layout
        gbc.gridx = 0;
        gbc.gridy = 0;
        content.add(new JLabel("Patient:"), gbc);
        gbc.gridx = 1;
        content.add(emergencyPatientCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        content.add(new JLabel("Available Doctor:"), gbc);
        gbc.gridx = 1;
        content.add(emergencyDoctorCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttons = new JPanel();
        JButton scheduleEmergencyBtn = new JButton("Schedule Emergency");
        JButton cancelEmergencyBtn = new JButton("Cancel");

        scheduleEmergencyBtn.addActionListener(e -> {
            Long patientId = extractIdFromComboBox(emergencyPatientCombo);
            Long doctorId = extractIdFromComboBox(emergencyDoctorCombo);

            if (patientId != null && doctorId != null) {
                // Schedule for next available slot (in 15 minutes)
                LocalDateTime emergencyTime = LocalDateTime.now().plusMinutes(15);
                boolean success = appointmentService.scheduleAppointment(patientId, doctorId, emergencyTime, "Emergency Room");

                if (success) {
                    addNotification("🚨 Emergency appointment scheduled for "
                            + emergencyTime.format(DateTimeFormatter.ofPattern("HH:mm")));
                    refreshData();
                    emergencyDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(emergencyDialog,
                            "Failed to schedule emergency appointment!",
                            "Error", JOptionPane.ERROR_MESSAGE);
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

    private void demonstratePatterns() {
        StringBuilder demo = new StringBuilder();
        demo.append("=== APPOINTMENT SYSTEM PATTERNS DEMO ===\n\n");
        demo.append("🎯 MEDIATOR PATTERN:\n");
        demo.append("• AppointmentMediator coordinates between Patient, Doctor, and Room components\n");
        demo.append("• Centralizes communication logic\n");
        demo.append("• Reduces coupling between components\n\n");

        demo.append("👀 OBSERVER PATTERN:\n");
        demo.append("• PatientObserver receives appointment notifications\n");
        demo.append("• DoctorObserver gets schedule updates\n");
        demo.append("• AdminObserver monitors all appointment activities\n\n");

        demo.append("🔄 PATTERN INTERACTION:\n");
        demo.append("• When appointment is scheduled:\n");
        demo.append("  1. Mediator validates availability\n");
        demo.append("  2. Creates appointment\n");
        demo.append("  3. Notifies all observers\n");
        demo.append("  4. Updates GUI notifications\n\n");

        demo.append("📈 BENEFITS:\n");
        demo.append("• Loose coupling between components\n");
        demo.append("• Real-time notifications\n");
        demo.append("• Extensible notification system\n");
        demo.append("• Centralized appointment logic\n");

        JDialog demoDialog = new JDialog(parentFrame, "Design Patterns Demo", true);
        demoDialog.setSize(600, 500);
        demoDialog.setLocationRelativeTo(this);

        JTextArea demoArea = new JTextArea(demo.toString());
        demoArea.setEditable(false);
        demoArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(demoArea);
        demoDialog.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> demoDialog.dispose());
        buttonPanel.add(closeBtn);

        demoDialog.add(buttonPanel, BorderLayout.SOUTH);
        demoDialog.setVisible(true);

        // Add to notifications
        addNotification("📖 Design patterns demonstration viewed");
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

    /**
     * Cleanup method to properly close database connections
     */
    public void cleanup() {
        try {
            // Close any open database connections
            if (appointmentDAO != null) {
                // Add cleanup logic if needed
            }
            if (patientDAO != null) {
                // Add cleanup logic if needed
            }
            if (staffDAO != null) {
                // Add cleanup logic if needed
            }
        } catch (Exception e) {
            System.err.println("Error during cleanup: " + e.getMessage());
        }
    }

    /**
     * Override dispose to ensure cleanup
     */
    @Override
    public void removeNotify() {
        cleanup();
        super.removeNotify();
    }
}

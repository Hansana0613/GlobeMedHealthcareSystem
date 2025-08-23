package com.globmed.view;

import com.globmed.service.AppointmentService;
import javax.swing.*;

/**
 *
 * @author Hansana
 */
public class AppointmentSchedulerPanel extends JPanel {

    private AppointmentService appointmentService = new AppointmentService();

    public AppointmentSchedulerPanel() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        jLabel1 = new JLabel();
        patientComboBox = new JComboBox<>();
        jLabel2 = new JLabel();
        staffComboBox = new JComboBox<>();
        jLabel3 = new JLabel();
        timeField = new JTextField();
        jLabel4 = new JLabel();
        locationField = new JTextField();
        scheduleButton = new JButton();
        appointmentTable = new JTable();
        jScrollPane1 = new JScrollPane();

        setBorder(BorderFactory.createTitledBorder("Appointment Scheduler"));

        jLabel1.setText("Patient:");

        patientComboBox.setModel(new DefaultComboBoxModel<>(new String[]{"John Doe", "Jane Smith"}));

        jLabel2.setText("Staff:");

        staffComboBox.setModel(new DefaultComboBoxModel<>(new String[]{"Dr. Alice Johnson", "Nurse Bob Lee"}));

        jLabel3.setText("Time (YYYY-MM-DD HH:MM):");

        jLabel4.setText("Location:");

        scheduleButton.setText("Schedule Appointment");

        appointmentTable.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "Patient", "Staff", "Time", "Location", "Status"}
        ));
        jScrollPane1.setViewportView(appointmentTable);

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel1)
                                        .addComponent(jLabel2)
                                        .addComponent(jLabel3)
                                        .addComponent(jLabel4))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(patientComboBox, 0, 150, Short.MAX_VALUE)
                                        .addComponent(staffComboBox, 0, 150, Short.MAX_VALUE)
                                        .addComponent(timeField)
                                        .addComponent(locationField)
                                        .addComponent(scheduleButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel1)
                                        .addComponent(patientComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel2)
                                        .addComponent(staffComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel3)
                                        .addComponent(timeField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel4)
                                        .addComponent(locationField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addComponent(scheduleButton)
                                .addContainerGap(50, Short.MAX_VALUE))
                        .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                                .addContainerGap())
        );
        scheduleButton.addActionListener(evt -> scheduleButtonActionPerformed(evt));
    } // </editor-fold>

    private void scheduleButtonActionPerformed(java.awt.event.ActionEvent evt) {
        String patient = (String) patientComboBox.getSelectedItem();
        String staff = (String) staffComboBox.getSelectedItem();
        String time = timeField.getText();
        String location = locationField.getText();
        appointmentService.bookAppointment(patient, staff, time, location);
    }

    // Variables declaration
    private JTable appointmentTable;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JScrollPane jScrollPane1;
    private JTextField locationField;
    private JComboBox<String> patientComboBox;
    private JButton scheduleButton;
    private JComboBox<String> staffComboBox;
    private JTextField timeField;
    // End of variables declaration
}

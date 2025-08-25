package com.globmed.view;

import com.globmed.dao.PatientDAO;
import com.globmed.model.Patient;
import com.globmed.service.PatientService;
import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Hansana
 */
public class PatientManagementPanel extends JPanel {

    private PatientService patientService = new PatientService();
    private PatientDAO patientDAO = new PatientDAO();

    public PatientManagementPanel() {
        initComponents();
        loadPatients();
    }

    private void loadPatients() {
        DefaultTableModel model = (DefaultTableModel) patientTable.getModel();
        model.setRowCount(0);
        for (Patient p : patientDAO.findAll()) {
            model.addRow(new Object[]{p.getId(), p.getName(), p.getDob(), p.getAddress(), p.getPhone()});
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        jLabel1 = new JLabel();
        nameField = new JTextField();
        jLabel2 = new JLabel();
        dobChooser = new JDateChooser(); // Replaced JTextField with JDateChooser
        jLabel3 = new JLabel();
        addressField = new JTextField();
        jLabel4 = new JLabel();
        phoneField = new JTextField();
        jLabel5 = new JLabel();
        historyField = new JTextArea();
        addButton = new JButton();
        editButton = new JButton();
        patientTable = new JTable();
        jScrollPane1 = new JScrollPane();

        setBorder(BorderFactory.createTitledBorder("Patient Management"));

        jLabel1.setText("Name:");

        jLabel2.setText("Date of Birth:");

        jLabel3.setText("Address:");

        jLabel4.setText("Phone:");

        jLabel5.setText("Medical History:");

        historyField.setColumns(20);
        historyField.setRows(5);
        jScrollPane1.setViewportView(historyField);

        addButton.setText("Add Patient");

        editButton.setText("Edit Patient");

        patientTable.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "Name", "DOB", "Address", "Phone"}
        ));
        jScrollPane1.setViewportView(patientTable);

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
                                        .addComponent(jLabel4)
                                        .addComponent(jLabel5))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(nameField)
                                        .addComponent(dobChooser, GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE) // Adjusted size
                                        .addComponent(addressField)
                                        .addComponent(phoneField)
                                        .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(addButton)
                                        .addComponent(editButton)
                                        .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel1)
                                        .addComponent(nameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(addButton))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel2)
                                        .addComponent(dobChooser, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE) // Adjusted height
                                        .addComponent(editButton))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel3)
                                        .addComponent(addressField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel4)
                                        .addComponent(phoneField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel5)
                                        .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                                .addContainerGap())
        );

        addButton.addActionListener(evt -> addButtonActionPerformed(evt));
    } // </editor-fold>

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {
        Patient patient = new Patient();
        patient.setName(nameField.getText());
        patient.setDob(new java.sql.Date(((JDateChooser) dobChooser).getDate().getTime())); // Convert to java.sql.Date
        patient.setAddress(addressField.getText());
        patient.setPhone(phoneField.getText());
        patient.setMedicalHistory(historyField.getText());
        patientDAO.save(patient);
        loadPatients();
    }

    // Variables declaration
    private JButton addButton;
    private JTextField addressField;
    private JButton editButton;
    private JTextArea historyField;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JLabel jLabel5;
    private JScrollPane jScrollPane1;
    private JTextField nameField;
    private JTextField phoneField;
    private JDateChooser dobChooser; // Replaced dobField
    private JTable patientTable;
    // End of variables declaration
}

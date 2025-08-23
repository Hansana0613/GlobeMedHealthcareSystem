package com.globmed.view;

import javax.swing.*;

/**
 *
 * @author Hansana
 */
public class StaffRolesPanel extends JPanel {

    public StaffRolesPanel() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        jLabel1 = new JLabel();
        staffNameField = new JTextField();
        jLabel2 = new JLabel();
        roleComboBox = new JComboBox<>();
        jLabel3 = new JLabel();
        permissionList = new JList<>();
        addStaffButton = new JButton();
        roleTable = new JTable();
        jScrollPane1 = new JScrollPane();
        jScrollPane2 = new JScrollPane();

        setBorder(BorderFactory.createTitledBorder("Staff & Roles"));

        jLabel1.setText("Staff Name:");

        jLabel2.setText("Role:");

        roleComboBox.setModel(new DefaultComboBoxModel<>(new String[] { "Admin", "Doctor", "Nurse", "Pharmacist" }));

        jLabel3.setText("Permissions:");

        permissionList.setModel(new AbstractListModel<String>() {
            String[] strings = { "View Patient Records", "Edit Patient Records", "Schedule Appointments", "Process Bills", "Generate Reports" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(permissionList);

        addStaffButton.setText("Add Staff");

        roleTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object[][] {},
            new String[] {"ID", "Name", "Role", "Permissions"}
        ));
        jScrollPane1.setViewportView(roleTable);

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                    .addComponent(staffNameField)
                    .addComponent(roleComboBox, 0, 150, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                    .addComponent(addStaffButton))
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
                    .addComponent(staffNameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(roleComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jScrollPane2, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(addStaffButton)
                .addContainerGap(50, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                .addContainerGap())
        );
    } // </editor-fold>

    // Variables declaration
    private JButton addStaffButton;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JList<String> permissionList;
    private JComboBox<String> roleComboBox;
    private JScrollPane jScrollPane1;
    private JScrollPane jScrollPane2;
    private JTextField staffNameField;
    private JTable roleTable;
    // End of variables declaration
}
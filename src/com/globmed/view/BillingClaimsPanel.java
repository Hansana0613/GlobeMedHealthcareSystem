package com.globmed.view;

import com.globmed.model.Bill;
import com.globmed.service.BillingService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Hansana
 */
public class BillingClaimsPanel extends JPanel {

    private BillingService billingService = new BillingService();

    public BillingClaimsPanel() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        jLabel1 = new JLabel();
        appointmentComboBox = new JComboBox<>();
        jLabel2 = new JLabel();
        itemComboBox = new JComboBox<>();
        jLabel3 = new JLabel();
        costField = new JTextField();
        addItemButton = new JButton();
        totalLabel = new JLabel();
        processClaimButton = new JButton();
        billTable = new JTable();
        jScrollPane1 = new JScrollPane();
        insuranceField = new JTextField();
        jLabel4 = new JLabel();

        setBorder(BorderFactory.createTitledBorder("Billing & Claims"));

        jLabel1.setText("Appointment:");

        appointmentComboBox.setModel(new DefaultComboBoxModel<>(new String[]{"App 1 - John Doe", "App 2 - Jane Smith"}));

        jLabel2.setText("Item Type:");

        itemComboBox.setModel(new DefaultComboBoxModel<>(new String[]{"Consultation", "Treatment", "Medication", "Diagnostic"}));

        jLabel3.setText("Cost:");

        addItemButton.setText("Add Item");

        totalLabel.setText("Total: $0.00");

        processClaimButton.setText("Process Claim");

        billTable.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{"Item", "Cost"}
        ));
        jScrollPane1.setViewportView(billTable);

        jLabel4.setText("Insurance Details:");

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
                                        .addComponent(appointmentComboBox, 0, 150, Short.MAX_VALUE)
                                        .addComponent(itemComboBox, 0, 150, Short.MAX_VALUE)
                                        .addComponent(costField)
                                        .addComponent(insuranceField)
                                        .addComponent(addItemButton)
                                        .addComponent(totalLabel)
                                        .addComponent(processClaimButton))
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
                                        .addComponent(appointmentComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel2)
                                        .addComponent(itemComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel3)
                                        .addComponent(costField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel4)
                                        .addComponent(insuranceField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addComponent(addItemButton)
                                .addGap(18, 18, 18)
                                .addComponent(totalLabel)
                                .addGap(18, 18, 18)
                                .addComponent(processClaimButton)
                                .addContainerGap(50, Short.MAX_VALUE))
                        .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                                .addContainerGap())
        );
        addItemButton.addActionListener(evt -> addItemButtonActionPerformed(evt));
        processClaimButton.addActionListener(evt -> processClaimButtonActionPerformed(evt));
    } // </editor-fold>

    private void addItemButtonActionPerformed(java.awt.event.ActionEvent evt) {
        DefaultTableModel model = (DefaultTableModel) billTable.getModel();
        String item = (String) itemComboBox.getSelectedItem();
        double cost = Double.parseDouble(costField.getText());
        model.addRow(new Object[]{item, cost});
        totalLabel.setText("Total: $" + (Double.parseDouble(totalLabel.getText().replace("Total: $", "")) + cost));
    }

    private void processClaimButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // Simulate bill creation
        Bill bill = billingService.createBill("Sample Bill");
        if (billingService.processClaim(bill)) {
            System.out.println("Claim processed.");
        }
    }

    // Variables declaration
    private JButton addItemButton;
    private JComboBox<String> appointmentComboBox;
    private JTable billTable;
    private JTextField costField;
    private JTextField insuranceField;
    private JComboBox<String> itemComboBox;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JScrollPane jScrollPane1;
    private JButton processClaimButton;
    private JLabel totalLabel;
    // End of variables declaration
}

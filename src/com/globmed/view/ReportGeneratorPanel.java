package com.globmed.view;

import javax.swing.*;

/**
 *
 * @author Hansana
 */
public class ReportGeneratorPanel extends JPanel {

    public ReportGeneratorPanel() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        jLabel1 = new JLabel();
        reportTypeComboBox = new JComboBox<>();
        jLabel2 = new JLabel();
        patientComboBox = new JComboBox<>();
        generateButton = new JButton();
        reportTextArea = new JTextArea();
        jScrollPane1 = new JScrollPane();

        setBorder(BorderFactory.createTitledBorder("Report Generator"));

        jLabel1.setText("Report Type:");

        reportTypeComboBox.setModel(new DefaultComboBoxModel<>(new String[] { "Treatment Summary", "Diagnostic Results", "Financial" }));

        jLabel2.setText("Patient:");

        patientComboBox.setModel(new DefaultComboBoxModel<>(new String[] { "John Doe", "Jane Smith" }));

        generateButton.setText("Generate Report");

        reportTextArea.setColumns(20);
        reportTextArea.setRows(5);
        jScrollPane1.setViewportView(reportTextArea);

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                    .addComponent(reportTypeComboBox, 0, 150, Short.MAX_VALUE)
                    .addComponent(patientComboBox, 0, 150, Short.MAX_VALUE)
                    .addComponent(generateButton))
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
                    .addComponent(reportTypeComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(patientComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(generateButton)
                .addContainerGap(50, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                .addContainerGap())
        );
    } // </editor-fold>

    // Variables declaration
    private JButton generateButton;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JScrollPane jScrollPane1;
    private JComboBox<String> patientComboBox;
    private JTextArea reportTextArea;
    private JComboBox<String> reportTypeComboBox;
    // End of variables declaration
}
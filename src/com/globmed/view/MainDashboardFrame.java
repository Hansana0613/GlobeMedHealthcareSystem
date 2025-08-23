package com.globmed.view;

import javax.swing.*;

/**
 *
 * @author Hansana
 */
public class MainDashboardFrame extends JFrame {

    public MainDashboardFrame() {
        initComponents();
        setLocationRelativeTo(null); // Center on screen
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        tabbedPane = new JTabbedPane();
        patientPanel = new PatientManagementPanel();
        appointmentPanel = new AppointmentSchedulerPanel();
        billingPanel = new BillingClaimsPanel();
        staffPanel = new StaffRolesPanel();
        reportPanel = new ReportGeneratorPanel();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("GlobeMed Dashboard");

        tabbedPane.addTab("Patient Management", patientPanel);
        tabbedPane.addTab("Appointment Scheduler", appointmentPanel);
        tabbedPane.addTab("Billing & Claims", billingPanel);
        tabbedPane.addTab("Staff & Roles", staffPanel);
        tabbedPane.addTab("Report Generator", reportPanel);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );

        pack();
    } // </editor-fold>

    // Variables declaration
    private AppointmentSchedulerPanel appointmentPanel;
    private BillingClaimsPanel billingPanel;
    private PatientManagementPanel patientPanel;
    private ReportGeneratorPanel reportPanel;
    private StaffRolesPanel staffPanel;
    private JTabbedPane tabbedPane;
    // End of variables declaration
}

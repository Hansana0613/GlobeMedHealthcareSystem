package com.globmed.view;

import com.globmed.service.PatientService;
import com.globmed.service.SecurityService;
import javax.swing.*;

/**
 *
 * @author Hansana
 */
public class LoginFrame extends JFrame {

    private SecurityService securityService = new SecurityService();

    public LoginFrame() {
        initComponents();
        setLocationRelativeTo(null); // Center on screen
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        jLabel1 = new JLabel();
        jLabel2 = new JLabel();
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        loginButton = new JButton();
        roleComboBox = new JComboBox<>();
        jLabel3 = new JLabel();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("GlobeMed Login");

        jLabel1.setText("Username:");

        jLabel2.setText("Password:");

        loginButton.setText("Login");

        roleComboBox.setModel(new DefaultComboBoxModel<>(new String[]{"Admin", "Doctor", "Nurse", "Pharmacist"}));

        jLabel3.setText("Role:");

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(50, 50, 50)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel1)
                                        .addComponent(jLabel2)
                                        .addComponent(jLabel3))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(usernameField)
                                        .addComponent(passwordField)
                                        .addComponent(roleComboBox, 0, 150, Short.MAX_VALUE)
                                        .addComponent(loginButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap(50, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(40, 40, 40)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel1)
                                        .addComponent(usernameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel2)
                                        .addComponent(passwordField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(roleComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel3))
                                .addGap(18, 18, 18)
                                .addComponent(loginButton)
                                .addContainerGap(40, Short.MAX_VALUE))
        );

        pack();
        loginButton.addActionListener(evt -> loginButtonActionPerformed(evt));
    } // </editor-fold>

    private void loginButtonActionPerformed(java.awt.event.ActionEvent evt) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String role = (String) roleComboBox.getSelectedItem();
        String secureInput = securityService.secureExecute(username + ":" + password + ":" + role);
        if (secureInput.contains("carol_admin:hashedpass3:Admin")) { // Placeholder check
            new MainDashboardFrame().setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials.");
        }
    }

    // Variables declaration
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JButton loginButton;
    private JComboBox<String> roleComboBox;
    private JPasswordField passwordField;
    private JTextField usernameField;
    // End of variables declaration
}

package com.globmed.main;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.globmed.view.LoginFrame;
import com.globmed.view.MainDashboardFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author Hansana
 */
public class Main {
    public static void main(String[] args) throws UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel(new FlatIntelliJLaf());
        java.awt.EventQueue.invokeLater(() -> {
            new MainDashboardFrame().setVisible(true);
        });
    }
}

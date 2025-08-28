/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.main;

//import com.globemed.gui.MainFrame;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.globemed.database.DatabaseConnection;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.sql.SQLException;

/**
 *
 * @author Hansana
 */
public class GlobeMedMain {

    public static void main(String[] args) {
        // Set Look and Feel
        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
        } catch (Exception e) {
            System.err.println("Error setting Look and Feel: " + e.getMessage());
        }

        // Test database connection
        try {
            DatabaseConnection.getConnection();
            System.out.println("Application starting...");
        } catch (SQLException e) {
            System.err.println("Failed to connect to database: " + e.getMessage());
            System.exit(1);
        }

        // Launch GUI
//        SwingUtilities.invokeLater(() -> {
//            try {
//                MainFrame frame = new MainFrame();
//                frame.setVisible(true);
//            } catch (Exception e) {
//                System.err.println("Error launching application: " + e.getMessage());
//                e.printStackTrace();
//            }
//        });
        // Shutdown hook to close database connection
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DatabaseConnection.closeConnection();
            System.out.println("Application closed.");
        }));
    }
}

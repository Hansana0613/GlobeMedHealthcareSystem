/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.database;

import java.sql.*;
import java.util.Properties;

/**
 *
 * @author Hansana
 */
public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/globemed_db";
    private static final String USERNAME = "root"; // Change as needed
    private static final String PASSWORD = "0000"; // Change as needed

    private static Connection connection = null;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Properties props = new Properties();
                props.setProperty("user", USERNAME);
                props.setProperty("password", PASSWORD);
                props.setProperty("useSSL", "false");
                props.setProperty("allowPublicKeyRetrieval", "true");

                connection = DriverManager.getConnection(URL, props);
                System.out.println("Database connected successfully!");
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver not found", e);
            }
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }
    
    /**
     * Begin a transaction
     */
    public static void beginTransaction() throws SQLException {
        Connection conn = getConnection();
        if (conn != null) {
            conn.setAutoCommit(false);
        }
    }
    
    /**
     * Commit the current transaction
     */
    public static void commitTransaction() throws SQLException {
        Connection conn = getConnection();
        if (conn != null) {
            conn.commit();
            conn.setAutoCommit(true);
        }
    }
    
    /**
     * Rollback the current transaction
     */
    public static void rollbackTransaction() {
        try {
            Connection conn = getConnection();
            if (conn != null) {
                conn.rollback();
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Error rolling back transaction: " + e.getMessage());
        }
    }
}

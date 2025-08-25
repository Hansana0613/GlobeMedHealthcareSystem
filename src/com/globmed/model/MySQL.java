package com.globmed.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 *
 * @author green
 */
public class MySQL {

    private static Connection connection;

    public static void createConection() throws Exception {

        if (connection == null) {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/globemed_db", "root", "0000");
        }

    }

    public static ResultSet executeSearch(String query) throws Exception {
        createConection();
        return connection.createStatement().executeQuery(query);
    }

    public static Integer executeIUD(String query) throws Exception {
        createConection();
        return connection.createStatement().executeUpdate(query);
    }

}

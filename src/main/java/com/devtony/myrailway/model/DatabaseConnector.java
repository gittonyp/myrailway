package com.devtony.myrailway.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    // PostgreSQL configuration
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/myrailway";
    private static final String DB_USER = "postgres";  // Default PostgreSQL user
    private static final String DB_PASSWORD = "asdf1234";

    // Initialize the database connection
    public static Connection getConnection() throws SQLException {
        try {
            // Load the PostgreSQL JDBC driver
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL JDBC Driver not found!", e);
        }

        // Create and return the connection
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    // Close the connection (optional helper method)
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Failed to close connection: " + e.getMessage());
            }
        }
    }
}
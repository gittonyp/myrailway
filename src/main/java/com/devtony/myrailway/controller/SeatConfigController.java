// File Path: /home/tony/intellih/myrailway/src/main/java/com/devtony/myrailway/controller/SeatConfigController.java
package com.devtony.myrailway.controller;

import com.devtony.myrailway.model.DatabaseConnector;
import com.devtony.myrailway.model.SeatConfiguration;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeatConfigController {
    private Connection connection;

    public SeatConfigController() {
        try {
            connection = DatabaseConnector.getConnection();
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
        }
    }

    public boolean addSeatConfig(SeatConfiguration config) {
        String sql = "INSERT INTO seat_configurations (train_id, class_name, total_seats, price) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, config.getTrainId());
            stmt.setString(2, config.getClassName());
            stmt.setInt(3, config.getTotalSeats());
            stmt.setDouble(4, config.getPrice());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding seat config: " + e.getMessage());
            return false;
        }
    }

    public List<SeatConfiguration> getConfigurationsByTrain(int trainId) {
        List<SeatConfiguration> configs = new ArrayList<>();
        String sql = "SELECT * FROM seat_configurations WHERE train_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, trainId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                SeatConfiguration config = new SeatConfiguration();
                config.setConfigId(rs.getInt("config_id"));
                config.setTrainId(rs.getInt("train_id"));
                config.setClassName(rs.getString("class_name"));
                config.setTotalSeats(rs.getInt("total_seats"));
                config.setPrice(rs.getDouble("price"));
                configs.add(config);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching seat configs: " + e.getMessage());
        }
        return configs;
    }
}

// File Path: /home/tony/intellih/myrailway/src/main/java/com/devtony/myrailway/controller/TrainController.java
package com.devtony.myrailway.controller;

import com.devtony.myrailway.model.DatabaseConnector;
import com.devtony.myrailway.model.Train;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TrainController {
    private Connection connection;

    public TrainController() {
        try {
            connection = DatabaseConnector.getConnection();
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
        }
    }

    // Get all trains from database
    public List<Train> getAllTrains() {
        List<Train> trains = new ArrayList<>();
        String query = "SELECT * FROM trains";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Train train = new Train();
                train.setTrainId(rs.getInt("train_id"));
                train.setTrainName(rs.getString("train_name"));
                train.setTrainNumber(rs.getString("train_number"));
                train.setTotalSeats(rs.getInt("total_seats"));
                trains.add(train);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching trains: " + e.getMessage());
        }
        return trains;
    }

    // Add new train to database
    public boolean addTrain(Train train) {
        String query = "INSERT INTO trains (train_name, train_number, total_seats) VALUES (?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, train.getTrainName());
            stmt.setString(2, train.getTrainNumber());
            stmt.setInt(3, train.getTotalSeats());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding train: " + e.getMessage());
            return false;
        }
    }

    // Update existing train
    public boolean updateTrain(Train train) {
        String query = "UPDATE trains SET train_name = ?, train_number = ?, total_seats = ? WHERE train_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, train.getTrainName());
            stmt.setString(2, train.getTrainNumber());
            stmt.setInt(3, train.getTotalSeats());
            stmt.setInt(4, train.getTrainId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating train: " + e.getMessage());
            return false;
        }
    }

    // Delete train by ID
    public boolean deleteTrain(int trainId) {
        String query = "DELETE FROM trains WHERE train_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, trainId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting train: " + e.getMessage());
            return false;
        }
    }

    // Close database connection
    public void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}

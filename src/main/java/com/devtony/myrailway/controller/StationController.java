// File Path: /home/tony/intellih/myrailway/src/main/java/com/devtony/myrailway/controller/StationController.java
package com.devtony.myrailway.controller;

import com.devtony.myrailway.model.DatabaseConnector;
import com.devtony.myrailway.model.Station;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StationController {
    private Connection connection;

    public StationController() {
        try {
            connection = DatabaseConnector.getConnection();
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
        }
    }

    public boolean addStation(Station station) {
        String sql = "INSERT INTO stations (station_name, station_code) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, station.getStationName());
            stmt.setString(2, station.getStationCode());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding station: " + e.getMessage());
            return false;
        }
    }
    public boolean updateStation(Station station) {
        String sql = "UPDATE stations SET station_name = ?, station_code = ? WHERE station_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, station.getStationName());
            stmt.setString(2, station.getStationCode());
            stmt.setInt(3, station.getStationId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating station: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteStation(int stationId) {
        String sql = "DELETE FROM stations WHERE station_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, stationId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting station: " + e.getMessage());
            return false;
        }
    }


    public List<Station> getAllStations() {
        List<Station> stations = new ArrayList<>();
        String sql = "SELECT * FROM stations";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Station station = new Station();
                station.setStationId(rs.getInt("station_id"));
                station.setStationName(rs.getString("station_name"));
                station.setStationCode(rs.getString("station_code"));
                stations.add(station);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching stations: " + e.getMessage());
        }
        return stations;
    }
}

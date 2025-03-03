package com.devtony.myrailway.controller;

import com.devtony.myrailway.model.DatabaseConnector;
import com.devtony.myrailway.model.Route;
import com.devtony.myrailway.model.Station;
import com.devtony.myrailway.model.Train;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RouteController {
    private Connection connection;

    public RouteController() {
        try {
            connection = DatabaseConnector.getConnection();
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
        }
    }

    public boolean addRoute(Route route) {
        String sql = "INSERT INTO routes (train_id, departure_time, arrival_time) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, route.getTrain().getTrainId());
            stmt.setTimestamp(2, Timestamp.valueOf(route.getDepartureTime()));
            stmt.setTimestamp(3, Timestamp.valueOf(route.getDepartureTime().plusHours(route.getDurationHours())));

            if (stmt.executeUpdate() > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int routeId = rs.getInt(1);
                    addRouteStops(routeId, route.getStations());
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error creating route: " + e.getMessage());
            return false;
        }
    }

    public List<Route> getAllRoutes() {
        List<Route> routes = new ArrayList<>();
        String sql = "SELECT r.route_id, r.departure_time, t.* FROM routes r "
                + "JOIN trains t ON r.train_id = t.train_id";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Train train = new Train();
                train.setTrainId(rs.getInt("train_id"));
                train.setTrainNumber(rs.getString("train_number"));
                train.setTrainName(rs.getString("train_name"));

                Route route = new Route();
                route.setRouteId(rs.getInt("route_id"));
                route.setTrain(train);
                route.setDepartureTime(rs.getTimestamp("departure_time").toLocalDateTime());
                route.setStations(getStationsForRoute(route.getRouteId())); // Populate stations

                routes.add(route);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching routes: " + e.getMessage());
        }
        return routes;
    }

    public List<Station> getStationsForRoute(int routeId) {
        List<Station> stations = new ArrayList<>();
        String sql = "SELECT s.* FROM route_stops rs "
                + "JOIN stations s ON rs.station_id = s.station_id "
                + "WHERE rs.route_id = ? ORDER BY station_order";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, routeId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Station station = new Station();
                station.setStationId(rs.getInt("station_id"));
                station.setStationName(rs.getString("station_name"));
                station.setStationCode(rs.getString("station_code"));
                stations.add(station); // Add the station to the list
            }
        } catch (SQLException e) {
            System.err.println("Error fetching route stations: " + e.getMessage());
        }
        return stations;
    }

    private void addRouteStops(int routeId, List<Station> stations) throws SQLException {
        String sql = "INSERT INTO route_stops (route_id, station_id, station_order) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < stations.size(); i++) {
                stmt.setInt(1, routeId);
                stmt.setInt(2, stations.get(i).getStationId());
                stmt.setInt(3, i + 1);
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

// Add to RouteController.java
public boolean updateRoute(Route route) {
    String updateRouteSQL = "UPDATE routes SET train_id = ?, departure_time = ?, arrival_time = ? WHERE route_id = ?";
    String deleteStopsSQL = "DELETE FROM route_stops WHERE route_id = ?";

    try {
        connection.setAutoCommit(false); // Start transaction

        // Update main route info
        try (PreparedStatement routeStmt = connection.prepareStatement(updateRouteSQL)) {
            routeStmt.setInt(1, route.getTrain().getTrainId());
            routeStmt.setTimestamp(2, Timestamp.valueOf(route.getDepartureTime()));
            routeStmt.setTimestamp(3, Timestamp.valueOf(route.getDepartureTime().plusHours(route.getDurationHours())));
            routeStmt.setInt(4, route.getRouteId());

            if (routeStmt.executeUpdate() == 0) {
                connection.rollback();
                return false;
            }
        }

        // Delete existing route stops
        try (PreparedStatement deleteStopsStmt = connection.prepareStatement(deleteStopsSQL)) {
            deleteStopsStmt.setInt(1, route.getRouteId());
            deleteStopsStmt.executeUpdate();
        }

        // Add new route stops
        addRouteStops(route.getRouteId(), route.getStations());

        connection.commit();
        return true;
    } catch (SQLException e) {
        try {
            connection.rollback();
        } catch (SQLException ex) {
            System.err.println("Rollback failed: " + ex.getMessage());
        }
        System.err.println("Error updating route: " + e.getMessage());
        return false;
    } finally {
        try {
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            System.err.println("Error resetting auto-commit: " + e.getMessage());
        }
    }
}

public boolean deleteRoute(int routeId) {
    String deleteRouteSQL = "DELETE FROM routes WHERE route_id = ?";
    String deleteStopsSQL = "DELETE FROM route_stops WHERE route_id = ?";

    try {
        connection.setAutoCommit(false);

        // Delete route stops first
        try (PreparedStatement deleteStopsStmt = connection.prepareStatement(deleteStopsSQL)) {
            deleteStopsStmt.setInt(1, routeId);
            deleteStopsStmt.executeUpdate();
        }

        // Delete the route
        try (PreparedStatement deleteRouteStmt = connection.prepareStatement(deleteRouteSQL)) {
            deleteRouteStmt.setInt(1, routeId);
            int affectedRows = deleteRouteStmt.executeUpdate();
            if (affectedRows == 0) {
                connection.rollback();
                return false;
            }
        }

        connection.commit();
        return true;
    } catch (SQLException e) {
        try {
            connection.rollback();
        } catch (SQLException ex) {
            System.err.println("Rollback failed: " + ex.getMessage());
        }
        System.err.println("Error deleting route: " + e.getMessage());
        return false;
    } finally {
        try {
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            System.err.println("Error resetting auto-commit: " + e.getMessage());
        }
    }
}}

// Fix getStationsForRoute method


// Update getAllRoutes to include stations


// Fix addRoute method's arrival_time calculation


// AuthController.java
package com.devtony.myrailway.controller;

import com.devtony.myrailway.model.DatabaseConnector;
import com.devtony.myrailway.model.User;
import com.devtony.myrailway.view.DashboardScreen;
import com.devtony.myrailway.view.LoginScreen;
import com.devtony.myrailway.view.RegisterScreen;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mindrot.jbcrypt.BCrypt;

public class AuthController {
    private Stage primaryStage;
    private String username;
    public AuthController(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void handleLogin(String name, String password) {
        try (Connection conn = DatabaseConnector.getConnection()) {
            // Query to fetch only the user with the given email
            String sql = "SELECT password_hash FROM users WHERE name = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name); // Set the email parameter
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // User found, verify password
                String storedHash = rs.getString("password_hash");
                if (BCrypt.checkpw(password, storedHash)) {
                    // Login successful
                    System.out.println("Login successful!");
                    new DashboardScreen(primaryStage, this, name);
                    username=name;
                    // Load profile screen or dashboard
                } else {
                    // Invalid password
                    System.out.println("Invalid password!");
                    showAlert("Error", "Invalid password!");
                }
            } else {
                // User not found
                System.out.println("User not found!");
                showAlert("Error", "User not found. Please register.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Database error: " + e.getMessage());
        }
    }
    private void showNotRegisteredAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Not Registered");
        alert.setHeaderText(null);
        alert.setContentText("You are not registered. Please register to continue.");

        // Add an OK button to the alert
        alert.getButtonTypes().setAll(ButtonType.OK);

        // Show the alert and wait for user response
        alert.showAndWait();
    }

    public User getUser(String username) {
        String sql = "SELECT user_id, name, email, phone, password_hash FROM users WHERE name = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setPasswordHash(rs.getString("password_hash"));
                return user;
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user: " + e.getMessage());
        }
        return null;
    }

    public void handleRegistration(String name, String email, String phone, String password, Stage stage) {
        // Hash password with BCrypt
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        // Create User object
        User newUser = new User(name, email, phone, hashedPassword);

        // Save to database
        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = "INSERT INTO users (name, email, phone, password_hash) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, newUser.getName());
            stmt.setString(2, newUser.getEmail());
            stmt.setString(3, newUser.getPhone());
            stmt.setString(4, newUser.getPasswordHash());
            stmt.executeUpdate();

            // Show success and switch to login
            showAlert("Success", "Registration successful! Please login.");
            loadLoginScreen(stage);
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // Duplicate entry (MySQL error code)
                showAlert("Error", "Email or phone already exists!");
            } else {
                showAlert("Error", "Database error: " + e.getMessage());
            }
        }
    }

    public void loadLoginScreen(Stage stage) {
        new LoginScreen(stage, this);
    }

    public void loadRegisterScreen(Stage stage) {
        new RegisterScreen(stage, this);
    }

    private void showAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public User getCurrentUser() {
        return getUser(username);
    }
    // Add to AuthController.java

}
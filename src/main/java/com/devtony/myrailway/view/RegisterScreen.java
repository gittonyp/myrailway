package com.devtony.myrailway.view;

import com.devtony.myrailway.controller.AuthController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RegisterScreen extends VBox {
    private TextField nameField = new TextField();
    private TextField emailField = new TextField();
    private TextField phoneField = new TextField();
    private PasswordField passwordField = new PasswordField();
    private Button registerButton = new Button("Register");
    private Button backButton = new Button("Back to Login");
    private AuthController authController;

    public RegisterScreen(Stage stage, AuthController authController) {
        this.authController = authController;
        setupUI(stage);
    }

    private void setupUI(Stage stage) {
        setAlignment(Pos.CENTER);
        setSpacing(10);
        setPadding(new Insets(20));

        // Title
        Label titleLabel = new Label("Register New User");
        titleLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");

        // Input Fields
        nameField.setPromptText("Name");
        nameField.setMaxWidth(250);
        emailField.setPromptText("Email");
        emailField.setMaxWidth(250);
        phoneField.setPromptText("Phone");
        phoneField.setMaxWidth(250);
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(250);

        // Buttons
        registerButton.setOnAction(e -> handleRegistration(stage));
        backButton.setOnAction(e -> authController.loadLoginScreen(stage));

        // Add components to layout
        getChildren().addAll(
                titleLabel,
                nameField,
                emailField,
                phoneField,
                passwordField,
                registerButton,
                backButton
        );

        // Create scene and update stage
        Scene scene = new Scene(this, 400, 450);
        stage.setScene(scene);
        stage.setTitle("Registration");
        stage.show();
    }

    private void handleRegistration(Stage stage) {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String password = passwordField.getText().trim();

        // Basic input validation
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Name, email, and password are required!");
            return;
        }

        // Validate email format (simple check)
        if (!email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid email format!");
            return;
        }

        // Register the user
        authController.handleRegistration(name, email, phone, password, stage);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
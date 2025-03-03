// LoginScreen.java (UI built in Java)
package com.devtony.myrailway.view;

import com.devtony.myrailway.controller.AuthController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginScreen extends VBox {
    private TextField name = new TextField();
    private PasswordField passwordField = new PasswordField();
    private Button loginButton = new Button("Login");
    private Button registerButton = new Button("Register");
    private AuthController authController;

    public LoginScreen(Stage stage, AuthController authController) {
        this.authController = authController;
        setupUI(stage);
    }

    private void setupUI(Stage stage) {
        // Layout and Styling
        setAlignment(Pos.CENTER);
        setSpacing(10);
        setPadding(new Insets(20));

        // Title
        Label titleLabel = new Label("Railway Reservation System");
        titleLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");

        // Email Field
        name.setPromptText("Email");
        name.setMaxWidth(200);

        // Password Field
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(200);

        // Buttons
        loginButton.setOnAction(e -> authController.handleLogin(name.getText(), passwordField.getText()));
        registerButton.setOnAction(e -> authController.loadRegisterScreen(stage));

        // Add components to layout
        getChildren().addAll(
                titleLabel,
                name,
                passwordField,
                loginButton,
                registerButton
        );

        // Create scene and set stage
        Scene scene = new Scene(this, 400, 300);
        stage.setScene(scene);
        stage.setTitle("Login");
        stage.show();
    }
}
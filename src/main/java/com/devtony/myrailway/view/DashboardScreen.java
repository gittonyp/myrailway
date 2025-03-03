// File Path: /home/tony/intellih/myrailway/src/main/java/com/devtony/myrailway/view/DashboardScreen.java
package com.devtony.myrailway.view;

import com.devtony.myrailway.controller.AuthController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DashboardScreen extends VBox {
    private AuthController authController;
    
    public DashboardScreen(Stage stage, AuthController authController, String userName) {
        this.authController = authController;
        setupUI(stage, userName);
    }

    private void setupUI(Stage stage, String userName) {
        setAlignment(Pos.CENTER);
        setSpacing(15);
        setPadding(new Insets(20));

        // Header
        Label header = new Label("Railway Management Dashboard");
        header.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");
        
        Label welcomeLabel = new Label("Welcome, " + userName + "!");
        welcomeLabel.setStyle("-fx-font-size: 16;");

        // Navigation Buttons
        Button manageTrainsBtn = new Button("Manage Trains");
        manageTrainsBtn.setPrefWidth(200);
        manageTrainsBtn.setOnAction(e -> showManageTrainsScreen(stage));

        Button bookTicketBtn = new Button("Book Ticket");
        bookTicketBtn.setPrefWidth(200);
        
        Button viewBookingsBtn = new Button("View Bookings");
        viewBookingsBtn.setPrefWidth(200);

        // In DashboardScreen.java add these buttons:
        Button manageStationsBtn = new Button("Manage Stations");
        manageStationsBtn.setPrefWidth(200);
        manageStationsBtn.setOnAction(e -> new ManageStationsScreen(stage,authController));

        Button planRouteBtn = new Button("Plan Route");
        planRouteBtn.setPrefWidth(200);

        Button seatConfigBtn = new Button("Seat Configuration");
        seatConfigBtn.setPrefWidth(200);

        // In DashboardScreen's setupUI method:
        planRouteBtn.setOnAction(e -> new PlanRouteScreen(stage,authController));
        seatConfigBtn.setOnAction(e -> new ManageSeatConfigScreen(stage,authController));


        Button logoutBtn = new Button("Logout");
        logoutBtn.setPrefWidth(200);
        logoutBtn.setOnAction(e -> authController.loadLoginScreen(stage));

        getChildren().addAll(
            header,
            welcomeLabel,
            manageTrainsBtn,
            bookTicketBtn,
            viewBookingsBtn,
                manageStationsBtn,
                planRouteBtn,
                seatConfigBtn,
            logoutBtn
        );

        stage.setScene(new Scene(this, 600, 400));
        stage.setTitle("Dashboard");
    }

    private void showManageTrainsScreen(Stage stage) {
        // Implementation for train management screen
        new ManageTrainsScreen(stage, authController);
    }

}

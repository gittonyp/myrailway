// File Path: /home/tony/intellih/myrailway/src/main/java/com/devtony/myrailway/view/ManageTrainsScreen.java
package com.devtony.myrailway.view;

import com.devtony.myrailway.controller.AuthController;
import com.devtony.myrailway.controller.TrainController;
import com.devtony.myrailway.model.DatabaseConnector;
import com.devtony.myrailway.model.Train;
import com.devtony.myrailway.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ManageTrainsScreen extends VBox {
    private TableView<Train> trainTable = new TableView<>();
    private TextField nameField = new TextField();
    private TextField numberField = new TextField();
    private TextField seatsField = new TextField();
    private TrainController trainController = new TrainController();

    public ManageTrainsScreen(Stage stage, AuthController authController) {
        setupUI(stage, authController);
    }

    private void setupUI(Stage stage, AuthController authController) {
        setSpacing(15);
        setPadding(new Insets(20));
        
        // Table setup
        TableColumn<Train, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("trainId"));
        
        TableColumn<Train, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("trainName"));
        
        TableColumn<Train, String> numberColumn = new TableColumn<>("Number");
        numberColumn.setCellValueFactory(new PropertyValueFactory<>("trainNumber"));
        
        TableColumn<Train, Integer> seatsColumn = new TableColumn<>("Seats");
        seatsColumn.setCellValueFactory(new PropertyValueFactory<>("totalSeats"));
        
        trainTable.getColumns().addAll(idColumn, nameColumn, numberColumn, seatsColumn);
        trainTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        // Form fields
        nameField.setPromptText("Train Name");
        numberField.setPromptText("Train Number");
        seatsField.setPromptText("Total Seats");
        
        // Buttons
        Button addButton = new Button("Add Train");
        addButton.setOnAction(e -> handleAddTrain());
        
        Button updateButton = new Button("Update Train");
        updateButton.setOnAction(e -> handleUpdateTrain());
        
        Button deleteButton = new Button("Delete Train");
        deleteButton.setOnAction(e -> handleDeleteTrain());
        
        Button backButton = new Button("Back to Dashboard");
        backButton.setOnAction(e -> new DashboardScreen(stage, authController, authController.getCurrentUser().getName()));
        
        HBox buttonBox = new HBox(10, addButton, updateButton, deleteButton, backButton);
        buttonBox.setAlignment(Pos.CENTER);
//        trainTable.setPrefHeight(400);


        getChildren().addAll(trainTable, nameField, numberField, seatsField, buttonBox);
        
        // Load initial data
        refreshTable();
        
        stage.setScene(new Scene(this, 800, 600));
        stage.setTitle("Manage Trains");
    }
    // Add to AuthController.java



    private void refreshTable() {
        ObservableList<Train> trains = FXCollections.observableArrayList(trainController.getAllTrains());
        trainTable.setItems(trains);
        System.out.println(trains.stream().sorted());
    }

    private void handleAddTrain() {
        if (validateInput()) {
            Train newTrain = new Train();
            newTrain.setTrainName(nameField.getText());
            newTrain.setTrainNumber(numberField.getText());
            newTrain.setTotalSeats(Integer.parseInt(seatsField.getText()));
            
            if (trainController.addTrain(newTrain)) {
                refreshTable();
                clearFields();
            }
        }
    }

    private void handleUpdateTrain() {
        Train selected = trainTable.getSelectionModel().getSelectedItem();
        if (selected != null && validateInput()) {
            selected.setTrainName(nameField.getText());
            selected.setTrainNumber(numberField.getText());
            selected.setTotalSeats(Integer.parseInt(seatsField.getText()));
            
            if (trainController.updateTrain(selected)) {
                refreshTable();
                clearFields();
            }
        }
    }

    private void handleDeleteTrain() {
        Train selected = trainTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, 
                "Delete train " + selected.getTrainName() + "?", ButtonType.YES, ButtonType.NO);
            if (alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
                trainController.deleteTrain(selected.getTrainId());
                refreshTable();
            }
        }
    }

    private boolean validateInput() {
        if (nameField.getText().isEmpty() || numberField.getText().isEmpty() || seatsField.getText().isEmpty()) {
            showAlert("Error", "All fields are required!");
            return false;
        }
        
        try {
            Integer.parseInt(seatsField.getText());
        } catch (NumberFormatException e) {
            showAlert("Error", "Seats must be a number!");
            return false;
        }
        
        return true;
    }

    private void clearFields() {
        nameField.clear();
        numberField.clear();
        seatsField.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

// File Path: /home/tony/intellih/myrailway/src/main/java/com/devtony/myrailway/view/ManageStationsScreen.java
package com.devtony.myrailway.view;

import com.devtony.myrailway.controller.AuthController;
import com.devtony.myrailway.controller.StationController;
import com.devtony.myrailway.model.Station;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ManageStationsScreen extends VBox {
    private TableView<Station> stationTable = new TableView<>();
    private TextField nameField = new TextField();
    private TextField codeField = new TextField();
    private StationController stationController = new StationController();

    public ManageStationsScreen(Stage stage, AuthController authController) {
        setupUI(stage,authController);
    }

    private void setupUI(Stage stage,AuthController authController) {
        setSpacing(10);
        setPadding(new Insets(20));

        // Table setup
        TableColumn<Station, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("stationId"));
        
        TableColumn<Station, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("stationName"));
        
        TableColumn<Station, String> codeCol = new TableColumn<>("Code");
        codeCol.setCellValueFactory(new PropertyValueFactory<>("stationCode"));

        stationTable.getColumns().addAll(idCol, nameCol, codeCol);
        
        // Form fields
        nameField.setPromptText("Station Name");
        codeField.setPromptText("Station Code");
        
        Button addButton = new Button("Add Station");
        addButton.setOnAction(e -> handleAddStation());

        Button updateButton = new Button("Update Station");
        updateButton.setOnAction(e -> handleUpdateStation());

        Button deleteButton = new Button("Delete Station");
        deleteButton.setOnAction(e -> handleDeleteStation());

        Button backButton = new Button("Back to Dashboard");
        backButton.setOnAction(e -> new DashboardScreen(stage, authController, authController.getCurrentUser().getName()));


        getChildren().addAll(stationTable, nameField, codeField, addButton,updateButton,deleteButton,backButton);
        refreshTable();
        
        stage.setScene(new Scene(this, 600, 400));
        stage.setTitle("Manage Stations");
    }


    private void refreshTable() {
        stationTable.setItems(FXCollections.observableArrayList(stationController.getAllStations()));
    }

    private void handleAddStation() {
        if (!nameField.getText().isEmpty() && !codeField.getText().isEmpty()) {
            Station station = new Station();
            station.setStationName(nameField.getText());
            station.setStationCode(codeField.getText());
            if (stationController.addStation(station)) {
                refreshTable();
                nameField.clear();
                codeField.clear();
            }
        }
    }
    private void handleUpdateStation() {
        Station selected = stationTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (!nameField.getText().isEmpty() && !codeField.getText().isEmpty()) {
                selected.setStationName(nameField.getText());
                selected.setStationCode(codeField.getText());

                if (stationController.updateStation(selected)) {
                    refreshTable();
                    clearFields();
                }
            } else {
                showAlert("Error", "Name and code fields are required!");
            }
        } else {
            showAlert("Error", "No station selected!");
        }
    }

    private void handleDeleteStation() {
        Station selected = stationTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Delete station " + selected.getStationName() + "?", ButtonType.YES, ButtonType.NO);
            if (alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
                if (stationController.deleteStation(selected.getStationId())) {
                    refreshTable();
                    clearFields();
                }
            }
        }
    }

    // Add these helper methods
    private void clearFields() {
        nameField.clear();
        codeField.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}

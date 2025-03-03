// File Path: /home/tony/intellih/myrailway/src/main/java/com/devtony/myrailway/view/ManageSeatConfigScreen.java
package com.devtony.myrailway.view;

import com.devtony.myrailway.controller.AuthController;
import com.devtony.myrailway.controller.SeatConfigController;
import com.devtony.myrailway.controller.TrainController;
import com.devtony.myrailway.model.SeatConfiguration;
import com.devtony.myrailway.model.Train;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ManageSeatConfigScreen extends VBox {
    private TableView<SeatConfiguration> configTable = new TableView<>();
    private ComboBox<Train> trainSelector = new ComboBox<>();
    private TextField classNameField = new TextField();
    private TextField seatsField = new TextField();
    private TextField priceField = new TextField();
    private SeatConfigController configController = new SeatConfigController();
    private TrainController trainController = new TrainController();

    public ManageSeatConfigScreen(Stage stage, AuthController authController) {
        setupUI(stage);
    }

    private void setupUI(Stage stage) {
        setSpacing(10);
        setPadding(new Insets(20));

        // Train selection
        trainSelector.setItems(FXCollections.observableArrayList(trainController.getAllTrains()));
        trainSelector.setPromptText("Select Train");

        // Table setup
        TableColumn<SeatConfiguration, String> classCol = new TableColumn<>("Class");
        classCol.setCellValueFactory(new PropertyValueFactory<>("className"));
        
        TableColumn<SeatConfiguration, Integer> seatsCol = new TableColumn<>("Seats");
        seatsCol.setCellValueFactory(new PropertyValueFactory<>("totalSeats"));
        
        TableColumn<SeatConfiguration, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        configTable.getColumns().addAll(classCol, seatsCol, priceCol);

        // Form fields
        classNameField.setPromptText("Class Name");
        seatsField.setPromptText("Total Seats");
        priceField.setPromptText("Price per Seat");

        Button addButton = new Button("Add Configuration");
        addButton.setOnAction(e -> handleAddConfig());

        getChildren().addAll(
            new Label("Select Train:"),
            trainSelector,
            configTable,
            classNameField,
            seatsField,
            priceField,
            addButton
        );

        stage.setScene(new Scene(this, 600, 400));
        stage.setTitle("Seat Configuration");
    }

    private void handleAddConfig() {
        if (validateInput()) {
            SeatConfiguration config = new SeatConfiguration();
            config.setTrainId(trainSelector.getValue().getTrainId());
            config.setClassName(classNameField.getText());
            config.setTotalSeats(Integer.parseInt(seatsField.getText()));
            config.setPrice(Double.parseDouble(priceField.getText()));
            
            if (configController.addSeatConfig(config)) {
                refreshTable();
                clearFields();
            }
        }
    }

    private boolean validateInput() {
        // Validation logic similar to other screens
        return true;
    }

    private void refreshTable() {
        if (trainSelector.getValue() != null) {
            configTable.setItems(FXCollections.observableArrayList(
                configController.getConfigurationsByTrain(trainSelector.getValue().getTrainId())
            ));
        }
    }

    private void clearFields() {
        classNameField.clear();
        seatsField.clear();
        priceField.clear();
    }
}

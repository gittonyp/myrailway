package com.devtony.myrailway.view;

import com.devtony.myrailway.controller.AuthController;
import com.devtony.myrailway.controller.RouteController;
import com.devtony.myrailway.controller.StationController;
import com.devtony.myrailway.controller.TrainController;
import com.devtony.myrailway.model.Route;
import com.devtony.myrailway.model.Station;
import com.devtony.myrailway.model.Train;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

public class PlanRouteScreen extends VBox {
    private ComboBox<Train> trainSelector = new ComboBox<>();
    private ListView<Station> stationList = new ListView<>();
    private ListView<Station> selectedStations = new ListView<>();
    private DatePicker departureDate = new DatePicker();
    private TextField departureTime = new TextField();
    private TextField durationHours = new TextField();
    private RouteController routeController = new RouteController();
    private StationController stationController = new StationController();
    private TableView<Route> routeTable = new TableView<>();
    private TableView<Station> routeStationsTable = new TableView<>();
    private Button createRouteBtn = new Button("Create Route");
    private Button updateRouteBtn = new Button("Update Route");
    private Button deleteRouteBtn = new Button("Delete Route");
    private Button moveUpBtn = new Button("↑");
    private Button moveDownBtn = new Button("↓");
    private Button backButton = new Button("Back to Dashboard");
    private Route selectedRoute;

    public PlanRouteScreen(Stage stage, AuthController authController) {
        setupUI(stage, authController);
    }

    private void setupUI(Stage stage, AuthController authController) {
        setSpacing(10);
        setPadding(new Insets(20));

        // Train selector setup
        trainSelector.setItems(FXCollections.observableArrayList(new TrainController().getAllTrains()));
        trainSelector.setConverter(new StringConverter<Train>() {
            @Override
            public String toString(Train train) {
                return train == null ? "" : train.getTrainName() + " (" + train.getTrainNumber() + ")";
            }
            @Override
            public Train fromString(String string) {
                return null;
            }
        });

        // Station lists setup
        stationList.setItems(FXCollections.observableArrayList(stationController.getAllStations()));
        stationList.setCellFactory(lv -> new ListCell<Station>() {
            @Override
            protected void updateItem(Station station, boolean empty) {
                super.updateItem(station, empty);
                setText(empty || station == null ? "" : station.getStationName());
            }
        });
        stationList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        Button addStationBtn = new Button("Add Selected Stations");
        addStationBtn.setOnAction(e -> addSelectedStations());

        // Form fields setup
        departureDate.setPromptText("Departure Date");
        departureTime.setPromptText("Time (HH:mm)");
        arrival.setPromptText("arrival in time");

        // Button actions
        createRouteBtn.setOnAction(e -> handleCreateRoute());
        updateRouteBtn.setOnAction(e -> handleUpdateRoute());
        deleteRouteBtn.setOnAction(e -> handleDeleteRoute());
        moveUpBtn.setOnAction(e -> moveStation(-1));
        moveDownBtn.setOnAction(e -> moveStation(1));
        backButton.setOnAction(e -> {
            new DashboardScreen(stage, authController,authController.getCurrentUser().getName());
        });

        // Route table setup
        TableColumn<Route, Integer> routeIdCol = new TableColumn<>("Route ID");
        routeIdCol.setCellValueFactory(new PropertyValueFactory<>("routeId"));
        TableColumn<Route, String> trainCol = new TableColumn<>("Train");
        trainCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTrain().getTrainName()));
        TableColumn<Route, LocalDateTime> departureCol = new TableColumn<>("Departure");
        departureCol.setCellValueFactory(new PropertyValueFactory<>("departureTime"));
        TableColumn<Route, Integer> durationCol = new TableColumn<>("Duration (hrs)");
        durationCol.setCellValueFactory(new PropertyValueFactory<>("durationHours"));
        routeTable.getColumns().addAll(routeIdCol, trainCol, departureCol, durationCol);
        routeTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedRoute = newVal;
            if (newVal != null) {
                trainSelector.getSelectionModel().select(newVal.getTrain());
                departureDate.setValue(newVal.getDepartureTime().toLocalDate());
                departureTime.setText(newVal.getDepartureTime().toLocalTime().toString());
//                durationHours.setText(String.valueOf(newVal.getDurationHours()));
                selectedStations.setItems(FXCollections.observableArrayList(newVal.getStations()));
            }
        });

        // Route stations table setup
        TableColumn<Station, String> stationNameCol = new TableColumn<>("Station");
        stationNameCol.setCellValueFactory(new PropertyValueFactory<>("stationName"));
        TableColumn<Station, String> stationCodeCol = new TableColumn<>("Code");
        stationCodeCol.setCellValueFactory(new PropertyValueFactory<>("stationCode"));
        routeStationsTable.getColumns().addAll(stationNameCol, stationCodeCol);

        // Layout organization
        VBox planningSection = new VBox(10,
                new Label("Select Train:"), trainSelector,
                new Label("Select Stations:"), stationList, addStationBtn,
                new Label("Selected Stations Order:"), selectedStations,
                new HBox(5, moveUpBtn, moveDownBtn),
                new Label("Departure:"), new HBox(10, departureDate, departureTime),
                new Label("Duration:"), durationHours
        );

        VBox existingRoutesSection = new VBox(10,
                new Label("Existing Routes:"), routeTable,
                new Label("Route Stations:"), routeStationsTable
        );

        HBox mainLayout = new HBox(planningSection, existingRoutesSection);
        planningSection.prefWidthProperty().bind(mainLayout.widthProperty().multiply(0.4));
        existingRoutesSection.prefWidthProperty().bind(mainLayout.widthProperty().multiply(0.6));

        HBox buttonBox = new HBox(10, createRouteBtn, updateRouteBtn, deleteRouteBtn, backButton);
        buttonBox.setAlignment(Pos.CENTER);

        getChildren().addAll(mainLayout, buttonBox);
        refreshRoutesTable();

        stage.setScene(new Scene(this, 1000, 700));
        stage.setTitle("Plan Route");
    }

    private void addSelectedStations() {
        selectedStations.getItems().addAll(stationList.getSelectionModel().getSelectedItems());
    }

    private void handleCreateRoute() {
        try {
            if (trainSelector.getValue() == null) {
                showAlert("Error", "Please select a train");
                return;
            }
            if (selectedStations.getItems().size() < 2) {
                showAlert("Error", "At least 2 stations required");
                return;
            }

            LocalDateTime departure = LocalDateTime.of(
                    departureDate.getValue(),
                    LocalTime.parse(departureTime.getText())
            );

            Route newRoute = new Route();
            newRoute.setTrain(trainSelector.getValue());
            newRoute.setDepartureTime(departure);
            newRoute.setDurationHours(Integer.parseInt(durationHours.getText()));
            newRoute.setStations(new ArrayList<>(selectedStations.getItems()));

            if (routeController.addRoute(newRoute)) {
                refreshRoutesTable();
                clearForm();
            }
        } catch (Exception e) {
            showAlert("Input Error", "Check date/time format and numerical values");
        }
    }

    private void handleUpdateRoute() {
        if (selectedRoute == null) {
            showAlert("Error", "Select a route to update");
            return;
        }

        try {
            selectedRoute.setTrain(trainSelector.getValue());
            selectedRoute.setDepartureTime(LocalDateTime.of(
                    departureDate.getValue(),
                    LocalTime.parse(departureTime.getText())
            ));
            selectedRoute.setDurationHours(Integer.parseInt(durationHours.getText()));
            selectedRoute.setStations(new ArrayList<>(selectedStations.getItems()));

            if (routeController.updateRoute(selectedRoute)) {
                refreshRoutesTable();
                clearForm();
            }
        } catch (Exception e) {
            showAlert("Update Error", e.getMessage());
        }
    }

    private void handleDeleteRoute() {
        if (selectedRoute != null && routeController.deleteRoute(selectedRoute.getRouteId())) {
            refreshRoutesTable();
            clearForm();
        }
    }

    private void moveStation(int direction) {
        int selectedIndex = selectedStations.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) return;

        int newIndex = selectedIndex + direction;
        if (newIndex < 0 || newIndex >= selectedStations.getItems().size()) return;

        Station station = selectedStations.getItems().remove(selectedIndex);
        selectedStations.getItems().add(newIndex, station);
        selectedStations.getSelectionModel().select(newIndex);
    }

    private void clearForm() {
        trainSelector.getSelectionModel().clearSelection();
        selectedStations.getItems().clear();
        departureDate.setValue(null);
        departureTime.clear();
        durationHours.clear();
        selectedRoute = null;
    }

    private void refreshRoutesTable() {
        routeTable.setItems(FXCollections.observableArrayList(routeController.getAllRoutes()));
    }

    private void showAlert(String title, String message) {
        new Alert(Alert.AlertType.ERROR, message, ButtonType.OK).showAndWait();
    }
}

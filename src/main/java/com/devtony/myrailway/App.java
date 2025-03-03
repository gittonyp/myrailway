// App.java
package com.devtony.myrailway;

import com.devtony.myrailway.controller.AuthController;
import com.devtony.myrailway.view.LoginScreen;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) {
        AuthController authController = new AuthController(primaryStage);
        new LoginScreen(primaryStage, authController); // Builds UI programmatically
    }

    public static void main(String[] args) {
        launch(args);
    }
}
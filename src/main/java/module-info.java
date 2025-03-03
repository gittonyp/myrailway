module com.devtony.com.myrailway {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jbcrypt;


    opens com.devtony.myrailway to javafx.fxml;
    exports com.devtony.myrailway;
    opens com.devtony.myrailway.model to javafx.base;
}
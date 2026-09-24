module com.example.mysticmaze {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;
    requires java.desktop;
    requires com.google.gson;

    opens com.example.mysticmaze.controllers to javafx.fxml;
    opens com.example.mysticmaze.models to com.google.gson;
    exports com.example.mysticmaze;
}

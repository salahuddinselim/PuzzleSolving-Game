module com.example.mysticmaze {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;
    requires java.desktop;

    opens com.example.mysticmaze.controllers to javafx.fxml;
    exports com.example.mysticmaze;
}

package com.example.mysticmaze.controllers;

import com.example.mysticmaze.utils.DBUtil;
import com.example.mysticmaze.utils.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Parent;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginPageController {

    @FXML public TextField usernameField;
    @FXML public PasswordField passwordField;
    int user_id;
    String name;
    String email;
    // Custom navigation variables
    private String previousPageFXML;
    private String targetPageFXML;

    // Called from other controllers before loading LoginPage
    public void setNavigationContext(String previousPageFXML, String targetPageFXML) {
        this.previousPageFXML = previousPageFXML;
        this.targetPageFXML = targetPageFXML;
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Username and password cannot be empty.");
            return;
        }

        if (DBUtil.validateUser(username, password)) {
            try {
                String query = "SELECT user_id, email, username FROM users WHERE username = ?";

                try (Connection conn = DBUtil.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(query)) {

                    stmt.setString(1, username);
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        user_id = rs.getInt("user_id");
                        name = rs.getString("username");
                        email = rs.getString("email");
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }
                //String query = "SELECT user_id FROM users WHERE username = ? AND password_hash = ?";
                Session.setUserId(user_id); // store into user logged
                Session.setEmail(email);
                Session.setUserName(name);
                // Determine next page
                String nextPage = (targetPageFXML != null) ? targetPageFXML : "/com/example/mysticmaze/fxmls/DashboardPage.fxml";

                FXMLLoader loader = new FXMLLoader(getClass().getResource(nextPage));
                Parent nextRoot = loader.load();

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(nextRoot));
                stage.setTitle("Mystic Maze");
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load next page.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password.");
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            String backPage = (previousPageFXML != null) ? previousPageFXML : "/com/example/mysticmaze/fxmls/HomePage.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(backPage));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not go back.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void register(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/mysticmaze/fxmls/registerPage.fxml"));

        System.out.println(fxmlLoader);
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setResizable(false);
        stage.setTitle("Puzzle solver");
        stage.setScene(scene);
        stage.show();
    }
}

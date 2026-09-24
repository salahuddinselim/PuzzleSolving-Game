package com.example.mysticmaze.controllers;

import com.example.mysticmaze.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class HomePageController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    private void CreateTeam(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/mysticmaze/fxmls/LoginPage.fxml"));
            Parent loginRoot = loader.load();

            // Pass navigation info
            LoginPageController controller = loader.getController();
            controller.setNavigationContext(
                    "/com/example/mysticmaze/fxmls/HomePage.fxml",              // previous page
                    "/com/example/mysticmaze/fxmls/CreateRoom.fxml"             // target after login
            );

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loginRoot));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void JoinTeam(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/mysticmaze/fxmls/LoginPage.fxml"));
            Parent loginRoot = loader.load();

            LoginPageController controller = loader.getController();
            controller.setNavigationContext(
                    "/com/example/mysticmaze/fxmls/DashboardPage.fxml",
                    "/com/example/mysticmaze/fxmls/JoinTeam.fxml"
            );

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loginRoot));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void LoginPage(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/mysticmaze/fxmls/loginPage.fxml"));

        System.out.println(fxmlLoader);
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setResizable(false);
        stage.setTitle("Puzzle solver");
        stage.setScene(scene);
        stage.show();
    }

    public void RegisterPage(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/mysticmaze/fxmls/registerPage.fxml"));

        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setResizable(false);
        stage.setTitle("Registration");
        stage.setScene(scene);
        stage.show();
    }

    public void AboutUs(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/mysticmaze/fxmls/aboutUsPage.fxml"));

        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setResizable(false);
        stage.setTitle("About Us ");
        stage.setScene(scene);
        stage.show();
    }

    public void LeaderBoard(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/mysticmaze/fxmls/LeaderBoardPage.fxml"));

        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setResizable(false);
        stage.setTitle("Leader Board ");
        stage.setScene(scene);
        stage.show();
    }

    public void Help(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/mysticmaze/fxmls/HelpPage.fxml"));

        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setResizable(false);
        stage.setTitle("Help");
        stage.setScene(scene);
        stage.show();
    }

    public void Player(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/mysticmaze/fxmls/PlayerPage.fxml"));

        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setResizable(false);
        stage.setTitle("Player");
        stage.setScene(scene);
        stage.show();
    }
    public void GiftBox(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/mysticmaze/fxmls/GiftBoxPage.fxml"));

        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setResizable(false);
        stage.setTitle("Player");
        stage.setScene(scene);
        stage.show();
    }

    public void Level(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/mysticmaze/fxmls/LevelPage.fxml"));

        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setResizable(false);
        stage.setTitle("Player");
        stage.setScene(scene);
        stage.show();
    }


}
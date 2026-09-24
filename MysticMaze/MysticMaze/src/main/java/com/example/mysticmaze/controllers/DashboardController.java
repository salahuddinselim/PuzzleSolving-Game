package com.example.mysticmaze.controllers;

import com.example.mysticmaze.network.GameClient;
import com.example.mysticmaze.utils.DBUtil;
import com.example.mysticmaze.utils.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.example.mysticmaze.utils.DBUtil.getCurrentUserRoom;

public class DashboardController {

    @FXML
    private Button welcomeLabel;
    @FXML
    public void initialize() {
        String username = Session.getUserName();
        if (username != null) {
            welcomeLabel.setText("Welcome, " + username);
        } else {
            welcomeLabel.setText("Welcome!");
        }
    }
    @FXML
    private void handleLogout(ActionEvent event) throws IOException {
        Session.clear(); // clears session data

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/mysticmaze/fxmls/loginPage.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Login - Mystic Maze");
    }

    public void CreateTeam(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/mysticmaze/fxmls/createRoom.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setResizable(false);
        stage.setTitle("Create a Team");
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void JoinTeam(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/mysticmaze/fxmls/joinTeam.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setResizable(false);
        stage.setTitle("Join A Team");
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void AboutUs(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/mysticmaze/fxmls/aboutUsPage.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setResizable(false);
        stage.setTitle("About Us");
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void LeaderBoard(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/mysticmaze/fxmls/LeaderBoardPage.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setResizable(false);
        stage.setTitle("Leader Board");
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void Help(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/mysticmaze/fxmls/HelpPage.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setResizable(false);
        stage.setTitle("Help");
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void ProfilePage(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/mysticmaze/fxmls/ProfilePage.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setResizable(false);
        stage.setTitle("Profile");
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void Player(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/mysticmaze/fxmls/PlayerPage.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setResizable(false);
        stage.setTitle("Player");
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void GiftBox(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/mysticmaze/fxmls/GiftBoxPage.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setResizable(false);
        stage.setTitle("Gift Box");
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void Level(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/mysticmaze/fxmls/LevelPage.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setResizable(false);
        stage.setTitle("Level");
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    public  void  GotoTeam(ActionEvent e){
        try {
            boolean flag = DBUtil.getCurrentUserRoom(Session.getUserId());
            if(flag){

                try {
                    GameClient client = new GameClient(message -> {
                        System.out.println("Server: " + message);
                    });
                    client.connect("localhost");
                    //client.send(gson.toJson(new Message("join", "client_user", roomName, "")));
                } catch (IOException ex) {
                   // statusLabel.setText("Could not connect to server.");
                    ex.printStackTrace();
                    return;
                }
                Parent root = FXMLLoader.load(getClass().getResource("/com/example/mysticmaze/fxmls/joinDashboard.fxml"));
                Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                stage.setResizable(false);
                stage.setTitle("Profile");
                stage.setScene(new Scene(root));
                stage.show();
            }
            else{
                // pop up create or join a room first
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }
}

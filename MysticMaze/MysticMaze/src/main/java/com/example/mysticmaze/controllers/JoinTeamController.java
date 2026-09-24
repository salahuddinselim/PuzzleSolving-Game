package com.example.mysticmaze.controllers;

import com.example.mysticmaze.models.Message;
import com.example.mysticmaze.network.GameClient;
import com.example.mysticmaze.utils.DBUtil;
import com.example.mysticmaze.utils.Session;
import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JoinTeamController {

    @FXML
    private TextField teammateInput;

    @FXML
    private Label statusLabel;

    @FXML
    private Button requestButton;
    private final Gson gson = new Gson();

    @FXML
    private void Join(ActionEvent event) throws IOException, SQLException {
        String inputCode = teammateInput.getText().trim();

        if (inputCode.isEmpty()) {
            statusLabel.setText("Please enter Team Code");
            return;
        }

        int user_id = Session.getUserId();
        // check the user already have a room
        if(DBUtil.isUserInActiveRoom(user_id)){
            statusLabel.setText("You already have a room, Please leave the room first");
            return;
        }

        try (Connection conn = DBUtil.getConnection()) {
            // 1. Check if room exists
            String roomQuery = "SELECT room_name,room_id FROM rooms WHERE room_code = ?";
            PreparedStatement roomStmt = conn.prepareStatement(roomQuery);
            roomStmt.setString(1, inputCode);
            ResultSet roomRs = roomStmt.executeQuery();

            if (roomRs.next()) {
                int roomId = roomRs.getInt("room_id");
                String roomName = roomRs.getString("room_name");

                // 2. Count number of users in that room
                String countQuery = "SELECT COUNT(*) AS count FROM room_members WHERE room_id = ?";
                PreparedStatement countStmt = conn.prepareStatement(countQuery);
                countStmt.setInt(1, roomId);
                ResultSet countRs = countStmt.executeQuery();
                countRs.next();
                int userCount = countRs.getInt("count");

                if (userCount < 3) {
                    // 3. Add current user to room
                    String insert = "INSERT INTO room_members (user_id, room_id) VALUES (?, ?)";
                    PreparedStatement insertStmt = conn.prepareStatement(insert);
                    insertStmt.setInt(1, Session.getUserId()); // assuming you're using Session to track login
                    insertStmt.setInt(2, roomId);
                    insertStmt.executeUpdate();

                    try {
                        GameClient client = new GameClient(message -> {
                            System.out.println("Server: " + message);
                        });
                        client.connect("localhost");
                        //client.send(gson.toJson(new Message("join", "client_user", roomName, "")));
                    } catch (IOException e) {
                        statusLabel.setText("Could not connect to server.");
                        e.printStackTrace();
                        return;
                    }

                    // 4. Load JoinDashboard.fxml
                    Parent root = FXMLLoader.load(getClass().getResource("/com/example/mysticmaze/fxmls/JoinDashboard.fxml"));
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.setResizable(false);
                    stage.setTitle("Team Dashboard");
                    stage.setScene(new Scene(root));
                    stage.show();
                } else {
                    statusLabel.setText("❌ Room is full.");
                }
            } else {
                statusLabel.setText("❌ Invalid room code.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("⚠️ Error connecting to server.");
        }
    }

    private String previousPageFXML = "/com/example/mysticmaze/fxmls/HomePage.fxml"; // ← set the previous page here

    @FXML
    private void goToNext(ActionEvent event) throws IOException {
        // Set current page as previous before navigating
        previousPageFXML = "/com/example/mysticmaze/fxmls/ThisPage.fxml"; // ← set current FXML file name

        Parent nextRoot = FXMLLoader.load(getClass().getResource("/com/example/mysticmaze/fxmls/NextPage.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(nextRoot));
    }

    @FXML
    private void handleBack(ActionEvent event) throws IOException {
        Parent backRoot = FXMLLoader.load(getClass().getResource(previousPageFXML));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(backRoot));
    }

}

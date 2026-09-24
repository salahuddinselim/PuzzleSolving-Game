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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Random;

public class CreateRoomController {
    @FXML private TextField roomNameField;
    @FXML private Label statusLabel;

    private final Gson gson = new Gson();

    @FXML
    public void createRoom(javafx.event.ActionEvent actionEvent) throws IOException {
        String roomName = roomNameField.getText().trim();
        if (roomName.isEmpty()) {
            statusLabel.setText("Please enter a room name!");
            return;
        }

        // Step 1: Connect to server
        try {
            GameClient client = new GameClient(message -> {
                System.out.println("Server: " + message);
            });
            client.connect("localhost");
            //client.send(gson.toJson(new Message("join", "host_user", roomName, "")));
        } catch (IOException e) {
            statusLabel.setText("Could not connect to server.");
            e.printStackTrace();
            return;
        }

        int roomCode =   new Random().nextInt(50000);

        // Step 2: Insert into database
        try (Connection conn = new DBUtil().getConnection()) {
            String sql = "INSERT INTO rooms (room_code, host_user_id, room_name) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, String.valueOf(roomCode));
            int hostUserId = Session.getUserId();
            stmt.setInt(2, hostUserId);
            stmt.setString(3, roomName);
            stmt.executeUpdate();


            // find the room id
            String query = "SELECT room_id FROM rooms WHERE host_user_id = ?";
            PreparedStatement stmt2 = conn.prepareStatement(query);
            stmt2.setInt(1, hostUserId);
            ResultSet rs = stmt2.executeQuery();
            int roomId = 0;
            if (rs.next()) {
                roomId = rs.getInt("room_id");
                System.out.println("Hosted Room ID: " + roomId);
            }
            if(DBUtil.isUserInActiveRoom(hostUserId)){
                statusLabel.setText("You are in active room, Please leave the room first");
                return;
            }

            String insert = "INSERT INTO room_members (user_id, room_id) VALUES (?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insert);
            insertStmt.setInt(1, Session.getUserId()); // assuming you're using Session to track login
            insertStmt.setInt(2, roomId);
            insertStmt.executeUpdate();

        } catch (SQLException e) {
            statusLabel.setText("Database error: Room not created.");
            e.printStackTrace();
            return;
        }

        // Step 3: Navigate to room dashboard
        statusLabel.setText("Room '" + roomCode + "' created successfully!");
        System.out.println("Room created: " + roomCode);

        Parent root = FXMLLoader.load(getClass().getResource("/com/example/mysticmaze/fxmls/joinDashboard.fxml"));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setResizable(false);
        stage.setTitle("Room Dashboard");
        stage.setScene(new Scene(root));
        stage.show();
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

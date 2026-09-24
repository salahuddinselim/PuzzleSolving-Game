package com.example.mysticmaze.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.example.mysticmaze.utils.DBUtil;

public class LeaderBoardController {

    @FXML private Label championLabel;
    @FXML private Label playerALabel;
    @FXML private Label playerBLabel;
    @FXML private Label playerCLabel;
    @FXML private Label playerDLabel;
    @FXML private Label playerWLabel;
    @FXML private Label playerXLabel;
    @FXML private Label playerYLabel;
    @FXML private Label playerZLabel;

    @FXML
    public void initialize() {
        List<String> roomNames = new ArrayList<>();

        String sql = """
            SELECT r.room_name
            FROM user_puzzle_performance upp
            JOIN rooms r ON upp.room_id = r.room_id
            GROUP BY upp.room_id, r.room_name
            ORDER BY COUNT(DISTINCT upp.puzzle_id) DESC,
                     SUM(upp.time_used) ASC,
                     SUM(upp.moves_used) ASC,
                     SUM(upp.hints_used) ASC
            LIMIT 9;
        """;

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                roomNames.add(rs.getString("room_name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Set top room as champion
        if (!roomNames.isEmpty()) {
            championLabel.setText(roomNames.get(0));
        } else {
            championLabel.setText("No Champion");
        }

        // Display remaining rooms (up to 8 more)
        if (roomNames.size() > 1) playerALabel.setText(roomNames.get(1));
        if (roomNames.size() > 2) playerBLabel.setText(roomNames.get(2));
        if (roomNames.size() > 3) playerCLabel.setText(roomNames.get(3));
        if (roomNames.size() > 4) playerDLabel.setText(roomNames.get(4));
        if (roomNames.size() > 5) playerWLabel.setText(roomNames.get(5));
        if (roomNames.size() > 6) playerXLabel.setText(roomNames.get(6));
        if (roomNames.size() > 7) playerYLabel.setText(roomNames.get(7));
        if (roomNames.size() > 8) playerZLabel.setText(roomNames.get(8));
    }

    @FXML
    private void handleBack(ActionEvent event) throws IOException {
        try {
            //  System.out.println("cliced");
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/com/example/mysticmaze/fxmls/HomePage.fxml"));
            Scene loginScene = new Scene(loginRoot);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(loginScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

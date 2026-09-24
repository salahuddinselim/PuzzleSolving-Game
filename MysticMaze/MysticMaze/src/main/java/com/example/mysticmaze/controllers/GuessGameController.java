package com.example.mysticmaze.controllers;

import com.example.mysticmaze.utils.DBUtil;
import com.example.mysticmaze.utils.Session;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GuessGameController {

    // Keep all your original FXML injections exactly as they were
    @FXML private Label sequenceLabel;
    @FXML private TextField answerField;
    @FXML private Button submitButton;
    @FXML private Button hintButton;
    @FXML private Label timerLabel;
    @FXML private Label bestSolveLabel;

    private int correctAnswer = 64;
    private int secondsElapsed = 0;
    private int bestTime = Integer.MAX_VALUE;
    private int movesUsed = 0;
    private int currentPuzzleId;
    private Timeline timer;

    @FXML
    public void initialize() {
        // Initialize with current puzzle from database
        loadCurrentPuzzle();

        // Start timer
        startTimer();

        // Set up button actions
        submitButton.setOnAction(e -> checkAnswer());
        hintButton.setOnAction(e -> {
            sequenceLabel.setText("Hint: It's a cube number.");
            movesUsed++;
        });
    }

    private void loadCurrentPuzzle() {
        String sql = "SELECT puzzle_id FROM player_progress " +
                "WHERE user_id = ? AND status = 'pending' " +
                "ORDER BY level LIMIT 1";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, Session.getUserId());
            ResultSet rs = stmt.executeQuery();

            currentPuzzleId = rs.next() ? rs.getInt("puzzle_id") : 1;

        } catch (SQLException e) {
            e.printStackTrace();
            currentPuzzleId = 1; // Fallback
        }
    }

    private void startTimer() {
        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondsElapsed++;
            timerLabel.setText("Time: " + secondsElapsed + "s");
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    private void checkAnswer() {
        try {
            int input = Integer.parseInt(answerField.getText().trim());
            movesUsed++; // Count this attempt

            if (input == correctAnswer) {
                handleCorrectAnswer();
            } else {
                sequenceLabel.setText("Wrong! Try again.");
            }
        } catch (NumberFormatException e) {
            sequenceLabel.setText("Enter a valid number.");
        }
    }

    private void handleCorrectAnswer() {
        timer.stop();
        sequenceLabel.setText("Correct! 1, 8, 27, 64");

        // Update best time if applicable
        if (bestSolveLabel != null && secondsElapsed < bestTime) {
            bestTime = secondsElapsed;
            bestSolveLabel.setText("Best Time: " + bestTime + "s");
        }

        submitButton.setDisable(true);
        saveGameResult();
    }

    private void saveGameResult() {
        int userId = Session.getUserId();
        int roomId = getCurrentRoomId(userId);

        if (roomId == -1) {
            System.out.println("No active room found for user");
            return;
        }

        String sql = "INSERT INTO user_puzzle_performance " +
                "(user_id, room_id, puzzle_id, moves_used, time_used) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, roomId);
            stmt.setInt(3, currentPuzzleId);
            stmt.setInt(4, movesUsed);
            stmt.setInt(5, secondsElapsed);

            stmt.executeUpdate();

            // Show completion message
            showCompletionAlert();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getCurrentRoomId(int userId) {
        String sql = "SELECT room_id FROM room_members " +
                "WHERE user_id = ? AND status = 'active'";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            return rs.next() ? rs.getInt("room_id") : -1;

        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private void showCompletionAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Puzzle Solved");
        alert.setHeaderText("Congratulations!");
        alert.setContentText(String.format(
                "You solved the puzzle in %d seconds with %d moves!",
                secondsElapsed, movesUsed
        ));
        alert.showAndWait();
    }
    @FXML
    private void handleBack(ActionEvent event) throws IOException {
        Parent backRoot = FXMLLoader.load(getClass().getResource("/com/example/mysticmaze/fxmls/HomePage.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(backRoot));
    }
}
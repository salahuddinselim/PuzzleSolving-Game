package com.example.mysticmaze.controllers;

import com.example.mysticmaze.utils.DBUtil;
import com.example.mysticmaze.utils.Session;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.Stack;

public class TohController {

    @FXML private Pane rod1, rod2, rod3;
    @FXML private Label moveLabel, timerLabel;

    Random random = new Random();
    int min = 3;
    int max = 6;
    // The formula is random.nextInt((max - min) + 1) + min;
    int randomNumber = random.nextInt((max - min) + 1) + min;

    private Pane selectedRod = null;
    private Rectangle selectedDisk = null;
    private final int diskHeight = 20;
    private final int spacing = 5;
    private final Stack<Rectangle> stack1 = new Stack<>();
    private final Stack<Rectangle> stack2 = new Stack<>();
    private final Stack<Rectangle> stack3 = new Stack<>();
    private int moveCount = 0;
    private long startTime;
    private Timeline timeline;
    private final int totalDisks = randomNumber;

    private int puzzleId;
    private int roomId;

    @FXML
    public void initialize() {
        loadPuzzleId();
        loadRoomId();
        initializeGame();
    }

    private void loadPuzzleId() {
        String sql = "SELECT puzzle_id FROM puzzles WHERE solution = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "remove the disc");
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                puzzleId = rs.getInt("puzzle_id");
            } else {
                puzzleId = -1;
                System.out.println("Puzzle ID not found for Tower of Hanoi!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            puzzleId = -1;
        }
    }

    private void loadRoomId() {
        String sql = "SELECT room_id FROM room_members WHERE user_id = ? AND status = 'active'";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, Session.getUserId());
            ResultSet rs = stmt.executeQuery();
            roomId = rs.next() ? rs.getInt("room_id") : -1;
        } catch (SQLException e) {
            e.printStackTrace();
            roomId = -1;
        }
    }

    private void initializeGame() {
        setupRods();
        createDisks();
        startTimer();
    }

    private void setupRods() {
        drawRod(rod1);
        drawRod(rod2);
        drawRod(rod3);

        javafx.application.Platform.runLater(() -> {
            if (rod1.getScene() != null) {
                rod1.getScene().getRoot().setStyle("-fx-background-color: #ffe6f0;");
            }
        });
    }

    private void drawRod(Pane rod) {
        Line line = new Line(75, 50, 75, 300);
        line.setStrokeWidth(8);
        line.setStroke(Color.LIGHTGRAY);
        rod.getChildren().add(line);
        rod.setStyle("-fx-background-color: transparent; -fx-border-color: #ccc; -fx-border-width: 2;");
    }

    private void createDisks() {
        int rodWidth = (int) rod1.getPrefWidth() - 20;
        int maxDiskWidth = rodWidth;

        for (int i = totalDisks; i > 0; i--) {
            int width = (int) (maxDiskWidth * (i / (double) totalDisks));
            Color color = Color.web(getRainbowColor(i));
            addDisk(rod1, stack1, width, color);
        }
        updateLayout(rod1, stack1);
    }

    private void addDisk(Pane rod, Stack<Rectangle> stack, int width, Color color) {
        Rectangle disk = new Rectangle(width, diskHeight, color);
        disk.setArcWidth(10);
        disk.setArcHeight(10);
        stack.push(disk);
        rod.getChildren().add(disk);
    }

    private void updateLayout(Pane rod, Stack<Rectangle> stack) {
        int centerX = (int) rod.getPrefWidth() / 2;
        int baseY = 300;

        for (int i = 0; i < stack.size(); i++) {
            Rectangle disk = stack.get(i);
            disk.setX(centerX - disk.getWidth() / 2);
            disk.setY(baseY - (i + 1) * (diskHeight + spacing));
            disk.setStroke(null);
        }
    }

    private void startTimer() {
        startTime = System.currentTimeMillis();
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateTimer()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void updateTimer() {
        long elapsed = (System.currentTimeMillis() - startTime) / 1000;
        timerLabel.setText("Time: " + elapsed + "s");
    }

    @FXML
    private void onRodClicked(MouseEvent event) {
        Pane clickedRod = (Pane) event.getSource();
        Stack<Rectangle> clickedStack = getStack(clickedRod);

        if (selectedRod == null) {
            handleFirstSelection(clickedRod, clickedStack);
        } else {
            handleSecondSelection(clickedRod, clickedStack);
        }
    }

    private void handleFirstSelection(Pane rod, Stack<Rectangle> stack) {
        if (!stack.isEmpty()) {
            selectedRod = rod;
            selectedDisk = stack.peek();
            selectedDisk.setStroke(Color.BLACK);
            selectedDisk.setStrokeWidth(2);
        }
    }

    private void handleSecondSelection(Pane clickedRod, Stack<Rectangle> clickedStack) {
        if (clickedRod == selectedRod) {
            deselectDisk();
            return;
        }

        Stack<Rectangle> sourceStack = getStack(selectedRod);
        if (sourceStack.isEmpty()) {
            deselectDisk();
            return;
        }

        Rectangle topDisk = sourceStack.peek();
        Stack<Rectangle> targetStack = getStack(clickedRod);

        if (isValidMove(topDisk, targetStack)) {
            executeMove(sourceStack, targetStack, topDisk, clickedRod);
            checkGameCompletion();
        } else {
            endGame(false);
        }

        deselectDisk();
    }

    private boolean isValidMove(Rectangle disk, Stack<Rectangle> targetStack) {
        return targetStack.isEmpty() || disk.getWidth() < targetStack.peek().getWidth();
    }

    private void executeMove(Stack<Rectangle> source, Stack<Rectangle> target, Rectangle disk, Pane targetRod) {
        source.pop();
        target.push(disk);
        selectedRod.getChildren().remove(disk);
        targetRod.getChildren().add(disk);
        updateLayout(selectedRod, source);
        updateLayout(targetRod, target);
        moveCount++;
        moveLabel.setText("Moves: " + moveCount);
    }

    private void checkGameCompletion() {
        if (stack3.size() == totalDisks) {
            endGame(true);
        }
    }

    private void endGame(boolean won) {
        long totalTime = (System.currentTimeMillis() - startTime) / 1000;
        timeline.stop();

        if (won) {
            showStyledAlert("🎉 YOU WON 🎉", "🏆 Well done! You solved it in\n" +
                    moveCount + " moves and " + totalTime + " seconds! 🎮");
            saveGameResult(totalTime, true);
        } else {
            showStyledAlert("💥 GAME OVER 💥", "⛔ Invalid move!\nTotal Moves: " +
                    moveCount + "\nTime: " + totalTime + " seconds. Try again! 🔁");
            saveGameResult(totalTime, false);
        }

        disableRods();
    }

    private void saveGameResult(long totalTime, boolean won) {
        try (Connection conn = DBUtil.getConnection()) {
            String perfSql = "INSERT INTO user_puzzle_performance (user_id, room_id, puzzle_id, moves_used, time_used, hints_used) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement perfStmt = conn.prepareStatement(perfSql)) {
                perfStmt.setInt(1, Session.getUserId());
                perfStmt.setInt(2, roomId);
                perfStmt.setInt(3, puzzleId);
                perfStmt.setInt(4, moveCount);
                perfStmt.setInt(5, (int) totalTime);
                perfStmt.setInt(6, 0);  // Tower of Hanoi doesn't use hints
                perfStmt.executeUpdate();
            }

            String progressSql = "UPDATE player_progress SET status = 'solved', end_time = NOW() WHERE user_id = ? AND puzzle_id = ?";
            try (PreparedStatement progressStmt = conn.prepareStatement(progressSql)) {
                progressStmt.setInt(1, Session.getUserId());
                progressStmt.setInt(2, puzzleId);
                progressStmt.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showDatabaseError();
        }
    }

    private void deselectDisk() {
        if (selectedDisk != null) {
            selectedDisk.setStroke(null);
        }
        selectedRod = null;
        selectedDisk = null;
    }

    private Stack<Rectangle> getStack(Pane rod) {
        if (rod == rod1) return stack1;
        if (rod == rod2) return stack2;
        return stack3;
    }

    private void disableRods() {
        rod1.setDisable(true);
        rod2.setDisable(true);
        rod3.setDisable(true);
    }

    private void showStyledAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.getDialogPane().setStyle("-fx-font-size: 16px; -fx-font-weight: bold; " +
                "-fx-background-color: #00000; -fx-text-fill: #222; " +
                "-fx-border-color: #ff69b4; -fx-border-width: 2px;");
        alert.showAndWait();
    }

    private void showDatabaseError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Database Error");
        alert.setHeaderText(null);
        alert.setContentText("Could not save game results to database.");
        alert.showAndWait();
    }

    private String getRainbowColor(int i) {
        String[] rainbow = {"#FF0000", "#FF7F00", "#FFFF00", "#00FF00", "#0000FF", "#4B0082", "#8B00FF"};
        return rainbow[(i - 1) % rainbow.length];
    }
}

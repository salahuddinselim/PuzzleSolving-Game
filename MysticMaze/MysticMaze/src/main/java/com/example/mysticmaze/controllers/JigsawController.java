package com.example.mysticmaze.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class JigsawController {

    @FXML private Pane puzzlePane;
    @FXML private Label player1Progress;
    @FXML private Label player2Progress;
    @FXML private Label moveCounter;
    @FXML private Label tipCounter;
    @FXML private Label timerLabel;
    @FXML private Label bestTimeLabel;
    @FXML private Button tipButton;
    @FXML private Button backButton;
    @FXML private ComboBox<String> sizeSelector;

    private int moves = 0;
    private int tipsUsed = 0;
    private int seconds = 0;
    private Timeline timeline;
    private int gridSize = 3;
    private Image fullImage;

    private List<PuzzlePiece> pieces = new ArrayList<>();
    private PuzzlePiece selectedPiece = null;

    @FXML
    public void initialize() {
        sizeSelector.getItems().addAll("3x3", "4x4", "5x5");
        sizeSelector.setValue("3x3");

        fullImage = new Image(Objects.requireNonNull(getClass().getResource("/com/example/mysticmaze/images/puzzle.jpg")).toExternalForm());

        startTimer();
        createPuzzle(gridSize);
    }

    private void startTimer() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            seconds++;
            int mins = seconds / 60;
            int secs = seconds % 60;
            timerLabel.setText(String.format("Time: %02d:%02d", mins, secs));
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void stopTimer() {
        if (timeline != null) {
            timeline.stop();
        }
    }

    private void createPuzzle(int size) {
        moves = 0;
        seconds = 0;
        moveCounter.setText("Moves: 0");
        timerLabel.setText("Time: 00:00");
        tipCounter.setText("Tips Used: 0");
        tipsUsed = 0;

        puzzlePane.getChildren().clear();
        pieces.clear();
        selectedPiece = null;

        double pw = fullImage.getWidth() / size;
        double ph = fullImage.getHeight() / size;
        double pieceDisplaySize = 550.0 / size;

        // Create pieces at correct positions
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                ImageView iv = new ImageView(fullImage);
                iv.setViewport(new javafx.geometry.Rectangle2D(col * pw, row * ph, pw, ph));
                iv.setFitWidth(pieceDisplaySize);
                iv.setFitHeight(pieceDisplaySize);

                StackPane pieceView = new StackPane(iv);
                pieceView.setPrefSize(pieceDisplaySize, pieceDisplaySize);

                PuzzlePiece piece = new PuzzlePiece(pieceView, row, col);
                piece.currentRow = row;
                piece.currentCol = col;

                piece.view.setLayoutX(col * pieceDisplaySize);
                piece.view.setLayoutY(row * pieceDisplaySize);

                piece.view.setOnMouseClicked(this::handlePieceClick);

                pieces.add(piece);
            }
        }

        // Shuffle positions (not nodes)
        Collections.shuffle(pieces);

        // Assign new currentRow/currentCol and reposition
        for (int i = 0; i < pieces.size(); i++) {
            PuzzlePiece piece = pieces.get(i);
            int newRow = i / size;
            int newCol = i % size;
            piece.currentRow = newRow;
            piece.currentCol = newCol;
            piece.view.setLayoutX(newCol * pieceDisplaySize);
            piece.view.setLayoutY(newRow * pieceDisplaySize);
        }

        puzzlePane.getChildren().addAll(pieces.stream().map(p -> p.view).toList());

        // Restart timer
        if (timeline != null) {
            timeline.stop();
        }
        seconds = 0;
        timeline.playFromStart();
    }

    private void handlePieceClick(MouseEvent event) {
        StackPane clickedView = (StackPane) event.getSource();

        PuzzlePiece clickedPiece = null;
        for (PuzzlePiece p : pieces) {
            if (p.view == clickedView) {
                clickedPiece = p;
                break;
            }
        }
        if (clickedPiece == null) return;

        if (selectedPiece == null) {
            selectedPiece = clickedPiece;
            selectedPiece.view.setStyle("-fx-border-color: red; -fx-border-width: 3px;");
        } else if (selectedPiece == clickedPiece) {
            // Deselect
            selectedPiece.view.setStyle("");
            selectedPiece = null;
        } else {
            // Swap positions
            swapPieces(selectedPiece, clickedPiece);
            selectedPiece.view.setStyle("");
            selectedPiece = null;

            incrementMoves();
            checkCompletion();
        }
    }

    private void swapPieces(PuzzlePiece p1, PuzzlePiece p2) {
        int tempRow = p1.currentRow;
        int tempCol = p1.currentCol;
        p1.currentRow = p2.currentRow;
        p1.currentCol = p2.currentCol;
        p2.currentRow = tempRow;
        p2.currentCol = tempCol;

        double size = 600.0 / gridSize;
        p1.view.setLayoutX(p1.currentCol * size);
        p1.view.setLayoutY(p1.currentRow * size);
        p2.view.setLayoutX(p2.currentCol * size);
        p2.view.setLayoutY(p2.currentRow * size);
    }

    private void checkCompletion() {
        for (PuzzlePiece piece : pieces) {
            if (!piece.isInCorrectPosition()) {
                return;
            }
        }
        completeGame();
    }

    public void incrementMoves() {
        moves++;
        moveCounter.setText("Moves: " + moves);
    }

    @FXML
    public void handleShowTip(ActionEvent event) {
        tipsUsed++;
        tipCounter.setText("Tips Used: " + tipsUsed);

        try {
            ImageView imageView = new ImageView(fullImage);
            imageView.setFitWidth(400);
            imageView.setPreserveRatio(true);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Puzzle Tip");
            alert.setHeaderText("Here is the full puzzle image!");
            alert.getDialogPane().setContent(imageView);
            alert.showAndWait();

        } catch (Exception e) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Image Error");
            error.setHeaderText("Could not load the tip image.");
            error.setContentText(e.getMessage());
            error.showAndWait();
        }
    }

    public void updatePlayerProgress(int player, int percent) {
        if (player == 1) {
            player1Progress.setText("Player 1: " + percent + "%");
        } else {
            player2Progress.setText("Player 2: " + percent + "%");
        }
    }

    public void completeGame() {
        stopTimer();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Puzzle Completed");
        alert.setHeaderText("Well done!");
        alert.setContentText("You completed the puzzle in " + timerLabel.getText().replace("Time: ", "") + " with " + moves + " moves.");
        alert.showAndWait();
    }

    @FXML
    public void handleBack(ActionEvent event) {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void handleSizeChange(ActionEvent event) {
        String value = sizeSelector.getValue();
        if (value != null && value.contains("x")) {
            try {
                gridSize = Integer.parseInt(value.split("x")[0]);
                createPuzzle(gridSize);
            } catch (Exception ignored) {
            }
        }
    }

    // Inner class to represent each puzzle piece
    public class PuzzlePiece {
        public StackPane view;
        public int correctRow, correctCol;
        public int currentRow, currentCol;

        public PuzzlePiece(StackPane view, int correctRow, int correctCol) {
            this.view = view;
            this.correctRow = correctRow;
            this.correctCol = correctCol;
            this.currentRow = correctRow;
            this.currentCol = correctCol;
        }

        public boolean isInCorrectPosition() {
            return correctRow == currentRow && correctCol == currentCol;
        }
    }
}

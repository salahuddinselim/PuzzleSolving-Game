package com.example.mysticmaze.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.util.*;

public class CrossWordController {

    @FXML private GridPane gridPane;
    @FXML private Label timerLabel;
    @FXML private Label hintsUsedLabel;
    @FXML private Label progressLabel;
    @FXML private Button hintButton;
    @FXML private Button finishButton;

    private final int GRID_SIZE = 10;
    private final List<String> WORDS = List.of("JAVA", "BYTE", "ON", "CODE", "AI", "CPU");

    private final Random random = new Random();
    private Cell[][] cells;
    private int hintsUsed = 0;
    private int foundWords = 0;
    private Set<String> foundSet = new HashSet<>();
    private Cell firstSelected = null;

    private Timeline stopwatch;
    private int elapsedSeconds = 0;

    @FXML
    public void initialize() {
        timerLabel.setText("00:00:00");
        progressLabel.setText("0 / " + WORDS.size());

        setupGrid();
        placeWords();
        fillRandomLetters();
        setupStopwatch();

        hintButton.setOnAction(e -> showHint());
        finishButton.setOnAction(e -> finishGame());
    }

    private void setupStopwatch() {
        stopwatch = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            elapsedSeconds++;
            int hours = elapsedSeconds / 3600;
            int minutes = (elapsedSeconds % 3600) / 60;
            int seconds = elapsedSeconds % 60;
            timerLabel.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
        }));
        stopwatch.setCycleCount(Timeline.INDEFINITE);
        stopwatch.play();
    }

    private void setupGrid() {
        gridPane.getChildren().clear();
        cells = new Cell[GRID_SIZE][GRID_SIZE];

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                Cell cell = new Cell(row, col);
                cell.setFont(Font.font(18));
                cell.setPrefSize(40, 40);
                cell.setOnMouseClicked(e -> {
                    if (e.getButton() == MouseButton.PRIMARY) handleSelection(cell);
                });
                gridPane.add(cell, col, row);
                cells[row][col] = cell;
            }
        }
    }

    private void handleSelection(Cell cell) {
        resetSelectionStyles();

        if (firstSelected == null) {
            firstSelected = cell;
            cell.setStyle("-fx-background-color: lightblue;");
        } else {
            List<Cell> path = getCellPath(firstSelected, cell);

            for (Cell c : path) {
                c.setStyle("-fx-background-color: #add8e6;"); // light blue
            }

            String word = extractWordFromPath(path);
            if (WORDS.contains(word) && !foundSet.contains(word)) {
                for (Cell c : path) {
                    c.setStyle("-fx-background-color: lightgreen;");
                    c.setDisable(true);
                }
                foundSet.add(word);
                foundWords++;
                progressLabel.setText(foundWords + " / " + WORDS.size());
            }

            firstSelected = null;
        }
    }

    private List<Cell> getCellPath(Cell start, Cell end) {
        List<Cell> path = new ArrayList<>();

        int dr = Integer.compare(end.row, start.row);
        int dc = Integer.compare(end.col, start.col);

        int r = start.row;
        int c = start.col;

        while (r != end.row + dr || c != end.col + dc) {
            if (r >= 0 && r < GRID_SIZE && c >= 0 && c < GRID_SIZE) {
                path.add(cells[r][c]);
            }
            r += dr;
            c += dc;
        }

        return path;
    }

    private String extractWordFromPath(List<Cell> path) {
        StringBuilder sb = new StringBuilder();
        for (Cell c : path) {
            sb.append(c.getText());
        }
        return sb.toString();
    }

    private void resetSelectionStyles() {
        for (Cell[] row : cells) {
            for (Cell c : row) {
                if (!c.isDisabled()) c.setStyle("-fx-alignment: center;");
            }
        }
    }

    private void placeWords() {
        for (String word : WORDS) {
            boolean placed = false;
            int attempts = 0;

            while (!placed && attempts < 100) {
                int row = random.nextInt(GRID_SIZE);
                int col = random.nextInt(GRID_SIZE);

                int[] directions = {-1, 0, 1};
                int dr = directions[random.nextInt(3)];
                int dc = directions[random.nextInt(3)];

                if (dr == 0 && dc == 0) continue;

                int endRow = row + dr * (word.length() - 1);
                int endCol = col + dc * (word.length() - 1);

                if (inBounds(endRow, endCol) && canPlaceWord(word, row, col, dr, dc)) {
                    for (int i = 0; i < word.length(); i++) {
                        cells[row + dr * i][col + dc * i].setText(String.valueOf(word.charAt(i)));
                    }
                    placed = true;
                }

                attempts++;
            }
        }
    }

    private boolean inBounds(int row, int col) {
        return row >= 0 && row < GRID_SIZE && col >= 0 && col < GRID_SIZE;
    }

    private boolean canPlaceWord(String word, int row, int col, int dr, int dc) {
        for (int i = 0; i < word.length(); i++) {
            int r = row + dr * i;
            int c = col + dc * i;

            if (!inBounds(r, c)) return false;

            String cellText = cells[r][c].getText();
            if (!cellText.isEmpty() && !cellText.equals(String.valueOf(word.charAt(i)))) {
                return false;
            }
        }
        return true;
    }

    private void fillRandomLetters() {
        for (Cell[] row : cells) {
            for (Cell c : row) {
                if (c.getText().isEmpty()) {
                    char randomChar = (char) ('A' + random.nextInt(26));
                    c.setText(String.valueOf(randomChar));
                }
            }
        }
    }

    private void showHint() {
        hintsUsed++;
        hintsUsedLabel.setText(String.valueOf(hintsUsed));

        List<String> remaining = new ArrayList<>(WORDS);
        remaining.removeAll(foundSet);

        if (!remaining.isEmpty()) {
            Alert a = new Alert(Alert.AlertType.INFORMATION, "Hint: One of the words is '" + remaining.get(0) + "'", ButtonType.OK);
            a.setHeaderText(null);
            a.setTitle("Hint");
            a.showAndWait();
        }
    }

    private void finishGame() {
        if (stopwatch != null) stopwatch.stop();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Complete");
        alert.setHeaderText(null);
        alert.setContentText("You finished the crossword in " + timerLabel.getText() +
                "\nWords Found: " + foundWords + " / " + WORDS.size() +
                "\nHints Used: " + hintsUsed);
        alert.showAndWait();
    }

    static class Cell extends TextField {
        int row, col;
        Cell(int r, int c) {
            super();
            this.row = r;
            this.col = c;
            setEditable(false);
            setFocusTraversable(false);
            setStyle("-fx-alignment: center; -fx-border-color: gray; -fx-border-width: 0.5px;");
        }
    }
}

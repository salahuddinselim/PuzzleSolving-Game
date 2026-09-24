package com.example.mysticmaze.controllers;

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

public class StartGameController {

    @FXML private Button level1, level2, level3, level4, level5,
            level6, level7, level8, level9, level10;

    @FXML private Label lock1, lock2, lock3, lock4, lock5,
            lock6, lock7, lock8, lock9, lock10;

    private int currentUnlockedLevel = 1;

    @FXML
    public void initialize() {
        setupLevels();

        Button[] levels = {
                level1, level2, level3, level4, level5,
                level6, level7, level8, level9, level10
        };

        for (int i = 0; i < levels.length; i++) {
            final int levelNum = i + 1;
            levels[i].setOnAction(e -> unlockNextLevel(levelNum));
        }
    }

    private void setupLevels() {
        Button[] levels = {
                level1, level2, level3, level4, level5,
                level6, level7, level8, level9, level10
        };

        Label[] locks = {
                lock1, lock2, lock3, lock4, lock5,
                lock6, lock7, lock8, lock9, lock10
        };

        for (int i = 0; i < levels.length; i++) {
            boolean unlocked = (i < currentUnlockedLevel);
            levels[i].setDisable(!unlocked);
            locks[i].setVisible(!unlocked);
        }
    }

    private void unlockNextLevel(int levelCompleted) {
        if (levelCompleted == currentUnlockedLevel && currentUnlockedLevel < 10) {
            currentUnlockedLevel++;
            setupLevels();
            System.out.println("Unlocked level " + currentUnlockedLevel);
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

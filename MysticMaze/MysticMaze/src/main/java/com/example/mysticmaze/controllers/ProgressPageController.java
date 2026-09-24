package com.example.mysticmaze.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ProgressPageController {

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


package com.example.mysticmaze.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlayerController {

    @FXML
    private HBox carousel;

    @FXML
    private Button backButton;

    private List<String> characterImages = List.of(
            "/com/example/mysticmaze/images/ashira.png",
            "/com/example/mysticmaze/images/echo.png",
            "/com/example/mysticmaze/images/iris.png",
            "/com/example/mysticmaze/images/juno.png",
            "/com/example/mysticmaze/images/rook.png",
            "/com/example/mysticmaze/images/vex.png"
    );

    private int currentIndex = 0;

    @FXML
    public void initialize() {
        updateCarousel();
    }

    private void updateCarousel() {
        carousel.getChildren().clear();
        int total = characterImages.size();
        for (int i = -2; i <= 2; i++) {
            int index = (currentIndex + i + total) % total;
            Image img = new Image(getClass().getResourceAsStream(characterImages.get(index)));
            ImageView view = new ImageView(img);
            view.setFitHeight(i == 0 ? 160 : 100);
            view.setFitWidth(i == 0 ? 160 : 100);
            carousel.getChildren().add(view);
        }
    }

    @FXML
    public void handleNext() {
        if (!characterImages.isEmpty()) {
            currentIndex = (currentIndex + 1) % characterImages.size();
            updateCarousel();
        }
    }

    @FXML
    public void handlePrevious() {
        if (!characterImages.isEmpty()) {
            currentIndex = (currentIndex - 1 + characterImages.size()) % characterImages.size();
            updateCarousel();
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

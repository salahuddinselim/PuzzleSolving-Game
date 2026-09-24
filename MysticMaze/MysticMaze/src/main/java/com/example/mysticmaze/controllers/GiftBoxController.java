package com.example.mysticmaze.controllers;

import javafx.animation.Animation;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GiftBoxController implements Initializable {

    @FXML
    private ImageView giftBoxImage;

    @FXML
    private Button claimRewardBtn;

    @FXML
    private Button backButton;
    @FXML
    private Label rewardMessageLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        animateGiftBox();
    }

    private void animateGiftBox() {
        // Scale (bounce)
        ScaleTransition scale = new ScaleTransition(Duration.seconds(0.6), giftBoxImage);
        scale.setFromX(1.0);
        scale.setToX(1.1);
        scale.setFromY(1.0);
        scale.setToY(1.1);
        scale.setAutoReverse(true);
        scale.setCycleCount(Animation.INDEFINITE);

        // Rotate (wiggle)
        RotateTransition rotate = new RotateTransition(Duration.seconds(0.4), giftBoxImage);
        rotate.setFromAngle(-8);
        rotate.setToAngle(8);
        rotate.setAutoReverse(true);
        rotate.setCycleCount(Animation.INDEFINITE);

        ParallelTransition dancing = new ParallelTransition(scale, rotate);
        dancing.play();
    }

    @FXML
    private void handleClaimReward(ActionEvent event) {
        rewardMessageLabel.setVisible(true); // Show the label when reward is claimed
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

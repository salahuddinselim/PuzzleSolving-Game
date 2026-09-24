package com.example.mysticmaze.controllers;

import com.example.mysticmaze.utils.DBUtil;
import com.example.mysticmaze.utils.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;

public class ProfilePageController {

    private final String previousPageFXML = "/com/example/mysticmaze/fxmls/DashboardPage.fxml";

    @FXML
    private Label usernameLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label userIdLabel;

    @FXML
    private ImageView profileImageView;

    @FXML
    private Button uploadPhotoBtn;

    @FXML
    private Button savePhotoBtn;

    private File selectedImageFile;

    @FXML
    private void handleBack(ActionEvent event) throws IOException {
        Parent backRoot = FXMLLoader.load(getClass().getResource(previousPageFXML));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(backRoot));
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    @FXML
    private void handlePhotoUpload(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Profile Photo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        Stage stage = (Stage) uploadPhotoBtn.getScene().getWindow();
        selectedImageFile = fileChooser.showOpenDialog(stage);

        if (selectedImageFile != null) {
            Image image = new Image(selectedImageFile.toURI().toString());
            profileImageView.setImage(image);
        }
    }

    @FXML
    private void handleSavePhoto(ActionEvent event) {
        if (selectedImageFile == null) {
            System.out.println("No image selected.");
            return;
        }

        int userId = Session.getUserId();

        String updateQuery = "UPDATE users SET profile_image = ? WHERE user_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateQuery);
             FileInputStream fis = new FileInputStream(selectedImageFile)) {

            stmt.setBinaryStream(1, fis, (int) selectedImageFile.length());
            stmt.setInt(2, userId);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Profile image uploaded successfully.");
                showAlert(Alert.AlertType.INFORMATION, "Success", "Profile image uploaded successfully!");
            } else {
                System.out.println("User not found or image not saved.");
                showAlert(Alert.AlertType.ERROR, "Error", "User not found or image not saved.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "User not found or image not saved.");}
    }

    @FXML
    public void initialize() {
        usernameLabel.setText("Username: " + Session.getUserName());
        emailLabel.setText("Email: " + Session.getUserEmail());
        userIdLabel.setText("User ID: " + Session.getUserId());

        loadProfileImage();
    }

    private void loadProfileImage() {
        int userId = Session.getUserId();
        String selectQuery = "SELECT profile_image FROM users WHERE user_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(selectQuery)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                byte[] imageBytes = rs.getBytes("profile_image");
                if (imageBytes != null) {
                    Image image = new Image(new java.io.ByteArrayInputStream(imageBytes));
                    profileImageView.setImage(image);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

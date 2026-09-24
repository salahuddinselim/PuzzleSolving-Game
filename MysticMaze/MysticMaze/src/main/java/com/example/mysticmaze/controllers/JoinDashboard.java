package com.example.mysticmaze.controllers;

import com.example.mysticmaze.models.Message;
import com.example.mysticmaze.utils.DBUtil;
import com.example.mysticmaze.utils.Session;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class JoinDashboard {

    // JoinDashboard Section
    @FXML
    private VBox chatVBox; // Your chat display container
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    //private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @FXML
    private Label roomName;
    @FXML
    private Label roomCode;
    @FXML
    private  Label roomCreator;
    @FXML
    private Label createdAt;

    @FXML
    private  VBox player1data;
    @FXML
    private  VBox player2data;
    @FXML
    private VBox player3data;


    @FXML
    private Label player1Moves;
    @FXML
    private Label player1Time;
    @FXML
    private Label player1Status;


    @FXML
    private Label player2Moves;
    @FXML
    private Label player2Time;
    @FXML
    private Label player2Status;

    @FXML
    private Label player3Moves;
    @FXML
    private Label player3Time;
    @FXML
    private Label player3Status;

    @FXML
    private TextArea messageField;


    @FXML
    private Button startGameButton;

    int current_user = Session.getUserId();

    // Level Buttons and Locks Section
    @FXML
    private Button level1, level2, level3, level4, level5,
            level6;

    @FXML
    private Label lock1, lock2, lock3, lock4, lock5,
            lock6;

    private int currentUnlockedLevel = 1;

    @FXML
    public void initialize() {
       int roomId = getRoomIdForCurrentUser();
        //int roomId = 10; // You can pass this dynamically
        autoRefreshChat(roomId);
        loadRoomDetails(roomId);
        loadCurrentUserInfoToVBox(player1data);
        getMaxLevelAttemptedByCurrentUserRoom();
        //loadTeammatesToVBox(player2data);

        // Init player statuses
        //updatePlayerStatus(1, 12, "00:45", "Ready");
       // updatePlayerStatus(2, 8, "00:38", "Offline");
        //updatePlayerStatus(3, 15, "01:02", "Offline");

        // Set up Start Game Button
        if (startGameButton != null) {
            startGameButton.setOnAction(e -> {
                try {
                    handleStartGame(e);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
        }




        // Setup Level Buttons if present
        if (level1 != null) {
            setupLevels();
            Button[] levels = {
                    level1, level2, level3, level4, level5,
                    level6
            };

            for (int i = 0; i < levels.length; i++) {
                final int levelNum = i + 1;
                levels[i].setOnAction(e -> unlockNextLevel(levelNum));
            }
        }
    }

    private void updatePlayerStatus(int playerNum, int moves, String time, String status) {
        Label movesLabel = switch (playerNum) {
            case 1 -> player1Moves;
            case 2 -> player2Moves;
            case 3 -> player3Moves;
            default -> null;
        };

        Label timeLabel = switch (playerNum) {
            case 1 -> player1Time;
            case 2 -> player2Time;
            case 3 -> player3Time;
            default -> null;
        };

        Label statusLabel = switch (playerNum) {
            case 1 -> player1Status;
            case 2 -> player2Status;
            case 3 -> player3Status;
            default -> null;
        };

        if (movesLabel != null) movesLabel.setText("Moves: " + moves);
        if (timeLabel != null) timeLabel.setText("Time: " + time);
        if (statusLabel != null) {
            statusLabel.setText("Status: " + status);
            switch (status.toLowerCase()) {
                case "ready" -> statusLabel.setStyle("-fx-text-fill: #00ff00;");
                case "waiting" -> statusLabel.setStyle("-fx-text-fill: #ffff00;");
                case "offline" -> statusLabel.setStyle("-fx-text-fill: #ff0000;");
                default -> statusLabel.setStyle("-fx-text-fill: white;");
            }
        }
    }

    private void handleStartGame(ActionEvent e) throws IOException {
        System.out.println("🟢 Start Game button clicked!");
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/mysticmaze/fxmls/TohPage.fxml"));
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setResizable(false);
        stage.setTitle("Create a Team");
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void setupLevels() {
        Button[] levels = {
                level1, level2, level3, level4, level5,
                level6
        };

        Label[] locks = {
                lock1, lock2, lock3, lock4, lock5,
                lock6
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
            System.out.println("✅ Unlocked level " + currentUnlockedLevel);
        }

    }



    @FXML
    private void sendMessage(ActionEvent event) {
        Message message = new Message();
        message.setMessage(messageField.getText().trim());
        message.setSenderId(current_user);

        // SQL query to retrieve the room_id based on current_user
        String sql = "SELECT room_id FROM room_members WHERE user_id = ?";

        String room_idd = "0";  // Default room_id if none is found

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Set the parameter for the prepared statement
            stmt.setInt(1, current_user);  // Pass the current_user as the parameter

            // Execute the query
            ResultSet rs = stmt.executeQuery();

            // Retrieve the room_id if a record is found
            if (rs.next()) {
                room_idd = rs.getString("room_id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Set the room_id for the message
        message.setRoomId(Integer.parseInt(room_idd));
        message.setType("message");

        // Insert the message into the database
        DBUtil.insertRoomMessage(message);
    }

    @FXML
    private void handleBack(ActionEvent event) throws IOException {
        /*Parent backRoot = FXMLLoader.load(getClass().getResource(previousPageFXML));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(backRoot));*/
    }

    @FXML
    private AnchorPane centerPane;

    // Method to dynamically load game into center pane
    private void loadGameInCenter(String fxmlPath) {
        try {
            Parent gameContent = FXMLLoader.load(getClass().getResource(fxmlPath));
            centerPane.getChildren().clear();
            centerPane.getChildren().add(gameContent);

            // Make game fill the center pane
            AnchorPane.setTopAnchor(gameContent, 0.0);
            AnchorPane.setBottomAnchor(gameContent, 0.0);
            AnchorPane.setLeftAnchor(gameContent, 0.0);
            AnchorPane.setRightAnchor(gameContent, 0.0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getMaxLevelAttemptedByCurrentUserRoom() {
        int currentUserId = Session.getUserId(); // Your current logged-in user
        int maxLevel = 0;

        String query = """
        SELECT MAX(p.level) AS max_level
        FROM user_puzzle_performance upp
        JOIN room_members rm ON upp.room_id = rm.room_id
        JOIN puzzles p ON upp.puzzle_id = p.puzzle_id
        WHERE rm.user_id = ?
    """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, currentUserId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                maxLevel = rs.getInt("max_level");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return maxLevel;
    }


    // Level button handlers
    @FXML
    private void handleLevel1(ActionEvent event) {
        loadGameInCenter("/com/example/mysticmaze/fxmls/Guess.fxml");
    }

    @FXML
    private void handleLevel2(ActionEvent event) {
        System.out.println(getMaxLevelAttemptedByCurrentUserRoom());
        if(getMaxLevelAttemptedByCurrentUserRoom()>=1) {
            System.out.println("Dhukse mone hoy ");
            loadGameInCenter("/com/example/mysticmaze/fxmls/CrossWord.fxml");
        }
        else {
            System.out.println("Unlock the previous level first ");
        }
    }

    @FXML
    private void handleLevel3(ActionEvent event) {
        if(getMaxLevelAttemptedByCurrentUserRoom()>=2) {
            loadGameInCenter("/com/example/mysticmaze/fxmls/ColorMap.fxml");
        }
        else {
            System.out.println("Unlock the previous level first ");
        }

    }

    @FXML
    private void handleLevel4(ActionEvent event) {
        if(getMaxLevelAttemptedByCurrentUserRoom()>=3) {
            loadGameInCenter("/com/example/mysticmaze/fxmls/Jigsaw.fxml");
        }
        else {
            System.out.println("Unlock the previous level first ");
        }
    }
    @FXML
    private void handleLevel5(ActionEvent event) {
        if(getMaxLevelAttemptedByCurrentUserRoom()>=4) {
            loadGameInCenter("/com/example/mysticmaze/fxmls/TohPage.fxml");
        }
        else {
            System.out.println("Unlock the previous level first ");
        }

    }
        // Message related kaj kam

    private void autoRefreshChat(int roomId) {
        scheduler.scheduleAtFixedRate(() -> {
            loadTeammatesToVBox(player2data);
            loadCurrentUserInfoToVBox(player1data);
            try (Connection conn = DBUtil.getConnection();//.getConnection("jdbc:mysql://localhost:3306/", "user", "pass");
                 PreparedStatement stmt = conn.prepareStatement(
                         "SELECT m.message, m.sent_at, u.username " +
                                 "FROM messages m JOIN users u ON m.sender_id = u.user_id " +
                                 "WHERE m.room_id = ? ORDER BY m.sent_at DESC LIMIT 5")) {

                stmt.setInt(1, roomId);
                ResultSet rs = stmt.executeQuery();

                List<String> chatLines = new ArrayList<>();
                while (rs.next()) {
                    String user = rs.getString("username");
                    String msg = rs.getString("message");
                    String time = rs.getString("sent_at");
                    chatLines.add(user + ": " + msg );
                }

                Collections.reverse(chatLines); // Oldest message first

                Platform.runLater(() -> {
                    chatVBox.getChildren().clear();
                    for (String line : chatLines) {
                        Label label = new Label(line);
                        label.setStyle("-fx-background-color: #2d2d2d; -fx-text-fill: white; -fx-padding: 5px; -fx-background-radius: 5px;");
                        chatVBox.getChildren().add(label);
                    }
                });

            } catch (SQLException e) {
                e.printStackTrace(); // You can log or show an alert
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    // rood data load
    private void loadRoomDetails(int roomId) {
        String sql = """
        SELECT r.room_name,r.room_code, r.created_at, u.username AS creator
        FROM rooms r
        JOIN users u ON r.host_user_id = u.user_id
        WHERE r.room_id = ?
        """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, roomId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String room_Code = rs.getString("room_code");
                String created_At = rs.getString("created_at");
                String creator = rs.getString("creator");
                String room_Name = rs.getString("room_name");

                roomName.setText("Room Name : "+room_Name);
                roomCode.setText("Room Code : "+room_Code);
                roomCreator.setText("Created By : "+creator);
                createdAt.setText("Created At :"+created_At);



                System.out.println("🟢 Room Code: " + roomCode);
                System.out.println("👤 Created By: " + creator);
                System.out.println("📅 Created At: " + created_At);
                System.out.println("📅 Room name : " + room_Name);

                // Optional: Display in FXML labels
                // roomCodeLabel.setText(roomCode);
                // creatorLabel.setText(creator);
                // createdAtLabel.setText(createdAt);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    private int getRoomIdForCurrentUser() {
        String sql = "SELECT room_id FROM room_members WHERE user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, Session.getUserId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("room_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    private void loadCurrentUserInfoToVBox(VBox userInfoBox) {
        int currentUserId = Session.getUserId();

        String sql = """
        SELECT u.username, u.email,
               COALESCE(SUM(p.moves_used), 0) AS total_moves,
               COALESCE(SUM(p.time_used), 0) AS total_time
        FROM users u
        LEFT JOIN user_puzzle_performance p ON u.user_id = p.user_id
        WHERE u.user_id = ?
        GROUP BY u.user_id
        """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, currentUserId);
            ResultSet rs = stmt.executeQuery();

            // Store data before ResultSet is closed
            String username = null, email = null;
            int totalMoves = 0, totalTime = 0;
            boolean found = false;

            if (rs.next()) {
                username = rs.getString("username");
                email = rs.getString("email");
                totalMoves = rs.getInt("total_moves");
                totalTime = rs.getInt("total_time");
                found = true;
            }

            // Update UI on FX thread
            final String finalUsername = username;
            final String finalEmail = email;
            final int finalMoves = totalMoves;
            final int finalTime = totalTime;
            final boolean userFound = found;

            Platform.runLater(() -> {
                userInfoBox.getChildren().clear();

                if (userFound) {
                    userInfoBox.getChildren().addAll(
                            createStyledLabel("👤 Username: " + finalUsername),
                            createStyledLabel("📧 Email: " + finalEmail),
                            createStyledLabel("🏃 Total Moves: " + finalMoves),
                            createStyledLabel("⏱ Total Time: " + finalTime + "s")
                    );
                } else {
                    userInfoBox.getChildren().add(createStyledLabel("⚠ No user data found."));
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadTeammatesToVBox(VBox teammateBox) {
        int currentUserId = Session.getUserId();

        String getRoomQuery = "SELECT room_id FROM room_members WHERE user_id = ?";
        String getTeammatesQuery = """
        SELECT u.username, u.email,rm.status
        FROM room_members rm
        JOIN users u ON rm.user_id = u.user_id
        WHERE rm.status = 'active' and rm.room_id = ? AND rm.user_id != ?
        LIMIT 2
        """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement roomStmt = conn.prepareStatement(getRoomQuery)) {

            roomStmt.setInt(1, currentUserId);
            ResultSet roomRs = roomStmt.executeQuery();

            if (roomRs.next()) {
                int roomId = roomRs.getInt("room_id");

                try (PreparedStatement teammateStmt = conn.prepareStatement(getTeammatesQuery)) {
                    teammateStmt.setInt(1, roomId);
                    teammateStmt.setInt(2, currentUserId);
                    ResultSet rs = teammateStmt.executeQuery();

                    List<Label> teammateLabels = new ArrayList<>();

                    while (rs.next()) {
                        String name = rs.getString("username");
                        String email = rs.getString("email");
                       // String status = rs.getString("status");

                        teammateLabels.add(createStyledLabel("👤 Username: " + name));
                        teammateLabels.add(createStyledLabel("📧 Email: " + email));
                        teammateLabels.add(new Label("..................."));
                    }

                    Platform.runLater(() -> {
                        teammateBox.getChildren().clear();
                        if (teammateLabels.isEmpty()) {
                            teammateBox.getChildren().add(createStyledLabel("😔 No teammates found"));
                        } else {
                            teammateBox.getChildren().addAll(teammateLabels);
                        }
                    });
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private  void  LeaveRoom(ActionEvent event){
        int userId = Session.getUserId(); // current user
        String sql = "UPDATE room_members SET status = 'inactive' WHERE user_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Status updated to inactive.");
            } else {
                System.out.println("⚠️ No record found to update.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLeaveRoom(ActionEvent event) throws IOException {
        int currentUser = Session.getUserId();  // Get current user ID from session


                System.out.println("✅ User status set to inactive.");


                // Optionally navigate back to StartGame screen
                Parent root = FXMLLoader.load(getClass().getResource("/com/example/mysticmaze/fxmls/DashboardPage.fxml"));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Mystic Maze ");
                stage.show();




    }




    private Label createStyledLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 12px; -fx-text-fill: white; -fx-background-color: #333; -fx-padding: 0px; -fx-background-radius: 5;");
        return label;
    }

}

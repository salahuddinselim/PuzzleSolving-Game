package com.example.mysticmaze.utils;

import com.example.mysticmaze.models.Message;
import com.example.mysticmaze.models.Puzzle;
import com.example.mysticmaze.models.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class DBUtil {

    // Override with the MYSTICMAZE_DB_URL / _USER / _PASSWORD environment variables
    private static final String URL = env("MYSTICMAZE_DB_URL", "jdbc:mysql://localhost:3306/mysticmaze");
    private static final String USER = env("MYSTICMAZE_DB_USER", "root");
    private static final String PASSWORD = env("MYSTICMAZE_DB_PASSWORD", "");

    private static String env(String name, String fallback) {
        String value = System.getenv(name);
        return value != null ? value : fallback;
    }

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Ensure the driver is loaded
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void insertUser(User user) {
        String sql = "INSERT INTO users (username, email, password_hash, created_at) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPasswordHash());
            stmt.setTimestamp(4, Timestamp.valueOf(user.getCreatedAt()));

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean validateUser(String username, String rawPassword) {
        String query = "SELECT user_id, password_hash FROM users WHERE username = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHashedPassword = rs.getString("password_hash");
                String inputHashedPassword = hashPassword(rawPassword); // your hash function
                /*
                System.out.println("input :"+inputHashedPassword);
                System.out.println("stored :"+storedHashedPassword);
                */
                return storedHashedPassword.equals(inputHashedPassword);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String hashPassword(String password) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();

        for (byte b : hash) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();

    }

    public static boolean getCurrentUserRoom(int userId) throws SQLException {
        String sql = "SELECT rm.room_id FROM room_members rm " +
                "WHERE rm.user_id = ? AND rm.status = 'active'";

        try (PreparedStatement stmt = DBUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            //int id  = rs.next();  // true if at least one active room is found
            return  rs.next();
        }
    }


    // puzzle related
    public static Puzzle getTowerOfHanoiPuzzle() {
        String query = "SELECT * FROM puzzles WHERE type = 'visual'";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            ResultSet rs = stmt.executeQuery();
            //System.out.println(rs);

            if (rs.next()) {
                String puzzleData = rs.getString("puzzle_data");
                // level, type, puzzle_data,solution, minimum_moves, minimum_time_taken, difficulty

              //  System.out.println("id : "+rs.getInt("puzzle_id"));
               // System.out.println("level : "+rs.getInt("level"));
                //System.out.println("type "+  rs.getString("type"));
                //System.out.println(rs.getString("puzzle_data"));
                //System.out.println(rs.getString("solution"));
                //System.out.println(rs.getInt("minimum_moves"));
                //System.out.println(rs.getFloat("minimum_time_taken"));
                //System.out.println(rs.getString("difficulty"));

                return new Puzzle(
                        rs.getInt("puzzle_id"),
                        rs.getInt("level"),
                        rs.getString("type"),
                        rs.getString("puzzle_data"),
                        rs.getString("solution"),
                        rs.getInt("minimum_moves"),
                        rs.getInt("minimum_time_taken"),
                        rs.getString("difficulty")
                );
            }

        } catch (SQLException  e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isUserInActiveRoom(int userId) throws SQLException {
        String sql = "SELECT rm.room_id FROM room_members rm " +
                "JOIN rooms r ON rm.room_id = r.room_id " +
                "WHERE rm.user_id = ? AND rm.status = 'active'";

        try (PreparedStatement stmt = DBUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();  // true if at least one active room is found
        }
    }

     public static void insertRoomMessage(Message message) {
            String sql = "INSERT INTO messages (room_id, sender_id, message, type) VALUES (?, ?, ?, ?)";

            try (Connection conn = DBUtil.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                System.out.print(message.getRoomId());

                stmt.setInt(1, message.getRoomId());
                stmt.setInt(2, message.getSenderId());
                stmt.setString(3, message.getMessage());
                stmt.setString(4, message.getType());

                stmt.executeUpdate();
           } catch (SQLException e) {
                e.printStackTrace();
           }
        }



}

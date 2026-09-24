package com.example.mysticmaze.utils;

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
        String query = "SELECT password_hash FROM users WHERE username = ?";

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


}

package com.example.mysticmaze.utils;

import java.sql.*;

public class GameSessionDAO {
    public static void saveSession(int playerId, String puzzleName, int moves, int timeTaken) {
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "INSERT INTO game_sessions (player_id, puzzle_name, moves, time_seconds) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, playerId);
            stmt.setString(2, puzzleName);
            stmt.setInt(3, moves);
            stmt.setInt(4, timeTaken);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

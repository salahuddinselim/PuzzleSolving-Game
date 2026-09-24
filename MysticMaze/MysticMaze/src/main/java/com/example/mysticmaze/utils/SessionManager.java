package com.example.mysticmaze.utils;

import com.example.mysticmaze.network.GameClient;
import com.example.mysticmaze.models.User;

public class SessionManager {
    private static GameClient gameClient;
    private static User currentPlayer;

    public static void setGameClient(GameClient client) {
        gameClient = client;
    }

    public static GameClient getGameClient() {
        return gameClient;
    }

    public static void setCurrentPlayer(User player) {
        currentPlayer = player;
    }

    public static User getCurrentPlayer() {
        return currentPlayer;
    }
}

package com.example.mysticmaze.network;

import com.example.mysticmaze.models.Message;
import com.google.gson.Gson;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.*;
public class GameServer {
    private static final int PORT = 9999;
    private static final Map<String, Set<ClientHandler>> rooms = new ConcurrentHashMap<>();
    private static final ExecutorService pool = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("GameServer started on port " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected from IP: " + clientSocket.getInetAddress().getHostAddress());
                ClientHandler handler = new ClientHandler(clientSocket);
                pool.execute(handler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, Set<ClientHandler>> getRooms() {
        return rooms;
    }
}

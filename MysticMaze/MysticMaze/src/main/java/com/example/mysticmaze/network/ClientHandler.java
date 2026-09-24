package com.example.mysticmaze.network;

import com.example.mysticmaze.models.Message;
import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private int roomId;
    private String username;
    private  int user_id;

    private static final Gson gson = new Gson();
    private static final Map<String, Set<ClientHandler>> rooms = GameServer.getRooms();

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            String input;
            while ((input = in.readLine()) != null) {
                Message message = gson.fromJson(input, Message.class);

                switch (message.getType()) {
                    case "join":
                        roomId = message.getRoomId();
                        user_id = message.getSenderId();
                        rooms.putIfAbsent(String.valueOf((roomId)), ConcurrentHashMap.newKeySet());

                        if (rooms.get(roomId).size() < 3) {
                            rooms.get(roomId).add(this);
                            System.out.println(username + " joined room " + roomId);
                            broadcastToRoom(roomId, input, this);
                        } else {
                            out.println(gson.toJson(new Message(roomId, user_id,"", "error")));
                        }
                        break;

                    case "chat":
                    case "progress":
                    case "notify":
                        broadcastToRoom(roomId, input, this);
                        break;
                }
            }
        }
        catch (SocketException e) {
            System.out.println("Client disconnected: " + socket.getRemoteSocketAddress());
        }
        catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (roomId != 0 && rooms.containsKey(roomId)) {
                    rooms.get(roomId).remove(this);
                    broadcastToRoom(roomId, gson.toJson(new Message(roomId, user_id, username + " left the room","notify")), this);
                }
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void broadcastToRoom(int roomId, String message, ClientHandler exclude) {
        for (ClientHandler client : rooms.getOrDefault(roomId, Set.of())) {
            if (client != exclude) {
                client.out.println(message);
            }
        }
    }
}

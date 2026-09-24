package com.example.mysticmaze.network;

import java.io.*;
import java.net.Socket;
import java.util.function.Consumer;

public class GameClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Consumer<String> messageListener;

    public GameClient(Consumer<String> messageListener) {
        this.messageListener = messageListener;
    }

    public void connect(String serverAddress) throws IOException {
        socket = new Socket(serverAddress, 9999);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Start thread to listen for incoming messages
        new Thread(() -> {
            String response;
            try {
                while ((response = in.readLine()) != null) {
                    if (messageListener != null) {
                        messageListener.accept(response);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void send(String message) {
        out.println(message);
    }

    public void close() throws IOException {
        socket.close();
    }
}

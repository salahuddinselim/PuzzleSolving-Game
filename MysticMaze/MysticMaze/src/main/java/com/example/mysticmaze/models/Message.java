// File: Message.java
package com.example.mysticmaze.models;

public class Message {
    private int roomId;
    private int senderId;
    private String message;  // message text or progress info
    private  String type;
    public  Message(){};
    public Message(int roomId, int senderId, String message, String type) {
        this.roomId = roomId;
        this.senderId = senderId;
        this.message = message;
        this.type = type;
    }

    public int getRoomId() {
        return roomId;
    }

    public int getSenderId() {
        return senderId;
    }

    public String getMessage() {
        return message;
    }

    public String getType() {
        return type;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setType(String type) {
        this.type = type;
    }
}

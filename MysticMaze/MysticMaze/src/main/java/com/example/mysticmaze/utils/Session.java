// Session.java
package com.example.mysticmaze.utils;

public class Session {
    private static int userId;
    private static String userName;
    private static String userEmail;

    public static void setUserId(int id) {
        userId = id;
    }

    public static void setEmail(String email){
        userEmail = email;
    }

    public static void setUserName(String name){
        userName = name;
    }

    public static int getUserId() {
        return userId;
    }

    public static String getUserName() {
        return userName;
    }

    public static String getUserEmail() {
        return userEmail;
    }

    public static void clear() {
        userId = 0;
        userName = null;
        userEmail = null;
    }
}

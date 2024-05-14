package com.CAN301_ios.forum_server.entity;

import java.util.Date;

public class User {
    int userID;
    String username;
    String password;
    String token;
    String phone;
    String email;
    Date registerDate;
    String salt;

    public User() {

    }

    public User(int userID, String username, String password) {
        this.userID = userID;
        this.username = username;
        this.password = password;
    }
}

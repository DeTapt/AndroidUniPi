package com.androidunipi.data;

import java.io.Serializable;

/**
 * Created by Tasos on 25/10/2016.
 */

public class NotificationObj implements Serializable{

    private int id;
    private int userId;
    private String userName;
    private String surName;
    private String userPhoto;
    private String message;
    private String email;
    private boolean read;
    private String time;
    private String date;
    private boolean isChat;
    private String chat_token;

    public void setTime (String time) {this.time = time;}
    public String getTime() {return time;}

    public void setDate(String date) {this.date = date;}
    public String getDate() {return date;}

    public void setId(int id) {this.id = id;}
    public int getId() {return  id;}

    public void setUserId(int userId) {this.userId = userId;}
    public int getUserId() {return userId;}

    public void setUserName(String userName) {this.userName = userName;}
    public String getUserName() {return userName;}

    public void setSurName(String surName) {this.surName = surName;}
    public String getsurName() {return surName;}

    public void setUserPhoto(String userPhoto) {this.userPhoto = userPhoto;}
    public String getUserPhoto() {return userPhoto;}

    public void setMessage(String message) {this.message = message;}
    public String getMessage() {return message;}

    public void setEmail(String email) {this.email = email;}
    public String getEmail() {return email;}

    public void setRead(boolean read) {this.read = read;}
    public boolean isRead() {return read;}

    public void setChat(boolean isChat) {this.isChat = isChat;}
    public boolean isChat() {return isChat;}

    public void setChat_token(String chat_token) {this.chat_token = chat_token;}
    public String getChat_token() {return  chat_token;}

}

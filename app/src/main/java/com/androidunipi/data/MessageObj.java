package com.androidunipi.data;

/**
 * Created by Tasos on 10/11/2016.
 */

public class MessageObj {

    private String id;
    private String text;
    private String name;
    private String photoUrl;
    private String authIDs;

    public MessageObj() {
    }

    public MessageObj(String text, String name, String photoUrl, String authIDs) {
        this.text = text;
        this.name = name;
        this.photoUrl = photoUrl;
        this.authIDs = authIDs;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getAuthIDs() {return authIDs;}

    public void setAuthIDs(String authIDs) {this.authIDs = authIDs;}

}


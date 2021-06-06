package com.example.habitapp.models;

import java.util.Date;
import java.util.UUID;

public class FriendRequest {
    String ID;
    String requesterUserID;
    String targetUserID;


    public FriendRequest() {
    }

    public FriendRequest(String requesterUserID, String targetUserID) {
        this.requesterUserID = requesterUserID;
        this.targetUserID = targetUserID;
        ID = UUID.randomUUID().toString();
    }

    public String getRequesterUserID() {
        return requesterUserID;
    }

    public void setRequesterUserID(String requesterUserID) {
        this.requesterUserID = requesterUserID;
    }

    public String getTargetUserID() {
        return targetUserID;
    }

    public void setTargetUserID(String targetUserID) {
        this.targetUserID = targetUserID;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}

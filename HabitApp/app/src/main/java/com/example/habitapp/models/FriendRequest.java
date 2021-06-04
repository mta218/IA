package com.example.habitapp.models;

import java.util.Date;

public class FriendRequest {
    String requesterUserID;
    String targetUserID;
    //Date requestSent;


    public FriendRequest() {
    }

    public FriendRequest(String requesterUserID, String targetUserID) {
        this.requesterUserID = requesterUserID;
        this.targetUserID = targetUserID;
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
}

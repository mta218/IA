package com.example.habitapp.models;

import java.util.Date;
import java.util.UUID;

public class FriendRequest {
    String requesterUserID;
    String targetUserID;
    String requesterName;


    public FriendRequest() {
    }

    public FriendRequest(String requesterUserID, String targetUserID, String requesterName) {
        this.requesterUserID = requesterUserID;
        this.targetUserID = targetUserID;
        this.requesterName = requesterName;
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

    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }
}

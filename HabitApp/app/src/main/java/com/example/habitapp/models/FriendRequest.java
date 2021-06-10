package com.example.habitapp.models;

/**
 * TThis is the FriendRequest class, it stores information about a friend request sent from one user to another
 * and has functionality to manipulate the data.
 *
 * @author Maximilian Ta
 * @version 0.1
 */

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

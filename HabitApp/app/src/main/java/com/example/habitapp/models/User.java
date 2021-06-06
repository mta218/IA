package com.example.habitapp.models;

import java.util.ArrayList;

/**
 * This is the User class, it stores all information about each user
 *
 * @author Maximilian Ta
 * @version 0.1
 */

public class User {
    String username;
    String displayName;
    ArrayList<String> habits; //stores ids
    ArrayList<String> friends; //stores ids
    ArrayList<FriendRequest> friendRequests; //stores friendRequest objects
    Settings settings;
    String profilePictureID;

    public User(String displayName, String username) {
        this.displayName = displayName;
        this.username = username;
        settings = new Settings();
    }

    public User() {
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public ArrayList<String> getHabits() {
        return habits;
    }

    public void setHabits(ArrayList<String> habits) {
        this.habits = habits;
    }

    public ArrayList<String> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<String> friends) {
        this.friends = friends;
    }

    public ArrayList<FriendRequest> getFriendRequests() {
        return friendRequests;
    }

    public void setFriendRequests(ArrayList<FriendRequest> friendRequests) {
        this.friendRequests = friendRequests;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public String getProfilePictureID() {
        return profilePictureID;
    }

    public void setProfilePictureID(String profilePictureID) {
        this.profilePictureID = profilePictureID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

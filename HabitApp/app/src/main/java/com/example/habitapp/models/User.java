package com.example.habitapp.models;

import java.util.ArrayList;

public class User {
    String username;
    String displayName;
    ArrayList<String> habits; //stores ids
    ArrayList<String> friends;
    ArrayList<String> friendRequest;
    Settings settings;
}

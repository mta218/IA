package com.example.habitapp.models;

/**
 * This is the Settings class, it stores all information about each user's settings. Each user has an instance of a Setting class.
 *
 * @author Maximilian Ta
 * @version 0.1
 */

public class Settings {
    boolean darkMode;


    public boolean isDarkMode() {
        return darkMode;
    }

    public void setDarkMode(boolean darkMode) {
        this.darkMode = darkMode;
    }
}

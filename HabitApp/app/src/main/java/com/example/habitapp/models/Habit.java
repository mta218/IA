package com.example.habitapp.models;

import com.example.habitapp.enums.Frequency;
import com.example.habitapp.enums.Goal;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class Habit {
    String ID;
    String ownerID;
    String title;
    Frequency freq;
    int trackedCount;
    int goal;
    Date goalDate;
    Goal goalType;
    int streak;
    Date lastUpdated;
    ArrayList<String> tags;

    public Habit(String title, Frequency freq, int goal, Date goalDate, Goal goalType, ArrayList<String> tags, String ownerID) {
        ID = UUID.randomUUID().toString();
        this.title = title;
        this.freq = freq;
        this.goal = goal;
        this.goalDate = goalDate;
        this.goalType = goalType;
        this.tags = tags;
        lastUpdated = null;
        this.ownerID = ownerID;
    }

    public Habit(String ID, String title, Frequency freq, int goal, Date goalDate, Goal goalType, ArrayList<String> tags, String ownerID) {
        this.ID = ID;
        this.title = title;
        this.freq = freq;
        this.goal = goal;
        this.goalDate = goalDate;
        this.goalType = goalType;
        this.tags = tags;
        lastUpdated = new Date();
        this.ownerID = ownerID;
    }

    public Habit() {
    }

    public void addTag(String newTag){
        tags.add(newTag);
    }

    public boolean goalReached(){
        if(goalType == Goal.AMOUNT){
            return trackedCount == goal;
        }
        else if(goalType == Goal.STREAK){
            return streak == goal;
        }
        return false;
    }

    public void updateHabit(int addAmount){
        trackedCount += addAmount;
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR,0);
        today.set(Calendar.MINUTE,0);
        today.set(Calendar.SECOND,0);

        if(lastUpdated == null){
            streak++;
            lastUpdated = today.getTime();
            return;
        }

        int daysBetween = (int) ChronoUnit.DAYS.between(today.toInstant(), lastUpdated.toInstant());

        if(freq == Frequency.DAILY){
            if(daysBetween == 1) {
                streak++;
            }
        }
        else if(freq == Frequency.WEEKLY){
            if(!updatedThisWeek()) {
                streak++;
            }
        }
        else if(freq == Frequency.MONTHLY){
            if(!updatedThisMonth()) {
                streak++;
            }
        }

        lastUpdated = today.getTime();
    }

    private boolean updatedThisWeek(){
        //https://stackoverflow.com/questions/10313797/how-to-check-a-day-is-in-the-current-week-in-java
        Calendar today = Calendar.getInstance();
        Calendar lastUpdatedAsCal = Calendar.getInstance();
        lastUpdatedAsCal.setTime(lastUpdated);
        int week = today.get(Calendar.WEEK_OF_YEAR);
        int year = today.get(Calendar.YEAR);
        int targetWeek = lastUpdatedAsCal.get(Calendar.WEEK_OF_YEAR);
        int targetYear = lastUpdatedAsCal.get(Calendar.YEAR);
        return week == targetWeek && year == targetYear;
    }

    private boolean updatedThisMonth(){
        //https://stackoverflow.com/questions/10313797/how-to-check-a-day-is-in-the-current-week-in-java
        Calendar today = Calendar.getInstance();
        Calendar lastUpdatedAsCal = Calendar.getInstance();
        lastUpdatedAsCal.setTime(lastUpdated);
        int month = today.get(Calendar.MONTH);
        int year = today.get(Calendar.YEAR);
        int targetMonth = lastUpdatedAsCal.get(Calendar.MONTH);
        int targetYear = lastUpdatedAsCal.get(Calendar.YEAR);
        return month == targetMonth && year == targetYear;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Frequency getFreq() {
        return freq;
    }

    public void setFreq(Frequency freq) {
        this.freq = freq;
    }

    public int getTrackedCount() {
        return trackedCount;
    }

    public void setTrackedCount(int trackedCount) {
        this.trackedCount = trackedCount;
    }

    public int getGoal() {
        return goal;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }

    public Date getGoalDate() {
        return goalDate;
    }

    public void setGoalDate(Date goalDate) {
        this.goalDate = goalDate;
    }

    public Goal getGoalType() {
        return goalType;
    }

    public void setGoalType(Goal goalType) {
        this.goalType = goalType;
    }

    public int getStreak() {
        return streak;
    }

    public void setStreak(int streak) {
        this.streak = streak;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public String lastUpdatedString(){
        if(lastUpdated == null){
            return "Last updated today";
        }
        long days = Duration.between(lastUpdated.toInstant(), Calendar.getInstance().toInstant()).toDays();
        if(days == 0){
            return "Last updated today";
        }
        else if(days < 7){
            if(days == 1){
                return "Last updated a day ago";
            }
            return "Last updated " + days + " days ago";
        }
        else if(days <= 28){
            int weeks = (int) Math.round(((int)days)/7.0);
            if(weeks == 1){
                return "Last updated a week ago";
            }
            return "Last updated " + weeks + " weeks ago";
        }
        else if(days <= 31){
            return "Last updated a month ago";
        }
        else if(days < 365){
            return "Last updated " + Math.round(((int)days)/30.5) + " months ago";
        }
        else {
            int years = (int) Math.round(((int)days)/365);
            if(years == 1){
                return "Last updated a year ago";
            }
            return "Last updated " + years + " years ago";
        }
    }

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public Habit copy(){
        //https://www.codevscolor.com/java-copy-string
        return new Habit(String.copyValueOf(title.toCharArray()), freq, goal, (Date) goalDate.clone(), goalType, new ArrayList<>(tags), String.copyValueOf(ownerID.toCharArray()));
    }

    public String tagsAsString(){
        String temp = "";
        for(String tag : tags){
            temp += tag + ",";
        }
        return temp.substring(0,temp.length()-1);
    }

}
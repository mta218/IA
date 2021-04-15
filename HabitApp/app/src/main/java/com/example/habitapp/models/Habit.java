package com.example.habitapp.models;

import com.example.habitapp.enums.Frequency;
import com.example.habitapp.enums.Goal;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

public class Habit {
    String ID;
    String title;
    Frequency freq;
    int trackedCount;
    int goal;
    Calendar goalDate;
    Goal goalType;
    int streak;
    Calendar lastUpdated;
    ArrayList<String> tags;

    public Habit(String title, Frequency freq, int goal, Calendar goalDate, Goal goalType, ArrayList<String> tags) {
        ID = UUID.randomUUID().toString();
        this.title = title;
        this.freq = freq;
        this.goal = goal;
        this.goalDate = goalDate;
        this.goalType = goalType;
        this.tags = tags;
    }

    public void addTag(String newTag){
        tags.add(newTag);
    }

    public boolean isGoalReached(){
        if(goalType == Goal.AMOUNT){
            return trackedCount == goal;
        }
        else if(goalType == Goal.DATE){
            //https://stackoverflow.com/questions/5046771/how-to-get-todays-date
            Calendar today = Calendar.getInstance();
            return today.get(Calendar.YEAR) == goalDate.get(Calendar.YEAR) && today.get(Calendar.DATE) == goalDate.get(Calendar.DATE) &&  today.get(Calendar.MONTH) == goalDate.get(Calendar.MONTH);
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
    }

    private boolean updatedThisWeek(){
        //https://stackoverflow.com/questions/10313797/how-to-check-a-day-is-in-the-current-week-in-java
        Calendar today = Calendar.getInstance();
        int week = today.get(Calendar.WEEK_OF_YEAR);
        int year = today.get(Calendar.YEAR);
        int targetWeek = lastUpdated.get(Calendar.WEEK_OF_YEAR);
        int targetYear = lastUpdated.get(Calendar.YEAR);
        return week == targetWeek && year == targetYear;
    }

    private boolean updatedThisMonth(){
        //https://stackoverflow.com/questions/10313797/how-to-check-a-day-is-in-the-current-week-in-java
        Calendar today = Calendar.getInstance();
        int month = today.get(Calendar.MONTH);
        int year = today.get(Calendar.YEAR);
        int targetMonth = lastUpdated.get(Calendar.MONTH);
        int targetYear = lastUpdated.get(Calendar.YEAR);
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

    public Calendar getGoalDate() {
        return goalDate;
    }

    public void setGoalDate(Calendar goalDate) {
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

    public Calendar getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Calendar lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public String lastUpdatedString(){
        long days = Duration.between(lastUpdated.toInstant(), Calendar.getInstance().toInstant()).toDays();
        if(days < 7){
            return "Last updated " + days + " days ago";
        }
        else if(days < 14){
            return "Last updated a week ago";
        }
        else if(days <= 28){
            return "Last updated " + Math.round(((int)days)/7.0) + " weeks ago";
        }
        else if(days <= 31){
            return "Last updated a month ago";
        }
        else if(days < 365){
            return "Last updated " + Math.round(((int)days)/30.5) + " months ago";
        }
        else if(days < 547){
            return "Last updated a year ago";
        }
        else {
            return "Last updated " + Math.round(((int)days)/365) + " years ago";
        }
    }
}
package com.example.habitapp.models;

import com.example.habitapp.enums.Frequency;
import com.example.habitapp.enums.Goal;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * This is the habit class, it stores all information about a user's habit and has functionality to manipulate the data.
 *
 * @author Maximilian Ta
 * @version 0.1
 */

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
    boolean hidden;
    int encouragement;

    public Habit(String title, Frequency freq, int goal, Date goalDate, Goal goalType, ArrayList<String> tags, String ownerID, boolean hidden) {
        ID = UUID.randomUUID().toString();
        this.title = title;
        this.freq = freq;
        this.goal = goal;
        this.goalDate = goalDate;
        this.goalType = goalType;
        this.tags = tags;
        lastUpdated = null;
        this.ownerID = ownerID;
        this.hidden = hidden;
    }

    public Habit(String ID, String title, Frequency freq, int goal, Date goalDate, Goal goalType, ArrayList<String> tags, String ownerID, Date lastUpated, int streak, int trackedCount, boolean hidden, int encouragement) {
        this.ID = ID;
        this.title = title;
        this.freq = freq;
        this.goal = goal;
        this.goalDate = goalDate;
        this.goalType = goalType;
        this.tags = tags;
        this.ownerID = ownerID;

        this.lastUpdated = lastUpated;
        this.streak = streak;
        this.trackedCount = trackedCount;

        this.hidden = hidden;

        this.encouragement = encouragement;
    }

    public Habit() {
    }

    /**
     * adds a tag to the tags arraylist
     *
     * @param newTag a String containing the new tag
     */
    public void addTag(String newTag) {
        tags.add(newTag);
    }

    /**
     * returns true or false indicating whether the goal has been reached
     */
    public boolean goalReached() {
        if (goalType == Goal.AMOUNT) {
            return trackedCount == goal;
        } else if (goalType == Goal.DAILY_STREAK || goalType == Goal.MONTHLY_STREAK || goalType == Goal.WEEKLY_STREAK) {
            return streak == goal;
        }
        return false;
    }

    /**
     * adds a value to the trackedCount of the habit and checks for streaks
     *
     * @param addAmount an integer storing the amount to add to trackedCount
     */
    public void updateHabit(int addAmount) {
        trackedCount += addAmount;

        if (lastUpdated == null || streak == 0) {
            streak++;
        } else {
            updateStreak(true);
        }

        lastUpdated = new Date();
    }

    /**
     * checks the current date against the last updated date to see if the habit still maintains its streak,
     * updates the streak value accordingly
     */
    public void updateStreak(boolean increaseStreak) {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        boolean shouldIncrease = false;

        if (lastUpdated != null) {

            if (goalType == Goal.DAILY_STREAK) {
                if (updatedYesterday()) {
                    shouldIncrease = true;
                } else if (!updatedToday()) {
                    streak = 0;
                }
            } else if (goalType == Goal.WEEKLY_STREAK) {
                if (updatedLastWeek()) {
                    shouldIncrease = true;
                } else if (!updatedThisWeek()) {
                    streak = 0;
                }
            } else if (goalType == Goal.MONTHLY_STREAK) {
                if (updatedLastMonth()) {
                    shouldIncrease = true;
                } else if (!updatedThisMonth()) {
                    streak = 0;
                }
            }
        }

        if (shouldIncrease && increaseStreak) {
            streak++;
        }
    }

    private boolean updatedYesterday() {
        Calendar aWeekAgo = Calendar.getInstance();
        aWeekAgo.add(Calendar.DATE, -1);
        Calendar lastUpdatedAsCal = Calendar.getInstance();
        lastUpdatedAsCal.setTime(lastUpdated);
        int day = aWeekAgo.get(Calendar.DAY_OF_YEAR);
        int year = aWeekAgo.get(Calendar.YEAR);
        int targetDay = lastUpdatedAsCal.get(Calendar.DAY_OF_YEAR);
        int targetYear = lastUpdatedAsCal.get(Calendar.YEAR);
        return day == targetDay && year == targetYear;
    }

    private boolean updatedToday() {
        Calendar aWeekAgo = Calendar.getInstance();
        Calendar lastUpdatedAsCal = Calendar.getInstance();
        lastUpdatedAsCal.setTime(lastUpdated);
        int day = aWeekAgo.get(Calendar.DAY_OF_YEAR);
        int year = aWeekAgo.get(Calendar.YEAR);
        int targetDay = lastUpdatedAsCal.get(Calendar.DAY_OF_YEAR);
        int targetYear = lastUpdatedAsCal.get(Calendar.YEAR);
        return day == targetDay && year == targetYear;
    }

    /**
     * called by updateStreak returns if the habit was updated in the previous week
     */
    private boolean updatedLastWeek() {
        //https://stackoverflow.com/questions/10313797/how-to-check-a-day-is-in-the-current-week-in-java
        Calendar aWeekAgo = Calendar.getInstance();
        aWeekAgo.add(Calendar.DATE, -7);
        Calendar lastUpdatedAsCal = Calendar.getInstance();
        lastUpdatedAsCal.setTime(lastUpdated);
        int week = aWeekAgo.get(Calendar.WEEK_OF_YEAR);
        int year = aWeekAgo.get(Calendar.YEAR);
        int targetWeek = lastUpdatedAsCal.get(Calendar.WEEK_OF_YEAR);
        int targetYear = lastUpdatedAsCal.get(Calendar.YEAR);
        return week == targetWeek && year == targetYear;
    }

    /**
     * called by updateStreak returns if the habit was updated in the current week
     */
    private boolean updatedThisWeek() {
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

    /**
     * called by updateStreak returns if the habit was updated in the previous month
     */
    private boolean updatedLastMonth() {
        //https://stackoverflow.com/questions/10313797/how-to-check-a-day-is-in-the-current-week-in-java
        Calendar aMonthAgo = Calendar.getInstance();
        aMonthAgo.add(Calendar.MONTH, -1);
        Calendar lastUpdatedAsCal = Calendar.getInstance();
        lastUpdatedAsCal.setTime(lastUpdated);
        int month = aMonthAgo.get(Calendar.MONTH);
        int year = aMonthAgo.get(Calendar.YEAR);
        int targetMonth = lastUpdatedAsCal.get(Calendar.MONTH);
        int targetYear = lastUpdatedAsCal.get(Calendar.YEAR);
        return month == targetMonth && year == targetYear;
    }

    /**
     * called by updateStreak returns if the habit was updated in the current month
     */
    private boolean updatedThisMonth() {
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

    /**
     * returns a String stating when the habit was last updated
     */
    public String lastUpdatedString() {
        if (lastUpdated == null) {
            return "New Habit!";
        }
        long days = Duration.between(lastUpdated.toInstant(), Calendar.getInstance().toInstant()).toDays();
        if (days == 0) {
            return "Last updated today";
        } else if (days < 7) {
            if (days == 1) {
                return "Last updated a day ago";
            }
            return "Last updated " + days + " days ago";
        } else if (days <= 28) {
            int weeks = (int) Math.round(((int) days) / 7.0);
            if (weeks == 1) {
                return "Last updated a week ago";
            }
            return "Last updated " + weeks + " weeks ago";
        } else if (days <= 31) {
            return "Last updated a month ago";
        } else if (days < 365) {
            return "Last updated " + Math.round(((int) days) / 30.5) + " months ago";
        } else {
            int years = Math.round(((int) days) / 365);
            if (years == 1) {
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

    /**
     * Returns a copy of the habit
     */
    public Habit copy() {
        //https://www.codevscolor.com/java-copy-string
        return new Habit(String.copyValueOf(title.toCharArray()), freq, goal, (Date) goalDate.clone(), goalType, new ArrayList<>(tags), String.copyValueOf(ownerID.toCharArray()), hidden);
    }

    /**
     * Returns the tags as a single string separated by commas
     */
    public String tagsAsString() {
        String temp = "";
        for (String tag : tags) {
            temp += tag + ",";
        }
        return temp.substring(0, temp.length() - 1);
    }

    /**
     * returns the how much of the goal has been completed as a decimal or 1 if there is no goal.
     */
    public double percentage() {
        if (getGoalType() == Goal.AMOUNT) {
            return getTrackedCount() / ((double) getGoal());
        } else if (getGoalType() != Goal.NONE) {
            return getStreak() / ((double) getGoal());
        }

        return 1;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public int getEncouragement() {
        return encouragement;
    }

    public void setEncouragement(int encouragement) {
        this.encouragement = encouragement;
    }
}
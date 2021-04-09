package com.example.habitapp.models;

import com.example.habitapp.enums.Frequency;
import com.example.habitapp.enums.Goal;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;

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
}
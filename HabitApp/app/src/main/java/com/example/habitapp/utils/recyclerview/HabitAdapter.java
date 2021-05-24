package com.example.habitapp.utils.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitapp.models.Habit;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

import com.example.habitapp.R;

/**
 * The CarpoolAdapter is the adapter class for the CarpoolRecyclerView, it holds
 * the habits that the recycler view displays as well as some other functionality.
 *
 * @author Maximilian Ta
 * @version 0.1
 */
public class HabitAdapter extends RecyclerView.Adapter<HabitViewHolder> {
    ArrayList<Habit> habitsToDisplay;
    private OnHabitListener mOnHabitListener;
    String tag;

    public HabitAdapter(ArrayList<Habit> habitsToDisplay, OnHabitListener OnHabitListener) {
        this.habitsToDisplay = habitsToDisplay;
        this.mOnHabitListener = OnHabitListener;
    }

    public HabitAdapter(ArrayList<Habit> habitsToDisplay, OnHabitListener OnHabitListener, String tag) {
        this.habitsToDisplay = habitsToDisplay;
        this.mOnHabitListener = OnHabitListener;
        this.tag = tag;
    }

    @NonNull
    @Override
    public HabitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //create the view from XML file
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.habit_row_view, parent, false);

        //give the view to the view holder to hold
        HabitViewHolder holder = new HabitViewHolder(view, mOnHabitListener);

        if (tag != null) {
            holder.addTag(tag);
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull HabitViewHolder holder, int position) {
        //set the text of each view
        holder.titleText.setText(habitsToDisplay.get(position).getTitle());
        holder.updatedText.setText(habitsToDisplay.get(position).lastUpdatedString());
        //position is index

    }

    /**
     * returns the number of items being displayed in the recycler view
     *
     * @return int, number of items
     */
    @Override
    public int getItemCount() {
        return habitsToDisplay.size();
    }

    /**
     * An interface which specifies that all classes which use this class must have a certain behaviour.
     * onCarpoolClick takes in 2 parameters, position which is the position of the element that was clicked,
     * and tag which allows multiple recycler views to be differentiated in a single activity/fragment.
     */
    public interface OnHabitListener {
        void onHabitClick(int position, String tag);
    }

    /**
     * Returns the habit object at the position (index) entered
     *
     * @param position the index of the habit in the arraylist, which
     *                 is also the position at which it appears in the
     *                 recycler view.
     * @return the habit object at the position (index) entered
     */
    public Habit getHabit(int position) {
        return habitsToDisplay.get(position);
    }

    /**
     * adds habit to habitsToDisplay arrayList
     *
     * @param habitToAdd is the listing to be added
     */
    public void addHabit(Habit habitToAdd) {
        habitsToDisplay.add(habitToAdd);
    }

    /**
     * deletes habit from habitsToDisplay arrayList
     *
     * @param id a string representing the id of a habit to be removed
     */
    public void deleteHabit(String id) {
        for (int i = 0; i < habitsToDisplay.size(); i++) {
            if (habitsToDisplay.get(i).getID().equals(id)) {
                habitsToDisplay.remove(i);
                return;
            }
        }
    }

    /**
     * replaces the habitsToDisplay arraylist with a new one
     *
     * @param habitsToDisplay the new arraylist
     */
    public void setArrayList(ArrayList<Habit> habitsToDisplay) {
        this.habitsToDisplay = habitsToDisplay;
    }

    /**
     * Appends all Habits in an arraylist to the end of the habitsToDisplay
     * arraylist
     *
     * @param habitsToAdd Arraylist of habits to add
     */
    public void addHabits(ArrayList<Habit> habitsToAdd) {
        this.habitsToDisplay.addAll(habitsToAdd);
    }

    /**
     * clears vehiclesToDisplay
     */
    public void clearArrayList() {
        this.habitsToDisplay.clear();
    }

    public String getTag(){
        return tag;
    }

    public void sortAlphabetically(){
        habitsToDisplay.sort(new Comparator<Habit>() {
            @Override
            public int compare(Habit h1, Habit h2) {
                return h1.getTitle().compareTo(h2.getTitle());
            }
        });

    }
    public void sortUrgency(){
        habitsToDisplay.sort(new Comparator<Habit>() {
            @Override
            public int compare(Habit h1, Habit h2) {
                //difference in days to goal date
                Calendar today = Calendar.getInstance();

                if(h1.getGoalDate() == null && h1.getGoalDate() == null){
                    return 0;
                }
                else if(h1.getGoalDate() != null && h1.getGoalDate() == null){
                    return 1;
                }
                else if(h1.getGoalDate() == null && h1.getGoalDate() != null){
                    return -1;
                }

                int daysBetween1 = (int) ChronoUnit.DAYS.between(today.toInstant(), h1.getGoalDate().toInstant());
                int daysBetween2 = (int) ChronoUnit.DAYS.between(today.toInstant(), h2.getGoalDate().toInstant());

                return daysBetween1 - daysBetween2;

            }
        });
    }
    public void sortGoal(){
        habitsToDisplay.sort(new Comparator<Habit>() {
            @Override
            public int compare(Habit h1, Habit h2) {
                //difference in percentage
                return (int) Math.round(h1.percentage() - h2.percentage());
            }
        });
    }

    public void sortLastUpdated(){

    }
}







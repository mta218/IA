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
import com.example.habitapp.models.User;

/**
 * The UserAdapter is the adapter class for the UserRecyclerView, it holds
 * the users that the recycler view displays as well as some other functionality.
 *
 * @author Maximilian Ta
 * @version 0.1
 */
public class UserAdapter extends RecyclerView.Adapter<UserViewHolder> {
    ArrayList<User> usersToDisplay;
    private OnUserListener mOnUserListener;
    String tag;

    public UserAdapter(ArrayList<User> usersToDisplay, OnUserListener onUserListener) {
        this.usersToDisplay = usersToDisplay;
        this.mOnUserListener = onUserListener;
    }

    public UserAdapter(ArrayList<User> usersToDisplay, OnUserListener onUserListener, String tag) {
        this.usersToDisplay = usersToDisplay;
        this.mOnUserListener = onUserListener;
        this.tag = tag;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //create the view from XML file
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_row_view, parent, false);

        //give the view to the view holder to hold
        UserViewHolder holder = new UserViewHolder(view, mOnUserListener);

        if (tag != null) {
            holder.addTag(tag);
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.displaynameText.setText(usersToDisplay.get(position).getDisplayName());
        holder.usernameText.setText(usersToDisplay.get(position).getUsername());
    }


    /**
     * returns the number of items being displayed in the recycler view
     *
     * @return int, number of items
     */
    @Override
    public int getItemCount() {
        return usersToDisplay.size();
    }

    /**
     * An interface which specifies that all classes which use this class must have a certain behaviour.
     * onCarpoolClick takes in 2 parameters, position which is the position of the element that was clicked,
     * and tag which allows multiple recycler views to be differentiated in a single activity/fragment.
     */
    public interface OnUserListener {
        void onUserClick(int position, String tag);
    }

    /**
     * Returns the user object at the position (index) entered
     *
     * @param position the index of the user in the arraylist, which
     *                 is also the position at which it appears in the
     *                 recycler view.
     * @return the user object at the position (index) entered
     */
    public User getUser(int position) {
        return usersToDisplay.get(position);
    }

    /**
     * adds user to usersToDisplay arrayList
     *
     * @param userToAdd is the user to be added
     */
    public void addUser(User userToAdd) {
        usersToDisplay.add(userToAdd);
    }

    /**
     * deletes user from usersToDisplay arrayList
     *
     * @param username a string representing the username of a user to be removed
     */
    public void deleteUser(String username) {
        for (int i = 0; i < usersToDisplay.size(); i++) {
            if (usersToDisplay.get(i).getUsername().equals(username)) {
                usersToDisplay.remove(i);
                return;
            }
        }
    }

    /**
     * replaces the usersToDisplay arraylist with a new one
     *
     * @param usersToDisplay the new arraylist
     */
    public void setArrayList(ArrayList<User> usersToDisplay) {
        this.usersToDisplay = usersToDisplay;
    }

    /**
     * Appends all Users in an arraylist to the end of the usersToDisplay
     * arraylist
     *
     * @param usersToAdd Arraylist of Users to add
     */
    public void addUser(ArrayList<User> usersToAdd) {
        this.usersToDisplay.addAll(usersToAdd);
    }

    /**
     * clears usersToDisplay
     */
    public void clearArrayList() {
        this.usersToDisplay.clear();
    }


    public String getTag(){
        return tag;
    }

    /**
     * Sorts the usersToDisplay in alphabetical order of display name of users
     *
     */
    public void sortAlphabetically(){
        usersToDisplay.sort(new Comparator<User>() {
            @Override
            public int compare(User u1, User u2) {
                return u1.getDisplayName().compareTo(u2.getDisplayName());
            }
        });

    }

}







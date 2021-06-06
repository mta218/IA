package com.example.habitapp.utils.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitapp.models.FriendRequest;
import com.example.habitapp.models.Habit;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

import com.example.habitapp.R;

/**
 * The FriendRequestAdapter is the adapter class for the FriendRequestRecyclerView, it holds
 * the requests that the recycler view displays as well as some other functionality.
 *
 * @author Maximilian Ta
 * @version 0.1
 */
public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestViewHolder> {
    ArrayList<FriendRequest> reqsToDisplay;
    private OnFriendRequestListener mOnFriendRequestListener;
    String tag;

    public FriendRequestAdapter(ArrayList<FriendRequest> reqsToDisplay, OnFriendRequestListener OnFriendRequestListener) {
        this.reqsToDisplay = reqsToDisplay;
        this.mOnFriendRequestListener = OnFriendRequestListener;
    }

    public FriendRequestAdapter(ArrayList<FriendRequest> reqsToDisplay, OnFriendRequestListener OnFriendRequestListener, String tag) {
        this.reqsToDisplay = reqsToDisplay;
        this.mOnFriendRequestListener = OnFriendRequestListener;
        this.tag = tag;
    }

    @NonNull
    @Override
    public FriendRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //create the view from XML file
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_request_row_view, parent, false);

        //give the view to the view holder to hold
        FriendRequestViewHolder holder = new FriendRequestViewHolder(view, mOnFriendRequestListener);

        if (tag != null) {
            holder.addTag(tag);
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull FriendRequestViewHolder holder, int position) {
        //set the text of each view
        holder.titleText.setText(reqsToDisplay.get(position).getRequesterName() + " Wants to Add You as Friend");
    }

    /**
     * returns the number of items being displayed in the recycler view
     *
     * @return int, number of items
     */
    @Override
    public int getItemCount() {
        return reqsToDisplay.size();
    }

    /**
     * An interface which specifies that all classes which use this class must have a certain behaviour.
     * onCarpoolClick takes in 2 parameters, position which is the position of the element that was clicked,
     * and tag which allows multiple recycler views to be differentiated in a single activity/fragment.
     */
    public interface OnFriendRequestListener {
        void onFriendRequestClick(int position, String tag);
    }

    /**
     * Returns the habit object at the position (index) entered
     *
     * @param position the index of the habit in the arraylist, which
     *                 is also the position at which it appears in the
     *                 recycler view.
     * @return the habit object at the position (index) entered
     */
    public FriendRequest getFriendRequest(int position) {
        return reqsToDisplay.get(position);
    }

    /**
     * adds habit to habitsToDisplay arrayList
     *
     * @param friendRequestToAdd is the friendRequest to be added
     */
    public void addFriendRequest(FriendRequest friendRequestToAdd) {
        reqsToDisplay.add(friendRequestToAdd);
    }


    /**
     * replaces the reqsToDisplay arraylist with a new one
     *
     * @param reqsToDisplay the new arraylist
     */
    public void setArrayList(ArrayList<FriendRequest> reqsToDisplay) {
        this.reqsToDisplay = reqsToDisplay;
    }

    /**
     * Appends all Habits in an arraylist to the end of the habitsToDisplay
     * arraylist
     *
     * @param friendRequestsToAdd Arraylist of friend requests to add
     */
    public void addHabits(ArrayList<FriendRequest> friendRequestsToAdd) {
        this.reqsToDisplay.addAll(friendRequestsToAdd);
    }

    /**
     * clears habitsToDisplay
     */
    public void clearArrayList() {
        this.reqsToDisplay.clear();
    }


    public String getTag(){
        return tag;
    }

    /**
     * Removes friend request from reqsToDisplay at specified position
     *
     * @param position
     */
    public void removeFriendRequest(int position){
        reqsToDisplay.remove(position);
    }


}







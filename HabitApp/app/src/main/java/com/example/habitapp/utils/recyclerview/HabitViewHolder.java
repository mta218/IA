package com.example.habitapp.utils.recyclerview;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitapp.R;

/**
 * The HabitViewHolder is the ViewHolder class for the HabitRecyclerView, it represents
 * a single item of the HabitRecyclerView, displaying information on one habit.
 *
 * @author Maximilian Ta
 * @version 0.1
 */
public class HabitViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    protected TextView titleText, updatedText;
    HabitAdapter.OnHabitListener onHabitListener;
    FriendHabitAdapter.OnHabitListener onFriendHabitListener;
    String tag;
    boolean friend;


    public HabitViewHolder(@NonNull View itemView, HabitAdapter.OnHabitListener onHabitListener) {
        super(itemView);

        titleText = (TextView) itemView.findViewById(R.id.titleText);
        updatedText = (TextView) itemView.findViewById(R.id.updatedText);
        this.onHabitListener = onHabitListener;

        itemView.setOnClickListener(this);
        friend = false;
    }

    public HabitViewHolder(@NonNull View itemView, FriendHabitAdapter.OnHabitListener onFriendHabitListener) {
        super(itemView);

        titleText = (TextView) itemView.findViewById(R.id.titleText);
        updatedText = (TextView) itemView.findViewById(R.id.updatedText);
        this.onFriendHabitListener = onFriendHabitListener;

        friend = true;

        itemView.setOnClickListener(this);
    }

    @Override
    /**
     * onClick will run when the view holder
     * is clicked by the user.
     */
    public void onClick(View view) {
        if(friend){
            onFriendHabitListener.onHabitClick(getAdapterPosition(), tag);
        }
        else{
            onHabitListener.onHabitClick(getAdapterPosition(), tag);
        }

    }


    /**
     * addTag is a setter for the string tag
     *
     * @param tag the new tag
     */
    public void addTag(String tag){
        this.tag = tag;
    }
}


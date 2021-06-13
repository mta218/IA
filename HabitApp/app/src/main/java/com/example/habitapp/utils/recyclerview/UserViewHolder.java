package com.example.habitapp.utils.recyclerview;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitapp.R;

/**
 * The UserViewHolder is the ViewHolder class for the UserRecyclerView, it represents
 * a single item of the UserRecyclerView, displaying information on one User.
 *
 * @author Maximilian Ta
 * @version 0.1
 */
public class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    protected TextView usernameText, displaynameText;
    UserAdapter.OnUserListener onUserListener;
    String tag;


    public UserViewHolder(@NonNull View itemView, UserAdapter.OnUserListener onUserListener) {
        super(itemView);

        displaynameText = (TextView) itemView.findViewById(R.id.displaynameText);
        usernameText = (TextView) itemView.findViewById(R.id.usernameText);
        this.onUserListener = onUserListener;

        itemView.setOnClickListener(this);
    }


    @Override
    /**
     * onClick will run when the view holder
     * is clicked by the user.
     */
    public void onClick(View view) {
        onUserListener.onUserClick(getAdapterPosition(), tag);

    }


    /**
     * addTag is a setter for the string tag
     *
     * @param tag the new tag
     */
    public void addTag(String tag) {
        this.tag = tag;
    }
}


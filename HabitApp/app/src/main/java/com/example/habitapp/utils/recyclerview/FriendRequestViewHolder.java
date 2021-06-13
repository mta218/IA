package com.example.habitapp.utils.recyclerview;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitapp.R;

/**
 * The FriendRequestViewHolder is the ViewHolder class for the FriendRequestRecyclerView, it represents
 * a single item of the recyclerView, displaying one friend request.
 *
 * @author Maximilian Ta
 * @version 0.1
 */
public class FriendRequestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    protected TextView titleText, clickText;
    FriendRequestAdapter.OnFriendRequestListener onFriendRequestListener;
    String tag;

    public FriendRequestViewHolder(@NonNull View itemView, FriendRequestAdapter.OnFriendRequestListener onFriendRequestListener) {
        super(itemView);

        titleText = (TextView) itemView.findViewById(R.id.titleText);
        clickText = (TextView) itemView.findViewById(R.id.clickText);
        this.onFriendRequestListener = onFriendRequestListener;

        itemView.setOnClickListener(this);
    }

    @Override
    /**
     * onClick will run when the view holder
     * is clicked by the user.
     */
    public void onClick(View view) {
        onFriendRequestListener.onFriendRequestClick(getAdapterPosition(), tag);
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


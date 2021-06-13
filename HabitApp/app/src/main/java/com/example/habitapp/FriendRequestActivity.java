package com.example.habitapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.habitapp.models.FriendRequest;
import com.example.habitapp.models.Habit;
import com.example.habitapp.models.User;
import com.example.habitapp.utils.HabitConstants;
import com.example.habitapp.utils.recyclerview.FriendRequestAdapter;
import com.example.habitapp.utils.recyclerview.HabitAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * FriendRequestActivity is where users can view incoming friend requests in a recycler view.
 *
 * @author Maximilian Ta
 * @version 0.1
 */

public class FriendRequestActivity extends AppCompatActivity implements FriendRequestAdapter.OnFriendRequestListener {

    RecyclerView requestRecyclerView;
    TextView emptyText;
    FirebaseFirestore fRef;
    FirebaseAuth mAuth;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_request);

        requestRecyclerView = findViewById(R.id.requestRecyclerView);
        FriendRequestAdapter adapter = new FriendRequestAdapter(new ArrayList<FriendRequest>(), this);
        requestRecyclerView.setAdapter(adapter);
        requestRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        emptyText = findViewById(R.id.emptyText);

        fRef = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onResume() {
        super.onResume();

        refresh();

    }

    /**
     * Accepts the friend request clicked, called when the recycler view is clicked
     *
     * @param position the position of the request in the recycler view
     * @param tag      unused
     */
    @Override
    public void onFriendRequestClick(final int position, String tag) {
        final FriendRequest friendRequest = ((FriendRequestAdapter) requestRecyclerView.getAdapter()).getFriendRequest(position);

        fRef.collection(HabitConstants.USER_PATH).document(friendRequest.getRequesterUserID()).update("friends", FieldValue.arrayUnion(friendRequest.getTargetUserID())).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Failed:\n" + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                fRef.collection(HabitConstants.USER_PATH).document(friendRequest.getTargetUserID()).update("friends", FieldValue.arrayUnion(friendRequest.getRequesterUserID())).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Failed:\n" + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        fRef.collection(HabitConstants.USER_PATH).document(friendRequest.getTargetUserID()).update("friendRequests", FieldValue.arrayRemove(friendRequest)).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Failed:\n" + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                FriendRequestAdapter adapter = (FriendRequestAdapter) requestRecyclerView.getAdapter();
                                adapter.removeFriendRequest(position);
                                adapter.notifyDataSetChanged();


                                Toast.makeText(getApplicationContext(), "Successfully Added " + friendRequest.getRequesterName() + " as Friend",
                                        Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    /**
     * Updates the recycler view with the latest requests stored on Firebase
     */
    private void refresh() {
        emptyText.setVisibility(View.GONE);
        ((FriendRequestAdapter) requestRecyclerView.getAdapter()).clearArrayList();
        fRef.collection(HabitConstants.USER_PATH).document(mAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    FriendRequestAdapter adapter = (FriendRequestAdapter) requestRecyclerView.getAdapter();
                    DocumentSnapshot document = task.getResult();
                    user = document.toObject(User.class);
                    if (user.getFriendRequests() != null) {
                        for (FriendRequest req : user.getFriendRequests()) {
                            adapter.addFriendRequest(req);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        emptyText.setVisibility(View.VISIBLE);
                    }


                } else {
                    Toast.makeText(getContext(), "Failed:\n Could not update properly",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private Context getContext() {
        return this;
    }

}
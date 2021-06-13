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

import com.example.habitapp.models.Habit;
import com.example.habitapp.models.User;
import com.example.habitapp.utils.HabitConstants;
import com.example.habitapp.utils.recyclerview.HabitAdapter;
import com.example.habitapp.utils.recyclerview.UserAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * The FriendActivity class displays a recycler view of all of the current user's friends
 *
 * @author Maximilian Ta
 * @version 0.1
 */

public class FriendActivity extends AppCompatActivity implements UserAdapter.OnUserListener {

    FirebaseFirestore fRef;
    FirebaseAuth mAuth;

    TextView emptyText;

    RecyclerView friendRecyclerView;

    ArrayList<User> userArrayList;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        fRef = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        emptyText = findViewById(R.id.emptyText);

        friendRecyclerView = findViewById(R.id.friendRecyclerView);
        UserAdapter adapter = new UserAdapter(new ArrayList<User>(), this);
        friendRecyclerView.setAdapter(adapter);
        friendRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        userArrayList = new ArrayList<>();

        refresh();
    }

    /**
     * Updates the recycler view with the latest habit information stored on Firebase
     */
    private void refresh() {
        emptyText.setVisibility(View.GONE);
        fRef.collection(HabitConstants.USER_PATH).document(mAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    user = task.getResult().toObject(User.class);
                    for (String friendID : user.getFriends()) {
                        fRef.collection(HabitConstants.USER_PATH).document(friendID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    addPending(task.getResult().toObject(User.class));
                                } else {
                                    Toast.makeText(getContext(), "Some friends could not be loaded, try again later",
                                            Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                    }
                } else {
                    Toast.makeText(getContext(), "Failed:\n Could not update properly",
                            Toast.LENGTH_SHORT).show();
                    emptyText.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * Opens UserProfileActivity for the user clicked in the recycler view, called when the recycler view is clicked
     *
     * @param position the position of the user in the recycler view
     * @param tag      unused
     */
    @Override
    public void onUserClick(int position, String tag) {
        Intent intent = new Intent(this, UserProfileActivity.class);
        User user = ((UserAdapter) friendRecyclerView.getAdapter()).getUser(position);
        intent.putExtra(HabitConstants.USERNAME_INTENT, user.getUsername());
        startActivity(intent);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private Context getContext() {
        return this;
    }

    /**
     * While the users are being loaded from Firebase, since they are loaded asynchronously, sorting must occur after they all are loaded.
     * addPending will add the users into a temporary ArrayList, sorting them and displaying them to the Recycler once all habits have been loaded.
     *
     * @param userToAdd The user to be added to the recycler view
     */
    void addPending(User userToAdd) {
        userArrayList.add(userToAdd);
        if (userArrayList.size() == user.getFriends().size()) {
            UserAdapter adapter = ((UserAdapter) friendRecyclerView.getAdapter());
            adapter.addUser(userArrayList);
            adapter.sortAlphabetically();
            adapter.notifyDataSetChanged();
        }
    }
}
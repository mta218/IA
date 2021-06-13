package com.example.habitapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.habitapp.models.FriendRequest;
import com.example.habitapp.models.Habit;
import com.example.habitapp.models.User;
import com.example.habitapp.utils.HabitConstants;
import com.example.habitapp.utils.recyclerview.HabitAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * UserProfileActivity displays all information about a user (that is not the current user)
 *
 * @author Maximilian Ta
 * @version 0.1
 */


public class UserProfileActivity extends AppCompatActivity implements HabitAdapter.OnHabitListener, AdapterView.OnItemSelectedListener {
    User user;
    String username;
    FirebaseAuth mAuth;
    FirebaseFirestore fRef;
    String userID;

    TextView usernameText, nameText, emptyText, friendText, recyclerViewTitleText;
    RecyclerView habitRecyclerView;
    ArrayList<Habit> allPendingArrayList;

    Button addFriendButton;

    Spinner sorterSpinner;

    int sorted = 0;
    int retrievedCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        username = getIntent().getStringExtra(HabitConstants.USERNAME_INTENT);
        usernameText = findViewById(R.id.usernameText);
        nameText = findViewById(R.id.nameText);
        habitRecyclerView = findViewById(R.id.habitRecyclerView);
        HabitAdapter adapter1 = new HabitAdapter(new ArrayList<Habit>(), this, HabitConstants.ALL_HABIT_RECYCLER_VIEW);
        habitRecyclerView.setAdapter(adapter1);
        habitRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        emptyText = findViewById(R.id.emptyText);
        friendText = findViewById(R.id.friendText);
        addFriendButton = findViewById(R.id.addFriendButton);
        recyclerViewTitleText = findViewById(R.id.recyclerViewTitleText);

        addFriendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                addFriend();
            }
        });

        allPendingArrayList = new ArrayList<>();

        sorterSpinner = findViewById(R.id.sorterSpinner);
        ArrayAdapter<CharSequence> sorterAdapter = ArrayAdapter.createFromResource(this.getContext(), R.array.sorting_types, R.layout.spinner_item);
        sorterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sorterSpinner.setAdapter(sorterAdapter);
        sorterSpinner.setOnItemSelectedListener(this);
        mAuth = FirebaseAuth.getInstance();

        fRef = FirebaseFirestore.getInstance();

        fRef.collection(HabitConstants.USER_PATH).whereEqualTo("username", username).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    user = null;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        user = document.toObject(User.class);
                        userID = document.getId();
                        break;
                    }

                    if (user != null) {
                        usernameText.setText(user.getUsername());
                        nameText.setText(user.getDisplayName());
                        if (user.getFriends() != null && user.getFriends().contains(mAuth.getUid())) {
                            friendText.setVisibility(View.VISIBLE);
                            refresh();
                        } else {
                            addFriendButton.setVisibility(View.VISIBLE);
                            habitRecyclerView.setVisibility(View.GONE);
                            recyclerViewTitleText.setVisibility(View.GONE);
                            sorterSpinner.setVisibility(View.GONE);
                        }


                    } else {
                        Toast.makeText(getActivity(), "User was not found.",
                                Toast.LENGTH_SHORT).show();
                        emptyText.setVisibility(View.VISIBLE);
                    }


                } else {
                    Toast.makeText(getActivity(), "Data could not be loaded, try again later.",
                            Toast.LENGTH_SHORT).show();
                    emptyText.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private Context getActivity() {
        return this;
    }

    /**
     * Updates the recycler view with the latest habit information stored on Firebase
     */
    private void refresh() {
        retrievedCount = 0;
        emptyText.setVisibility(View.GONE);
        ((HabitAdapter) habitRecyclerView.getAdapter()).clearArrayList();
        allPendingArrayList.clear();
        if (user.getHabits() != null) {
            for (String habitID : user.getHabits()) {
                fRef.collection(HabitConstants.HABIT_PATH)
                        .document(habitID)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    Habit habit = document.toObject(Habit.class);
                                    retrievedCount++;
                                    addPending(HabitConstants.ALL_HABIT_RECYCLER_VIEW, habit);


                                } else {
                                    Toast.makeText(getContext(), "Failed:\n Could not update properly",
                                            Toast.LENGTH_SHORT).show();
                                    emptyText.setVisibility(View.VISIBLE);
                                }
                            }
                        });
            }
        } else {
            emptyText.setVisibility(View.VISIBLE);
        }

    }

    private Context getContext() {
        return this;
    }

    /**
     * While the habits are being loaded from Firebase, since they are loaded asynchronously, sorting must occur after they all are loaded.
     * addPending will add the habits into a temporary ArrayList, sorting them and displaying them to the Recycler once all habits have been loaded.
     *
     * @param tag        A String representing the tag of the ArrayList that the incoming habits will be added to
     * @param habitToAdd The habit to be added to the recycler view
     */
    void addPending(String tag, Habit habitToAdd) {
        if (tag.equals(HabitConstants.ALL_HABIT_RECYCLER_VIEW)) {
            if (!habitToAdd.isHidden()) {
                allPendingArrayList.add(habitToAdd);
            }
            if (retrievedCount == user.getHabits().size()) {
                if (allPendingArrayList.size() == 0) {
                    emptyText.setVisibility(View.VISIBLE);
                } else {
                    HabitAdapter habitAdapter = ((HabitAdapter) habitRecyclerView.getAdapter());
                    habitAdapter.addHabits(allPendingArrayList);
                    switch (sorted) {
                        case 0: //Urgency
                            habitAdapter.sortUrgency();
                            break;
                        case 1: //Name
                            habitAdapter.sortAlphabetically();
                            break;
                        case 2: //Goal (percentage)
                            habitAdapter.sortGoal();
                            break;
                        case 3: //Last Updated
                            habitAdapter.sortLastUpdated();
                            break;
                    }
                    habitAdapter.notifyDataSetChanged();
                }

            }
        }
    }

    /**
     * This is the method called when a spinner is clicked. It will change the method of sorting of the Recycler view.
     *
     * @param adapterView
     * @param view
     * @param i
     * @param l
     */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        HabitAdapter habitAdapter = ((HabitAdapter) habitRecyclerView.getAdapter());
        sorted = i;

        switch (i) {
            case 0: //Urgency
                habitAdapter.sortUrgency();
                break;
            case 1: //Name
                habitAdapter.sortAlphabetically();
                break;
            case 2: //Goal (percentage)
                habitAdapter.sortGoal();
                break;
            case 3: //Last Updated
                habitAdapter.sortLastUpdated();
                break;
        }

        habitAdapter.notifyDataSetChanged();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    /**
     * Opens HabitProfileActivity for the habit clicked in the recycler view, called when the recycler view is clicked
     *
     * @param position the position of the habit in the recycler view
     * @param tag      unused
     */
    @Override
    public void onHabitClick(int position, String tag) {
        Intent intent = new Intent(this.getActivity(), HabitProfileActivity.class);
        Habit habit = null;
        if (tag.equals(HabitConstants.ALL_HABIT_RECYCLER_VIEW)) {
            habit = ((HabitAdapter) habitRecyclerView.getAdapter()).getHabit(position);
        }

        intent.putExtra(HabitConstants.HABIT_ID_INTENT, habit.getID());
        startActivity(intent);
    }

    /**
     * If the displayed user is currently not friends with the current user, the add friend button will be visible.
     * This method is called when the button is pressed, and will send a friend request to the displayed user.
     */
    private void addFriend() {
        fRef.collection(HabitConstants.USER_PATH).document(mAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    user = document.toObject(User.class);

                    FriendRequest req = new FriendRequest(mAuth.getUid(), userID, user.getDisplayName());
                    fRef.collection(HabitConstants.USER_PATH).document(userID).update("friendRequests", FieldValue.arrayUnion(req)).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Failed:\n" + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(), "Request Successfully Sent",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });


                } else {
                    Toast.makeText(getContext(), "Request could not be sent",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}
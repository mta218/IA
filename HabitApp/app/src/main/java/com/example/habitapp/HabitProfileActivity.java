package com.example.habitapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.habitapp.models.Habit;
import com.example.habitapp.utils.HabitConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class HabitProfileActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseFirestore fRef;
    boolean isOwner;
    String habitID;
    Habit habit;
    String ownerID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_profile);

        mAuth = FirebaseAuth.getInstance();
        ownerID = mAuth.getUid();

        habitID = getIntent().getStringExtra(HabitConstants.HABIT_ID_INTENT);

        fRef = FirebaseFirestore.getInstance();

        fRef.collection(HabitConstants.HABIT_PATH).document(habitID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    habit = task.getResult().toObject(Habit.class);

                    update();
                }
            }

        });
    }

    void update(){

    }
}
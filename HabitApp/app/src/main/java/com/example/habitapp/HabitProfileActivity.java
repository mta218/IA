package com.example.habitapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.habitapp.enums.Goal;
import com.example.habitapp.models.Habit;
import com.example.habitapp.utils.HabitConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.Duration;
import java.util.Calendar;

public class HabitProfileActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseFirestore fRef;
    boolean isOwner;
    String habitID;
    Habit habit;
    String ownerID;
    Button updateButton, editButton, copyButton;
    ProgressBar progressBar;
    TextView titleText, progressText, lastUpdatedText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_profile);

        mAuth = FirebaseAuth.getInstance();
        ownerID = mAuth.getUid();

        habitID = getIntent().getStringExtra(HabitConstants.HABIT_ID_INTENT);
        progressBar = findViewById(R.id.progressBar);
        titleText = findViewById(R.id.titleText);
        progressText = findViewById(R.id.progressText);
        lastUpdatedText = findViewById(R.id.lastUpdatedText);

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

    void update() {
        titleText.setText(habit.getTitle());
        isOwner = false;
        if(habit.getOwnerID().equals(mAuth.getUid())){
            isOwner = true;
        }

        if(habit.getGoalType() != Goal.NONE && isOwner){
            lastUpdatedText.setText(habit.lastUpdatedString()+"\nKeep updating this habit to reach your goal!");
        }
        else{
            lastUpdatedText.setText(habit.lastUpdatedString());
        }


        int percentage = 0;

        if (habit.getGoalType() == Goal.AMOUNT) {
            percentage = (int) Math.round((100.0 * habit.getTrackedCount()) / habit.getGoal());
        } else if (habit.getGoalType() == Goal.STREAK) {
            percentage = (int) Math.round((100.0 * habit.getStreak()) / habit.getGoal());
        }

        //TODO: add thing for date / no date

        if(isOwner){
            progressText.setText("You have reached " + percentage + "% of your goal");
        }
        else{   //TODO: make this change for not same user
            progressText.setText("You have reached " + percentage + "% of your goal");
        }


        progressBar.setProgress(percentage); //percentage

    }
}
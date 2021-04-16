package com.example.habitapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.habitapp.enums.Goal;
import com.example.habitapp.models.Habit;
import com.example.habitapp.utils.HabitConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
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
    EditText updateInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_profile);

        mAuth = FirebaseAuth.getInstance();
        ownerID = mAuth.getUid();
        updateInput = findViewById(R.id.updateInput);
        updateButton = findViewById(R.id.updateButton);

        progressBar = findViewById(R.id.progressBar);
        titleText = findViewById(R.id.titleText);
        progressText = findViewById(R.id.progressText);
        lastUpdatedText = findViewById(R.id.lastUpdatedText);

        fRef = FirebaseFirestore.getInstance();

        updateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                updateHabit();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fRef = FirebaseFirestore.getInstance();
        habitID = getIntent().getStringExtra(HabitConstants.HABIT_ID_INTENT);
        fRef.collection(HabitConstants.HABIT_PATH).document(habitID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    habit = task.getResult().toObject(Habit.class);
                    updateUI();
                }
            }

        });
    }

   private void updateUI() {
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

    void updateHabit(){
        String input = updateInput.getText().toString();
        if(input.equals("")){
            Toast.makeText(getApplicationContext(), "Please enter a value",
                    Toast.LENGTH_SHORT).show();
        }
        else{
            habit.updateHabit(Integer.parseInt(input));
            fRef.collection(HabitConstants.HABIT_PATH).document(habit.getID()).set(habit).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Failed:\n" + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    FirebaseFirestore fRef = FirebaseFirestore.getInstance();
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    fRef.collection(HabitConstants.USER_PATH).document(mAuth.getUid()).update("habits", FieldValue.arrayUnion(habit.getID())).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Failed:\n" + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(), "\"" + habit.getTitle() + "\" was Successfully Updated",
                                    Toast.LENGTH_SHORT).show();
                            updateUI();
                        }
                    });
                }
            });
        }

    }



}
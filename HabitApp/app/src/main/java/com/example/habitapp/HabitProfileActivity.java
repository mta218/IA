package com.example.habitapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.habitapp.enums.Goal;
import com.example.habitapp.models.Habit;
import com.example.habitapp.models.User;
import com.example.habitapp.utils.HabitConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.Duration;
import java.util.Calendar;
import java.util.Date;

public class HabitProfileActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseFirestore fRef;
    boolean isOwner;
    String habitID;
    Habit habit;
    String ownerID;
    Button updateButton, editButton, copyButton;
    ProgressBar progressBar;
    TextView titleText, progressText, lastUpdatedText, dateText;
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
        dateText = findViewById(R.id.dateText);

        editButton = findViewById(R.id.editButton);

        fRef = FirebaseFirestore.getInstance();

        updateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                updateHabit();
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                goToEditActivity();
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
                    habit.updateStreak();
                    updateUI();
                }
            }

        });
    }

    /**
     * Refreshes the UI with the latest data of the habit
     *
     */
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

       getPercentage();

       if(habit.getGoalDate() != null){
           long days = Duration.between(Calendar.getInstance().toInstant(), habit.getGoalDate().toInstant()).toDays();
            System.out.println("true or false (or fralse lol): "+ (days < 0));

           if(days < 0){
               dateText.setText("The Goal Date has passed\nYou may edit this Habit to change or remove the Goal Date");
           }
           else if(days == 0){
               dateText.setText("Goal date is today.");
           }
           else if(days < 7){
               if(days == 1){
                   dateText.setText("1 day until Goal Date");
               }
               dateText.setText(days + " days until Goal Date");
           }
           else if(days <= 28){
               int weeks = (int) Math.round(((int)days)/7.0);
               if(weeks == 1){
                   dateText.setText("1 week until Goal Date");
               }
               dateText.setText(weeks + " weeks until Goal Date");
           }
           else if(days <= 31){
               dateText.setText("1 month until Goal Date");
           }
           else if(days < 365){
               dateText.setText(Math.round(((int)days)/30.5) + " months until Goal Date");
           }
           else {
               int years = (int) Math.round(((int)days)/365);
               if(years == 1){
                   dateText.setText("1 year until Goal Date");
               }
               dateText.setText(years + " years until Goal Date");
           }
       }
       else{
           dateText.setText("");
       }

        if(isOwner){
            progressText.setText("You have reached " + getPercentage() + "% of your goal");
        }
        else{   //TODO: make this change for not same user
            fRef.collection(HabitConstants.USER_PATH).document(habit.getOwnerID()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            progressText.setText(document.toObject(User.class).getDisplayName() + " have reached " + getPercentage() + "% of your goal");
                        } else {
                            Toast.makeText(getApplicationContext(), "Failed:\n Could not update properly",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Failed:\n Could not update properly",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        progressBar.setProgress(getPercentage()); //percentage

    }

    /**
     * Called when getting the value for the progress bar, returns the percentage of the goal completion to the nearest int
     *
     * @return An integer representing the percentage of the goal completion
     */
    private int getPercentage(){
        int percentage = 0;

        if (habit.getGoalType() == Goal.AMOUNT) {
            percentage = (int) Math.round((100.0 * habit.getTrackedCount()) / habit.getGoal());
        } else if (habit.getGoalType() != Goal.NONE) {
            percentage = (int) Math.round((100.0 * habit.getStreak()) / habit.getGoal());
        }

        return percentage;
    }

    /**
     * Increments the trackedCount value of the habit by the value entered into the updateInput EditText,
     * displays a success/error message on completion.
     *
     */
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

    /**
     * Opens EditHabitActivity
     *
     */
    private void goToEditActivity(){
        Intent intent = new Intent(this, EditHabitActivity.class);
        intent.putExtra(HabitConstants.HABIT_ID_INTENT, habit.getID());
        startActivityForResult(intent,1001);
    }

    /**
     * Called by EditHabitActivity to close this activity
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1001){
            finish();
        }

    }

}
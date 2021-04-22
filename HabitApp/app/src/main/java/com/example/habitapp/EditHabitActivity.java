package com.example.habitapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.habitapp.enums.Goal;
import com.example.habitapp.models.Habit;
import com.example.habitapp.utils.HabitConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;

public class EditHabitActivity extends AppCompatActivity {

    Button deleteGoalButton, deleteHabitButton, deleteDateButton;
    Spinner goalSpinner;
    EditText editTitleInput, editDateInput, editGoalInput;
    TextView editTitle;
    FirebaseAuth mAuth;
    FirebaseFirestore fRef;
    String habitID;
    Habit habit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_habit);

        editDateInput = findViewById(R.id.editDateInput);
        deleteGoalButton = findViewById(R.id.deleteGoalButton);
        deleteHabitButton = findViewById(R.id.deleteHabitButton);
        deleteDateButton = findViewById(R.id.deleteDateButton);
        goalSpinner = findViewById(R.id.goalSpinner);
        editTitleInput = findViewById(R.id.editTitleInput);
        editGoalInput = findViewById(R.id.editGoalInput);
        editTitle = findViewById(R.id.editTitle);


        mAuth = FirebaseAuth.getInstance();
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
        editTitleInput.setText(habit.getTitle());

        if (habit.getGoalType() == Goal.NONE) {
            editGoalInput.setVisibility(View.INVISIBLE);
        } else {
            deleteGoalButton.setVisibility(View.VISIBLE);
            editGoalInput.setText(habit.getGoal() + "");
            System.out.println("peepeepoopoo " + habit.getGoal());
        }

        if (habit.getGoalDate() == null) {
            editDateInput.setVisibility(View.INVISIBLE);
        } else {
            deleteDateButton.setVisibility(View.VISIBLE);
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            editDateInput.setText(formatter.format(habit.getGoalDate()));
        }
    }


}
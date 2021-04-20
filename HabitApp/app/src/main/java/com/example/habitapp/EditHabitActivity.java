package com.example.habitapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.habitapp.models.Habit;
import com.example.habitapp.utils.HabitConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
                }
            }

        });
    }




}
package com.example.habitapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.habitapp.enums.Frequency;
import com.example.habitapp.enums.Goal;
import com.example.habitapp.models.Habit;
import com.example.habitapp.utils.HabitConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EditHabitActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Button deleteGoalButton, deleteHabitButton, deleteDateButton, confirmButton;
    Spinner goalSpinner;
    EditText editTitleInput, editDateInput, editGoalInput;
    TextView editTitle;
    FirebaseAuth mAuth;
    FirebaseFirestore fRef;
    String habitID;
    Habit habit;
    Frequency habitFreq;

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
        confirmButton = findViewById(R.id.confirmButton);

        Spinner goalSpinner = findViewById(R.id.goalSpinner);
        ArrayAdapter<CharSequence> goalAdapter = ArrayAdapter.createFromResource(this, R.array.goal_types, android.R.layout.simple_spinner_item);
        goalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        goalSpinner.setAdapter(goalAdapter);
        goalSpinner.setOnItemSelectedListener(this);

        mAuth = FirebaseAuth.getInstance();

        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                confirmChanges();
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
                    habitFreq = habit.getFreq();
                    switch (habitFreq){
                        case NONE:
                            goalSpinner.setSelection(0);
                            break;
                        case DAILY:
                            goalSpinner.setSelection(1);
                            break;
                        case WEEKLY:
                            goalSpinner.setSelection(2);
                            break;
                        case MONTHLY:
                            goalSpinner.setSelection(3);
                            break;
                    }

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
            //System.out.println("peepeepoopoo " + habit.getGoal());
        }

        if (habit.getGoalDate() == null) {
            editDateInput.setVisibility(View.INVISIBLE);
        } else {
            deleteDateButton.setVisibility(View.VISIBLE);
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            editDateInput.setText(formatter.format(habit.getGoalDate()));
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (i) {
            case 0:
                habitFreq = Frequency.NONE;
                break;
            case 1:
                habitFreq = Frequency.DAILY;
                break;
            case 2:
                habitFreq = Frequency.WEEKLY;
                break;
            case 3:
                habitFreq = Frequency.MONTHLY;
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void confirmChanges(){
        try{
            Habit newHabit = new Habit(editTitleInput.getText().toString(),habitFreq, Integer.parseInt(editGoalInput.getText().toString()), new SimpleDateFormat("dd/MM/yyyy").parse(editGoalInput.getText().toString()), Goal goalType, ArrayList<String> tags, String ownerID);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
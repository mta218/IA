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
import android.widget.Toast;

import com.example.habitapp.enums.Frequency;
import com.example.habitapp.enums.Goal;
import com.example.habitapp.models.Habit;
import com.example.habitapp.utils.HabitConstants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class CreateHabitActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Frequency freq;
    Goal goal;
    EditText dateInput, goalInput, titleInput, tagsInput;
    Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_habit);

        dateInput = findViewById(R.id.dateInput);
        goalInput = findViewById(R.id.goalInput);
        titleInput = findViewById(R.id.titleInput);
        tagsInput = findViewById(R.id.tagsInput);

        Spinner freqSpinner = findViewById(R.id.freqSpinner);
        ArrayAdapter<CharSequence> freqAdapter = ArrayAdapter.createFromResource(this, R.array.frequencies, android.R.layout.simple_spinner_item);
        freqAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        freqSpinner.setAdapter(freqAdapter);
        freqSpinner.setOnItemSelectedListener(this);

        Spinner goalSpinner = findViewById(R.id.goalSpinner);
        ArrayAdapter<CharSequence> goalAdapter = ArrayAdapter.createFromResource(this, R.array.goal_types, android.R.layout.simple_spinner_item);
        goalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        goalSpinner.setAdapter(goalAdapter);
        goalSpinner.setOnItemSelectedListener(this);

        submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                addHabit();
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (adapterView.getId() == R.id.freqSpinner) {
            switch (i) {
                case 0:
                    freq = Frequency.NONE;
                    break;
                case 1:
                    freq = Frequency.DAILY;
                    break;
                case 2:
                    freq = Frequency.WEEKLY;
                    break;
                case 3:
                    freq = Frequency.MONTHLY;
                    break;
            }
        } else if (adapterView.getId() == R.id.goalSpinner) {
            switch (i) {
                case 0:
                    goal = Goal.NONE;
                    goalInput.setVisibility(View.INVISIBLE);
                    dateInput.setVisibility(View.INVISIBLE);
                    break;
                case 1:
                    goal = Goal.STREAK;
                    goalInput.setVisibility(View.VISIBLE);
                    dateInput.setVisibility(View.INVISIBLE);
                    break;
                case 2:
                    goal = Goal.AMOUNT;
                    goalInput.setVisibility(View.VISIBLE);
                    dateInput.setVisibility(View.INVISIBLE);
                    break;
                case 3:
                    goal = Goal.DATE;
                    goalInput.setVisibility(View.INVISIBLE);
                    dateInput.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void addHabit() {
        String titleString = titleInput.getText().toString();
        String goalString = goalInput.getText().toString();
        String dateString = dateInput.getText().toString();

        if (titleString.equals("")) {
            Toast.makeText(getApplicationContext(), "Please enter a title",
                    Toast.LENGTH_SHORT).show();
        } else if (goal == Goal.DATE) {
            if (goalString.equals("")) {
                Toast.makeText(getApplicationContext(), "Please enter goal information",
                        Toast.LENGTH_SHORT).show();
            } else {
                try {
                    Date goalDate = new SimpleDateFormat("dd/MM/yyyy").parse(dateString);

                    Calendar goalDateAsCal = Calendar.getInstance();
                    goalDateAsCal.setTime(goalDate);

                    //https://alvinalexander.com/java/java-today-get-todays-date-now/
                    if (goalDateAsCal.before(Calendar.getInstance().getTime())) {
                        throw new Exception();
                    }

                    updateDatabase(new Habit(titleString, freq, 0, goalDateAsCal, goal ,getTags()));
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Failed:\nInvalid Date",
                            Toast.LENGTH_SHORT).show();
                }

            }
        } else if (goal == Goal.AMOUNT || goal == Goal.STREAK) {
            if (goalString.equals("")) {
                Toast.makeText(getApplicationContext(), "Please enter goal information",
                        Toast.LENGTH_SHORT).show();
            } else {
                updateDatabase(new Habit(titleString, freq, Integer.parseInt(goalString), null, goal ,getTags()));
            }
        }
    }

    void updateDatabase(final Habit habit){
        FirebaseFirestore fRef = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        fRef.collection(HabitConstants.HABIT_PATH).document(habit.getID()).set(habit).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Failed:\n"+e.getMessage(),
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
                        Toast.makeText(getApplicationContext(), "Failed:\n"+e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "\""+habit.getTitle()+"\" was Successfully Created",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        });

    }

    private ArrayList<String> getTags(){
        ArrayList<String> list = new ArrayList<String>();
        Collections.addAll(list, tagsInput.getText().toString().trim().replaceAll("\\s+", "").split(","));
        return list;
    }

}
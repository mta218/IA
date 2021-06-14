package com.example.habitapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.habitapp.enums.Frequency;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * This is the EditHabitActivity, this is where Users can edit the details of the habit and delete them.
 *
 * @author Maximilian Ta
 * @version 0.1
 */

public class EditHabitActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Button deleteHabitButton, confirmButton;
    Spinner goalSpinner, freqSpinner;
    EditText editTitleInput, editDateInput, editGoalInput, editTagsInput;
    TextView editTitle;
    FirebaseAuth mAuth;
    FirebaseFirestore fRef;
    String habitID;
    Habit habit;
    Frequency habitFreq;
    Goal habitGoal;
    CheckBox hiddenHabitCheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_habit);

        editDateInput = findViewById(R.id.editDateInput);
        deleteHabitButton = findViewById(R.id.deleteHabitButton);
        goalSpinner = findViewById(R.id.goalSpinner);
        editTitleInput = findViewById(R.id.editTitleInput);
        editGoalInput = findViewById(R.id.editGoalInput);
        editTitle = findViewById(R.id.editTitle);
        confirmButton = findViewById(R.id.confirmButton);
        editTagsInput = findViewById(R.id.editTagsInput);
        hiddenHabitCheckbox = findViewById(R.id.hiddenHabitCheckbox);

        Spinner goalSpinner = findViewById(R.id.goalSpinner);
        ArrayAdapter<CharSequence> goalAdapter = ArrayAdapter.createFromResource(this, R.array.goal_types, R.layout.spinner_item);
        goalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        goalSpinner.setAdapter(goalAdapter);
        goalSpinner.setOnItemSelectedListener(this);

        freqSpinner = findViewById(R.id.freqSpinner);
        ArrayAdapter<CharSequence> freqAdapter = ArrayAdapter.createFromResource(this, R.array.frequencies, R.layout.spinner_item);
        freqAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        freqSpinner.setAdapter(freqAdapter);
        freqSpinner.setOnItemSelectedListener(this);

        mAuth = FirebaseAuth.getInstance();

        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                confirmChanges();
            }
        });

        deleteHabitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                deleteHabit();
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
                    switch (habitFreq) {
                        case NONE:
                            freqSpinner.setSelection(0);
                            break;
                        case DAILY:
                            freqSpinner.setSelection(1);
                            break;
                        case WEEKLY:
                            freqSpinner.setSelection(2);
                            break;
                        case MONTHLY:
                            freqSpinner.setSelection(3);
                            break;
                    }

                    habitGoal = habit.getGoalType();
                    switch (habitGoal) {
                        case NONE:
                            goalSpinner.setSelection(0);
                            break;
                        case DAILY_STREAK:
                            goalSpinner.setSelection(1);
                            break;
                        case WEEKLY_STREAK:
                            goalSpinner.setSelection(2);
                            break;
                        case MONTHLY_STREAK:
                            goalSpinner.setSelection(3);
                            break;
                        case AMOUNT:
                            goalSpinner.setSelection(4);
                            break;
                    }
                    updateUI();
                }
            }

        });
    }

    /**
     * Refreshes the UI with the latest data of the habit
     */
    private void updateUI() {
        editTitleInput.setText(habit.getTitle());
        editTagsInput.setText(habit.tagsAsString());
        hiddenHabitCheckbox.setChecked(habit.isHidden());

        if (habitGoal == Goal.NONE) {
            editGoalInput.setVisibility(View.INVISIBLE);
            editDateInput.setVisibility(View.INVISIBLE);
        } else {
            editGoalInput.setText(habit.getGoal() + "");
            //System.out.println("peepeepoopoo " + habit.getGoal());
        }

        if (habit.getGoalDate() != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            editDateInput.setText(formatter.format(habit.getGoalDate()));
        }


    }


    /**
     * This is the method called when a spinner is clicked. It will update the appropriate value of the habit
     * (either the frequency or goal type) depending on which spinner clicked.
     *
     * @param adapterView
     * @param view
     * @param i
     * @param l
     */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (adapterView.getId() == R.id.goalSpinner) {
            switch (i) {
                case 0:
                    habitGoal = Goal.NONE;
                    editGoalInput.setVisibility(View.INVISIBLE);
                    break;
                case 1:
                    habitGoal = Goal.DAILY_STREAK;
                    editGoalInput.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    habitGoal = Goal.WEEKLY_STREAK;
                    editGoalInput.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    habitGoal = Goal.MONTHLY_STREAK;
                    editGoalInput.setVisibility(View.VISIBLE);
                    break;
                case 4:
                    habitGoal = Goal.AMOUNT;
                    editGoalInput.setVisibility(View.VISIBLE);
                    break;
            }
        } else if (adapterView.getId() == R.id.freqSpinner) {
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
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    /**
     * Creates a new Habit from the new information entered and updates Firebase.
     */
    public void confirmChanges() {
        try {
            Date newDate;

            if (editDateInput.getText().toString().equals("")) {
                newDate = null;
            } else {
                newDate = new SimpleDateFormat("dd/MM/yyyy").parse(editDateInput.getText().toString());
            }

            int goalInputNum;
            if (editGoalInput.getText().toString().equals("")) {
                goalInputNum = 0;
            } else {
                goalInputNum = Integer.parseInt(editGoalInput.getText().toString());
            }

            Habit newHabit = new Habit(habitID, editTitleInput.getText().toString(), habitFreq, goalInputNum, newDate, habitGoal, getTags(),
                    habit.getOwnerID(), habit.getLastUpdated(), habit.getStreak(), habit.getTrackedCount(), hiddenHabitCheckbox.isChecked(), habit.getEncouragement());
            fRef.collection(HabitConstants.HABIT_PATH).document(habitID).set(newHabit).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Failed:\n" + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getApplicationContext(), "Successfully Updated",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Converts the string entered into the editTagsInput editText into an ArrayList of tags.
     *
     * @return an ArrayList of Strings, each representing a tag for the habit
     */
    private ArrayList<String> getTags() {
        ArrayList<String> list = new ArrayList<String>();
        String[] temp = editTagsInput.getText().toString().trim().replaceAll("\\s+", "").split(",");
        for (int i = 0; i < temp.length; i++) {
            temp[i] = temp[i].toLowerCase();
        }
        Collections.addAll(list, temp);
        return list;
    }


    /**
     * Called when the delete button is pressed, attempts to remove the habit from Firebase. If unsuccessful, displays an error message.
     */
    private void deleteHabit() {
        //https://stackoverflow.com/questions/36747369/how-to-show-a-pop-up-in-android-studio-to-confirm-an-order
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Delete Habit?");
        builder.setMessage("You are about to delete a habit.\nThis action cannot be undone.");
        builder.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseFirestore fRef = FirebaseFirestore.getInstance();
                        fRef.collection(HabitConstants.HABIT_PATH).document(habit.getID()).delete().addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Could not delete the Habit:\n" + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                FirebaseFirestore fRef = FirebaseFirestore.getInstance();
                                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                fRef.collection(HabitConstants.USER_PATH).document(mAuth.getUid()).update("habits", FieldValue.arrayRemove(habit.getID())).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "Could not update the user data:\n" + e.getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getApplicationContext(), "\"" + habit.getTitle() + "\" was Successfully Deleted",
                                                Toast.LENGTH_SHORT).show();
                                        closeCallingActivity();
                                        finish();
                                    }
                                });
                            }
                        });
                    }
                });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Do nothing
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Called when the back button is pressed, prompts the user with a dialogue stating that
     * unsaved changes will be lost.
     */
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Close Activity?");
        builder.setMessage("Any unsaved changes will be lost.");
        builder.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        closeActivity();
                    }
                });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //cancel, do nothing
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    /**
     * Calls finish()
     */
    private void closeActivity() {
        finish();
    }

    /**
     * Called when habit is deleted to close the HabitProfileActivity
     */
    private void closeCallingActivity() {
        finishActivity(1001); //?? idk but I got this from this one dude https://stackoverflow.com/questions/10379134/finish-an-activity-from-another-activity its like one of the last replies
    }

}
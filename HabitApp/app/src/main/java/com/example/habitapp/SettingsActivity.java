package com.example.habitapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.habitapp.models.Habit;
import com.example.habitapp.models.Settings;
import com.example.habitapp.models.User;
import com.example.habitapp.utils.HabitConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * This is the SettingsActivity, this is where users can edit the settings of their account
 *
 * @author Maximilian Ta
 * @version 0.1
 */
public class SettingsActivity extends AppCompatActivity {

    Switch darkModeSwitch;
    Button signOutButton, confirmButton;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    private FirebaseFirestore fRef;
    User user;
    int success;

    TextView usernameText, displaynameText;
    EditText usernameEdit, displaynameEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        darkModeSwitch = findViewById(R.id.darkModeSwitch);
        usernameText = findViewById(R.id.usernameText);
        displaynameText = findViewById(R.id.displaynameText);
        usernameEdit = findViewById(R.id.usernameEdit);
        displaynameEdit = findViewById(R.id.displaynameEdit);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        fRef = FirebaseFirestore.getInstance();
        getUserInfo();

        success = 0;

        darkModeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!darkModeSwitch.isChecked()) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    user.getSettings().setDarkMode(false);

                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    user.getSettings().setDarkMode(true);

                }

                fRef.collection(HabitConstants.USER_PATH).document(currentUser.getUid()).update("settings", user.getSettings()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        
                    }
                });

            }
        });

        signOutButton = findViewById(R.id.signOutButton);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mAuth.signOut();
                finish();

            }
        });

        confirmButton = findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                updateUserInfo();

            }
        });
    }

    /**
     * Retrieves the user information from firebase
     *
     */
    private void getUserInfo() {
        fRef.collection(HabitConstants.USER_PATH).document(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    user = task.getResult().toObject(User.class);
                    updateUI();
                }
            }
        });
    }

    /**
     * Updates the UI with the current user information
     *
     */
    private void updateUI() {
        Settings settings = user.getSettings();
        usernameText.setText(user.getUsername());
        displaynameText.setText(user.getDisplayName());
        usernameEdit.setText(user.getUsername());
        displaynameEdit.setText(user.getDisplayName());



        if (settings.isDarkMode()) {
            darkModeSwitch.setChecked(true);
        } else {
            darkModeSwitch.setChecked(false);
        }
    }

    /**
     * Called when confirmButton is pressed, validates the new username and display name, updates firebase with the new values.
     *
     *
     */
    private void updateUserInfo(){
        final String username = usernameEdit.getText().toString();
        final String displayName = displaynameEdit.getText().toString();
        if(username.equals("") || displayName.equals("")){
            Toast.makeText(getApplicationContext(), "Please enter a username and a name.",
                    Toast.LENGTH_SHORT).show();
        }
        else{
            //https://www.techiedelight.com/check-string-contains-alphanumeric-characters-java/
            if(username.length() < 4 || username.length() > 20){
                Toast.makeText(getApplicationContext(), "Username must be between 4 and 20 characters.",
                        Toast.LENGTH_SHORT).show();
            }
            else if(username.matches("^[a-zA-Z0-9]*$")){
                fRef.collection(HabitConstants.USER_PATH).whereEqualTo("username", username).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            boolean exists = false;
                            String id = "";
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                exists = true;
                                id = document.getId();
                                break;
                            }

                            if(exists && !id.equals(mAuth.getUid())){
                                Toast.makeText(getApplicationContext(), "Username already exists",
                                        Toast.LENGTH_SHORT).show();
                            }
                            else{
                                fRef.collection(HabitConstants.USER_PATH).document(mAuth.getUid()).update("username",username).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        success();
                                    }
                                });
                                fRef.collection(HabitConstants.USER_PATH).document(mAuth.getUid()).update("displayName",displayName).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        success();
                                    }
                                });
                            }


                        } else {
                            Toast.makeText(getActivity(), "Please enter a title or tags",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else{
                Toast.makeText(getApplicationContext(), "Username must only contain numbers and letters",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Context getActivity(){
        return this;
    }

    /**
     * Used to display a Toast when both display name and username is successfully updated.
     *
     */
    private void success(){
        success++;
        if(success == 2){
            Toast.makeText(getApplicationContext(), "Successfully updated",
                    Toast.LENGTH_SHORT).show();
            success = 0;
        }
    }

    /**
     * Called when the back button is pressed, prompts the user with a dialogue stating that
     * unsaved changes will be lost.
     *
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
                //cancel

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void closeActivity(){
        finish();
    }

}
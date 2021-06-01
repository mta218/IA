package com.example.habitapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

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
import com.google.firebase.firestore.QuerySnapshot;

/**
 * This is the SettingsActivity, this is where users can edit the settings of their account
 *
 * @author Maximilian Ta
 * @version 0.1
 */
public class SettingsActivity extends AppCompatActivity {

    Switch darkModeSwitch;
    Button signOutButton;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    private FirebaseFirestore fRef;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        darkModeSwitch = findViewById(R.id.darkModeSwitch);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        fRef = FirebaseFirestore.getInstance();
        getUserInfo();

        // Saving state of our app
        // using SharedPreferences
        //SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        //final SharedPreferences.Editor editor = sharedPreferences.edit();
        //final boolean isDarkModeOn = sharedPreferences.getBoolean("isDarkModeOn", false);

        // When user reopens the app
        // after applying dark/light mode
        //if (isDarkModeOn) {
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        //} else {
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        //}

        darkModeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // When user taps the enable/disable
                // dark mode button
                if (!darkModeSwitch.isChecked()) {

                    // if dark mode is on it
                    // will turn it off
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    // it will set isDarkModeOn
                    // boolean to false
                    //editor.putBoolean("isDarkModeOn", false);
                    //editor.apply();
                    user.getSettings().setDarkMode(false);

                } else {
                    // if dark mode is off
                    // it will turn it on
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

                    // it will set isDarkModeOn
                    // boolean to true
                    //editor.putBoolean("isDarkModeOn", true);
                    //editor.apply();

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


    }

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

    private void updateUI() {
        Settings settings = user.getSettings();
        if (settings.isDarkMode()) {
            darkModeSwitch.setChecked(true);
        } else {
            darkModeSwitch.setChecked(false);
        }
    }
}
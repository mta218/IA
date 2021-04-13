package com.example.habitapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class SettingsActivity extends AppCompatActivity {

    Switch darkModeSwitch;
    Button signOutButton;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    private FirebaseFirestore fRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        darkModeSwitch = findViewById(R.id.darkModeSwitch);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        fRef = FirebaseFirestore.getInstance();



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
                } else {
                    // if dark mode is off
                    // it will turn it on
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

                    // it will set isDarkModeOn
                    // boolean to true
                    //editor.putBoolean("isDarkModeOn", true);
                    //editor.apply();

                }
            }
        });

        signOutButton = findViewById(R.id.signOutButton);
        signOutButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                mAuth.signOut();
                finish();

            }
        });


    }

    private void getUserInfo(){
        fRef.collection("Users/"+currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

            }
        });
    }
}
package com.example.habitapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.habitapp.models.Habit;
import com.example.habitapp.models.User;
import com.example.habitapp.utils.HabitConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class WelcomeActivity extends AppCompatActivity {

    EditText inputUsername, inputName;
    Button continueButton;

    FirebaseAuth mAuth;
    FirebaseFirestore fRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        inputName = findViewById(R.id.inputName);
        inputUsername = findViewById(R.id.inputUsername);
        continueButton = findViewById(R.id.continueButton);

        fRef = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        continueButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                validate();
            }
        });
    }

    /**
     * Validates the username and display name entered, if it is valid, sets the user's username and displayname.
     */
    private void validate() {
        final String username = inputUsername.getText().toString();
        final String displayName = inputName.getText().toString();
        if (username.equals("") || displayName.equals("")) {
            Toast.makeText(getApplicationContext(), "Please enter a username and a name.",
                    Toast.LENGTH_SHORT).show();
        } else {
            //https://www.techiedelight.com/check-string-contains-alphanumeric-characters-java/
            if (username.length() < 4 || username.length() > 20) {
                Toast.makeText(getApplicationContext(), "Username must be between 4 and 20 characters.",
                        Toast.LENGTH_SHORT).show();
            } else if (username.matches("^[a-zA-Z0-9]*$")) {
                fRef.collection(HabitConstants.USER_PATH).whereEqualTo("username", username).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            boolean exists = false;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                exists = true;
                                break;
                            }

                            if (exists) {
                                Toast.makeText(getApplicationContext(), "Username already exists",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                fRef.collection(HabitConstants.USER_PATH).document(mAuth.getUid()).set(new User(displayName, username)).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "Failed:\n" + e.getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Intent intent = new Intent(getActivity(), MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                            }


                        } else {
                            Toast.makeText(getActivity(), "Please enter a title or tags",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "Username must only contain numbers and letters",
                        Toast.LENGTH_SHORT).show();
            }
        }

    }


    private Context getActivity() {
        return this;
    }


}
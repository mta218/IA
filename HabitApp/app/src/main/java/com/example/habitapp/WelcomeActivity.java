package com.example.habitapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

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

    private void validate(){
        String username = inputUsername.getText().toString();
        String displayName = inputName.getText().toString();
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

            }
            else{
                Toast.makeText(getApplicationContext(), "Username must only contain numbers and letters",
                        Toast.LENGTH_SHORT).show();
            }
        }

    }





}
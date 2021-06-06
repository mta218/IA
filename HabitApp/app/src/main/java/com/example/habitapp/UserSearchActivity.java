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
import com.example.habitapp.utils.recyclerview.HabitAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class UserSearchActivity extends AppCompatActivity {

    EditText usernameSearchInput;
    Button searchButton;
    FirebaseAuth mAuth;
    FirebaseFirestore fRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);

        usernameSearchInput = findViewById(R.id.usernameSearchInput);
        searchButton = findViewById(R.id.searchButton);

        fRef = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        View.OnClickListener searchListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search();
            }
        };
        searchButton.setOnClickListener(searchListener);


    }

    private void search(){
        String username = usernameSearchInput.getText().toString().trim();

        fRef.collection(HabitConstants.USER_PATH).whereEqualTo("username", username).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    User user = null;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        user = document.toObject(User.class);
                        break;
                    }

                    if(user != null){
                        Intent intent = new Intent(getActivity(), UserProfileActivity.class);
                        intent.putExtra(HabitConstants.USERNAME__INTENT, user.getUsername());
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(getActivity(), "User was not found.",
                                Toast.LENGTH_SHORT).show();
                    }


                } else {
                            Toast.makeText(getActivity(), "Data could not be loaded, try again later.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private Context getActivity(){
        return this;
    }
}
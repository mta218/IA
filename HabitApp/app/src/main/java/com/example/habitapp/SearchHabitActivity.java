package com.example.habitapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.habitapp.models.Habit;
import com.example.habitapp.models.User;
import com.example.habitapp.utils.HabitConstants;
import com.example.habitapp.utils.recyclerview.HabitAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

public class SearchHabitActivity extends AppCompatActivity implements HabitAdapter.OnHabitListener, AdapterView.OnItemSelectedListener{

    Button createButton, searchButton;
    RecyclerView searchHabitRecyclerView;
    FirebaseFirestore fRef;
    FirebaseAuth mAuth;
    ArrayList<Habit> allPendingArrayList;
    User user;
    Spinner sorterSpinner;
    EditText editSearchTags, editSearchTitle;

    int sorted = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_habit);

        fRef = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        editSearchTags = findViewById(R.id.editSearchTags);
        editSearchTitle = findViewById(R.id.editSearchTitle);

        allPendingArrayList = new ArrayList<>();

        searchButton = findViewById(R.id.searchButton);
        View.OnClickListener searchListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchButtonPressed();
            }
        };
        searchButton.setOnClickListener(searchListener);

        searchHabitRecyclerView = findViewById(R.id.searchHabitRecyclerView);
        HabitAdapter adapter = new HabitAdapter(new ArrayList<Habit>(), this, HabitConstants.ALL_HABIT_RECYCLER_VIEW);
        searchHabitRecyclerView.setAdapter(adapter);
        searchHabitRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        sorterSpinner = findViewById(R.id.searchSorterSpinner);
        ArrayAdapter<CharSequence> sorterAdapter = ArrayAdapter.createFromResource(this, R.array.sorting_types, android.R.layout.simple_spinner_item);
        sorterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sorterSpinner.setAdapter(sorterAdapter);
        sorterSpinner.setOnItemSelectedListener(this);
    }

    private void searchButtonPressed(){
        ((HabitAdapter) searchHabitRecyclerView.getAdapter()).clearArrayList();
        allPendingArrayList.clear();
        String titleString = editSearchTitle.getText().toString();
        String tagsString = editSearchTags.getText().toString();

        if(!titleString.equals("") && !tagsString.equals("")){
            searchBoth(titleString);
        }
        else if(titleString.equals("") && !tagsString.equals("")){
            searchForTag();
        }
        else if(!titleString.equals("") && tagsString.equals("")){
            searchForTitle(titleString);
        }
        else{
            Toast.makeText(this, "Please enter a title or tags",
                    Toast.LENGTH_SHORT).show();
        }


    }

    private void searchForTag(){
        fRef.collection(HabitConstants.HABIT_PATH).whereEqualTo("ownerID", mAuth.getUid()).whereArrayContainsAny("tags", getTags()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<Habit> habits = new ArrayList();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        habits.add(document.toObject(Habit.class));
                    }
                    addHabits(habits);
                } else {
                    Toast.makeText(getActivity(), "Please enter a title or tags",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void searchForTitle(String title){
        fRef.collection(HabitConstants.HABIT_PATH).whereEqualTo("ownerID", mAuth.getUid()).whereEqualTo("title", title).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<Habit> habits = new ArrayList();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        habits.add(document.toObject(Habit.class));
                    }
                    addHabits(habits);
                } else {
                    Toast.makeText(getActivity(), "Please enter a title or tags",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void searchBoth(String title){
        fRef.collection(HabitConstants.HABIT_PATH).whereEqualTo("ownerID", mAuth.getUid()).whereEqualTo("title", title).whereArrayContainsAny("tags", getTags()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<Habit> habits = new ArrayList();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        habits.add(document.toObject(Habit.class));
                    }
                    addHabits(habits);
                } else {
                    Toast.makeText(getActivity(), "Please enter a title or tags",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private Activity getActivity(){
        return this;
    }




    private void addHabits(ArrayList<Habit> habits){
        HabitAdapter habitAdapter = ((HabitAdapter) searchHabitRecyclerView.getAdapter());
        habitAdapter.addHabits(habits);
        switch (sorted) {
            case 0: //Urgency
                habitAdapter.sortUrgency();
                break;
            case 1: //Name
                habitAdapter.sortAlphabetically();
                break;
            case 2: //Goal (percentage)
                habitAdapter.sortGoal();
                break;
            case 3: //Last Updated
                habitAdapter.sortLastUpdated();
                break;
        }
        habitAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        HabitAdapter habitAdapter = ((HabitAdapter) searchHabitRecyclerView.getAdapter());

        sorted = i;

        switch (i) {
            case 0: //Urgency
                habitAdapter.sortUrgency();
                break;
            case 1: //Name
                habitAdapter.sortAlphabetically();
                break;
            case 2: //Goal (percentage)
                habitAdapter.sortGoal();
                break;
            case 3: //Last Updated
                habitAdapter.sortLastUpdated();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onHabitClick(int position, String tag) {

    }

    private ArrayList<String> getTags() {
        ArrayList<String> list = new ArrayList<String>();
        String[] temp = editSearchTags.getText().toString().trim().replaceAll("\\s+", "").split(",");
        for (int i = 0; i < temp.length; i++) {
            temp[i] = temp[i].toLowerCase();
        }
        Collections.addAll(list, temp);
        return list;
    }
}
package com.example.habitapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.habitapp.models.Habit;
import com.example.habitapp.models.User;
import com.example.habitapp.utils.HabitConstants;
import com.example.habitapp.utils.recyclerview.HabitAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_habit);

        fRef = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        allPendingArrayList = new ArrayList<>();

        searchButton = findViewById(R.id.searchButton);
        View.OnClickListener searchListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search();
            }
        };
        searchButton.setOnClickListener(searchListener);

        searchHabitRecyclerView = findViewById(R.id.searchHabitRecyclerView);
        HabitAdapter adapter = new HabitAdapter(new ArrayList<Habit>(), this, HabitConstants.ALL_HABIT_RECYCLER_VIEW);
        searchHabitRecyclerView.setAdapter(adapter);
        searchHabitRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        sorterSpinner = findViewById(R.id.sorterSpinner);
        ArrayAdapter<CharSequence> sorterAdapter = ArrayAdapter.createFromResource(this, R.array.sorting_types, android.R.layout.simple_spinner_item);
        sorterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sorterSpinner.setAdapter(sorterAdapter);
        sorterSpinner.setOnItemSelectedListener(this);
    }

    private void search(){
        
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        HabitAdapter habitAdapter = ((HabitAdapter) searchHabitRecyclerView.getAdapter());
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
package com.example.habitapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

/**
 * This is the SearchHabitActivity, this is where users can search through habits they own.
 *
 * @author Maximilian Ta
 * @version 0.1
 */
public class SearchHabitActivity extends AppCompatActivity implements HabitAdapter.OnHabitListener, AdapterView.OnItemSelectedListener{

    Button searchButton;
    RecyclerView searchHabitRecyclerView;
    FirebaseFirestore fRef;
    FirebaseAuth mAuth;
    ArrayList<Habit> allPendingArrayList, toBeFilteredArrayList;
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
        ArrayAdapter<CharSequence> sorterAdapter = ArrayAdapter.createFromResource(this, R.array.sorting_types, R.layout.spinner_item);
        sorterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sorterSpinner.setAdapter(sorterAdapter);
        sorterSpinner.setOnItemSelectedListener(this);
    }

    /**
     * Called when search button is pressed, and calls the appropriate search function
     *
     */
    private void searchButtonPressed(){
        fRef.collection(HabitConstants.HABIT_PATH).whereEqualTo("ownerID", mAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    toBeFilteredArrayList = new ArrayList();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        toBeFilteredArrayList.add(document.toObject(Habit.class));
                    }
                    search();
                } else {
                    Toast.makeText(getActivity(), "Please enter a title or tags",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        searchButtonPressed();
    }

    /**
     * called when search button is pressed, searches for a habit with a corresponding title and tags.
     *
     */
    private void search(){
        HabitAdapter adapter =  ((HabitAdapter) searchHabitRecyclerView.getAdapter());
        adapter.clearArrayList();

        String titleString = editSearchTitle.getText().toString();
        String tagsString = editSearchTags.getText().toString();


        if(tagsString.equals("") && titleString.equals("")){

                Toast.makeText(this, "Please enter a title or tags",
                        Toast.LENGTH_SHORT).show();

        }
        else{
            if(!tagsString.equals("")){
                searchForTag();
            }
            if(!titleString.equals("")){
                searchForTitle(titleString);
            }

            adapter.addHabits(toBeFilteredArrayList);
            switch (sorted) {
                case 0: //Urgency
                    adapter.sortUrgency();
                    break;
                case 1: //Name
                    adapter.sortAlphabetically();
                    break;
                case 2: //Goal (percentage)
                    adapter.sortGoal();
                    break;
                case 3: //Last Updated
                    adapter.sortLastUpdated();
                    break;
            }
            adapter.notifyDataSetChanged();

        }



    }

    /**
     *
     * Searches and displays habits with the appropriate tag
     *
     */
    private void searchForTag(){
        ArrayList<Habit> tempArrayList = new ArrayList<>(toBeFilteredArrayList);
        for(Habit habit : tempArrayList){
            if(!habit.getTags().containsAll(getTags())){
                toBeFilteredArrayList.remove(habit);
            }
        }
    }

    /**
     *
     * Searches and displays habits with a title that has the first few characters which matches the letters entered
     *
     * @param title the title to be searched
     */
    private void searchForTitle(String title){
        ArrayList<Habit> tempArrayList = new ArrayList<>(toBeFilteredArrayList);
        for(Habit habit : tempArrayList){
            if(!habit.getTitle().toLowerCase().substring(0,title.length()).equals(title.toLowerCase().trim())){
                toBeFilteredArrayList.remove(habit);
            }
        }
    }


    /**
     * Returns the current instance of the SearchHabitActivity running
     *
     * @return
     */
    private Activity getActivity(){
        return this;
    }



    /**
     * This is the method called when a spinner is clicked. It will change the method of sorting of the Recycler view.
     *
     * @param adapterView
     * @param view
     * @param i
     * @param l
     */
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

    /**
     * Opens HabitProfileActivity for the habit clicked in the recycler view, called when the recycler view is clicked
     *
     * @param position the position of the habit in the recycler view
     * @param tag unused
     */
    @Override
    public void onHabitClick(int position, String tag) {
        Intent intent = new Intent(this.getActivity(), HabitProfileActivity.class);
        Habit habit = ((HabitAdapter) searchHabitRecyclerView.getAdapter()).getHabit(position);

        intent.putExtra(HabitConstants.HABIT_ID_INTENT, habit.getID());
        startActivity(intent);
    }

    /**
     * Converts the string entered into the editSearchTags editText into an ArrayList of tags.
     *
     * @return an ArrayList of Strings, each representing a tag for the habit
     */
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
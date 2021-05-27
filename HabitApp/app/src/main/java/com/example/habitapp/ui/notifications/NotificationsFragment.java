package com.example.habitapp.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitapp.CreateHabitActivity;
import com.example.habitapp.HabitProfileActivity;
import com.example.habitapp.R;
import com.example.habitapp.SearchHabitActivity;
import com.example.habitapp.enums.Goal;
import com.example.habitapp.models.Habit;
import com.example.habitapp.models.User;
import com.example.habitapp.utils.HabitConstants;
import com.example.habitapp.utils.recyclerview.HabitAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * This is the HabitFragment, it contains all UI that allows for users to view, edit and update their habits.
 *
 */
public class NotificationsFragment extends Fragment implements HabitAdapter.OnHabitListener, AdapterView.OnItemSelectedListener{
    //for habits
    Button createButton, searchButton;
    RecyclerView urgentHabitRecyclerView, allHabitRecyclerView;
    FirebaseFirestore fRef;
    FirebaseAuth mAuth;
    ArrayList<Habit> allPendingArrayList;
    User user;
    Spinner sorterSpinner;

    int sorted = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        fRef = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        allPendingArrayList = new ArrayList<>();

        createButton = root.findViewById(R.id.createButton);
        View.OnClickListener createListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addHabit();
            }
        };
        createButton.setOnClickListener(createListener);

        searchButton = root.findViewById(R.id.searchButton);
        View.OnClickListener searchListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToSearch();
            }
        };
        searchButton.setOnClickListener(searchListener);

        allHabitRecyclerView = root.findViewById(R.id.allHabitRecyclerView);
        HabitAdapter adapter1 = new HabitAdapter(new ArrayList<Habit>(), this, HabitConstants.ALL_HABIT_RECYCLER_VIEW);
        allHabitRecyclerView.setAdapter(adapter1);
        allHabitRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        sorterSpinner = root.findViewById(R.id.sorterSpinner);
        ArrayAdapter<CharSequence> sorterAdapter = ArrayAdapter.createFromResource(this.getContext(), R.array.sorting_types, android.R.layout.simple_spinner_item);
        sorterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sorterSpinner.setAdapter(sorterAdapter);
        sorterSpinner.setOnItemSelectedListener(this);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        refresh();

    }


    /**
     * Opens the CreateHabitActivity
     *
     */
    void addHabit() {
        Intent intent = new Intent(this.getActivity(), CreateHabitActivity.class);
        startActivity(intent);
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
        Habit habit = null;
        if (tag.equals(HabitConstants.URGENT_HABIT_RECYCLER_VIEW)) {
            //habit = ((HabitAdapter) urgentHabitRecyclerView.getAdapter()).getHabit(position);
        }
        else if (tag.equals(HabitConstants.ALL_HABIT_RECYCLER_VIEW)) {
            habit = ((HabitAdapter) allHabitRecyclerView.getAdapter()).getHabit(position);
        }

        intent.putExtra(HabitConstants.HABIT_ID_INTENT, habit.getID());
        startActivity(intent);
    }

    /**
     * Updates the recycler view with the latest habit information stored on Firebase
     *
     */
    private void refresh() {
        ((HabitAdapter) allHabitRecyclerView.getAdapter()).clearArrayList();
        allPendingArrayList.clear();
        fRef.collection(HabitConstants.USER_PATH).document(mAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    user = document.toObject(User.class);

                    for (String habitID : user.getHabits()) {
                        fRef.collection(HabitConstants.HABIT_PATH)
                                .document(habitID)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            Habit habit = document.toObject(Habit.class);
                                            addPending(HabitConstants.ALL_HABIT_RECYCLER_VIEW , habit);
                                        } else {
                                            Toast.makeText(getContext(), "Failed:\n Could not update properly",
                                                    Toast.LENGTH_SHORT).show();
                                            //TODO: display something in place of the recycler view because its empty?
                                        }
                                    }
                                });
                    }

                } else {
                    Toast.makeText(getContext(), "Failed:\n Could not update properly",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    /**
     * While the habits are being loaded from Firebase, since they are loaded asynchronously, sorting must occur after they all are loaded.
     * addPending will add the habits into a temporary ArrayList, sorting them and displaying them to the Recycler once all habits have been loaded.
     *
     * @param tag A String representing the tag of the ArrayList that the incoming habits will be added to
     * @param habitToAdd The habit to be added to the recycler view
     */
    void addPending(String tag, Habit habitToAdd){
        if(tag.equals(HabitConstants.ALL_HABIT_RECYCLER_VIEW)){
            allPendingArrayList.add(habitToAdd);
            if(allPendingArrayList.size() == user.getHabits().size()){
                HabitAdapter habitAdapter = ((HabitAdapter) allHabitRecyclerView.getAdapter());
                habitAdapter.addHabits(allPendingArrayList);
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
        }
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
        HabitAdapter habitAdapter = ((HabitAdapter) allHabitRecyclerView.getAdapter());
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

        habitAdapter.notifyDataSetChanged();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    /**
     * Opens the SearchHabitActivity, called when the search button is pressed
     *
     */
    private void goToSearch(){
        Intent intent = new Intent(this.getActivity(), SearchHabitActivity.class);
        startActivity(intent);
    }


}
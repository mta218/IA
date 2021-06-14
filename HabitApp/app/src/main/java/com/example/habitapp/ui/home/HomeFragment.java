package com.example.habitapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitapp.HabitProfileActivity;
import com.example.habitapp.MainActivity;
import com.example.habitapp.R;
import com.example.habitapp.SettingsActivity;
import com.example.habitapp.models.Habit;
import com.example.habitapp.models.User;
import com.example.habitapp.utils.HabitConstants;
import com.example.habitapp.utils.recyclerview.HabitAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements HabitAdapter.OnHabitListener {

    private HomeViewModel homeViewModel;
    private Button goToSettingsButton;

    FirebaseFirestore fRef;
    FirebaseAuth mAuth;
    ArrayList<Habit> allPendingArrayList;
    User user;
    RecyclerView urgentRecyclerView;
    TextView emptyText;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        goToSettingsButton = root.findViewById(R.id.settingsButton);

        View.OnClickListener goToSettingsListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToSettings();
            }
        };
        goToSettingsButton.setOnClickListener(goToSettingsListener);

        urgentRecyclerView = root.findViewById(R.id.urgentRecyclerView);

        HabitAdapter adapter1 = new HabitAdapter(new ArrayList<Habit>(), this);
        urgentRecyclerView.setAdapter(adapter1);
        urgentRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        emptyText = root.findViewById(R.id.emptyText);

        allPendingArrayList = new ArrayList<>();

        fRef = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        refresh();


        return root;
    }

    /**
     * Takes the user to the SettingsActivity
     */
    private void goToSettings() {
        Intent intent = new Intent(this.getActivity(), SettingsActivity.class);
        startActivity(intent);
    }

    /**
     * Opens HabitProfileActivity for the habit clicked in the recycler view, called when the recycler view is clicked
     *
     * @param position the position of the habit in the recycler view
     * @param tag      unused
     */
    @Override
    public void onHabitClick(int position, String tag) {
        Intent intent = new Intent(this.getActivity(), HabitProfileActivity.class);
        Habit habit = ((HabitAdapter) urgentRecyclerView.getAdapter()).getHabit(position);

        intent.putExtra(HabitConstants.HABIT_ID_INTENT, habit.getID());
        startActivity(intent);
    }


    /**
     * Updates the recycler view with the latest habit information stored on Firebase
     */
    private void refresh() {
        emptyText.setVisibility(View.GONE);
        ((HabitAdapter) urgentRecyclerView.getAdapter()).clearArrayList();
        ((HabitAdapter) urgentRecyclerView.getAdapter()).notifyDataSetChanged();
        allPendingArrayList.clear();
        fRef.collection(HabitConstants.USER_PATH).document(mAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    user = document.toObject(User.class);
                    if (user.getHabits() != null) {
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
                                                addPending(habit);
                                            } else {
                                                Toast.makeText(getContext(), "Failed:\n Could not update properly",
                                                        Toast.LENGTH_SHORT).show();
                                                emptyText.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    });
                        }
                    } else {
                        emptyText.setVisibility(View.VISIBLE);
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
     * @param habitToAdd The habit to be added to the recycler view
     */
    void addPending(Habit habitToAdd) {
        allPendingArrayList.add(habitToAdd);
        if (allPendingArrayList.size() == user.getHabits().size()) {
            HabitAdapter habitAdapter = ((HabitAdapter) urgentRecyclerView.getAdapter());
            habitAdapter.addHabits(allPendingArrayList);
            habitAdapter.sortUrgency();
            habitAdapter.notifyDataSetChanged();
        }
    }
}
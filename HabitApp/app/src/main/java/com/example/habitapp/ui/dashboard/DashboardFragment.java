package com.example.habitapp.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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

import com.example.habitapp.FriendActivity;
import com.example.habitapp.FriendRequestActivity;
import com.example.habitapp.HabitProfileActivity;
import com.example.habitapp.R;
import com.example.habitapp.SearchHabitActivity;
import com.example.habitapp.UserSearchActivity;
import com.example.habitapp.models.Habit;
import com.example.habitapp.models.User;
import com.example.habitapp.utils.HabitConstants;
import com.example.habitapp.utils.recyclerview.FriendHabitAdapter;
import com.example.habitapp.utils.recyclerview.HabitAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class DashboardFragment extends Fragment implements FriendHabitAdapter.OnHabitListener, AdapterView.OnItemSelectedListener{

    private SocialViewModel socialViewModel;

    RecyclerView socialRecyclerView;
    FirebaseFirestore fRef;
    FirebaseAuth mAuth;
    TextView emptyText;

    ArrayList<Habit> toBeFilteredArrayList;

    Button goToSearchButton, friendReqButton,friendsButton;

    Spinner sorterSpinner;

    int sorted;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        socialViewModel =
                new ViewModelProvider(this).get(SocialViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        sorted = 0;


        fRef = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        goToSearchButton = root.findViewById(R.id.goToSearchButton);
        View.OnClickListener searchListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToSearch();
            }
        };
        goToSearchButton.setOnClickListener(searchListener);

        friendReqButton = root.findViewById(R.id.friendReqButton);
        friendReqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToReqs();
            }
        });

        friendsButton = root.findViewById(R.id.friendsButton);
        friendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFriends();
            }
        });


        socialRecyclerView = root.findViewById(R.id.socialRecyclerView);
        FriendHabitAdapter adapter = new FriendHabitAdapter(new ArrayList<Habit>(), this);
        socialRecyclerView.setAdapter(adapter);
        socialRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        emptyText = root.findViewById(R.id.emptyText);



        sorterSpinner = root.findViewById(R.id.sorterSpinner2);
        ArrayAdapter<CharSequence> sorterAdapter = ArrayAdapter.createFromResource(this.getContext(), R.array.sorting_types, R.layout.spinner_item);
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

    private void goToSearch(){
        Intent intent = new Intent(this.getActivity(), UserSearchActivity.class);
        startActivity(intent);
    }

    private void goToReqs(){
        Intent intent = new Intent(this.getActivity(), FriendRequestActivity.class);
        startActivity(intent);
    }

    private void goToFriends(){
        Intent intent = new Intent(this.getActivity(), FriendActivity.class);
        startActivity(intent);
    }

    @Override
    public void onHabitClick(int position, String tag) {
        Intent intent = new Intent(this.getActivity(), HabitProfileActivity.class);

        Habit habit = ((FriendHabitAdapter) socialRecyclerView.getAdapter()).getHabit(position);


        intent.putExtra(HabitConstants.HABIT_ID_INTENT, habit.getID());
        startActivity(intent);
    }

    private void refresh(){
        fRef.collection(HabitConstants.USER_PATH).document(mAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    final User user = task.getResult().toObject(User.class);

                    emptyText.setVisibility(View.GONE);
                    ((FriendHabitAdapter) socialRecyclerView.getAdapter()).clearArrayList();

                    fRef.collection(HabitConstants.HABIT_PATH).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                toBeFilteredArrayList = new ArrayList();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Habit habit = document.toObject(Habit.class);
                                    if(user.getFriends().contains(habit.getOwnerID()) && !habit.isHidden()){
                                        toBeFilteredArrayList.add(habit);
                                    }
                                }

                                FriendHabitAdapter adapter = (FriendHabitAdapter) socialRecyclerView.getAdapter();
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

                            } else {
                                Toast.makeText(getActivity(), "Please try again later",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } else {
                    Toast.makeText(getActivity(), "Please try again later.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });



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
        FriendHabitAdapter habitAdapter = ((FriendHabitAdapter) socialRecyclerView.getAdapter());

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

}
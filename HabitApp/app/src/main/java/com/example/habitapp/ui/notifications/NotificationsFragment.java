package com.example.habitapp.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitapp.CreateHabitActivity;
import com.example.habitapp.HabitProfileActivity;
import com.example.habitapp.R;
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

public class NotificationsFragment extends Fragment implements HabitAdapter.OnHabitListener {
    //for habits
    Button createButton;
    RecyclerView urgentHabitRecyclerView, allHabitRecyclerView;
    FirebaseFirestore fRef;
    FirebaseAuth mAuth;
    ArrayList<Habit> urgentPendingArrayList, allPendingArrayList;
    User user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        fRef = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        allPendingArrayList = new ArrayList<>();
        urgentPendingArrayList = new ArrayList<>();

        createButton = root.findViewById(R.id.createButton);
        View.OnClickListener createListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addHabit();
            }
        };
        createButton.setOnClickListener(createListener);

        allHabitRecyclerView = root.findViewById(R.id.allHabitRecyclerView);
        HabitAdapter adapter1 = new HabitAdapter(new ArrayList<Habit>(), this, HabitConstants.ALL_HABIT_RECYCLER_VIEW);
        allHabitRecyclerView.setAdapter(adapter1);
        allHabitRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));



        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        refresh();
    }

    void addHabit() {
        Intent intent = new Intent(this.getActivity(), CreateHabitActivity.class);
        startActivity(intent);
    }

    @Override
    public void onHabitClick(int position, String tag) {
        if (tag.equals(HabitConstants.URGENT_HABIT_RECYCLER_VIEW)) {
            Intent intent = new Intent(this.getActivity(), HabitProfileActivity.class);
            Habit habit = ((HabitAdapter) urgentHabitRecyclerView.getAdapter()).getHabit(position);

            //intent.putExtra(HabitConstants.VEHICLE_TYPE_EXTRA, type);
            //intent.putExtra(HabitConstants.USER_ID, user.getId());
            startActivity(intent);
        }
        else if (tag.equals(HabitConstants.ALL_HABIT_RECYCLER_VIEW)) {
            Intent intent = new Intent(this.getActivity(), HabitProfileActivity.class);
            Habit habit = ((HabitAdapter) allHabitRecyclerView.getAdapter()).getHabit(position);

            //intent.putExtra(HabitConstants.VEHICLE_TYPE_EXTRA, type);
            //intent.putExtra(HabitConstants.USER_ID, user.getId());
            startActivity(intent);
        }
    }

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

    void addPending(String tag, Habit habitToAdd){
        if(tag.equals(HabitConstants.ALL_HABIT_RECYCLER_VIEW)){
            allPendingArrayList.add(habitToAdd);
            if(allPendingArrayList.size() == user.getHabits().size()){
                ((HabitAdapter) allHabitRecyclerView.getAdapter()).addHabits(allPendingArrayList);
                allHabitRecyclerView.getAdapter().notifyDataSetChanged();
            }
        }
    }

}
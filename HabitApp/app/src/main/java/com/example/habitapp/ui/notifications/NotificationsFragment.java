package com.example.habitapp.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.habitapp.CreateHabitActivity;
import com.example.habitapp.MainActivity;
import com.example.habitapp.R;
import com.example.habitapp.SettingsActivity;

public class NotificationsFragment extends Fragment {
    //for habits
    private HabitViewModel habitViewModel;
    Button createButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        habitViewModel =
                new ViewModelProvider(this).get(HabitViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        final TextView textView = root.findViewById(R.id.text_notifications);
        habitViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        createButton = root.findViewById(R.id.createButton);
        View.OnClickListener createListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addHabit();
            }
        };
        createButton.setOnClickListener(createListener);

        return root;
    }

    void addHabit(){
        Intent intent = new Intent( this.getActivity(), CreateHabitActivity.class);
        startActivity(intent);
    }

}
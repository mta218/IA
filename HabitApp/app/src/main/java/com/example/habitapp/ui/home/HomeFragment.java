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

import com.example.habitapp.MainActivity;
import com.example.habitapp.R;
import com.example.habitapp.SettingsActivity;
import com.example.habitapp.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private Button goToSettingsButton;

    FirebaseFirestore fRef;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        goToSettingsButton = root.findViewById(R.id.settingsButton);

        View.OnClickListener goToSettingsListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToSettings();
            }
        };
        goToSettingsButton.setOnClickListener(goToSettingsListener);


        return root;
    }

    private void goToSettings(){
        Intent intent = new Intent(this.getActivity(), SettingsActivity.class);
        startActivity(intent);
    }




}
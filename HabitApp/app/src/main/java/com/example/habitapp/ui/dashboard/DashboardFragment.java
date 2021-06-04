package com.example.habitapp.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class DashboardFragment extends Fragment {

    private SocialViewModel socialViewModel;

    RecyclerView urgentHabitRecyclerView, allHabitRecyclerView;
    FirebaseFirestore fRef;
    FirebaseAuth mAuth;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        socialViewModel =
                new ViewModelProvider(this).get(SocialViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);


        fRef = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        return root;


    }
}